/**
 * Lexer -> (Token) -> Parser -> (AST) -> Interpreter -> output
 */

import interpreter.Interpreter
import interpreter.Lexer
import interpreter.Parser
import symbol_table.SemanticAnalyzer
import symbol_table.SymbolTable
import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
//    while (true) {
//        print("> ")
//        val text = readLine().toString()
//
//        if (text.isEmpty()) continue
//
//        val lexer = Lexer(text)
//        val parser = Parser(lexer)
//        val interpreter = Interpreter(parser)
//
//        println(interpreter.interpret())
//    }


    val path = if (args.isEmpty()) readLine().toString() else args[0]
    val bufferedReader: BufferedReader = File(path).bufferedReader()
    val text = bufferedReader.use { it.readText() }

    val lexer = Lexer(text)
    val parser = Parser(lexer)

//    val symbolTable = SymbolTable()
//    symbolTable.visit(parser.parse())
//    println(symbolTable)

    val semanticAnalyzer = SemanticAnalyzer()
    semanticAnalyzer.visit(parser.parse())
    println(semanticAnalyzer.symbolTable)

//    val interpreter = Interpreter(parser)
//    interpreter.interpret()
}
