package wci.frontend.subc;

import static wci.message.MessageType.PARSER_SUMMARY;

import java.util.ArrayList;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.subc.parsers.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;
import wci.message.*;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.message.MessageType.PARSER_SUMMARY;


/**
 * <h1>SubCParserTD</h1>
 *
 * <p>The top-down SubC parser.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCParserTD extends Parser
{
    protected static SubCErrorHandler errorHandler = new SubCErrorHandler();
    protected static StringBuilder nestedStacks = new StringBuilder();

    private SymTabEntry routineId;
    /**
     * Constructor.
     * @param scanner the scanner to be used with this parser.
     */
    public SubCParserTD(Scanner scanner)
    {
        super(scanner);
    }

    /**
     * Constructor for subclasses.
     * @param parent the parent parser.
     */
    public SubCParserTD(SubCParserTD parent)
    {
        super(parent.getScanner());
    }

    /**
     * Getter.
     * @return the error handler.
     */
    public SubCErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    /**
     * Parse a SubC source program and generate the symbol table
     * and the intermediate code.
     * @throws Exception if an error occurred.
     */
    public void parse()
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        Predefined.initialize(symTabStack);

        try {
            Token token = nextToken();
            ICode iCode = ICodeFactory.createICode();

            routineId = symTabStack.getProgramId();
            // Push a new symbol table onto the symbol table stack and set
            // the routine's symbol table and intermediate code.
            routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
            routineId.setAttribute(ROUTINE_ICODE, iCode);
            routineId.setAttribute(ROUTINE_ROUTINES, new ArrayList<>());

            // Parse a program.
            ProgramParser programParser = new ProgramParser(this);
            programParser.parse(token, routineId);
            token = currentToken();

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            //System.out.println(nestedStacks);
            sendMessage(new Message(PARSER_SUMMARY,
                                    new Number[] {token.getLineNumber(),
                                                  getErrorCount(),
                                                  elapsedTime}));
        }
        catch (java.io.IOException ex) {
            errorHandler.abortTranslation(IO_ERROR, this);
        }
    }

    /**
     * Return the number of syntax errors found by the parser.
     * @return the error count.
     */
    public int getErrorCount()
    {
        return errorHandler.getErrorCount();
    }

    /**
     * Synchronize the parser.
     * @param syncSet the set of token types for synchronizing the parser.
     * @return the token where the parser has synchronized.
     * @throws Exception if an error occurred.
     */
    public Token synchronize(EnumSet syncSet)
        throws Exception
    {
        Token token = currentToken();

        // If the current token is not in the synchronization set,
        // then it is unexpected and the parser must recover.
        if (!syncSet.contains(token.getType())) {

            // Flag the unexpected token.
            errorHandler.flag(token, UNEXPECTED_TOKEN, this);

            // Recover by skipping tokens that are not
            // in the synchronization set.
            do {
                token = nextToken();
            } while (!(token instanceof EofToken) &&
                     !syncSet.contains(token.getType()));
        }

        return token;
    }
}
