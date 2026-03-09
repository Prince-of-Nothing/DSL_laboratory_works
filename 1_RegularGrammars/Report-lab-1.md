# Topic: Determinism in Finite Automata. Conversion from NDFA 2 DFA. Chomsky Hierarchy.

### Course: Formal Languages & Finite Automata
### Author: Pleșu Dinu FAF-241

----

## Theory

#### A formal grammar is a set of rules used to generate strings in a language. It consists of:

- VN (Non-terminals): Symbols that can be replaced (e.g.: S, A, B).
- VT (Terminals): Symbols that appear in the final string (e.g.: a, b, c).
- P (Production rules): Transform non-terminals into sequences of terminals and/or non-terminals.
- Start Symbol: The initial non-terminal from which the derivation begins.

#### A finite automaton (FA) is a computational model used to recognize patterns in strings. It consists of:

- Q (States): A finite set of states.
- Σ (Alphabet): A finite set of input symbols.
- δ (Transition function): Defines state changes based on input symbols.
- q₀ (Start state): The state where execution begins.
- F (Final states): Accepting states that determine if a string is valid.

#### Grammar to Finite Automaton Conversion
- Each non-terminal is treated as a state.
- Production rules define transitions.
- Terminal-only transitions lead to final states.

    A finite automaton can verify if a string belongs to a language by following state transitions based on input symbols. If the automaton reaches a final state, the string is accepted.


## Objectives:

1. Discover what a language is and what it needs to have in order to be considered a formal one;

2. Provide the initial setup for the evolving project that you will work on during this semester. You can deal with each laboratory work as a separate task or project to demonstrate your understanding of the given themes, but you also can deal with labs as stages of making your own big solution, your own project. Do the following:

    a. Create GitHub repository to deal with storing and updating your project;

    b. Choose a programming language. Pick one that will be easiest for dealing with your tasks, you need to learn how to solve the problem itself, not everything around the problem (like setting up the project, launching it correctly and etc.);

    c. Store reports separately in a way to make verification of your work simpler (duh)

3. According to your variant number, get the grammar definition and do the following:

    a. Implement a type/class for your grammar;

    b. Add one function that would generate 5 valid strings from the language expressed by your given grammar;

    c. Implement some functionality that would convert and object of type Grammar to one of type Finite Automaton;

    d. For the Finite Automaton, please add a method that checks if an input string can be obtained via the state transition from it;

## Implementation description
### Grammar Class
The `Grammar` class represents the given grammar with:
- **Terminal symbols** (`VT`)
- **Non-terminal symbols** (`VN`)
- **Start symbol** (`S`)
- **Production rules** (`P`)


A method generates five valid strings based on the production rules.

### FiniteAutomaton Class
The `FiniteAutomaton` class represents the equivalent finite automaton with:
- **Alphabet** (`Sigma`)
- **Transition function** (`delta`)
- **Initial state** (`q0`)
- **States** (`Q`)
- **Final states** (`F`)

A method verifies whether a given string is valid according to the automaton.

### Code Snippets
#### Grammar Class
```java
public class Grammar {
    //variables

    public Grammar(Set<String> VN, Set<String> VT, Map<String, List<String>> P, String S) {
        //basic constructor
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
              for (var entry : P.entrySet()) {
            String fromState = String.valueOf(entry.getKey());
            states.add(fromState);
            transitions.putIfAbsent(fromState, new HashMap<>());

            for (String production : entry.getValue()) {
                char terminal = production.charAt(0);
                String toState = (production.length() > 1) ? String.valueOf(production.charAt(1)) : "FINAL";
                transitions.get(fromState).put(terminal, toState);
                states.add(toState);
                if (!VN.contains(toState.charAt(0))) {
                    finalStates.add(toState);
                }
            }
        }

        return new FiniteAutomaton(states, sigma, transitions, startState, finalStates);
    }
}
```

#### FiniteAutomaton Class
```java
class FiniteAutomaton {

    public FiniteAutomaton(Set<String> states, Set<Character> sigma, Map<String, Map<Character, String>> transitions, String startState, Set<String> finalStates) {//...
    }

    public boolean stringBelongToLanguage(String input) {
        String currentState = startState;

        for (char symbol : input.toCharArray()) {
            if (!transitions.containsKey(currentState) || !transitions.get(currentState).containsKey(symbol))
                return false;
            currentState = transitions.get(currentState).get(symbol);
        }
        return finalStates.contains(currentState);
    }
}

```

#### Main Execution
```java
public class Main {
    public static void main(String[] args) {
        // VN, VT and P rules

        Grammar grammar = new Grammar(VN, VT, P, 'S');

        System.out.println("Generated words:");
        for (int i = 0; i < 5; i++) {
            System.out.println(grammar.generateString());
        }

        FiniteAutomaton fa = grammar.toFiniteAutomaton();
    }
}
```
## Conclusions / Screenshots / Results

<img width="920" height="224" alt="image" src="https://github.com/user-attachments/assets/8ad5c95d-abda-47a3-bfe9-e0f7fd24b697" />

- Successfully implemented the Grammar class.
- Generated valid strings based on the production rules.
- Converted the grammar into a finite automaton.
- Verified string acceptance using FA state transitions.

## References
- Hopcroft E. and others. Introduction to Automata Theory, Languages and Computation
- LFPC Guide (ELSE)
- Introduction to formal languages (ELSE)
