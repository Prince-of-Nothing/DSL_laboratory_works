# Topic: Chomsky Normal Form

### Course: Formal Languages & Finite Automata
### Author: Student | Variant 20

----

## Theory
Chomsky Normal Form is a restricted form of context-free grammar where every production must be one of the following:
- `A -> BC`
- `A -> a`

This normalization is useful because it removes irregular rules and prepares the grammar for formal analysis and parsing algorithms such as CYK.

## Objectives
For Variant 20 the required steps are:

1. Eliminate epsilon productions.
2. Eliminate renaming productions.
3. Eliminate inaccessible symbols.
4. Eliminate nonproductive symbols.
5. Obtain the Chomsky Normal Form.

## Variant 20 Grammar
The laboratory work is implemented for the following grammar:

```text
G = (VN, VT, P, S)
VN = {S, A, B, C, D}
VT = {a, b}

P:
1.  S -> aB
2.  S -> bA
3.  S -> A
4.  A -> B
5.  A -> Sa
6.  A -> bBA
7.  A -> b
8.  B -> b
9.  B -> bS
10. B -> aD
11. B -> epsilon
12. D -> AA
13. C -> Ba
```

This grammar is a good CNF exercise because it contains:
- an epsilon production: `B -> epsilon`
- renaming productions: `S -> A`, `A -> B`
- an inaccessible symbol: `C`
- mixed and long productions such as `A -> bBA`

## Implementation Description
The solution is implemented in Java using three main files:

- `Grammar.java` stores `VN`, `VT`, the start symbol, and the production rules.
- `CNFConverter.java` performs the normalization stages.
- `Main.java` defines the Variant 20 grammar and prints the grammar after each transformation.

The production rules are stored as:

```java
Map<String, Set<List<String>>> productions
```

This makes the converter flexible enough to work with right-hand sides of different lengths.

## Transformation Pipeline
The converter follows the same order as the assignment:

1. epsilon elimination
2. renaming elimination
3. inaccessible symbol elimination
4. nonproductive symbol elimination
5. conversion to CNF

### 1. Eliminate epsilon productions
The converter first computes the nullable symbols. In this grammar, `B` is nullable because of `B -> epsilon`. Then it rebuilds all productions by generating valid alternatives where nullable symbols may disappear.

### 2. Eliminate renaming productions
Renaming rules are productions such as `S -> A` and `A -> B`. The converter computes the closure of unit transitions and replaces them with the non-unit productions reachable through those symbols.

### 3. Eliminate inaccessible symbols
Starting from `S`, the converter marks all reachable non-terminals. Since `C` cannot be reached from `S`, it is removed together with its productions.

### 4. Eliminate nonproductive symbols
A non-terminal is productive if it can derive a terminal string. The converter removes symbols and rules that can never produce terminal words.

### 5. Obtain Chomsky Normal Form
The final stage transforms the grammar into valid CNF:
- terminals inside larger productions are replaced with helper symbols such as `T1 -> a`
- long right-hand sides are split into binary productions using helper symbols such as `N1`, `N2`

## Program Execution
`Main.java` prints:

```text
Original grammar
After epsilon elimination
After unit elimination
After inaccessible symbol elimination
After nonproductive symbol elimination
Final CNF grammar
```

At the end, the program validates that every production is either:
- one terminal, or
- two non-terminals

## Difficulties Faced
1. The grammar contains several transformation cases at once.
   Because of that, the elimination stages must be applied in the correct order.

2. The right-hand sides are not all the same length.
   Using `List<String>` for productions made the converter more robust than a character-based approach.

3. The grammar includes both unreachable and nullable structures.
   That makes it useful for demonstration, but it also means the report must explain why some symbols disappear during the process.

## Conclusions
- The implementation now uses the exact Variant 20 grammar.
- All required normalization stages are visible separately.
- The final grammar is validated to ensure it satisfies Chomsky Normal Form.
- The program is structured clearly enough to present step by step during the lab defense.

## References
- [Chomsky normal form](https://en.wikipedia.org/wiki/Chomsky_normal_form)
- [Context-free grammar](https://en.wikipedia.org/wiki/Context-free_grammar)
- [CYK algorithm](https://en.wikipedia.org/wiki/CYK_algorithm)
