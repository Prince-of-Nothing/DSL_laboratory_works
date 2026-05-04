# Topic: Regular Expressions

### Course: Formal Languages & Finite Automata
### Author: Student | Variant 4

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
4. Implement the bonus part by showing the sequence of processing for a regular expression.
5. Generate valid words for the three expressions from Variant 4.

## Variant 4 Input
The program is currently configured for these three expressions:

1. `(S|T)(U|V)W^*Y^[+]24`
2. `L(M|N)O^[3]P*Q(2|3)`
3. `R*S(T|U|N)W(X|Y|Z)^[2]`

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
The interpreter walks the syntax tree and produces a valid word:
- `ChoiceNode` randomly selects one branch from an alternation
- `SequenceNode` concatenates child results
- quantifiers repeat the previous symbol or group according to the parsed rule

Undefined repetition is capped at 5:

```java
int count = switch (quantifier.kind()) {
    case STAR -> random.nextInt(MAX_REPETITION + 1);
    case PLUS -> 1 + random.nextInt(MAX_REPETITION);
    case QUESTION -> random.nextInt(2);
    case EXACT -> quantifier.count();
};
```

### Bonus: Processing Trace
The bonus requirement is covered by the trace functionality. It prints the internal tree built from the regex, showing what was grouped first and which quantifier was attached to each symbol or alternation.

Example:

```text
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
```

## Program Execution
For each Variant 4 regex the program:
- generates five sample strings
- validates every generated string against an equivalent Java regex
- prints `OK` or `FAIL`
- prints the processing trace for the first expression

Example output:

```text
Pattern: L(M|N)O^[3]P*Q(2|3)
  LNOOOPQ3             OK
  LMOOOPQ3             OK
  LMOOOPPQ2            OK
```

## Difficulties Faced
1. The task notation uses `^` as a readable replacement for superscript-style quantifiers.
   The solution was to interpret `^*`, `^[+]`, and `^[n]` as postfix repetition operators on the previous symbol or group.

2. A quantifier must apply only to the previous symbol or grouped expression.
   The lexer was kept symbol-based so a pattern like `P*` affects only `P`, not a longer literal chunk.

3. Validation must still work with Java's regex engine.
   The custom notation is translated internally into normal Java regex syntax before validation.

## Conclusions
- The program satisfies the task requirement of dynamically interpreting the regular expressions.
- Undefined repetition is limited to 5 occurrences.
- The generator produces valid words for the three Variant 4 expressions.
- The bonus requirement is implemented through the processing trace.
- The code is structured clearly enough to present and explain during the lab defense.

## References
- [Regular expression](https://en.wikipedia.org/wiki/Regular_expression)
- [Formal language](https://en.wikipedia.org/wiki/Formal_language)
- [Recursive descent parser](https://en.wikipedia.org/wiki/Recursive_descent_parser)
