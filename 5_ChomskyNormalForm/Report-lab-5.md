# Topic: Chomsky Normal Form

### Course: Formal Languages & Finite Automata
### Author: Student

----

## Theory
Chomsky Normal Form restricts a context-free grammar to productions of the form:
- `A -> BC`
- `A -> a`

This restriction is useful because it removes irregular rule shapes and prepares the grammar for standard algorithms such as CYK.

## Objectives
1. Implement a reusable CNF converter.
2. Handle epsilon productions, unit productions, inaccessible symbols, and nonproductive symbols.
3. Keep the converter generic enough to work with grammars beyond a single hardcoded rule.

## Implementation Description
The lab was rewritten in Java around three source files:

- `Grammar.java` stores non-terminals, terminals, the start symbol, and production rules.
- `CNFConverter.java` performs the normalization steps.
- `Main.java` builds a sample grammar and prints every conversion stage.

The grammar is represented with:

```java
Map<String, Set<List<String>>> productions
```

Using lists for the right-hand side means the converter can work with symbols of any length, not only single-character variables.

### Conversion pipeline
The converter applies the standard sequence:

1. eliminate epsilon productions
2. eliminate unit productions
3. eliminate inaccessible symbols
4. eliminate nonproductive symbols
5. rewrite the grammar into CNF

### Epsilon elimination
Nullable symbols are computed first. Then every valid combination of removing nullable symbols from a right-hand side is generated.

### Unit elimination
Rules such as `A -> B` are removed through a closure over reachable unit transitions. Each non-terminal inherits only the non-unit productions of the symbols it reaches.

### Final CNF conversion
The last stage does two things:
- replaces terminals inside long or mixed productions with helper non-terminals such as `T1 -> a`
- breaks long right-hand sides into binary rules using helper symbols such as `N1`, `N2`

## Program Execution
`Main.java` prints the grammar after every step:

```text
Original grammar
After epsilon elimination
After unit elimination
After inaccessible symbol elimination
After nonproductive symbol elimination
Final CNF grammar
```

At the end, the program also checks that every rule is either terminal-only or binary.

## Conclusions
- The converter is now structured around a reusable `Grammar` model and a focused `CNFConverter`.
- Each normalization stage is visible and easy to present.
- The final validation confirms that the resulting grammar satisfies CNF.

## References
- [Chomsky normal form](https://en.wikipedia.org/wiki/Chomsky_normal_form)
- [CYK algorithm](https://en.wikipedia.org/wiki/CYK_algorithm)
