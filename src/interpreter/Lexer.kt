package interpreter

class Lexer(private val text: String) {
    /**
     * Takes a String and makes tokens.
     */

    private var pos = 0
    private var currentChar = this.text[this.pos]

    private val reservedKeywords = mutableMapOf<String, Token>().apply {
        this["main"] = Token(TokenType.MAIN, "main")
        this["var"] = Token(TokenType.VAR, "var")
        this["proc"] = Token(TokenType.PROC, "proc")

        this["Int"] = Token(TokenType.INT_TYPE, "Int")
        this["Float"] = Token(TokenType.FLOAT_TYPE, "Float")
    }

    private fun error() {
        throw Exception("Invalid character: '${this.currentChar}'.")
    }

    private fun advance() {
        ++this.pos
        if (this.pos < this.text.length) this.currentChar = this.text[this.pos]
        else this.currentChar = Character.MIN_VALUE
    }

    private fun skipComment() {
        while (this.currentChar != '\n') this.advance()
        this.advance()
    }

    private fun skipWhitespace() {
        while (currentChar.isWhitespace() && this.currentChar != Character.MIN_VALUE) this.advance()
    }

    private fun number(): Token {
        /**
         * Makes an integer or a float token.
         */

        var res = ""
        while (this.currentChar != Character.MIN_VALUE && this.currentChar.isDigit()) {
            res += this.currentChar
            this.advance()
        }

        return if (this.currentChar == '.') {
            res += this.currentChar
            this.advance()

            while (this.currentChar != Character.MIN_VALUE && this.currentChar.isDigit()) {
                res += this.currentChar
                this.advance()
            }

            Token(TokenType.FLOAT, res.toFloat())
        } else Token(TokenType.INT, res.toInt())
    }

    private fun id(): Token {
        /**
         * Makes a token for a variable or a reserved keyword.
         */

        var res = ""
        while (this.currentChar != Character.MIN_VALUE && (this.currentChar.isLetterOrDigit() || this.currentChar == '_')) {
            res += this.currentChar
            this.advance()
        }

        return if (this.reservedKeywords.containsKey(res)) this.reservedKeywords[res] as Token
        else Token(TokenType.ID, res)
    }

    fun getNextToken(): Token {
        while (this.currentChar != Character.MIN_VALUE) {
            if (this.currentChar.isWhitespace()) {
                this.skipWhitespace()
                continue
            } else if (this.currentChar == '#') {
                this.advance()
                this.skipComment()
                continue
            } else if (this.currentChar.isDigit()) return this.number()
            else if (this.currentChar.isLetter()) return this.id()

            val currentChar = this.currentChar
            this.advance()

            when (currentChar) {
                '+' -> return Token(TokenType.PLUS, '+')
                '-' -> return Token(TokenType.MINUS, '-')
                '*' -> return Token(TokenType.MUL, '*')
                '/' -> return Token(TokenType.DIV, '/')
                '(' -> return Token(TokenType.L_ROUND, '(')
                ')' -> return Token(TokenType.R_ROUND, ')')
                '{' -> return Token(TokenType.L_CURLY, '{')
                '}' -> return Token(TokenType.R_CURLY, '}')
                '=' -> return Token(TokenType.ASSIGN, '=')
                ';' -> return Token(TokenType.SEMI, ';')
                ':' -> return Token(TokenType.COLON, ':')
                ',' -> return Token(TokenType.COMMA, ',')
                '.' -> return Token(TokenType.DOT, '.')
            }

            this.error()
        }

        return Token(TokenType.EOF, null)
    }
}