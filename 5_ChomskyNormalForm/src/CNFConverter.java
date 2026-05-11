import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CNFConverter {
    public Grammar eliminateEpsilon(Grammar grammar) {
        Grammar result = grammar.copy();
        Set<String> nullable = nullableSymbols(result);
        Map<String, Set<List<String>>> productions = result.getProductions();
        List<String> keys = new ArrayList<>(productions.keySet());

        for (String lhs : keys) {
            Set<List<String>> rebuilt = new LinkedHashSet<>();
            for (List<String> rhs : productions.get(lhs)) {
                if (rhs.isEmpty()) {
                    continue;
                }
                rebuilt.add(List.copyOf(rhs));
                List<Integer> nullablePositions = new ArrayList<>();
                for (int index = 0; index < rhs.size(); index++) {
                    if (nullable.contains(rhs.get(index))) {
                        nullablePositions.add(index);
                    }
                }

                int combinations = 1 << nullablePositions.size();
                for (int mask = 1; mask < combinations; mask++) {
                    List<String> candidate = new ArrayList<>();
                    for (int index = 0; index < rhs.size(); index++) {
                        int nullableIndex = nullablePositions.indexOf(index);
                        boolean remove = nullableIndex >= 0 && ((mask >> nullableIndex) & 1) == 1;
                        if (!remove) {
                            candidate.add(rhs.get(index));
                        }
                    }
                    if (!candidate.isEmpty()) {
                        rebuilt.add(List.copyOf(candidate));
                    }
                }
            }
            productions.put(lhs, rebuilt);
        }
        return result;
    }

    public Grammar eliminateUnit(Grammar grammar) {
        Grammar result = grammar.copy();
        Set<String> nonTerminals = result.getNonTerminals();
        Map<String, Set<List<String>>> productions = result.getProductions();

        for (String lhs : new ArrayList<>(nonTerminals)) {
            Set<String> closure = new LinkedHashSet<>();
            Deque<String> queue = new ArrayDeque<>();
            closure.add(lhs);
            queue.add(lhs);

            while (!queue.isEmpty()) {
                String current = queue.removeFirst();
                for (List<String> rhs : productions.getOrDefault(current, Set.of())) {
                    if (rhs.size() == 1 && nonTerminals.contains(rhs.getFirst()) && closure.add(rhs.getFirst())) {
                        queue.add(rhs.getFirst());
                    }
                }
            }

            Set<List<String>> rebuilt = new LinkedHashSet<>();
            for (String symbol : closure) {
                for (List<String> rhs : productions.getOrDefault(symbol, Set.of())) {
                    if (!(rhs.size() == 1 && nonTerminals.contains(rhs.getFirst()))) {
                        rebuilt.add(List.copyOf(rhs));
                    }
                }
            }
            productions.put(lhs, rebuilt);
        }

        return result;
    }

    public Grammar eliminateInaccessible(Grammar grammar) {
        Grammar result = grammar.copy();
        Set<String> reachable = new LinkedHashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        reachable.add(result.getStartSymbol());
        queue.add(result.getStartSymbol());

        while (!queue.isEmpty()) {
            String current = queue.removeFirst();
            for (List<String> rhs : result.getProductions().getOrDefault(current, Set.of())) {
                for (String symbol : rhs) {
                    if (result.getNonTerminals().contains(symbol) && reachable.add(symbol)) {
                        queue.add(symbol);
                    }
                }
            }
        }

        result.getNonTerminals().retainAll(reachable);
        result.getProductions().keySet().removeIf(lhs -> !reachable.contains(lhs));
        return result;
    }

    public Grammar eliminateNonProductive(Grammar grammar) {
        Grammar result = grammar.copy();
        Set<String> productive = new LinkedHashSet<>();
        boolean changed = true;

        while (changed) {
            changed = false;
            for (Map.Entry<String, Set<List<String>>> entry : result.getProductions().entrySet()) {
                if (productive.contains(entry.getKey())) {
                    continue;
                }
                for (List<String> rhs : entry.getValue()) {
                    boolean valid = true;
                    for (String symbol : rhs) {
                        if (!result.getTerminals().contains(symbol) && !productive.contains(symbol)) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        productive.add(entry.getKey());
                        changed = true;
                        break;
                    }
                }
            }
        }

        result.getNonTerminals().retainAll(productive);
        result.getProductions().keySet().removeIf(lhs -> !productive.contains(lhs));
        for (Set<List<String>> rules : result.getProductions().values()) {
            rules.removeIf(rhs -> rhs.stream().anyMatch(symbol ->
                !result.getTerminals().contains(symbol) && !productive.contains(symbol)
            ));
        }
        return result;
    }

    public Grammar toCNF(Grammar grammar) {
        Grammar source = grammar.copy();
        Grammar result = new Grammar(
            source.getNonTerminals(),
            source.getTerminals(),
            source.getStartSymbol()
        );
        int[] counter = {1};
        Map<String, String> terminalHelpers = new java.util.LinkedHashMap<>();
        Map<List<String>, String> binaryHelpers = new java.util.LinkedHashMap<>();

        for (Map.Entry<String, Set<List<String>>> entry : source.getProductions().entrySet()) {
            String lhs = entry.getKey();
            for (List<String> rhs : entry.getValue()) {
                if (rhs.size() == 1 && result.getTerminals().contains(rhs.getFirst())) {
                    result.addProduction(lhs, rhs);
                    continue;
                }

                List<String> rewritten = new ArrayList<>(rhs);
                if (rewritten.size() >= 2) {
                    for (int index = 0; index < rewritten.size(); index++) {
                        String symbol = rewritten.get(index);
                        if (result.getTerminals().contains(symbol)) {
                            rewritten.set(index, terminalHelper(result, symbol, terminalHelpers, counter));
                        }
                    }
                }

                if (rewritten.size() <= 2) {
                    result.addProduction(lhs, rewritten);
                    continue;
                }

                String currentLhs = lhs;
                for (int index = 0; index < rewritten.size() - 2; index++) {
                    List<String> suffixPair = List.of(
                        rewritten.get(index + 1),
                        index + 2 == rewritten.size() - 1
                            ? rewritten.get(index + 2)
                            : helperForSuffix(
                                result,
                                rewritten.subList(index + 2, rewritten.size()),
                                binaryHelpers,
                                counter
                            )
                    );
                    String helper = helperForPair(result, suffixPair, binaryHelpers, counter);
                    addRule(result, currentLhs, List.of(rewritten.get(index), helper));
                    currentLhs = null;
                    break;
                }
                if (rewritten.size() == 3) {
                    String helper = helperForPair(
                        result,
                        List.of(rewritten.get(1), rewritten.get(2)),
                        binaryHelpers,
                        counter
                    );
                    addRule(result, lhs, List.of(rewritten.get(0), helper));
                }
            }
        }

        return result;
    }

    private String helperForSuffix(
        Grammar grammar,
        List<String> suffix,
        Map<List<String>, String> binaryHelpers,
        int[] counter
    ) {
        if (suffix.size() == 2) {
            return helperForPair(grammar, List.of(suffix.get(0), suffix.get(1)), binaryHelpers, counter);
        }

        String nested = helperForSuffix(grammar, suffix.subList(1, suffix.size()), binaryHelpers, counter);
        return helperForPair(grammar, List.of(suffix.getFirst(), nested), binaryHelpers, counter);
    }

    private String helperForPair(
        Grammar grammar,
        List<String> pair,
        Map<List<String>, String> binaryHelpers,
        int[] counter
    ) {
        List<String> key = List.copyOf(pair);
        if (binaryHelpers.containsKey(key)) {
            return binaryHelpers.get(key);
        }

        String helper = freshSymbol(grammar, "N", counter);
        binaryHelpers.put(key, helper);
        addRule(grammar, helper, key);
        return helper;
    }

    public boolean isCNF(Grammar grammar) {
        for (Collection<List<String>> rules : grammar.getProductions().values()) {
            for (List<String> rhs : rules) {
                if (rhs.size() == 1 && grammar.getTerminals().contains(rhs.getFirst())) {
                    continue;
                }
                if (rhs.size() == 2
                    && grammar.getNonTerminals().contains(rhs.get(0))
                    && grammar.getNonTerminals().contains(rhs.get(1))) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    private Set<String> nullableSymbols(Grammar grammar) {
        Set<String> nullable = new LinkedHashSet<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, Set<List<String>>> entry : grammar.getProductions().entrySet()) {
                if (nullable.contains(entry.getKey())) {
                    continue;
                }
                for (List<String> rhs : entry.getValue()) {
                    if (rhs.isEmpty() || rhs.stream().allMatch(nullable::contains)) {
                        nullable.add(entry.getKey());
                        changed = true;
                        break;
                    }
                }
            }
        }
        return nullable;
    }

    private String terminalHelper(
        Grammar grammar,
        String terminal,
        Map<String, String> terminalHelpers,
        int[] counter
    ) {
        if (terminalHelpers.containsKey(terminal)) {
            return terminalHelpers.get(terminal);
        }
        String helper = freshSymbol(grammar, "T", counter);
        terminalHelpers.put(terminal, helper);
        addRule(grammar, helper, List.of(terminal));
        return helper;
    }

    private String freshSymbol(Grammar grammar, String prefix, int[] counter) {
        while (true) {
            String candidate = prefix + counter[0]++;
            if (!grammar.getNonTerminals().contains(candidate) && !grammar.getTerminals().contains(candidate)) {
                grammar.getNonTerminals().add(candidate);
                return candidate;
            }
        }
    }

    private void addRule(Grammar grammar, String lhs, List<String> rhs) {
        grammar.addProduction(lhs, rhs);
    }
}
