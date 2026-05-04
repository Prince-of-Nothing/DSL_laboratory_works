import java.util.List;

public interface ASTNode {
    void appendTo(StringBuilder builder, int depth);

    default String prettyPrint() {
        StringBuilder builder = new StringBuilder();
        appendTo(builder, 0);
        return builder.toString().trim();
    }

    static void indent(StringBuilder builder, int depth) {
        builder.append("  ".repeat(depth));
    }
}

final class ProgramNode implements ASTNode {
    private final List<ASTNode> statements;

    ProgramNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("Program").append(System.lineSeparator());
        for (ASTNode statement : statements) {
            statement.appendTo(builder, depth + 1);
        }
    }
}

final class BlockNode implements ASTNode {
    private final List<ASTNode> statements;

    BlockNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("Block").append(System.lineSeparator());
        for (ASTNode statement : statements) {
            statement.appendTo(builder, depth + 1);
        }
    }
}

final class VariableDeclarationNode implements ASTNode {
    private final String name;
    private final ASTNode initializer;

    VariableDeclarationNode(String name, ASTNode initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("VarDecl ").append(name).append(System.lineSeparator());
        if (initializer != null) {
            initializer.appendTo(builder, depth + 1);
        }
    }
}

final class FunctionDeclarationNode implements ASTNode {
    private final String name;
    private final List<String> parameters;
    private final BlockNode body;

    FunctionDeclarationNode(String name, List<String> parameters, BlockNode body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("FunctionDecl ").append(name).append(parameters).append(System.lineSeparator());
        body.appendTo(builder, depth + 1);
    }
}

final class ReturnNode implements ASTNode {
    private final ASTNode value;

    ReturnNode(ASTNode value) {
        this.value = value;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("Return").append(System.lineSeparator());
        if (value != null) {
            value.appendTo(builder, depth + 1);
        }
    }
}

final class IfNode implements ASTNode {
    private final ASTNode condition;
    private final BlockNode thenBranch;
    private final BlockNode elseBranch;

    IfNode(ASTNode condition, BlockNode thenBranch, BlockNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("If").append(System.lineSeparator());
        ASTNode.indent(builder, depth + 1);
        builder.append("Condition").append(System.lineSeparator());
        condition.appendTo(builder, depth + 2);
        ASTNode.indent(builder, depth + 1);
        builder.append("Then").append(System.lineSeparator());
        thenBranch.appendTo(builder, depth + 2);
        if (elseBranch != null) {
            ASTNode.indent(builder, depth + 1);
            builder.append("Else").append(System.lineSeparator());
            elseBranch.appendTo(builder, depth + 2);
        }
    }
}

final class ExpressionStatementNode implements ASTNode {
    private final ASTNode expression;

    ExpressionStatementNode(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("ExprStmt").append(System.lineSeparator());
        expression.appendTo(builder, depth + 1);
    }
}

final class AssignmentNode implements ASTNode {
    private final String name;
    private final ASTNode value;

    AssignmentNode(String name, ASTNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("Assign ").append(name).append(System.lineSeparator());
        value.appendTo(builder, depth + 1);
    }
}

final class BinaryNode implements ASTNode {
    private final String operator;
    private final ASTNode left;
    private final ASTNode right;

    BinaryNode(String operator, ASTNode left, ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("Binary ").append(operator).append(System.lineSeparator());
        left.appendTo(builder, depth + 1);
        right.appendTo(builder, depth + 1);
    }
}

final class UnaryNode implements ASTNode {
    private final String operator;
    private final ASTNode operand;

    UnaryNode(String operator, ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("Unary ").append(operator).append(System.lineSeparator());
        operand.appendTo(builder, depth + 1);
    }
}

final class CallNode implements ASTNode {
    private final String callee;
    private final List<ASTNode> arguments;

    CallNode(String callee, List<ASTNode> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("Call ").append(callee).append(System.lineSeparator());
        for (ASTNode argument : arguments) {
            argument.appendTo(builder, depth + 1);
        }
    }
}

final class IdentifierNode implements ASTNode {
    private final String name;

    IdentifierNode(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append("Identifier ").append(name).append(System.lineSeparator());
    }
}

final class LiteralNode implements ASTNode {
    private final String kind;
    private final String value;

    LiteralNode(String kind, String value) {
        this.kind = kind;
        this.value = value;
    }

    @Override
    public void appendTo(StringBuilder builder, int depth) {
        ASTNode.indent(builder, depth);
        builder.append(kind).append(" ").append(value).append(System.lineSeparator());
    }
}
