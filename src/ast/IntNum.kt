package ast

import interpreter.Token

class IntNum(val token: Token) : AST() {
    val value = token.value as Int
}
