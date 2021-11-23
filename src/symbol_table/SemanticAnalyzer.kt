package symbol_table

import ast.*

class SemanticAnalyzer {
    val symbolTable = SymbolTable()

    fun visit(node: AST) {
        println("SemanticAnalyser: Visit Node: $node")
        when (node) {
            is Compound -> {
                for (child in node.children) this.visit(child)
            }
            is NoOp -> {}
            is VarDecl -> {
                val typeName = node.typeNode.value
                val typeSymbol = this.symbolTable.lookup(typeName as String)

                val varName = node.varNode.value
                if (this.symbolTable.lookup(varName as String) != null)
                    throw Exception("Semantic error: Duplicate identifier '$varName' found.")

                val varSymbol = VarSymbol(varName as String, typeSymbol as BuiltinTypeSymbol)
                this.symbolTable.insert(varSymbol)

                if (node.value != null) this.visit(node.value)
            }
            is Var -> {
                val varName = node.value
                this.symbolTable.lookup(varName as String) ?:
                    throw Exception("Semantic error: Identifier '$varName' not found.")
            }
            is Assign -> {
                this.visit(node.right)
                this.visit(node.left)
            }
            is BinOp -> {
                this.visit(node.left)
                this.visit(node.right)
            }
        }
    }
}
