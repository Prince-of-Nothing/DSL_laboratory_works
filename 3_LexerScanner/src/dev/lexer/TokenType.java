
package dev.lexer;

public enum TokenType {
    // Single-character tokens
    PLUS, MINUS, STAR, SLASH, CARET,
    LPAREN, RPAREN,

    // Literals
    INTEGER, FLOAT, IDENTIFIER,

    // End of input
    EOF
}