package ast

import interpreter.Token

class FloatNum(val token: Token) : AST() {
    val value = token.value as Float
}
