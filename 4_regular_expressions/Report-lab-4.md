# Topic: Regular Expressions

### Course: Formal Languages & Finite Automata
### Author: Student | Variant 4

----

## Theory
Regular expressions describe sets of valid strings. In this lab the idea is not to hardcode how each example is produced, but to read a regex dynamically, build an internal representation for it, and generate words that belong to the same language.

The implementation handles:
- alternation: `(A|B)`
- concatenation: implicit
- quantifiers: `*`, `+`, `?`
- exact repetition in the reference style: `^n`

## Objectives
1. Build a dynamic regex generator.
2. Limit open repetition to at most 5 occurrences.
3. Show the processing steps as a bonus feature.
4. Generate valid strings for the Variant 4 expressions.

## Implementation Description
The lab was rewritten as a small Java pipeline inside `src`:

- `RegexLexer.java` converts the regex into tokens.
- `RegexParser.java` builds an AST with sequence, choice, and quantified nodes.
- `RegexInterpreter.java` walks the AST and generates random valid strings.
- `RegexGenerator.java` acts as a small facade over the whole pipeline.
- `Main.java` runs the Variant 4 examples and validates the results.

Flow:

```text
regex text -> tokens -> AST -> generated string
```

### Lexer
The lexer emits one literal symbol at a time. This is important because a postfix quantifier such as `P*` must apply only to `P`, not to a longer chunk before it.

```java
switch (current) {
    case '(' -> tokens.add(new RegexToken(RegexTokenKind.LPAREN, null));
    case '*' -> tokens.add(new RegexToken(RegexTokenKind.STAR, null));
    default -> tokens.add(new RegexToken(RegexTokenKind.WORD, consumeSymbol()));
}
```

### Parser
The parser uses recursive descent. Parenthesized groups become either a `ChoiceNode` or a `SequenceNode`, and optional quantifiers are attached to the node that was just parsed.

```java
options.add(parseSequence(EnumSet.of(RegexTokenKind.OR, RegexTokenKind.RPAREN)));
while (match(RegexTokenKind.OR)) {
    options.add(parseSequence(EnumSet.of(RegexTokenKind.OR, RegexTokenKind.RPAREN)));
}
```

### Interpreter
The interpreter generates strings by traversing the AST. For `*` and `+`, repetition is capped at 5.

```java
int count = switch (quantifier.kind()) {
    case STAR -> random.nextInt(MAX_REPETITION + 1);
    case PLUS -> 1 + random.nextInt(MAX_REPETITION);
    case QUESTION -> random.nextInt(2);
    case EXACT -> quantifier.count();
};
```

## Program Execution
`Main.java` runs the three Variant 4 patterns:

1. `(S|T)(U|V)W*Y+24`
2. `L(M|N)O3P*Q(2|3)`
3. `R*S(T|U|N)W(X|Y|Z)2`

For each regex the program:
- generates five sample strings
- validates them with Java's `Pattern`
- prints the parse trace for the first example

Example output:

```text
Pattern: L(M|N)O3P*Q(2|3)
  LMO3PPPQ3            OK
  LNO3PPPPQ2           OK
```

## Conclusions
- The generator now follows a clear Java lexer -> parser -> interpreter structure.
- Regexes are interpreted dynamically instead of being hardcoded.
- Repetition is limited to 5 where needed.
- The trace output makes the internal processing easy to explain during the presentation.

## References
- [Regular expression](https://en.wikipedia.org/wiki/Regular_expression)
- [Recursive descent parser](https://en.wikipedia.org/wiki/Recursive_descent_parser)
