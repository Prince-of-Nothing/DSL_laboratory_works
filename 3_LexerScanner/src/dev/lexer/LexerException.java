package dev.lexer;

public class LexerException extends RuntimeException {
    public LexerException(String message, int position) {
        super(message + " at position " + position);
    }
}