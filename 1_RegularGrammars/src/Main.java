import java.util.*;
//Variant 20

public class Main {
    public static void main(String[] args) {
        Set<Character> VN = new HashSet<>(Arrays.asList('S', 'A', 'B','C'));
        Set<Character> VT = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd', 'f'));
        Map<Character, List<String>> P = new HashMap<>();
        P.put('S', Arrays.asList("dA"));
        P.put('A', Arrays.asList("d", "aB"));
        P.put('B', Arrays.asList("bC"));
        P.put('C', Arrays.asList("cA", "aS"));

        Grammar grammar = new Grammar(VN, VT, P, 'S');

        System.out.println("Generated words:");
        for (int i = 0; i < 5; i++) {
            System.out.println(grammar.generateString());
        }

        FiniteAutomaton fa = grammar.toFiniteAutomaton();
        System.out.println("\nChecking if words belong to language:");
        System.out.println("abcd ? " + fa.stringBelongToLanguage("abcd"));
        System.out.println("gd ? " + fa.stringBelongToLanguage("gd"));
        System.out.println("dd ? " + fa.stringBelongToLanguage("dd"));
        System.out.println("dabcd ? " + fa.stringBelongToLanguage("dabcd"));
    }
}