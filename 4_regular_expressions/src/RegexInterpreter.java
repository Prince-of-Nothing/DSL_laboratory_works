import java.util.Random;

public class RegexInterpreter {
    private static final int MAX_REPETITION = 5;

    private final RegexNode tree;
    private final Random random;

    public RegexInterpreter(RegexNode tree) {
        this(tree, new Random());
    }

    public RegexInterpreter(RegexNode tree, Random random) {
        this.tree = tree;
        this.random = random;
    }

    public String generate() {
        return emit(tree);
    }

    // Determines repetition count from a quantifier (null → exactly 1).
    private int repetitionCount(Quantifier quantifier) {
        if (quantifier == null) return 1;
        return switch (quantifier.kind()) {
            case STAR     -> random.nextInt(MAX_REPETITION + 1);   // 0–5
            case PLUS     -> 1 + random.nextInt(MAX_REPETITION);   // 1–5
            case QUESTION -> random.nextInt(2);                     // 0 or 1
            case EXACT    -> quantifier.count();
        };
    }

    // Emits a node, repeating based on its quantifier.
    // For a ChoiceNode, the same branch is chosen once and then repeated N times,
    // (X|Y|Z)^[2] evaluates the group twice independently, so it can produce "XY", "YZ", "XX", etc.
    private String emit(RegexNode node) {
        int count = repetitionCount(node.getQuantifier());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) result.append(emitOnce(node));
        return result.toString();
    }

    // Evaluates the node exactly once, ignoring its quantifier.
    private String emitOnce(RegexNode node) {
        if (node instanceof LiteralNode literalNode) {
            return literalNode.value();
        } else if (node instanceof SequenceNode sequenceNode) {
            StringBuilder builder = new StringBuilder();
            for (RegexNode child : sequenceNode.items()) builder.append(emit(child));
            return builder.toString();
        } else if (node instanceof ChoiceNode choiceNode) {
            int index = random.nextInt(choiceNode.options().size());
            return emit(choiceNode.options().get(index));
        }
        throw new IllegalStateException("Unsupported node type: " + (node == null ? "null" : node.getClass().getSimpleName()));
    }
}
