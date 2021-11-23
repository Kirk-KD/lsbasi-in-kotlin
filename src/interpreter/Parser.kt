package interpreter

import ast.*

class Parser(private val lexer: Lexer) {
    /**
     * Makes an AST (Abstract Syntax Tree) from the tokens got from the Lexer.
     */

    private var currentToken = this.lexer.getNextToken()

    private fun error(s: String="None") {
        throw Exception("Invalid syntax. Token: ${this.currentToken}. Additional info: $s")
    }

    private fun eat(tokenType: TokenType) {
        /**
         * Compares the current token's type with the desired token type
         * and if they match, assign the next token to the current token.
         * Otherwise, raise an exception.
         */

        if (this.currentToken.type == tokenType) this.currentToken = this.lexer.getNextToken()
        else this.error("TokenType $tokenType was expected.")
    }

    fun parse(): AST {
        return this.program()
    }

    /**
     * GRAMMARS
     *
     * program              : MAIN statement
     * compound_statement   : L_CURLY statement_list R_CURLY
     * statement_list       : statement | statement SEMI statement_list
     * statement            : compound_statement | assignment_statement | decl_statement | empty
     * variable_decl        : ID COLON type_spec (ASSIGN expr)
     * type_spec            : INT_TYPE | FLOAT_TYPE
     * assignment_statement : variable ASSIGN expr
     * var_decl_statement   : var_decl_list
     * var_decl_list        : VAR variable_decl (COMMA variable_decl)*
     * procedure_decl       : PROC ID statement
     * proc_decl_statement  : function_decl
     * empty                :
     * expr                 : term ((PLUS | MINUS) term)*
     * term                 : factor ((MUL | DIV) factor)*
     * factor               : PLUS factor
     *                      | MINUS factor
     *                      | INT
     *                      | L_ROUND expr R_ROUND
     *                      | variable
     * variable             : ID
     */

    private fun program(): AST {
        this.eat(TokenType.MAIN)
        val node = this.compoundStatement()
        if (this.currentToken.type != TokenType.EOF) this.error()
        return node
    }

    private fun statement(): AST {
        return when (this.currentToken.type) {
            TokenType.L_CURLY -> this.compoundStatement()
            TokenType.ID      -> this.varAssignmentStatement()
            TokenType.VAR     -> this.varDeclStatement()
            TokenType.PROC    -> this.procDeclStatement()
            else              -> this.empty()
        }
    }

    private fun compoundStatement(): AST {
        this.eat(TokenType.L_CURLY)
        val nodes = this.statementList()
        this.eat(TokenType.R_CURLY)

        val root = Compound()
        for (node in nodes) root.children.add(node)
        return root
    }

    private fun statementList(): MutableList<AST> {
        val res = mutableListOf(this.statement())

        while (this.currentToken.type == TokenType.SEMI) {
            this.eat(TokenType.SEMI)
            res.add(this.statement())
        }

        return res
    }

    private fun varDeclStatement(): AST {
        val nodes = this.varDeclList()
        val root = Compound()
        for (node in nodes) root.children.add(node)
        return root
    }

    private fun varDeclList(): MutableList<AST> {
        this.eat(TokenType.VAR)
        val varDeclNodes = mutableListOf(this.variableDecl())

        while (this.currentToken.type == TokenType.COMMA) {
            this.eat(TokenType.COMMA)
            varDeclNodes.add(this.variableDecl())
        }

        return varDeclNodes
    }

    private fun varAssignmentStatement(): AST {
        val left = this.variable()
        val token = this.currentToken
        this.eat(TokenType.ASSIGN)
        val right = this.expr()

        return Assign(left, token, right)
    }

    private fun variableDecl(): AST {
        val varNode = this.variable() as Var
        this.eat(TokenType.COLON)
        val typeNode = this.typeSpec() as Type

        var value: AST? = null
        if (this.currentToken.type == TokenType.ASSIGN) {
            this.eat(TokenType.ASSIGN)
            value = this.expr()
        }

        return VarDecl(varNode, typeNode, value)
    }

    private fun procDeclStatement(): AST {
        return this.procedureDecl()
    }

    private fun procedureDecl(): AST {
        this.eat(TokenType.PROC)
        val funcName = this.currentToken.value
        this.eat(TokenType.ID)
        val funcBody = this.statement()

        return ProcDecl(funcName as String, funcBody)
    }

    private fun typeSpec(): AST {
        val token = this.currentToken

        if (this.currentToken.type == TokenType.INT_TYPE) this.eat(TokenType.INT_TYPE)
        else this.eat(TokenType.FLOAT_TYPE)

        return Type(token)
    }

    private fun variable(): AST {
        val node = Var(this.currentToken)
        this.eat(TokenType.ID)

        return node
    }

    private fun empty(): AST {
        return NoOp()
    }

    private fun expr(): AST {
        var node = this.term()

        while (this.currentToken.type in arrayOf(TokenType.PLUS, TokenType.MINUS)) {
            val token = this.currentToken

            if (token.type == TokenType.PLUS) this.eat(TokenType.PLUS)
            else if (token.type == TokenType.MINUS) this.eat(TokenType.MINUS)

            node = BinOp(node, token, this.term())
        }

        return node
    }

    private fun term(): AST {
        var node = this.factor()

        while (this.currentToken.type in arrayOf(TokenType.MUL, TokenType.DIV)) {
            val token = this.currentToken

            if (token.type == TokenType.MUL) this.eat(TokenType.MUL)
            else if (token.type == TokenType.DIV) this.eat(TokenType.DIV)

            node = BinOp(node, token, this.factor())
        }

        return node
    }

    private fun factor(): AST {
        val token = this.currentToken

        when (token.type) {
            TokenType.INT -> {
                this.eat(TokenType.INT)

                return IntNum(token)
            }
            TokenType.FLOAT -> {
                this.eat(TokenType.FLOAT)

                return FloatNum(token)
            }
            TokenType.L_ROUND -> {
                this.eat(TokenType.L_ROUND)
                val node = this.expr()
                this.eat(TokenType.R_ROUND)

                return node
            }
            TokenType.PLUS -> {
                this.eat(TokenType.PLUS)

                return UnaryOp(token, this.factor())
            }
            TokenType.MINUS -> {
                this.eat(TokenType.MINUS)

                return UnaryOp(token, this.factor())
            }
            else -> return this.variable()
        }
    }
}
