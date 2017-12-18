package wci.frontend.subc.parsers;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.SLOT;

import java.util.EnumSet;

import wci.frontend.EofToken;
import wci.frontend.Token;
import wci.frontend.TokenType;

import wci.frontend.subc.SubCErrorCode;
import wci.frontend.subc.SubCParserTD;
import wci.frontend.subc.SubCTokenType;
import wci.intermediate.Definition;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTab;
import wci.intermediate.SymTabEntry;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.SymTabEntryImpl;
import wci.intermediate.symtabimpl.SymTabKeyImpl;


/**
 * <h1>StatementParser</h1>
 *
 * <p>Parse a SubC statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class StatementParser extends SubCParserTD {
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public StatementParser(SubCParserTD parent) {
        super(parent);
    }

    // Synchronization set for starting a statement.
    protected static final EnumSet<SubCTokenType> STMT_START_SET = EnumSet.of(LEFT_BRACE, SWITCH, FOR, SubCTokenType.IF,
            DO, WHILE, IDENTIFIER, SEMICOLON, INT, CHAR, DOUBLE, FLOAT, CONST,RETURN);

    // Synchronization set for following a statement.
    protected static final EnumSet<SubCTokenType> STMT_FOLLOW_SET = EnumSet.of(SEMICOLON, ELSE, WHILE, CASE);

    /**
     * Parse a statement.
     * To be overridden by the specialized statement parser subclasses.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry parentID) throws Exception {
        ICodeNode statementNode = null;

        switch ((SubCTokenType) token.getType()) {

        case LEFT_BRACE: {
            CompoundStatementParser compoundParser = new CompoundStatementParser(this);
            statementNode = compoundParser.parse(token, parentID);
            break;
        }

        case IDENTIFIER: {
            String name = token.getText();
            SymTabEntry id = symTabStack.lookup(name);
            Definition idDefn = id != null ? id.getDefinition() : DefinitionImpl.UNDEFINED;

            // Assignment statement or procedure call.
            switch ((DefinitionImpl) idDefn) {
                case VARIABLE:
                case VALUE_PARM:
                case VAR_PARM:
                case UNDEFINED: {
                    AssignmentStatementParser assignmentParser = new AssignmentStatementParser(this);
                    statementNode = assignmentParser.parse(token,parentID);
                    break;
                }
                case FUNCTION:
                case PROCEDURE: {
                    CallParser callParser = new CallParser(this);
                    statementNode = callParser.parse(token);
                    break;
                }
                default: {
                    errorHandler.flag(token, SubCErrorCode.UNEXPECTED_TOKEN, this);
                    token = nextToken(); // consume identifier
                }
            }

            break;
        }

        case INT:
        case CHAR:
        case FLOAT:
        case DOUBLE:{
            VariableDeclarationsParser variableParser = new VariableDeclarationsParser(this);
            variableParser.setDefinition(DefinitionImpl.VARIABLE);
            variableParser.parse(token, parentID);
            statementNode = ICodeFactory.createICodeNode(NO_OP);
            break;
        }
        case CONST: {
            token = nextToken(); // consume CONST
            ConstantDefinitionsParser constantDefinitionsParser = new ConstantDefinitionsParser(this);
            constantDefinitionsParser.parse(token, parentID);
            statementNode = ICodeFactory.createICodeNode(NO_OP);
            break;
        }

        case DO: {
            RepeatStatementParser repeatParser = new RepeatStatementParser(this);
            statementNode = repeatParser.parse(token, parentID);
            break;
        }

        case WHILE: {
            WhileStatementParser whileParser = new WhileStatementParser(this);
            statementNode = whileParser.parse(token, parentID);
            break;
        }
        /*
        case FOR: {
            ForStatementParser forParser = new ForStatementParser(this);
            statementNode = forParser.parse(token);
            break;
        }
        */
        case IF: {
            IfStatementParser ifParser = new IfStatementParser(this);
            statementNode = ifParser.parse(token, parentID);
            break;
        }
        /*
        case SWITCH: {
            CaseStatementParser caseParser = new CaseStatementParser(this);
            statementNode = caseParser.parse(token);
            break;
        }
        */
        case RETURN: {
            ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN);
            assignNode.setTypeSpec(parentID.getTypeSpec());
            SymTabEntry targetId = new SymTabEntryImpl(parentID.getName(), symTabStack.getLocalSymTab());
            targetId.setDefinition(DefinitionImpl.VARIABLE);
            targetId.setTypeSpec(parentID.getTypeSpec());

            // Set its slot number in the local variables array.
            int slot = targetId.getSymTab().maxSlotNumber() + 1;
            targetId.setAttribute(SLOT, slot);

            // Create the variable node and set its name attribute.
            ICodeNode variableNode = ICodeFactory.createICodeNode(VARIABLE);
            variableNode.setAttribute(ID, targetId);
            variableNode.setTypeSpec(parentID.getTypeSpec());

            // The ASSIGN node adopts the variable node as its first child.
            assignNode.addChild(variableNode);
            token = nextToken(); // Consume RETURN

            if (token.getType() != SEMICOLON) {
                ExpressionParser expressionParser = new ExpressionParser(this);
                assignNode.addChild(expressionParser.parse(token));

                if (parentID.getDefinition() == DefinitionImpl.PROCEDURE) {
                    errorHandler.flag(token, SubCErrorCode.INVALID_ASSIGMENT_VOID, this);
                }
            } else if (parentID.getDefinition() == DefinitionImpl.FUNCTION) {
                errorHandler.flag(token, SubCErrorCode.MISSING_EXPRESSION, this);
            }

            statementNode = assignNode;
            token = nextToken(); // Consume semicolon
            break;
        }
        default: {
            statementNode = ICodeFactory.createICodeNode(NO_OP);
            break;
        }
        }

        // Set the current line number as an attribute.
        setLineNumber(statementNode, token);

        return statementNode;
    }

    /**
     * Set the current line number as a statement node attribute.
     * @param node ICodeNode
     * @param token Token
     */
    protected void setLineNumber(ICodeNode node, Token token) {
        if (node != null) {
            node.setAttribute(LINE, token.getLineNumber());
        }
    }

    /**
     * Parse a statement list.
     * @param token the curent token.
     * @param parentNode the parent node of the statement list.
     * @param terminator the token type of the node that terminates the list.
     * @param errorCode the error code if the terminator token is missing.
     * @throws Exception if an error occurred.
     */
    protected void parseList(Token token, ICodeNode parentNode, SymTabEntry routineId, SubCTokenType terminator,
            SubCErrorCode errorCode) throws Exception {
        // Synchronization set for the terminator.
        EnumSet<SubCTokenType> terminatorSet = STMT_START_SET.clone();
        terminatorSet.add(terminator);

        // Loop to parse each statement until the END token
        // or the end of the source file.
        while (!(token instanceof EofToken) && (token.getType() != terminator)) {

            // Parse a statement.  The parent node adopts the statement node.
            ICodeNode statementNode = parse(token, routineId);
            parentNode.addChild(statementNode);

            token = currentToken();
            TokenType tokenType = token.getType();

            // Look for the semicolon between statements.
            if (tokenType == SEMICOLON) {
                token = nextToken(); // consume the ;
            }

            // If at the start of the next statement, then missing a semicolon.
            //else if (STMT_START_SET.contains(tokenType)) {
            //   errorHandler.flag(token, MISSING_SEMICOLON, this);
            //}

            // Synchronize at the start of the next statement
            // or at the terminator.
            token = synchronize(terminatorSet);
        }

        // Look for the terminator token.
        if (token.getType() == terminator) {
            token = nextToken();
        } else {
            errorHandler.flag(token, errorCode, this);
        }
    }
}
