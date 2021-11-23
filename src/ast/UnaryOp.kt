package ast

import interpreter.Token

class UnaryOp(val op: Token, val expr: AST) : AST() {
    val token = op
}
