package app

import models.Project
import org.apache.commons.cli.*
import java.io.File

fun constructOptions(): Options {
    val options = Options()

    Option("i", true, "input").apply {
        isRequired = true
    }.also { options.addOption(it) }

    Option("c", true, "config").apply {
        isRequired = true
    }.also { options.addOption(it) }

    return options
}

fun main(args: Array<String>) {
    val options = constructOptions()
    val parser = DefaultParser()

    val cmd: CommandLine? = try {
        parser.parse(options, args)
    } catch (ex: ParseException) {
        println(ex.message)
        val formatter = HelpFormatter()
        formatter.printHelp("Visual Reflexion", options)
        null
    }

    if (cmd == null) {
        System.exit(1)
    } else {
        val inputFilePath = cmd.getOptionValue("i")
        val configFilePath = cmd.getOptionValue("c")

        val inputFile = File(inputFilePath)
        val configFile = File(configFilePath)

        try {
            val project = Project.createFromFile(inputFile, configFile)
            val graph = project.analyzeAll().toGraphvizModel("G")
            println(graph.toString())
        } catch (ex: Exception) {
            println(ex.toString())
        }
    }
}
