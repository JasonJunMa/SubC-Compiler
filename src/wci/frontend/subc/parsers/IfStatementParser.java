package wci.frontend.subc.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.subc.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>IfStatementParser</h1>
 *
 * <p>Parse a SubC IF statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class IfStatementParser extends StatementParser {
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public IfStatementParser(SubCParserTD parent) {
        super(parent);
    }

    // Synchronization set for THEN.
    private static final EnumSet<SubCTokenType> THEN_SET = StatementParser.STMT_START_SET.clone();
    static {
        THEN_SET.add(LEFT_BRACE);
        THEN_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse an IF statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry parentId) throws Exception {
        token = nextToken(); // consume the IF

        // Create an IF node.
        ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);

        // Parse the expression.
        // The IF node adopts the expression subtree as its first child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        ICodeNode exprNode = expressionParser.parse(token);
        ifNode.addChild(exprNode);

        // Type check: The expression type must be boolean.
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec() : Predefined.undefinedType;
        if (!TypeChecker.isBoolean(exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }

        // Synchronize at the THEN.
        token = synchronize(THEN_SET);
        if (token.getType() == LEFT_BRACE) {
            //DO NOTHING
        } else {
            errorHandler.flag(token, MISSING_LEFT_BRACE, this);
        }

        // Parse the THEN statement.
        // The IF node adopts the statement subtree as its second child.
        StatementParser statementParser = new StatementParser(this);
        ifNode.addChild(statementParser.parse(token, parentId));
        token = currentToken();

        // Look for an ELSE.
        if (token.getType() == ELSE) {
            token = nextToken(); // consume the ELSE

            // Parse the ELSE statement.
            // The IF node adopts the statement subtree as its third child.
            ifNode.addChild(statementParser.parse(token, parentId));
        }

        return ifNode;
    }
}
