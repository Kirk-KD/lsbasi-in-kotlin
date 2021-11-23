package ast

import interpreter.Token

class Type(val token: Token) : AST() {
    val value = token.value
}
