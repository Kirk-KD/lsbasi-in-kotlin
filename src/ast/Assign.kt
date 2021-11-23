package ast

import interpreter.Token

class Assign(val left: AST, val op: Token, val right: AST) : AST() {
    val token = op
}
