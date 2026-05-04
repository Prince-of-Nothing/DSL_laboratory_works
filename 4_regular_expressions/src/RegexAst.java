import java.util.ArrayList;
import java.util.List;

enum QuantifierKind {
    STAR,
    PLUS,
    QUESTION,
    EXACT
}

record Quantifier(QuantifierKind kind, int count) {
    String suffix() {
        return switch (kind) {
            case STAR -> " *";
            case PLUS -> " +";
            case QUESTION -> " ?";
            case EXACT -> " ^" + count;
        };
    }
}

abstract class RegexNode {
    private Quantifier quantifier;

    public Quantifier getQuantifier() {
        return quantifier;
    }

    public void setQuantifier(Quantifier quantifier) {
        this.quantifier = quantifier;
    }

    public String describe(int depth) {
        List<String> lines = new ArrayList<>();
        appendDescription(lines, depth);
        return String.join(System.lineSeparator(), lines);
    }

    protected String indent(int depth) {
        return "  ".repeat(depth);
    }

    protected String quantifierSuffix() {
        return quantifier == null ? "" : quantifier.suffix();
    }

    protected abstract void appendDescription(List<String> lines, int depth);
}

final class LiteralNode extends RegexNode {
    private final String value;

    LiteralNode(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    protected void appendDescription(List<String> lines, int depth) {
        lines.add(indent(depth) + "WORD " + value + quantifierSuffix());
    }
}

final class SequenceNode extends RegexNode {
    private final List<RegexNode> items;

    SequenceNode(List<RegexNode> items) {
        this.items = items;
    }

    public List<RegexNode> items() {
        return items;
    }

    @Override
    protected void appendDescription(List<String> lines, int depth) {
        lines.add(indent(depth) + "SEQ" + quantifierSuffix());
        for (RegexNode item : items) {
            item.appendDescription(lines, depth + 1);
        }
    }
}

final class ChoiceNode extends RegexNode {
    private final List<RegexNode> options;

    ChoiceNode(List<RegexNode> options) {
        this.options = options;
    }

    public List<RegexNode> options() {
        return options;
    }

    @Override
    protected void appendDescription(List<String> lines, int depth) {
        lines.add(indent(depth) + "OR" + quantifierSuffix());
        for (RegexNode option : options) {
            option.appendDescription(lines, depth + 1);
        }
    }
}
