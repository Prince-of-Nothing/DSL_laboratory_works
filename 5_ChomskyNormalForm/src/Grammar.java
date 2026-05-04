import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Grammar {
    private final Set<String> nonTerminals;
    private final Set<String> terminals;
    private final Map<String, Set<List<String>>> productions;
    private final String startSymbol;

    public Grammar(Collection<String> nonTerminals, Collection<String> terminals, String startSymbol) {
        this.nonTerminals = new LinkedHashSet<>(nonTerminals);
        this.terminals = new LinkedHashSet<>(terminals);
        this.productions = new LinkedHashMap<>();
        this.startSymbol = startSymbol;
    }

    public void addProduction(String lhs, String... rhs) {
        addProduction(lhs, Arrays.asList(rhs));
    }

    public void addProduction(String lhs, List<String> rhs) {
        productions.computeIfAbsent(lhs, key -> new LinkedHashSet<>()).add(List.copyOf(rhs));
        nonTerminals.add(lhs);
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public Map<String, Set<List<String>>> getProductions() {
        return productions;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public Grammar copy() {
        Grammar clone = new Grammar(nonTerminals, terminals, startSymbol);
        for (Map.Entry<String, Set<List<String>>> entry : productions.entrySet()) {
            for (List<String> rhs : entry.getValue()) {
                clone.addProduction(entry.getKey(), rhs);
            }
        }
        return clone;
    }

    public String format() {
        StringBuilder builder = new StringBuilder();
        builder.append("VN = ").append(nonTerminals).append(System.lineSeparator());
        builder.append("VT = ").append(terminals).append(System.lineSeparator());
        builder.append("S  = ").append(startSymbol).append(System.lineSeparator());
        builder.append("P:").append(System.lineSeparator());

        List<String> keys = new ArrayList<>(productions.keySet());
        keys.sort(Comparator.naturalOrder());
        for (String lhs : keys) {
            List<String> renderedRules = productions.get(lhs).stream()
                .map(Grammar::formatRule)
                .sorted()
                .toList();
            builder.append("  ").append(lhs).append(" -> ")
                .append(String.join(" | ", renderedRules))
                .append(System.lineSeparator());
        }
        return builder.toString().trim();
    }

    private static String formatRule(List<String> rhs) {
        return rhs.isEmpty() ? "epsilon" : String.join(" ", rhs);
    }
}
