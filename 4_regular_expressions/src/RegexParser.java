import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class RegexParser {
    private final List<RegexToken> tokens;
    private int cursor;

    public RegexParser(List<RegexToken> tokens) {
        this.tokens = tokens;
        this.cursor = 0;
    }

    public RegexNode parse() {
        RegexNode node = parseSequence(EnumSet.of(RegexTokenKind.EOF));
        consume(RegexTokenKind.EOF, "Expected end of input");
        return node;
    }

    private RegexNode parseSequence(Set<RegexTokenKind> stopTokens) {
        List<RegexNode> items = new ArrayList<>();
        while (!stopTokens.contains(peek().kind())) {
            items.add(parseItem());
        }
        if (items.isEmpty()) {
            return new SequenceNode(List.of());
        }
        return items.size() == 1 ? items.getFirst() : new SequenceNode(items);
    }

    private RegexNode parseItem() {
        RegexToken token = peek();
        RegexNode node;
        if (token.kind() == RegexTokenKind.WORD) {
            advance();
            node = new LiteralNode(token.value());
        } else if (token.kind() == RegexTokenKind.LPAREN) {
            advance();
            List<RegexNode> options = new ArrayList<>();
            options.add(parseSequence(EnumSet.of(RegexTokenKind.OR, RegexTokenKind.RPAREN)));
            while (match(RegexTokenKind.OR)) {
                options.add(parseSequence(EnumSet.of(RegexTokenKind.OR, RegexTokenKind.RPAREN)));
            }
            consume(RegexTokenKind.RPAREN, "Expected ')'");
            node = options.size() == 1 ? options.getFirst() : new ChoiceNode(options);
        } else {
            throw new IllegalArgumentException("Unexpected token " + token);
        }

        node.setQuantifier(parseQuantifier());
        return node;
    }

    private Quantifier parseQuantifier() {
        RegexToken token = peek();
        if (token.kind() == RegexTokenKind.STAR) {
            advance();
            return new Quantifier(QuantifierKind.STAR, 0);
        }
        if (token.kind() == RegexTokenKind.PLUS) {
            advance();
            return new Quantifier(QuantifierKind.PLUS, 0);
        }
        if (token.kind() == RegexTokenKind.QUESTION) {
            advance();
            return new Quantifier(QuantifierKind.QUESTION, 0);
        }
        if (token.kind() != RegexTokenKind.CARET) {
            return null;
        }

        advance();

        if (match(RegexTokenKind.LBRACKET)) {
            RegexToken target = advance();
            consume(RegexTokenKind.RBRACKET, "Expected ']'");
            return toQuantifier(target, "Expected *, +, ?, or a numeric repetition count inside '^[...]'");
        }

        RegexToken target = advance();
        return toQuantifier(target, "Expected *, +, ?, or a numeric repetition count after '^'");
    }

    private Quantifier toQuantifier(RegexToken target, String errorMessage) {
        return switch (target.kind()) {
            case STAR -> new Quantifier(QuantifierKind.STAR, 0);
            case PLUS -> new Quantifier(QuantifierKind.PLUS, 0);
            case QUESTION -> new Quantifier(QuantifierKind.QUESTION, 0);
            case WORD -> {
                if (!target.value().chars().allMatch(Character::isDigit)) {
                    throw new IllegalArgumentException(errorMessage);
                }
                yield new Quantifier(QuantifierKind.EXACT, Integer.parseInt(target.value()));
            }
            default -> throw new IllegalArgumentException(errorMessage);
        };
    }

    private RegexToken peek() {
        return tokens.get(cursor);
    }

    private RegexToken advance() {
        return tokens.get(cursor++);
    }

    private boolean match(RegexTokenKind kind) {
        if (peek().kind() == kind) {
            cursor++;
            return true;
        }
        return false;
    }

    private RegexToken consume(RegexTokenKind kind, String message) {
        RegexToken token = peek();
        if (token.kind() != kind) {
            throw new IllegalArgumentException(message);
        }
        cursor++;
        return token;
    }
}
