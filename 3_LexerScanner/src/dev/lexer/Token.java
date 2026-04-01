package dev.lexer;

public record Token(TokenType type, String lexeme, int position) {
    @Override public String toString() {
        return type + "('" + lexeme + "' @" + position + ")";
    }
}