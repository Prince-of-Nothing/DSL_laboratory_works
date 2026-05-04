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

    private String emit(RegexNode node) {
        String raw;
        if (node instanceof LiteralNode literalNode) {
            raw = literalNode.value();
        } else if (node instanceof SequenceNode sequenceNode) {
            StringBuilder builder = new StringBuilder();
            for (RegexNode child : sequenceNode.items()) {
                builder.append(emit(child));
            }
            raw = builder.toString();
        } else if (node instanceof ChoiceNode choiceNode) {
            int index = random.nextInt(choiceNode.options().size());
            raw = emit(choiceNode.options().get(index));
        } else {
            throw new IllegalStateException("Unsupported node type " + node.getClass().getSimpleName());
        }

        return applyQuantifier(raw, node.getQuantifier());
    }

    private String applyQuantifier(String value, Quantifier quantifier) {
        if (quantifier == null) {
            return value;
        }

        int count = switch (quantifier.kind()) {
            case STAR -> random.nextInt(MAX_REPETITION + 1);
            case PLUS -> 1 + random.nextInt(MAX_REPETITION);
            case QUESTION -> random.nextInt(2);
            case EXACT -> quantifier.count();
        };

        return value.repeat(count);
    }
}
