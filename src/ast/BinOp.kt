package ast

import interpreter.Token

class BinOp(val left: AST, val op: Token, val right: AST) : AST() {
    val token = op
}
