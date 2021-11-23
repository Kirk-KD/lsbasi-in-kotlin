package interpreter

enum class TokenType {
    INT,
    FLOAT,
    PLUS,
    MINUS,
    MUL,
    DIV,
    L_ROUND,
    R_ROUND,
    L_CURLY,
    R_CURLY,
    ASSIGN,
    SEMI,
    COLON,
    COMMA,
    DOT,
    ID,
    INT_TYPE,
    FLOAT_TYPE,
    EOF,

    // reserved keywords
    MAIN,
    VAR,
    PROC
}
