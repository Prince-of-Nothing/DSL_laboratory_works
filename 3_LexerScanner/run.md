# Lexer Scanner - Compilation and Execution Guide

## Prerequisites
- Java Development Kit (JDK) 8 or higher installed
- Command line access (Terminal/Command Prompt)

## Directory Structure
```
3_LexerScanner/
├── src/
│   ├── dev/lexer/
│   │   ├── Lexer.java
│   │   ├── LexerException.java
│   │   ├── Main.java
│   │   ├── Token.java
│   │   └── TokenType.java
│   └── Lexer/
│       └── Main.java (alternative version)
└── run.md (this file)
```

## How to Compile and Run

### Step 1: Navigate to Source Directory
```bash
cd 3_LexerScanner/src
```

### Step 2: Compile Java Files
```bash
javac dev/lexer/*.java
```

### Step 3: Run the Program
```bash
java dev.lexer.Main
```

## Expected Output
When you run the program, you should see output like this:
```
✔ functions_and_arithmetic - parsing: "sin(0.5) + cos(2) - 3.14^2 / x"
✔ whitespace_and_star_slash - parsing: "   42 * 7 / 3"
✔ leading_dot_floats - parsing: ".75 + .25"
✔ identifier_with_underscore - parsing: "foo_bar1"
✔ lone_dot_error - parsing: "."
✔ unexpected_character_error - parsing: "1 @ 2"
All lexer tests passed.
```

## What the Program Does
The lexer program runs a series of test cases that:

1. **Token Recognition Tests**: Verifies that the lexer correctly identifies different types of tokens:
   - **Identifiers**: `sin`, `cos`, `x`, `foo_bar1`
   - **Numbers**: Integers (`42`, `7`, `3`, `2`) and Floats (`0.5`, `3.14`, `.75`, `.25`)
   - **Operators**: `+`, `-`, `*`, `/`, `^`
   - **Symbols**: `(`, `)`
   - **Whitespace**: Properly handled and ignored

2. **Error Handling Tests**: Ensures the lexer properly throws exceptions for:
   - Invalid characters like `@`
   - Malformed tokens like a lone `.`

## Test Cases Explained

| Test Case | Input | Purpose |
|-----------|-------|---------|
| `functions_and_arithmetic` | `"sin(0.5) + cos(2) - 3.14^2 / x"` | Tests function names, parentheses, arithmetic operators, and mixed number types |
| `whitespace_and_star_slash` | `"   42 * 7 / 3"` | Tests whitespace handling and multiplication/division operators |
| `leading_dot_floats` | `".75 + .25"` | Tests floating-point numbers that start with a decimal point |
| `identifier_with_underscore` | `"foo_bar1"` | Tests identifiers containing underscores and numbers |
| `lone_dot_error` | `"."` | Tests error handling for invalid single dot character |
| `unexpected_character_error` | `"1 @ 2"` | Tests error handling for unsupported characters |

## Troubleshooting

### Common Issues:

1. **"javac: command not found"**
   - Make sure Java JDK is installed and added to your PATH

2. **"package dev.lexer does not exist"**
   - Ensure you're running from the `src` directory
   - Check that all Java files are in the correct `dev/lexer/` subdirectory

3. **Compilation errors**
   - Make sure all `.java` files are present in `dev/lexer/`
   - Check for syntax errors in the code

4. **"Could not find or load main class"**
   - Ensure you're using the correct package name: `dev.lexer.Main`
   - Verify you compiled the files first with `javac`

## Alternative Commands (All-in-One)
```bash
# From the project root directory
cd 3_LexerScanner/src && javac dev/lexer/*.java && java dev.lexer.Main
```

This will navigate to the source directory, compile all Java files, and run the program in one command.