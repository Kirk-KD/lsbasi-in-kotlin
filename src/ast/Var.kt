package ast

import interpreter.Token

class Var(val token: Token) : AST() {
    val value = token.value
}
