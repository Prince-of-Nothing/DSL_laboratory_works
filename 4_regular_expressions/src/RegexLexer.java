import java.util.ArrayList;
import java.util.List;

public class RegexLexer {
    private final String source;
    private int cursor;

    public RegexLexer(String source) {
        this.source = source;
        this.cursor = 0;
    }

    public List<RegexToken> lex() {
        List<RegexToken> tokens = new ArrayList<>();
        while (cursor < source.length()) {
            char current = source.charAt(cursor);
            if (Character.isWhitespace(current)) {
                cursor++;
                continue;
            }
            switch (current) {
                case '(' -> {
                    cursor++;
                    tokens.add(new RegexToken(RegexTokenKind.LPAREN, null));
                }
                case ')' -> {
                    cursor++;
                    tokens.add(new RegexToken(RegexTokenKind.RPAREN, null));
                }
                case '|' -> {
                    cursor++;
                    tokens.add(new RegexToken(RegexTokenKind.OR, null));
                }
                case '*' -> {
                    cursor++;
                    tokens.add(new RegexToken(RegexTokenKind.STAR, null));
                }
                case '+' -> {
                    cursor++;
                    tokens.add(new RegexToken(RegexTokenKind.PLUS, null));
                }
                case '?' -> {
                    cursor++;
                    tokens.add(new RegexToken(RegexTokenKind.QUESTION, null));
                }
                case '^' -> {
                    cursor++;
                    tokens.add(new RegexToken(RegexTokenKind.CARET, null));
                }
                case '[' -> {
                    cursor++;
                    tokens.add(new RegexToken(RegexTokenKind.LBRACKET, null));
                }
                case ']' -> {
                    cursor++;
                    tokens.add(new RegexToken(RegexTokenKind.RBRACKET, null));
                }
                default -> tokens.add(new RegexToken(RegexTokenKind.WORD, consumeSymbol()));
            }
        }
        tokens.add(new RegexToken(RegexTokenKind.EOF, null));
        return tokens;
    }

    private String consumeSymbol() {
        char current = source.charAt(cursor);
        if (Character.isWhitespace(current) || "()|*+?^[]".indexOf(current) >= 0) {
            throw new IllegalArgumentException("Unexpected symbol at position " + cursor);
        }
        cursor++;
        return String.valueOf(current);
    }
}
