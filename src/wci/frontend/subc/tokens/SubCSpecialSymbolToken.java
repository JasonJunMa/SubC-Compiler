package wci.frontend.subc.tokens;

import wci.frontend.*;
import wci.frontend.subc.*;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;

/**
 * <h1>SubCSpecialSymbolToken</h1>
 *
 * <p> SubC special symbol tokens.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCSpecialSymbolToken extends SubCToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public SubCSpecialSymbolToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a SubC special symbol token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        char currentChar = currentChar();

        text = Character.toString(currentChar);
        type = null;

        switch (currentChar) {

            // Single-character special symbols.
            case '.':  case ':':  case '#':  case '(':  case ']':
            case ',':  case '\'': case '~':  case ')':  case '{':
            case ';':  case '\"':  case '$':  case '[':  case '}':
            case '_':  case '^':  case '?':{
                nextChar();  // consume character
                break;
            }

            // + or += or ++
            case '+': {
                currentChar = nextChar();  // consume '+';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }
                else if(currentChar == '+'){
                    text += currentChar;
                    nextChar();  // consume '+'
                }

                break;
            }

            // - or -= or --
            case '-': {
                currentChar = nextChar();  // consume '-';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }
                else if(currentChar == '-'){
                    text += currentChar;
                    nextChar();  // consume '-'
                }
                break;
            }

            // * or *=
            case '*': {
                currentChar = nextChar();  // consume '>';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            // / or /=
            case '/': {
                currentChar = nextChar();  // consume '/';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            // % or %=
            case '%': {
                currentChar = nextChar();  // consume '%';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            // = or ==
            case '=': {
                currentChar = nextChar();  // consume '=';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            // ! or !=
            case '!': {
                currentChar = nextChar();  // consume '!';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            // < or <= or <<
            case '<': {
                currentChar = nextChar();  // consume '<';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }
                else if(currentChar == '<'){
                    text += currentChar;
                    nextChar();  // consume '<'
                }

                break;
            }

            // > or >= or >>
            case '>': {
                currentChar = nextChar();  // consume '>';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }
                else if(currentChar == '>'){
                    text += currentChar;
                    nextChar();  // consume '>'
                }

                break;
            }

            // & or &&
            case '&': {
                currentChar = nextChar();  // consume '&';

                if (currentChar == '&') {
                    text += currentChar;
                    nextChar();  // consume '&'
                }

                break;
            }

            // | or ||
            case '|': {
                currentChar = nextChar();  // consume '|';

                if (currentChar == '|') {
                    text += currentChar;
                    nextChar();  // consume '||'
                }

                break;
            }


            default: {
                nextChar();  // consume bad character
                type = ERROR;
                value = INVALID_CHARACTER;
            }
        }

        // Set the type if it wasn't an error.
        if (type == null) {
            type = SPECIAL_SYMBOLS.get(text);
        }
    }
}
