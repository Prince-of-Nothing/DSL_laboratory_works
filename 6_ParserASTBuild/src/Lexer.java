import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {
    private final String source;
    private final boolean verbose;
    private int cursor;
    private int line;
    private int column;

    public Lexer(String source) {
        this(source, false);
    }

    public Lexer(String source, boolean verbose) {
        this.source = source;
        this.verbose = verbose;
        this.cursor = 0;
        this.line = 1;
        this.column = 1;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        if (verbose) {
            System.out.println("\n[Lexer] Starting tokenization...");
        }
        while (cursor < source.length()) {
            Token matched = matchToken();
            if (matched == null) {
                throw new IllegalArgumentException("Unexpected character '" + source.charAt(cursor)
                    + "' at " + line + ":" + column);
            }
            if (matched.type().ignored()) {
                if (verbose) {
                    System.out.printf("[Lexer] at %d:%-3d SKIP %-10s '%s'%n",
                        matched.line(), matched.column(), matched.type(), matched.lexeme().replace("\n", "\\n"));
                }
            } else {
                tokens.add(matched);
                if (verbose) {
                    System.out.printf("[Lexer] at %d:%-3d %-12s '%s'%n",
                        matched.line(), matched.column(), matched.type(), matched.lexeme());
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        if (verbose) {
            System.out.printf("[Lexer] Done -- %d tokens produced.%n", tokens.size() - 1);
        }
        return tokens;
    }

    private Token matchToken() {
        String remaining = source.substring(cursor);
        for (TokenType type : TokenType.values()) {
            if (type == TokenType.EOF) {
                continue;
            }
            Matcher matcher = type.pattern().matcher(remaining);
            if (!matcher.lookingAt()) {
                continue;
            }
            String lexeme = matcher.group();
            Token token = new Token(type, lexeme, line, column);
            advance(lexeme);
            return token;
        }
        return null;
    }

    private void advance(String lexeme) {
        cursor += lexeme.length();
        for (int index = 0; index < lexeme.length(); index++) {
            if (lexeme.charAt(index) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
    }
}
