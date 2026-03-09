# Determinism in Finite Automata. Conversion from NDFA 2 DFA. Chomsky Hierarchy.

### Course: Formal Languages & Finite Automata
### Author: Pleșu Dinu FAF-241

----

## Theory

#### A finite automaton (FA) is a computational model used to recognize patterns in strings. It consists of:

- Q (States): A finite set of states.
- Σ (Alphabet): A finite set of input symbols.
- δ (Transition function): Defines state changes based on input symbols.
- q₀ (Start state): The state where execution begins.
- F (Final states): Accepting states that determine if a string is valid.

#### Deterministic vs. Non-Deterministic Finite Automata
- **Deterministic Finite Automaton (DFA):** Each state has exactly one transition for each symbol in the alphabet.
- **Non-Deterministic Finite Automaton (NDFA):** A state can have multiple transitions for the same symbol or ε-moves (empty transitions).


#### NDFA to DFA Conversion
The subset construction algorithm is used to convert an NDFA into an equivalent DFA:
1. Each state in the DFA represents a set of states in the NDFA.
2. Process all transitions from the NDFA to form deterministic transitions.
3. Identify final states as those that contain any accepting state from the NDFA.

#### Chomsky Hierarchy
Grammars are classified into four types:
- **Type 0 (Unrestricted Grammar):** No restrictions on production rules.
- **Type 1 (Context-Sensitive Grammar):** Productions must maintain string length or increase it.
- **Type 2 (Context-Free Grammar):** Each production has a single non-terminal on the left-hand side.
- **Type 3 (Regular Grammar):** Productions follow strict forms (A → aB or A → a).

## Objectives:

1. Understand what an automaton is and what it can be used for.

2. Continuing the work in the same repository and the same project, the following need to be added:
    a. Provide a function in your grammar type/class that could classify the grammar based on Chomsky hierarchy.

    b. For this you can use the variant from the previous lab.

3. According to your variant number (by universal convention it is register ID), get the finite automaton definition and do the following tasks:

    a. Implement conversion of a finite automaton to a regular grammar.

    b. Determine whether your FA is deterministic or non-deterministic.

    c. Implement some functionality that would convert an NDFA to a DFA.
    
    d. Represent the finite automaton graphically (Optional, and can be considered as a __*bonus point*__):
      
    - You can use external libraries, tools or APIs to generate the figures/diagrams.
        
    - Your program needs to gather and send the data about the automaton and the lib/tool/API return the visual representation.

Please consider that all elements of the task 3 can be done manually, writing a detailed report about how you've done the conversion and what changes have you introduced. In case if you'll be able to write a complete program that will take some finite automata and then convert it to the regular grammar - this will be **a good bonus point**.
## Implementation description

### Grammar Class (`Grammar.java`)
The `Grammar` class from Lab 1 is extended with a `classifyGrammar()` method. It inspects every production rule and determines whether the grammar is right-linear or left-linear. If all productions conform to `A → aB` or `A → a` (right-linear) or `A → Ba` / `A → a` (left-linear), the grammar is classified as **Type 3 – Regular**; otherwise it falls back to **Type 2 – Context-Free** (since the structure always has a single non-terminal on the left-hand side).

```java
public String classifyGrammar() {
    boolean rightLinear = true;
    boolean leftLinear = true;

    for (var entry : P.entrySet()) {
        for (String rhs : entry.getValue()) {
            if (rhs.length() == 1) {
                if (!VT.contains(rhs.charAt(0))) { rightLinear = false; leftLinear = false; }
            } else if (rhs.length() == 2) {
                if (!(VT.contains(rhs.charAt(0)) && VN.contains(rhs.charAt(1)))) rightLinear = false;
                if (!(VN.contains(rhs.charAt(0)) && VT.contains(rhs.charAt(1)))) leftLinear = false;
            } else { rightLinear = false; leftLinear = false; }
        }
    }
    if (rightLinear || leftLinear) return "Type 3 - Regular Grammar";
    return "Type 2 - Context-Free Grammar";
}
```

### FiniteAutomaton Class (`FiniteAutomaton.java`)
The `FiniteAutomaton` class is updated to support the full NDFA model (transitions map each symbol to a *set* of destination states). Three key methods were added or updated:

**`isDeterministic()`** – Scans all transition sets; if any symbol leads to more than one state the automaton is non-deterministic.

**`toRegularGrammar()`** – Converts the NDFA to an equivalent right-linear regular grammar. For every transition `δ(q, a) = r` a production `q → ar` is added. When `r` is also a final state an additional terminal-only production `q → a` is emitted so strings can terminate at that point.

```java
public Map<String, List<String>> toRegularGrammar() {
    Map<String, List<String>> grammarProductions = new LinkedHashMap<>();
    for (var stateEntry : transitions.entrySet()) {
        String fromState = stateEntry.getKey();
        grammarProductions.putIfAbsent(fromState, new ArrayList<>());
        for (var symbolEntry : stateEntry.getValue().entrySet()) {
            char symbol = symbolEntry.getKey();
            for (String toState : symbolEntry.getValue()) {
                grammarProductions.get(fromState).add(symbol + toState);
                if (finalStates.contains(toState))
                    grammarProductions.get(fromState).add(String.valueOf(symbol));
            }
        }
    }
    return grammarProductions;
}
```

**`stringBelongToLanguage()`** – Simulates the NDFA using a set of currently active states (subset simulation), accepting the string if any active state at the end is a final state.

**`toDFA()`** – Implements the subset construction algorithm. Each DFA state is a *set* of NDFA states. Starting from `{q0}`, all reachable state-sets are explored symbol by symbol. A DFA state is accepting if it contains at least one NDFA final state.

### DFA Class (`DFA.java` / nested in `FiniteAutomaton.java`)
The `DFA` class stores the result of the subset construction and also provides a `stringBelongToLanguage()` method for deterministic string validation.

### Main Class (`Main.java`)
Demonstrates all features in sequence: grammar string generation and Chomsky classification, Grammar-to-FA conversion with string checking, NDFA determinism detection, FA-to-grammar conversion, NDFA-to-DFA conversion, and string acceptance on both the NDFA and the derived DFA.

## Conclusions / Screenshots / Results

- NDFA Definition: Implemented the finite automaton for Variant 20.
- Determinism Check: The automaton is correctly identified as non-deterministic (q0 has two transitions on symbol 'a').
- FA to Regular Grammar: Successfully derived a right-linear regular grammar from the NDFA (e.g., q0 → aq0 | aq1, q2 → c | cq3).
- Chomsky Classification: The Variant 20 grammar is classified as Type 3 – Regular Grammar.
- NDFA to DFA Conversion: Successfully converted the NDFA to an equivalent DFA using the subset construction algorithm (DFA states: {q0}, {q0,q1}, {q2}, {q3}).
- String acceptance results are consistent between the NDFA simulation and the derived DFA.
- Graphical Representation: (Optional bonus) Visual representation can be generated using external tools/libraries.

This project demonstrates the conversion of a non-deterministic finite automaton into a deterministic one using the subset construction algorithm. Additionally, it shows the derivation of a regular grammar from the automaton, reaffirming its position within the Chomsky hierarchy (Type 3). The modular design allows for easy extensions, such as adding graphical representations or further testing.

## References
- Hopcroft E. and others. Introduction to Automata Theory, Languages and Computation
- LFPC Guide (ELSE)
- Introduction to formal languages (ELSE)
