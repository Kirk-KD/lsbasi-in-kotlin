package interpreter

import ast.*

class Interpreter(private val parser: Parser) {
    /**
     * Interprets the AST parsed by the Parser.
     */

    val globalScope = mutableMapOf<String, Number>()

    private fun error() {
        throw Exception("Invalid operator.")
    }

    private fun visit(node: AST): Any? {
        println("Interpreter: Visit Node: $node")
        when (node) {
            is IntNum -> return node.value
            is FloatNum -> return node.value
            is BinOp -> {
                val left = (this.visit(node.left) as Number).toFloat()
                val right = (this.visit(node.right) as Number).toFloat()
                when (node.op.type) {
                    TokenType.PLUS -> return left + right
                    TokenType.MINUS -> return left - right
                    TokenType.MUL -> return left * right
                    TokenType.DIV -> return left / right
                    else -> this.error()
                }
            }
            is UnaryOp -> {
                when (node.op.type) {
                    TokenType.MINUS -> return -(this.visit(node.expr) as Int)
                    TokenType.PLUS -> return +(this.visit(node.expr) as Int)
                    else -> this.error()
                }
            }
            is Compound -> {
                for (child in node.children) this.visit(child)
                return null
            }
            is NoOp -> return null
            is Assign -> {
                val key = (node.left as Var).value as String
                try {
                    val value = this.visit(node.right) as Int
                    this.globalScope[key] = value
                } catch (e: java.lang.ClassCastException) {
                    val value = this.visit(node.right) as Float
                    this.globalScope[key] = value
                }

                return null
            }
            is Var -> {
                val varName = node.value as String

                if (varName in this.globalScope.keys) return this.globalScope[varName]
                else throw Exception("Name error: '$varName'.")
            }
            is Type -> return null  // do nothing
            is VarDecl -> {
                if (node.value != null) {
                    val key = node.varNode.value as String
                    this.globalScope[key] = this.visit(node.value) as Number
                }

                return null
            }
            is ProcDecl -> {
                return null
            }

            else -> this.error()
        }

        this.error()
        return null  // won't reach here
    }

    fun interpret(): Any? {
        val tree = this.parser.parse()
        return this.visit(tree)
    }
}
