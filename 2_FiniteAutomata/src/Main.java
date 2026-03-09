import java.util.*;
//Variant 20
public class Main {
    public static void main(String[] args) {
        // --- Grammar (Variant 20) ---
        Set<Character> VN = new HashSet<>(Arrays.asList('S', 'A', 'B', 'C'));
        Set<Character> VT = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd'));
        Map<Character, List<String>> P = new HashMap<>();
        P.put('S', Arrays.asList("dA"));
        P.put('A', Arrays.asList("d", "aB"));
        P.put('B', Arrays.asList("bC"));
        P.put('C', Arrays.asList("cA", "aS"));

        Grammar grammar = new Grammar(VN, VT, P, 'S');

        System.out.println("=== Grammar (Variant 20) ===");
        System.out.println("Generated strings:");
        for (int i = 0; i < 5; i++) {
            System.out.println("  " + grammar.generateString());
        }
        System.out.println("Chomsky Classification: " + grammar.classifyGrammar());

        FiniteAutomaton grammarFA = grammar.toFiniteAutomaton();
        System.out.println("\nChecking strings via Grammar FA:");
        System.out.println("  \"dabc\" belongs: " + grammarFA.stringBelongToLanguage("dabc"));
        System.out.println("  \"dd\" belongs: " + grammarFA.stringBelongToLanguage("dd"));

        // --- NDFA (Variant 20) ---
        Set<String> states = new HashSet<>(Arrays.asList("q0", "q1", "q2", "q3"));
        Set<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b', 'c'));

        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();
        transitions.put("q0", Map.of('a', new HashSet<>(Arrays.asList("q0", "q1"))));
        transitions.put("q1", Map.of('b', new HashSet<>(Arrays.asList("q2"))));
        transitions.put("q2", new HashMap<>(Map.of('a', new HashSet<>(Arrays.asList("q2")), 'c', new HashSet<>(Arrays.asList("q3")))));
        transitions.put("q3", Map.of('c', new HashSet<>(Arrays.asList("q3"))));

        String startState = "q0";
        Set<String> finalStates = new HashSet<>(Arrays.asList("q3"));

        FiniteAutomaton fa = new FiniteAutomaton(states, alphabet, transitions, startState, finalStates);

        System.out.println("\n=== Finite Automaton (Variant 20) ===");
        System.out.println("Is deterministic: " + (fa.isDeterministic() ? "DFA" : "NDFA"));

        System.out.println("\nFA to Regular Grammar:");
        Map<String, List<String>> grammarRules = fa.toRegularGrammar();
        for (var entry : new TreeMap<>(grammarRules).entrySet()) {
            for (String rule : entry.getValue()) {
                System.out.println("  " + entry.getKey() + " -> " + rule);
            }
        }

        System.out.println("\nChecking strings via NDFA:");
        System.out.println("  \"abc\" belongs: " + fa.stringBelongToLanguage("abc"));
        System.out.println("  \"aabc\" belongs: " + fa.stringBelongToLanguage("aabc"));
        System.out.println("  \"abcc\" belongs: " + fa.stringBelongToLanguage("abcc"));
        System.out.println("  \"ab\" belongs: " + fa.stringBelongToLanguage("ab"));

        DFA dfa = fa.toDFA();
        System.out.println("\n" + dfa);

        System.out.println("Checking strings via DFA:");
        System.out.println("  \"abc\" belongs: " + dfa.stringBelongToLanguage("abc"));
        System.out.println("  \"aabc\" belongs: " + dfa.stringBelongToLanguage("aabc"));
        System.out.println("  \"abcc\" belongs: " + dfa.stringBelongToLanguage("abcc"));
        System.out.println("  \"ab\" belongs: " + dfa.stringBelongToLanguage("ab"));
    }
}
