package wci.frontend.subc.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.subc.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>AssignmentStatementParser</h1>
 *
 * <p>Parse a SubC assignment statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class AssignmentStatementParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public AssignmentStatementParser(SubCParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for the = token.
    private static final EnumSet<SubCTokenType> EQUALS_SET =
        ExpressionParser.EXPR_START_SET.clone();
    static {
        EQUALS_SET.add(EQUALS);
        EQUALS_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse an assignment statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        // Create the ASSIGN node.
        ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN);

        // Parse the target variable.
        VariableParser variableParser = new VariableParser(this);
        ICodeNode targetNode = variableParser.parse(token);
        TypeSpec targetType = targetNode != null ? targetNode.getTypeSpec()
                                                 : Predefined.undefinedType;

        // The ASSIGN node adopts the variable node as its first child.
        assignNode.addChild(targetNode);

        // Look for the = token.
        token = synchronize(EQUALS_SET);
        if (token.getType() == EQUALS) {
            token = nextToken(); // consume the =
        }
        else {
            errorHandler.flag(token, MISSING_EQUALS, this);
        }

        // Parse the expression.  The ASSIGN node adopts the expression's
        // node as its second child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        ICodeNode exprNode = expressionParser.parse(token);
        assignNode.addChild(exprNode);

        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                             : Predefined.undefinedType;
        if (!TypeChecker.areAssignmentCompatible(targetType, exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }

        assignNode.setTypeSpec(targetType);

        // check whether the last token of the statment is a ;
        token = currentToken();
        if (token.getType() != SEMICOLON) {
            errorHandler.flag(token, MISSING_SEMICOLON, this);
        }
        return assignNode;
    }
}
