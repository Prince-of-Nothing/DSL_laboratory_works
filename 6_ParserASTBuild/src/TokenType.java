import java.util.regex.Pattern;

public enum TokenType {
    WHITESPACE("\\s+", true),
    COMMENT("//[^\\n]*", true),

    FUNCTION("function\\b"),
    RETURN("return\\b"),
    VAR("var\\b"),
    IF("if\\b"),
    ELSE("else\\b"),

    AND("&&"),
    OR("\\|\\|"),
    EQ("=="),
    NE("!="),
    LE("<="),
    GE(">="),
    ASSIGN("="),
    LT("<"),
    GT(">"),
    PLUS("\\+"),
    MINUS("-"),
    STAR("\\*"),
    SLASH("/"),
    PERCENT("%"),
    CARET("\\^"),
    BANG("!"),

    COMMA(","),
    SEMICOLON(";"),
    LPAREN("\\("),
    RPAREN("\\)"),
    LBRACE("\\{"),
    RBRACE("\\}"),

    NUMBER("\\d+(?:\\.\\d+)?"),
    STRING("\"[^\"\\n]*\""),
    IDENTIFIER("[A-Za-z_][A-Za-z0-9_]*"),
    EOF("$");

    private final Pattern pattern;
    private final boolean ignored;

    TokenType(String regex) {
        this(regex, false);
    }

    TokenType(String regex, boolean ignored) {
        this.pattern = Pattern.compile(regex);
        this.ignored = ignored;
    }

    public Pattern pattern() {
        return pattern;
    }

    public boolean ignored() {
        return ignored;
    }
}
