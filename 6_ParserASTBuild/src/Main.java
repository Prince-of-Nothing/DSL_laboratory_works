import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String[] EXAMPLES = {
        "var z = sin(0.5) + cos(2) - 3.14 ^ 2 / x;",
        "if (x > 10) { var y = 5; } else { var y = 0; }",
        "function add(a, b) { return a + b; }",
        "var result = (a + b) * (c - d);",
        "function max(a, b) { if (a > b) { return a; } else { return b; } }",
        "var n = 2 ^ 10;",
        "var flag = a && b || !c;",
        "var msg = \"hello\";",
        "var r = (x + y) * (x - y) / (x + 1);",
        "if (a != b) { x = a - b; } else { x = 0; }"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Parser & AST Builder ===");
        System.out.println("Enter source code, a number 1-10 for a preset example, or 'exit' to quit.");
        System.out.println();
        for (int i = 0; i < EXAMPLES.length; i++) {
            System.out.printf("  %2d. %s%n", i + 1, EXAMPLES[i]);
        }

        while (true) {
            System.out.print("\nSource> ");
            System.out.flush();
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }
            if (input.isEmpty()) {
                continue;
            }

            String source = input;
            if (input.matches("[1-9]|10")) {
                int idx = Integer.parseInt(input) - 1;
                source = EXAMPLES[idx];
                System.out.println("Using example " + input + ": " + source);
            }

            try {
                List<Token> tokens = new Lexer(source).tokenize();
                ProgramNode program = new Parser(tokens).parse();

                System.out.println("\nAST:");
                System.out.println(program.prettyPrint());

                System.out.print("Show tokens? [y/n]: ");
                System.out.flush();
                if (scanner.hasNextLine()) {
                    String answer = scanner.nextLine().trim().toLowerCase();
                    if (answer.equals("y") || answer.equals("yes")) {
                        System.out.println("\nTokens:");
                        for (Token token : tokens) {
                            if (token.type() == TokenType.EOF) continue;
                            System.out.printf("  %-12s %-14s at %d:%d%n",
                                token.type(), token.lexeme(), token.line(), token.column());
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println("=".repeat(60));
        }

        scanner.close();
    }
}
