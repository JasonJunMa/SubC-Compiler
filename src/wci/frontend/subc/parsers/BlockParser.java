package wci.frontend.subc.parsers;

import wci.frontend.*;
import wci.frontend.subc.*;
import wci.intermediate.*;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>BlockParser</h1>
 *
 * <p>Parse a SubC block.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class BlockParser extends SubCParserTD {
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public BlockParser(SubCParserTD parent) {
        super(parent);
    }

    /**
     * Parse a block.
     * @param token the initial token.
     * @param routineId the symbol table entry of the routine name.
     * @return the root node of the parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry routineId) throws Exception {

        StatementParser statementParser = new StatementParser(this);

        token = synchronize(StatementParser.STMT_START_SET);
        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        // Look for the LEFT_BRACE token to parse a compound statement.
        if (tokenType == LEFT_BRACE) {
            rootNode = statementParser.parse(token, routineId);
        }

        // Missing LEFT_BRACE: Attempt to parse anyway if possible.
        else {
            errorHandler.flag(token, MISSING_LEFT_BRACE, this);

            if (StatementParser.STMT_START_SET.contains(tokenType)) {
                rootNode = ICodeFactory.createICodeNode(COMPOUND);
                statementParser.parseList(token, rootNode, routineId, RIGHT_BRACE, MISSING_RIGHT_BRACE);
            }
        }

        return rootNode;
    }
}
