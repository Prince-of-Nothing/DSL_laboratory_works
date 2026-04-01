
package dev.lexer;

public class Main {
    public static void main(String[] args) {
        run("functions_and_arithmetic", "sin(0.5) + cos(2) - 3.14^2 / x", () -> expectTokens(
                "sin(0.5) + cos(2) - 3.14^2 / x",
                new Token(TokenType.IDENTIFIER, "sin", 0),
                new Token(TokenType.LPAREN, "(", 0),
                new Token(TokenType.FLOAT, "0.5", 0),
                new Token(TokenType.RPAREN, ")", 0),
                new Token(TokenType.PLUS, "+", 0),
                new Token(TokenType.IDENTIFIER, "cos", 0),
                new Token(TokenType.LPAREN, "(", 0),
                new Token(TokenType.INTEGER, "2", 0),
                new Token(TokenType.RPAREN, ")", 0),
                new Token(TokenType.MINUS, "-", 0),
                new Token(TokenType.FLOAT, "3.14", 0),
                new Token(TokenType.CARET, "^", 0),
                new Token(TokenType.INTEGER, "2", 0),
                new Token(TokenType.SLASH, "/", 0),
                new Token(TokenType.IDENTIFIER, "x", 0),
                new Token(TokenType.EOF, "", 0)
        ));

        run("whitespace_and_star_slash", "   42 * 7 / 3", () -> expectTokens(
                "   42 * 7 / 3",
                new Token(TokenType.INTEGER, "42", 0),
                new Token(TokenType.STAR, "*", 0),
                new Token(TokenType.INTEGER, "7", 0),
                new Token(TokenType.SLASH, "/", 0),
                new Token(TokenType.INTEGER, "3", 0),
                new Token(TokenType.EOF, "", 0)
        ));

        run("leading_dot_floats", ".75 + .25", () -> expectTokens(
                ".75 + .25",
                new Token(TokenType.FLOAT, ".75", 0),
                new Token(TokenType.PLUS, "+", 0),
                new Token(TokenType.FLOAT, ".25", 0),
                new Token(TokenType.EOF, "", 0)
        ));

        run("identifier_with_underscore", "foo_bar1", () -> expectTokens(
                "foo_bar1",
                new Token(TokenType.IDENTIFIER, "foo_bar1", 0),
                new Token(TokenType.EOF, "", 0)
        ));

        run("lone_dot_error", ".", () -> expectThrows(
                ".",
            "Unexpected character '.'"
        ));

        run("unexpected_character_error", "1 @ 2", () -> expectThrows(
                "1 @ 2",
                "Unexpected character '@'"
        ));

        System.out.println("All lexer tests passed.");
    }

    private static void expectTokens(String source, Token... expected) {
        var actual = new Lexer(source).lex();
        if (actual.size() != expected.length) {
            throw new AssertionError("Token count mismatch: expected " + expected.length + " got " + actual.size());
        }
        for (int i = 0; i < expected.length; i++) {
            Token a = actual.get(i);
            Token e = expected[i];
            if (a.type() != e.type() || !a.lexeme().equals(e.lexeme())) {
                throw new AssertionError("Mismatch at index " + i + ": expected " + e + " got " + a);
            }
        }
    }

    private static void expectThrows(String source, String messageSubstring) {
        try {
            new Lexer(source).lex();
        } catch (LexerException ex) {
            if (!ex.getMessage().contains(messageSubstring)) {
                throw new AssertionError("Exception message mismatch: " + ex.getMessage());
            }
            return;
        }
        throw new AssertionError("Expected LexerException for input: " + source);
    }

    private static void run(String name, String source, Runnable test) {
        try {
            test.run();
            System.out.println("✔ " + name + " - parsing: \"" + source + "\"");
        } catch (RuntimeException | AssertionError ex) {
            System.err.println("✖ " + name + " - parsing: \"" + source + "\" - " + ex.getMessage());
            throw ex;
        }
    }
}