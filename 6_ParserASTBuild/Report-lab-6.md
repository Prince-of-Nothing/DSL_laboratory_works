# Topic: Parser & Building an Abstract Syntax Tree

### Course: Formal Languages & Finite Automata
### Author: Student

----

## Theory
Parsing transforms a token stream into a structured representation that follows the grammar of a language. An Abstract Syntax Tree keeps only the structure that matters for later stages such as semantic analysis, interpretation, or compilation.

## Objectives
1. Define token types with regular expressions.
2. Implement a lexer that produces typed tokens with position information.
3. Build AST data structures for the processed language.
4. Implement a simple parser that creates the AST.

## Implementation Description
The lab was rewritten as a compact Java pipeline:

- `TokenType.java` defines token categories and the regex used to recognize them.
- `Token.java` stores token type, lexeme, line, and column.
- `Lexer.java` scans the source text and emits tokens.
- `ASTNode.java` contains the AST interface and the concrete node classes.
- `Parser.java` uses recursive descent parsing to build the AST.
- `Main.java` runs a few demonstration programs and prints both tokens and ASTs.

Flow:

```text
source code -> tokens -> AST
```

### Token recognition with regex
Each token type stores a compiled regex pattern. The lexer tries the token types in order and uses the first one that matches at the current cursor position.

```java
FUNCTION("function\\b"),
RETURN("return\\b"),
VAR("var\\b"),
NUMBER("\\d+(?:\\.\\d+)?"),
IDENTIFIER("[A-Za-z_][A-Za-z0-9_]*")
```

### Lexer
The lexer also tracks line and column so that parse errors can point to the exact source location.

```java
Matcher matcher = type.pattern().matcher(remaining);
if (matcher.lookingAt()) {
    String lexeme = matcher.group();
    Token token = new Token(type, lexeme, line, column);
    advance(lexeme);
    return token;
}
```

### AST and parser
The parser supports:
- variable declarations
- function declarations
- return statements
- if / else blocks
- assignments
- arithmetic and logical expressions
- function calls

Operator precedence is implemented through dedicated parsing methods:
- assignment
- logical OR / AND
- equality and comparison
- addition and subtraction
- multiplication, division, modulo
- power
- unary expressions
- primary expressions

## Program Execution
`Main.java` demonstrates the pipeline on examples such as:

1. `var z = sin(0.5) + cos(2) - 3.14 ^ 2 / x;`
2. `if (x > 10) { var y = 5; } else { var y = 0; }`
3. `function add(a, b) { return a + b; }`

For each input the program prints:
- the source code
- the token stream with positions
- the generated AST

## Conclusions
- Token recognition is now explicitly regex-based, as requested in the task.
- The lexer and parser are separated cleanly.
- The AST output is readable and suitable for presentation.
- The implementation stays small enough to follow during a lab defense.

## References
- [Parsing](https://en.wikipedia.org/wiki/Parsing)
- [Abstract syntax tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree)
- [Recursive descent parser](https://en.wikipedia.org/wiki/Recursive_descent_parser)
