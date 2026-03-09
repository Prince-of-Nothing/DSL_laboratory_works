import java.util.*;

class Grammar {
    private Set<Character> VN;
    private Set<Character> VT;
    private Map<Character, List<String>> P;
    private char startSymbol;

    public Grammar(Set<Character> VN, Set<Character> VT, Map<Character, List<String>> P, char startSymbol) {
        this.VN = VN;
        this.VT = VT;
        this.P = P;
        this.startSymbol = startSymbol;
    }

    public String generateString() {
        StringBuilder result = new StringBuilder();
        char current = startSymbol;

        while (VN.contains(current)) {
            List<String> productions = P.get(current);
            if (productions == null || productions.isEmpty()) break;
            String selectedProduction = productions.get(new Random().nextInt(productions.size()));
            result.append(selectedProduction.charAt(0));

            if (selectedProduction.length() > 1)
                current = selectedProduction.charAt(1);
            else
                break;
        }
        return result.toString();
    }

    public FiniteAutomaton toFiniteAutomaton() {
        Set<String> states = new HashSet<>();
        Set<Character> sigma = new HashSet<>(VT);
        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();
        String startState = String.valueOf(startSymbol);
        Set<String> finalStates = new HashSet<>();

        String finalState = "FINAL";
        states.add(finalState);
        finalStates.add(finalState);

        for (var entry : P.entrySet()) {
            String fromState = String.valueOf(entry.getKey());
            states.add(fromState);
            transitions.putIfAbsent(fromState, new HashMap<>());

            for (String production : entry.getValue()) {
                char terminal = production.charAt(0);
                String toState = (production.length() > 1) ? String.valueOf(production.charAt(1)) : finalState;

                transitions.get(fromState).computeIfAbsent(terminal, k -> new HashSet<>()).add(toState);
                states.add(toState);
                if (toState.equals(finalState) || (!toState.isEmpty() && !VN.contains(toState.charAt(0)))) {
                    finalStates.add(toState);
                }
            }
        }

        return new FiniteAutomaton(states, sigma, transitions, startState, finalStates);
    }

    public String classifyGrammar() {
        boolean rightLinear = true;
        boolean leftLinear = true;

        for (var entry : P.entrySet()) {
            for (String rhs : entry.getValue()) {
                if (rhs.isEmpty()) continue;

                if (rhs.length() == 1) {
                    if (!VT.contains(rhs.charAt(0))) {
                        rightLinear = false;
                        leftLinear = false;
                    }
                } else if (rhs.length() == 2) {
                    boolean rl = VT.contains(rhs.charAt(0)) && VN.contains(rhs.charAt(1));
                    boolean ll = VN.contains(rhs.charAt(0)) && VT.contains(rhs.charAt(1));
                    if (!rl) rightLinear = false;
                    if (!ll) leftLinear = false;
                } else {
                    rightLinear = false;
                    leftLinear = false;
                }
            }
        }

        if (rightLinear || leftLinear) return "Type 3 - Regular Grammar";
        return "Type 2 - Context-Free Grammar";
    }
}
