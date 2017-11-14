import com.scitools.understand.Entity

fun printGraph(entity: Entity) {
    // 文字列表現のグラフが得られる
    println(
            entity.freetext("CGraph")
    )
}
