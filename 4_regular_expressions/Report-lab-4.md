# Topic: Regular Expressions

### Course: Formal Languages & Finite Automata
### Author: Pleșu DInu FAF-241 
### VariantȘ 4

----

## Theory
Regular expressions are a formal way to describe patterns in strings. They are used to define valid words in a language, validate input, search text, and model repetitive structures compactly.

In this laboratory work the important part is that the expressions are interpreted dynamically. The generator must receive a regex-like input and produce valid strings from it, instead of using hardcoded generation logic for each individual example.

For Variant 4 the expressions are written in a text-friendly notation where quantifiers may appear as `^*`, `^[+]`, or `^[n]`. In the implementation these are interpreted as postfix operators applied to the previous symbol or group:
- `W^*` means zero to many `W`
- `Y^[+]` means one to many `Y`
- `O^[3]` means exactly three `O`
- `(X|Y|Z)^[2]` means the whole group is repeated exactly two times

To avoid extremely long generated words, open repetition is limited to 5 occurrences, as required in the task.

## Objectives
1. Explain what regular expressions are and what they are used for.
2. Write code that dynamically interprets the given regular expressions and generates valid combinations of symbols.
3. Limit undefined repetition to at most 5 occurrences.
4. Generate valid words for the three expressions from Variant 4.

## Variant 4 Input
The program is currently configured for these three expressions:

1. `(S|T)(U|V)W^*Y^[+]24`
2. `L(M|N)O^[3]P*Q(2|3)`
3. `R*S(T|U|V)W(X|Y|Z)^[2]`

Examples expected by the task:
- `{SUWWY24, SVWY24, ...}`
- `{LMOOOPPPQ2, LNOOOPQ3, ...}`
- `{RSTWXX, RRRSUWYY, ...}`

## Implementation Description
The solution is organized as a small Java pipeline inside `src`:

- `RegexLexer.java` transforms the input expression into tokens.
- `RegexParser.java` builds an abstract syntax tree for concatenation, alternation, and quantifiers.
- `RegexInterpreter.java` traverses the tree and generates random valid words.
- `RegexGenerator.java` offers simple entry points for generation and tracing.
- `Main.java` runs the Variant 4 examples and validates the generated words.

General flow:

```text
regex text -> tokens -> AST -> generated word
```

### Lexer
The lexer reads the custom notation symbol by symbol. It recognizes parentheses, alternation, postfix operators, and the bracketed form used after `^`.

```java
case '^' -> tokens.add(new RegexToken(RegexTokenKind.CARET, null));
case '[' -> tokens.add(new RegexToken(RegexTokenKind.LBRACKET, null));
case ']' -> tokens.add(new RegexToken(RegexTokenKind.RBRACKET, null));
default -> tokens.add(new RegexToken(RegexTokenKind.WORD, consumeSymbol()));
```

### Parser
The parser uses recursive descent. A parsed item can be:
- a literal symbol
- a grouped alternation such as `(S|T)`
- a grouped sequence

After parsing a symbol or group, the parser checks whether it is followed by a quantifier such as `*`, `+`, `?`, `^3`, or `^[2]`.

```java
if (match(RegexTokenKind.LBRACKET)) {
    RegexToken target = advance();
    consume(RegexTokenKind.RBRACKET, "Expected ']'");
    return toQuantifier(target, "Expected *, +, ?, or a numeric repetition count");
}
```

### Interpreter
The interpreter walks the syntax tree and produces a valid word.
It separates two concerns: **how many times** a node is evaluated (determined by its quantifier) and **what to generate once** (the core structural evaluation).

For every node, `emit` first resolves the repetition count from the node's quantifier, then calls `emitOnce` that many times and concatenates the results:

```java
private String emit(RegexNode node) {
    int count = repetitionCount(node.getQuantifier()); // 0–5 for STAR/PLUS, exact for EXACT
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < count; i++) result.append(emitOnce(node));
    return result.toString();
}
```

This means quantified groups like `(X|Y|Z)^[2]` call `emitOnce` twice **independently**, so each repetition picks a fresh random option — the result can be `XY`, `YZ`, `XX`, etc., covering the full language `{X,Y,Z}²`.

