package wci.frontend.subc.parsers;

import static wci.frontend.subc.SubCErrorCode.MISSING_PERIOD;
import static wci.frontend.subc.SubCTokenType.DOT;

import wci.frontend.Token;
import wci.frontend.subc.SubCParserTD;
import wci.intermediate.SymTabEntry;

/**
 * <h1>ProgramParser</h1>
 *
 * <p>Parse a SubC program.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ProgramParser extends DeclarationsParser {
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public ProgramParser(SubCParserTD parent) {
        super(parent);
    }

    /**
     * Parse a program.
     * @param token the initial token.
     * @param parentId the symbol table entry of the parent routine's name.
     * @return null
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {

        // Parse the program.
        DeclarationsParser routineParser = new DeclarationsParser(this);
        routineParser.parse(token, parentId);

        return null;
    }
}
