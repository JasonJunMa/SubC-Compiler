package wci.frontend.subc;

import wci.frontend.*;
import wci.frontend.subc.parsers.*;
import wci.intermediate.*;
import wci.message.*;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;
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
     *  @throws Exception if an error occurred.
     */
    public void parse()
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        iCode = ICodeFactory.createICode();

        try {
            Token token = nextToken();
            ICodeNode rootNode = null;

            // Look for the BEGIN token to parse a compound statement.
            if (token.getType() == LEFT_BRACE) {
                StatementParser statementParser = new StatementParser(this);
                rootNode = statementParser.parse(token);
                token = currentToken();
            }
            else {
                errorHandler.flag(token, UNEXPECTED_TOKEN, this);
            }

            // Look for the final period.
            //if (token.getType() != DOT) {
            //    errorHandler.flag(token, MISSING_PERIOD, this);
            //}
            token = currentToken();

            // Set the parse tree root node.
            if (rootNode != null) {
                iCode.setRoot(rootNode);
            }

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
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
}
