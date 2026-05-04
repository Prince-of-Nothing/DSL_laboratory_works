import java.util.List;

public class Main {
    private static final List<String> EXAMPLES = List.of(
        "var z = sin(0.5) + cos(2) - 3.14 ^ 2 / x;",
        "if (x > 10) { var y = 5; } else { var y = 0; }",
        "function add(a, b) { return a + b; }",
        "var result = (a + b) * (c - d);"
    );

    public static void main(String[] args) {
        for (String source : EXAMPLES) {
            System.out.println("Source:");
            System.out.println(source);
            System.out.println("\nTokens:");

            List<Token> tokens = new Lexer(source).tokenize();
            for (Token token : tokens) {
                if (token.type() == TokenType.EOF) {
                    continue;
                }
                System.out.printf("  %-12s %-12s at %d:%d%n",
                    token.type(), token.lexeme(), token.line(), token.column());
            }

            ProgramNode program = new Parser(tokens).parse();
            System.out.println("\nAST:");
            System.out.println(program.prettyPrint());
            System.out.println("\n" + "=".repeat(70) + "\n");
        }
    }
}