`emitOnce` handles the structural cases:
- `LiteralNode` → returns its character
- `SequenceNode` → concatenates `emit` results for each child
- `ChoiceNode` → picks a random option and delegates to `emit`

Undefined repetition is capped at 5:

```java
private int repetitionCount(Quantifier quantifier) {
    if (quantifier == null) return 1;
    return switch (quantifier.kind()) {
        case STAR     -> random.nextInt(MAX_REPETITION + 1);   // 0–5
        case PLUS     -> 1 + random.nextInt(MAX_REPETITION);   // 1–5
        case QUESTION -> random.nextInt(2);
        case EXACT    -> quantifier.count();
    };
}
```

## Program Execution
For each Variant 4 regex the program generates five sample strings and prints them in set notation.

Program output:

```text
REGULAR EXPRESSIONS - VARIANT 4
============================================================

Pattern: (S|T)(U|V)W^*Y^[+]24
{TUWYYYY24, TUWY24, SVWYYYY24, TUWWYYY24, TUWWWY24, ...}

Pattern: L(M|N)O^[3]P*Q(2|3)
{LNOOOPPPPQ3, LNOOOPPQ2, LNOOOPQ2, LNOOOQ3, LMOOOPPPPQ2, ...}

Pattern: R*S(T|U|V)W(X|Y|Z)^[2]
{RRRRSVWXX, SVWXX, RRRRSUWZZ, RSUWZZ, STWZZ, ...}
```

Notes on the output:
- Pattern 1: `W^*` can produce zero W's (epsilon), so `TUWY24` is valid alongside `TUWWWY24`.
- Pattern 2: `P*` produces zero to five P's; `O^[3]` always gives exactly three O's.
- Pattern 3: `R*` can produce zero R's; `(X|Y|Z)^[2]` evaluates the group twice independently, so it can produce any two-character combination such as `XX`, `YZ`, `ZX`, etc.

## Processing Traces
The AST built from each regex (for reference):

```text
Pattern: (S|T)(U|V)W^*Y^[+]24
SEQ
  OR
    WORD S
    WORD T
  OR
    WORD U
    WORD V
  WORD W *
  WORD Y +
  WORD 2
  WORD 4

Pattern: L(M|N)O^[3]P*Q(2|3)
SEQ
  WORD L
  OR
    WORD M
    WORD N
  WORD O ^3
  WORD P *
  WORD Q
  OR
    WORD 2
    WORD 3

Pattern: R*S(T|U|V)W(X|Y|Z)^[2]
SEQ
  WORD R *
  WORD S
  OR
    WORD T
    WORD U
    WORD V
  WORD W
  OR ^2
    WORD X
    WORD Y
    WORD Z
```

## Difficulties Faced
1. The task notation uses `^` as a readable replacement for superscript-style quantifiers.
   The solution was to interpret `^*`, `^[+]`, and `^[n]` as postfix repetition operators on the previous symbol or group.

2. A quantifier must apply only to the previous symbol or grouped expression.
   The lexer was kept symbol-based so a pattern like `P*` affects only `P`, not a longer literal chunk.

3. A quantifier on a group like `(X|Y|Z)^[2]` means the whole group is evaluated that many times independently, not that a single chosen branch is duplicated. Each evaluation picks a fresh random option from the alternation.

## Conclusions
- The program satisfies the task requirement of dynamically interpreting the regular expressions.
- Undefined repetition (`^*` / `^[+]`) is limited to at most 5 occurrences; `^*` also correctly allows zero occurrences (epsilon).
- `(X|Y|Z)^[2]` evaluates the group twice independently, producing any combination from `{X,Y,Z}²`.
- The generator produces valid words for all three Variant 4 expressions.
- The code is structured clearly enough to present and explain during the lab defense.

## References
- [Regular expression](https://en.wikipedia.org/wiki/Regular_expression)
- [Formal language](https://en.wikipedia.org/wiki/Formal_language)
- [Recursive descent parser](https://en.wikipedia.org/wiki/Recursive_descent_parser)
