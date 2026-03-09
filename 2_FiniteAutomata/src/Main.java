import java.util.*;
//Variant 20
public class Main {
    public static void main(String[] args) {
        //Q
        Set<String> states = new HashSet<>(Arrays.asList("q0", "q1", "q2", "q3"));
        //∑
        Set<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b','c'));

        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();

        transitions.put("q0", Map.of('a', Set.of("q0", "q1")));
        transitions.put("q1", Map.of('b', Set.of("q2")));
        transitions.put("q2",Map.of('a',Set.of("q2"),'c',Set.of("q3")));
        transitions.put("q3", Map.of('c', Set.of("q3")));
        
        //S
        String startState = "q0";
        //F
        Set<String> finalStates = Set.of("q3");

        FiniteAutomaton fa = new FiniteAutomaton(states, alphabet, transitions, startState, finalStates);
        System.out.println("Type of Automaton: " + (fa.isDeterministic() ? "DFA" : "NDFA"));

        DFA dfa = fa.toDFA();
        System.out.println("\n" + dfa);

        System.out.println("\nClassification of Grammar: " + fa.getGrammarType());
    }
}
