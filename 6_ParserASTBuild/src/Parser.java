import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private final boolean verbose;
    private int cursor;
    private int traceDepth;

    public Parser(List<Token> tokens) {
        this(tokens, false);
    }

    public Parser(List<Token> tokens, boolean verbose) {
        this.tokens = tokens;
        this.verbose = verbose;
        this.cursor = 0;
        this.traceDepth = 0;
    }

    private void enter(String rule) {
        if (!verbose) return;
        System.out.printf("[Parser] %s%-28s  token: %s '%s'%n",
            "  ".repeat(traceDepth), rule, peek().type(), peek().lexeme());
        traceDepth++;
    }

    private void exit() {
        if (!verbose) return;
        traceDepth--;
    }

    public ProgramNode parse() {
        enter("parse");
        List<ASTNode> statements = new ArrayList<>();
        while (!check(TokenType.EOF)) {
            statements.add(parseStatement());
        }
        exit();
        return new ProgramNode(statements);
    }

    private ASTNode parseStatement() {
        enter("parseStatement");
        ASTNode result;
        if (match(TokenType.VAR)) {
            result = parseVariableDeclaration();
        } else if (match(TokenType.FUNCTION)) {
            result = parseFunctionDeclaration();
        } else if (match(TokenType.IF)) {
            result = parseIfStatement();
        } else if (match(TokenType.RETURN)) {
            result = parseReturnStatement();
        } else if (match(TokenType.LBRACE)) {
            result = parseBlockBody();
        } else {
            ASTNode expression = parseExpression();
            consumeOptional(TokenType.SEMICOLON);
            result = new ExpressionStatementNode(expression);
        }
        exit();
        return result;
    }

    private ASTNode parseVariableDeclaration() {
        enter("parseVarDecl");
        String name = consume(TokenType.IDENTIFIER, "Expected variable name").lexeme();
        ASTNode initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }
        consumeOptional(TokenType.SEMICOLON);
        exit();
        return new VariableDeclarationNode(name, initializer);
    }

    private ASTNode parseFunctionDeclaration() {
        enter("parseFunctionDecl");
        String name = consume(TokenType.IDENTIFIER, "Expected function name").lexeme();
        consume(TokenType.LPAREN, "Expected '(' after function name");
        List<String> parameters = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                parameters.add(consume(TokenType.IDENTIFIER, "Expected parameter").lexeme());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RPAREN, "Expected ')'");
        consume(TokenType.LBRACE, "Expected '{' before function body");
        FunctionDeclarationNode node = new FunctionDeclarationNode(name, parameters, parseBlockBody());
        exit();
        return node;
    }

    private ASTNode parseIfStatement() {
        enter("parseIfStmt");
        consume(TokenType.LPAREN, "Expected '(' after if");
        ASTNode condition = parseExpression();
        consume(TokenType.RPAREN, "Expected ')'");
        consume(TokenType.LBRACE, "Expected '{' before then block");
        BlockNode thenBranch = parseBlockBody();
        BlockNode elseBranch = null;
        if (match(TokenType.ELSE)) {
            consume(TokenType.LBRACE, "Expected '{' before else block");
            elseBranch = parseBlockBody();
        }
        exit();
        return new IfNode(condition, thenBranch, elseBranch);
    }

    private ASTNode parseReturnStatement() {
        enter("parseReturn");
        ASTNode value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }
        consumeOptional(TokenType.SEMICOLON);
        exit();
        return new ReturnNode(value);
    }

    private BlockNode parseBlockBody() {
        enter("parseBlock");
        List<ASTNode> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !check(TokenType.EOF)) {
            statements.add(parseStatement());
        }
        consume(TokenType.RBRACE, "Expected '}'");
        exit();
        return new BlockNode(statements);
    }

    private ASTNode parseExpression() {
        enter("parseExpression");
        ASTNode node = parseAssignment();
        exit();
        return node;
    }

    private ASTNode parseAssignment() {
        enter("parseAssignment");
        ASTNode target = parseLogicalOr();
        if (match(TokenType.ASSIGN)) {
            if (!(target instanceof IdentifierNode identifier)) {
                throw error("Assignment target must be an identifier");
            }
            ASTNode value = parseAssignment();
            exit();
            return new AssignmentNode(identifier.name(), value);
        }
        exit();
        return target;
    }

    private ASTNode parseLogicalOr() {
        enter("parseLogicalOr");
        ASTNode node = parseLogicalAnd();
        while (match(TokenType.OR)) {
            node = new BinaryNode("||", node, parseLogicalAnd());
        }
        exit();
        return node;
    }

    private ASTNode parseLogicalAnd() {
        enter("parseLogicalAnd");
        ASTNode node = parseEquality();
        while (match(TokenType.AND)) {
            node = new BinaryNode("&&", node, parseEquality());
        }
        exit();
        return node;
    }

    private ASTNode parseEquality() {
        enter("parseEquality");
        ASTNode node = parseComparison();
        while (match(TokenType.EQ) || match(TokenType.NE)) {
            Token operator = previous();
            node = new BinaryNode(operator.lexeme(), node, parseComparison());
        }
        exit();
        return node;
    }

    private ASTNode parseComparison() {
        enter("parseComparison");
        ASTNode node = parseTerm();
        while (match(TokenType.LT) || match(TokenType.LE) || match(TokenType.GT) || match(TokenType.GE)) {
            Token operator = previous();
            node = new BinaryNode(operator.lexeme(), node, parseTerm());
        }
        exit();
        return node;
    }

    private ASTNode parseTerm() {
        enter("parseTerm");
        ASTNode node = parseFactor();
        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            Token operator = previous();
            node = new BinaryNode(operator.lexeme(), node, parseFactor());
        }
        exit();
        return node;
    }

    private ASTNode parseFactor() {
        enter("parseFactor");
        ASTNode node = parsePower();
        while (match(TokenType.STAR) || match(TokenType.SLASH) || match(TokenType.PERCENT)) {
            Token operator = previous();
            node = new BinaryNode(operator.lexeme(), node, parsePower());
        }
        exit();
        return node;
    }

    private ASTNode parsePower() {
        enter("parsePower");
        ASTNode node = parseUnary();
        if (match(TokenType.CARET)) {
            node = new BinaryNode("^", node, parsePower());
        }
        exit();
        return node;
    }

    private ASTNode parseUnary() {
        enter("parseUnary");
        if (match(TokenType.BANG) || match(TokenType.MINUS)) {
            ASTNode node = new UnaryNode(previous().lexeme(), parseUnary());
            exit();
            return node;
        }
        ASTNode node = parsePrimary();
        exit();
        return node;
    }

    private ASTNode parsePrimary() {
        enter("parsePrimary");
        if (match(TokenType.NUMBER)) {
            exit();
            return new LiteralNode("Number", previous().lexeme());
        }
        if (match(TokenType.STRING)) {
            exit();
            return new LiteralNode("String", previous().lexeme());
        }
        if (match(TokenType.IDENTIFIER)) {
            String name = previous().lexeme();
            if (match(TokenType.LPAREN)) {
                List<ASTNode> arguments = new ArrayList<>();
                if (!check(TokenType.RPAREN)) {
                    do {
                        arguments.add(parseExpression());
                    } while (match(TokenType.COMMA));
                }
                consume(TokenType.RPAREN, "Expected ')'");
                exit();
                return new CallNode(name, arguments);
            }
            exit();
            return new IdentifierNode(name);
        }
        if (match(TokenType.LPAREN)) {
            ASTNode expression = parseExpression();
            consume(TokenType.RPAREN, "Expected ')'");
            exit();
            return expression;
        }
        throw error("Unexpected token " + peek());
    }

    private boolean match(TokenType type) {
        if (!check(type)) {
            return false;
        }
        cursor++;
        return true;
    }

    private boolean check(TokenType type) {
        return peek().type() == type;
    }

    private void consumeOptional(TokenType type) {
        if (check(type)) {
            cursor++;
        }
    }

    private Token consume(TokenType type, String message) {
        if (!check(type)) {
            throw error(message + " at " + peek().line() + ":" + peek().column());
        }
        return tokens.get(cursor++);
    }

    private Token peek() {
        return tokens.get(cursor);
    }

    private Token previous() {
        return tokens.get(cursor - 1);
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message);
    }
}
