# Topic: Intro to formal languages. Regular grammars. Finite Automata.
## Course: Formal Languages & Finite Automata
## Author: Pleșu Dinu
# Theory

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


# Objectives:
- Implement a class to represent a grammar.

- Generate valid strings from the given grammar.

- Implement functionality to convert the grammar into a finite automaton.

- Check if a given string belongs to the language of the finite automaton.

## References

- Hopcroft E. and others. Introduction to Automata Theory, Languages and Computation
- LFPC Guide (ELSE)
- Introduction to formal languages (ELSE)