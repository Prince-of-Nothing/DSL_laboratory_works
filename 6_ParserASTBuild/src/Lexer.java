import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {
    private final String source;
    private int cursor;
    private int line;
    private int column;

    public Lexer(String source) {
        this.source = source;
        this.cursor = 0;
        this.line = 1;
        this.column = 1;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (cursor < source.length()) {
            Token matched = matchToken();
            if (matched == null) {
                throw new IllegalArgumentException("Unexpected character '" + source.charAt(cursor)
                    + "' at " + line + ":" + column);
            }
            if (!matched.type().ignored()) {
                tokens.add(matched);
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
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
