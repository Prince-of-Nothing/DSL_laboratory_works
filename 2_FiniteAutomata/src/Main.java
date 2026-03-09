import java.util.*;

public class Main {
    public static void main(String[] args) {
        Set<String> states = new HashSet<>(Arrays.asList("q0", "q1", "q2", "q3"));
        Set<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b','c'));

        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();

        transitions.put("q0", Map.of('a', Set.of("q0")));
        String startState = "q0";
        Set<String> finalStates = Set.of("q3");

        FiniteAutomaton fa = new FiniteAutomaton(states, alphabet, transitions, startState, finalStates);
        System.out.println("Automaton Type: " + (fa.isDeterministic() ? "DFA" : "NDFA"));

        DFA dfa = fa.toDFA();
        System.out.println("\n" + dfa);

        System.out.println("\nGrammar Classification: " + fa.getGrammarType());
    }
}
