
package dev.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private final int length;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
        this.length = input.length();
    }

    public List<Token> lex() {
        List<Token> tokens = new ArrayList<>();
        while (!isAtEnd()) {
            skipWhitespace();
            if (isAtEnd()) break;
            int start = pos;
            char c = advance();

            switch (c) {
                case '+': tokens.add(new Token(TokenType.PLUS, "+", start)); break;
                case '-': tokens.add(new Token(TokenType.MINUS, "-", start)); break;
                case '*': tokens.add(new Token(TokenType.STAR, "*", start)); break;
                case '/': tokens.add(new Token(TokenType.SLASH, "/", start)); break;
                case '^': tokens.add(new Token(TokenType.CARET, "^", start)); break;
                case '(': tokens.add(new Token(TokenType.LPAREN, "(", start)); break;
                case ')': tokens.add(new Token(TokenType.RPAREN, ")", start)); break;
                default:
                    if (isDigit(c) || (c == '.' && peekDigit())) {
                        tokens.add(numberToken(start, c));
                    } else if (isAlpha(c)) {
                        tokens.add(identifierToken(start, c));
                    } else {
                        throw new LexerException("Unexpected character '" + c + "'", start);
                    }
            }
        }
        tokens.add(new Token(TokenType.EOF, "", pos));
        return tokens;
    }

    private Token numberToken(int start, char first) {
        boolean seenDot = (first == '.');
        StringBuilder sb = new StringBuilder();
        sb.append(first);

        while (!isAtEnd()) {
            char c = peek();
            if (isDigit(c)) {
                sb.append(advance());
            } else if (c == '.' && !seenDot) {
                seenDot = true;
                sb.append(advance());
            } else break;
        }

        String lexeme = sb.toString();
        if (lexeme.equals(".")) {
            throw new LexerException("Lone dot is not a number", start);
        }
        return new Token(seenDot ? TokenType.FLOAT : TokenType.INTEGER, lexeme, start);
    }

    private Token identifierToken(int start, char first) {
        StringBuilder sb = new StringBuilder();
        sb.append(first);
        while (!isAtEnd() && (isAlphaNumeric(peek()) || peek() == '_')) {
            sb.append(advance());
        }
        return new Token(TokenType.IDENTIFIER, sb.toString(), start);
    }

    private void skipWhitespace() {
        while (!isAtEnd() && Character.isWhitespace(peek())) advance();
    }

    private char advance() { return input.charAt(pos++); }
    private char peek() { return input.charAt(pos); }
    private boolean peekDigit() { return !isAtEnd() && isDigit(peek()); }
    private boolean isAtEnd() { return pos >= length; }
    private boolean isDigit(char c) { return c >= '0' && c <= '9'; }
    private boolean isAlpha(char c) { return Character.isLetter(c); }
    private boolean isAlphaNumeric(char c) { return isAlpha(c) || isDigit(c); }

    // Convenience runner
    public static void main(String[] args) {
        String source = "sin(0.5) + cos(2) - 3.14^2 / x";
        List<Token> tokens = new Lexer(source).lex();
        tokens.forEach(System.out::println);
    }
}