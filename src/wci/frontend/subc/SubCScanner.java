package wci.frontend.subc;

import wci.frontend.*;
import wci.frontend.subc.tokens.*;

import static wci.frontend.Source.EOF;
import static wci.frontend.Source.EOL;
import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;

/**
 * <h1>PascalScanner</h1>
 *
 * <p>The Pascal scanner.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCScanner extends Scanner
{
    /**
     * Constructor
     * @param source the source to be used with this scanner.
     */
    public SubCScanner(Source source)
    {
        super(source);
    }

    /**
     * Extract and return the next Pascal token from the source.
     * @return the next token.
     * @throws Exception if an error occurred.
     */
    protected Token extractToken()
        throws Exception
    {
        skipWhiteSpace();

        Token token;
        char currentChar = currentChar();

        // Construct the next token.  The current character determines the
        // token type.
        if (currentChar == EOF) {
            token = new EofToken(source);
        }
        else if (Character.isLetter(currentChar)||currentChar=='_') { //if the first character is a letter or a '_'
            //if the first character is '_'
            if(currentChar=='_'){
              if(Character.isLetter(source.peekChar())){ //check if the next character is a letter
                token = new SubCWordToken(source); //the next character is a letter, then the token could be a word
              }
              else if (SubCTokenType.SPECIAL_SYMBOLS
                       .containsKey(Character.toString(currentChar))) {
                  token = new SubCSpecialSymbolToken(source);
              }
              else{
                //the next character is not a letter, the token can not be a word, go to next
                token = new SubCErrorToken(source, INVALID_CHARACTER,
                                           Character.toString(currentChar));
                nextChar(); // consume character
                nextChar();
              }
            }
            //else the firt character is a letter
            else{
              token = new SubCWordToken(source);
            }
        }
        else if (Character.isDigit(currentChar)) {
            token = new SubCNumberToken(source);
        }
        else if (currentChar == '\"') { //double quote is a string
            token = new SubCStringToken(source);
        }
        else if (SubCTokenType.SPECIAL_SYMBOLS
                 .containsKey(Character.toString(currentChar))) {
            token = new SubCSpecialSymbolToken(source);
        }
        else {
            token = new SubCErrorToken(source, INVALID_CHARACTER,
                                       Character.toString(currentChar));
            nextChar(); // consume character
        }

        return token;
    }

    /**
     * Skip whitespace characters by consuming them.  A comment is whitespace.
     * @throws Exception if an error occurred.
     */
    private void skipWhiteSpace()
        throws Exception
    {
        char currentChar = currentChar();

        while (Character.isWhitespace(currentChar) || (currentChar == '/')) {

            // Start of a comment?
            if (currentChar == '/') {
              //check if the next character is '/'
              if(source.peekChar()=='/'){
                do{
                  currentChar = nextChar();
                }while(currentChar != EOL);
                currentChar = nextChar();
              }
              //check if the next character is '*', which means there will be a block of comment
              else if(source.peekChar() == '*'){
                //consume comment character until we find a close "*/"
                currentChar=nextChar();
                do {
                    currentChar = nextChar(); // consume comment characters
                } while ((currentChar != '*') &&(source.peekChar()!='/')&& (currentChar != EOF));
                // System.out.println(currentChar);
                // Found closing '*'?
                if (currentChar == '*') {
                    currentChar = nextChar(); // consume the '*',move to '/'
                    currentChar = nextChar(); // consume the '/',move to next
                }
              }
              else{
                return;//do nothing, because the '/' here will be used as a divider
              }
            }

            // Not a comment. is a whitespace
            else {
                currentChar = nextChar(); // consume whitespace character
            }
        }
    }
}
