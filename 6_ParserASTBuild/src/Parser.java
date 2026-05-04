import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int cursor;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.cursor = 0;
    }

    public ProgramNode parse() {
        List<ASTNode> statements = new ArrayList<>();
        while (!check(TokenType.EOF)) {
            statements.add(parseStatement());
        }
        return new ProgramNode(statements);
    }

    private ASTNode parseStatement() {
        if (match(TokenType.VAR)) {
            return parseVariableDeclaration();
        }
        if (match(TokenType.FUNCTION)) {
            return parseFunctionDeclaration();
        }
        if (match(TokenType.IF)) {
            return parseIfStatement();
        }
        if (match(TokenType.RETURN)) {
            return parseReturnStatement();
        }
        if (match(TokenType.LBRACE)) {
            return parseBlockBody();
        }
        ASTNode expression = parseExpression();
        consumeOptional(TokenType.SEMICOLON);
        return new ExpressionStatementNode(expression);
    }

    private ASTNode parseVariableDeclaration() {
        String name = consume(TokenType.IDENTIFIER, "Expected variable name").lexeme();
        ASTNode initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }
        consumeOptional(TokenType.SEMICOLON);
        return new VariableDeclarationNode(name, initializer);
    }

    private ASTNode parseFunctionDeclaration() {
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
        return new FunctionDeclarationNode(name, parameters, parseBlockBody());
    }

    private ASTNode parseIfStatement() {
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
        return new IfNode(condition, thenBranch, elseBranch);
    }

    private ASTNode parseReturnStatement() {
        ASTNode value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }
        consumeOptional(TokenType.SEMICOLON);
        return new ReturnNode(value);
    }

    private BlockNode parseBlockBody() {
        List<ASTNode> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !check(TokenType.EOF)) {
            statements.add(parseStatement());
        }
        consume(TokenType.RBRACE, "Expected '}'");
        return new BlockNode(statements);
    }

    private ASTNode parseExpression() {
        return parseAssignment();
    }

    private ASTNode parseAssignment() {
        ASTNode target = parseLogicalOr();
        if (match(TokenType.ASSIGN)) {
            if (!(target instanceof IdentifierNode identifier)) {
                throw error("Assignment target must be an identifier");
            }
            ASTNode value = parseAssignment();
            return new AssignmentNode(identifier.name(), value);
        }
        return target;
    }

    private ASTNode parseLogicalOr() {
        ASTNode node = parseLogicalAnd();
        while (match(TokenType.OR)) {
            node = new BinaryNode("||", node, parseLogicalAnd());
        }
        return node;
    }

    private ASTNode parseLogicalAnd() {
        ASTNode node = parseEquality();
        while (match(TokenType.AND)) {
            node = new BinaryNode("&&", node, parseEquality());
        }
        return node;
    }

    private ASTNode parseEquality() {
        ASTNode node = parseComparison();
        while (match(TokenType.EQ) || match(TokenType.NE)) {
            Token operator = previous();
            node = new BinaryNode(operator.lexeme(), node, parseComparison());
        }
        return node;
    }

    private ASTNode parseComparison() {
        ASTNode node = parseTerm();
        while (match(TokenType.LT) || match(TokenType.LE) || match(TokenType.GT) || match(TokenType.GE)) {
            Token operator = previous();
            node = new BinaryNode(operator.lexeme(), node, parseTerm());
        }
        return node;
    }

    private ASTNode parseTerm() {
        ASTNode node = parseFactor();
        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            Token operator = previous();
            node = new BinaryNode(operator.lexeme(), node, parseFactor());
        }
        return node;
    }

    private ASTNode parseFactor() {
        ASTNode node = parsePower();
        while (match(TokenType.STAR) || match(TokenType.SLASH) || match(TokenType.PERCENT)) {
            Token operator = previous();
            node = new BinaryNode(operator.lexeme(), node, parsePower());
        }
        return node;
    }

    private ASTNode parsePower() {
        ASTNode node = parseUnary();
        if (match(TokenType.CARET)) {
            node = new BinaryNode("^", node, parsePower());
        }
        return node;
    }

    private ASTNode parseUnary() {
        if (match(TokenType.BANG) || match(TokenType.MINUS)) {
            return new UnaryNode(previous().lexeme(), parseUnary());
        }
        return parsePrimary();
    }

    private ASTNode parsePrimary() {
        if (match(TokenType.NUMBER)) {
            return new LiteralNode("Number", previous().lexeme());
        }
        if (match(TokenType.STRING)) {
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
                return new CallNode(name, arguments);
            }
            return new IdentifierNode(name);
        }
        if (match(TokenType.LPAREN)) {
            ASTNode expression = parseExpression();
            consume(TokenType.RPAREN, "Expected ')'");
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
