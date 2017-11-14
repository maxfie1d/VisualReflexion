package scripts

import data.DefUseData
import data.VariableData
import models.FunctionTable
import models.VariableTable

// 関数表、変数表のDiffを取る関数


data class FuncTableDiffResult(
        // expectedだけに含まれる関数
        val onlyExpectedFuncNames: List<String>,
        // actualだけに含まれる関数
        val onlyActualFuncNames: List<String>
)

data class TestResult(val success: Boolean, val comment: String)
data class VarTestResult(val varData: VariableData, val results: List<TestResult>)

fun <T : Any?> test(itemName: String, expected: T, actual: T, comparer: ((a: T, b: T) -> Boolean)? = null): TestResult {
    val equality = if (comparer == null) {
        actual == expected
    } else {
        comparer(actual, expected)
    }

    val comment = if (equality) "" else "$itemName が異なります。 Expected: $expected, Actual: $actual"

    return TestResult(equality, comment)
}

/**
 * 関数表のDiffを表示する
 */
fun diffFuncTable(expected: FunctionTable, actual: FunctionTable) {
    val onlyExpectedFuncNames = expected.filter {
        actual.findByName(it.funcName) == null
    }.map { it.funcName }

    val onlyActualFuncNames = actual.filter {
        expected.findByName(it.funcName) == null
    }.map { it.funcName }

    val failCount = actual
            .map { func ->
                val other = expected.findByName(func.funcName)
                Pair(func, other)
            }
            .map { (func, other) ->
                if (other != null) {

                    // id, func_name, kind, file_path, declare_range を比較する
                    // idは異なる場合があるのでスキップ
                    // func_nameは一致しているはずなのでスキップ
                    listOf(
                            test("kind", other.kind, func.kind),
                            test("file_path", other.filePath, func.filePath) { a, b ->
                                a.endsWith(b) || b.endsWith(a)
                            },
                            test("declare_range", other.declareRange, func.declareRange)
                    )
                } else {
                    emptyList()
                }
            }.flatten().count { !it.success }

    for (func in actual) {
        // expectedな関数表の中に存在するか
        val other = expected.findByName(func.funcName)
        if (other != null) {
        }
    }

    val totalFailCount = onlyExpectedFuncNames.count()
    +onlyActualFuncNames.count()
    +failCount

    println("Total fail count: $totalFailCount")
}

fun getFuncNameFromFuncTable(funcId: String, funcTable: FunctionTable): String {
    return if (funcId.isEmpty()) {
        ""
    } else {
        val fd = funcTable.findById(funcId)
        fd?.funcName ?: "__OUTSIDE__"
    }
}

fun compareDefUse(target: String, expectedDefUses: List<DefUseData>, expectedFuncTable: FunctionTable,
                  actualDefUses: List<DefUseData>, actualFuncTable: FunctionTable): List<TestResult> {

    val rr = actualDefUses.map { defuse ->
        // 該当するactualDefuseを見つける
        val other = expectedDefUses.find {
            val lineNumberSame = it.programPoint.lineNumber == defuse.programPoint.lineNumber
            val f1Name = getFuncNameFromFuncTable(it.programPoint.funcId, expectedFuncTable)
            val f2Name = getFuncNameFromFuncTable(defuse.programPoint.funcId, actualFuncTable)
            val memberNameSame = it.memberName == defuse.memberName
            val functionSame = f1Name == f2Name
            val derivedFromExistSame = it.derivedFromFuncId.isEmpty() && defuse.derivedFromFuncId.isEmpty()
                    || it.derivedFromFuncId.isNotEmpty() && defuse.derivedFromFuncId.isNotEmpty()

            // FIXME: いったん行番号の違いを無視することにした
//            lineNumberSame &&
            memberNameSame &&
                    derivedFromExistSame &&
                    functionSame
        }
        Pair(defuse, other)
    }.map { (defuse, other) ->
        if (other == null) {
            listOf(
                    TestResult(false, "マッチする${target}が存在しません. $defuse")
            )
        } else {
            // derived_fromとmember_nameをテストする
            val expectedDerivedFromFunc = getFuncNameFromFuncTable(other.derivedFromFuncId, expectedFuncTable)
            val actualDerivedFromFunc = getFuncNameFromFuncTable(defuse.derivedFromFuncId, actualFuncTable)

            listOf(
                    test("$target(membername)", other.memberName, defuse.memberName),
                    test("$target(derived_from)", expectedDerivedFromFunc, actualDerivedFromFunc)
            )
        }
    }.flatten()

    return listOf(
            listOf(test("$target count", expectedDefUses.count(), actualDefUses.count())),
            rr
    ).flatten()
}

fun diffVarTable(expected: VariableTable, expectedFuncTable: FunctionTable,
                 actual: VariableTable, actualFuncTable: FunctionTable) {

    // expectedにだけ存在する変数名
    val onlyExpectedVarNames = expected.filter {
        actual.findByNameAndTypeAndFile(it.name, it.type, it.file) == null
    }

    // actualにだけ存在する変数名
    val onlyActualVarNames = actual.filter {
        expected.findByNameAndTypeAndFile(it.name, it.type, it.file) == null
    }

    println("Only expected var names: ${onlyExpectedVarNames.count()} item(s)")
    displayNotExistVars(onlyExpectedVarNames)
    println("")
    println("Only actual var names: ${onlyActualVarNames.count()} item(s)")
    displayNotExistVars(onlyActualVarNames)

    // file, func, var, def, useを比較する
    val results: List<VarTestResult> = actual.map {
        val other = expected.findByNameAndTypeAndFile(it.name, it.type, it.file)
        Pair(it, other)
    }.filter { (it, other) -> other != null }
            .map { (it, other) -> Pair(it, other!!) }
            .map { (varData, otherVarData) ->
                // defを比較する
                val compareDefResults = compareDefUse("defs", otherVarData.def, expectedFuncTable, varData.def, actualFuncTable)
                val compareUsesResults = compareDefUse("uses", otherVarData.use, expectedFuncTable, varData.use, actualFuncTable)
                // funcを比較する
                val list = listOf(
                        listOf(test("func", otherVarData.func, varData.func)),
//                        compareDefResults, // 一時的にdefの比較をoffに
                        compareUsesResults
                )
                        .flatten()

                VarTestResult(varData, list)
            }

    results.forEach {
        val failCount = it.results.count { !it.success }
        if (failCount > 0) {
            println("変数 ${it.varData.type} ${it.varData.name} で ${failCount}件のエラーが見つかりました。")
            it.results.filter { !it.success }.forEach { println(it.comment) }
            println("====================")
        }
    }

    val totalFailCount = results.map { it.results.count { !it.success } }.sum()
    println("Total fail count: $totalFailCount")
}

fun displayNotExistVars(vars: List<VariableData>) {
    vars.forEachIndexed { index, varData ->
        println("${index + 1} : ${varData.type} ${varData.name}")
    }
}
