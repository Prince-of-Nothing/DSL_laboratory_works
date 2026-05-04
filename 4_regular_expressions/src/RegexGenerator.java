import java.util.ArrayList;
import java.util.List;

public final class RegexGenerator {
    private RegexGenerator() {
    }

    public static String generate(String regex) {
        RegexNode tree = buildTree(regex);
        return new RegexInterpreter(tree).generate();
    }

    public static List<String> generateMany(String regex, int count) {
        RegexNode tree = buildTree(regex);
        RegexInterpreter interpreter = new RegexInterpreter(tree);
        List<String> results = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            results.add(interpreter.generate());
        }
        return results;
    }

    public static String trace(String regex) {
        RegexNode tree = buildTree(regex);
        return tree.describe(0);
    }

    private static RegexNode buildTree(String regex) {
        List<RegexToken> tokens = new RegexLexer(regex).lex();
        return new RegexParser(tokens).parse();
    }
}
