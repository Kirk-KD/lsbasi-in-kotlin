package symbol_table

import ast.*

class SymbolTable {
    private val symbols = mutableMapOf<String, Symbol>()

    init {
        this.initBuiltinSymbols(arrayOf(
            "Int",
            "Float"
        ))
    }

    override fun toString(): String {
        var res = "-------------------------\n\nSymbolTable Contents\n"
        for ((name, symbol) in this.symbols) {
            res += " * $name = $symbol\n"
        }
        return "$res\n-------------------------"
    }

    private fun initBuiltinSymbols(symbolNames: Array<String>) {
        for (name in symbolNames) this.insert(BuiltinTypeSymbol(name))
    }

    fun insert(symbol: Symbol) {
        println("SymbolTable: Insert Symbol: $symbol")
        this.symbols[symbol.name] = symbol
    }

    fun lookup(name: String): Symbol? {
        println("SymbolTable: Lookup Symbol: '$name'")
        return this.symbols[name]
    }

    fun visit(node: AST) {
        println("SymbolTable: Visit Node: $node")
        when (node) {
            is IntNum -> {}
            is FloatNum -> {}
            is BinOp -> {
                this.visit(node.left)
                this.visit(node.right)
            }
            is UnaryOp -> this.visit(node.expr)
            is Compound -> for (child in node.children) this.visit(child)
            is NoOp -> {}
            is Assign -> {
                val varName = (node.left as Var).value as String
                this.lookup(varName) ?: throw Exception("Name error: '$varName'.")

                this.visit(node.right)
            }
            is Var -> {
                val varName = node.value as String
                this.lookup(varName) ?: throw Exception("Name error: '$varName'")
            }
            is VarDecl -> {
                val typeSymbol = this.lookup(node.typeNode.value as String)
                val varSymbol = VarSymbol(node.varNode.value as String, typeSymbol as BuiltinTypeSymbol)
                this.insert(varSymbol)
            }
            is ProcDecl -> {}
        }
    }
}