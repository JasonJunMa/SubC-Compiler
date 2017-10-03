package wci.frontend.subc.tokens;

import wci.frontend.*;
import wci.frontend.subc.*;

import static wci.frontend.subc.SubCTokenType.*;

/**
 * <h1>PascalErrorToken</h1>
 *
 * <p>Pascal error token.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCErrorToken extends SubCToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch subsequent characters.
     * @param errorCode the error code.
     * @param tokenText the text of the erroneous token.
     * @throws Exception if an error occurred.
     */
    public SubCErrorToken(Source source, SubCErrorCode errorCode,
                          String tokenText)
        throws Exception
    {
            super(source);

            this.text = tokenText;
            this.type = ERROR;
            this.value = errorCode;
    }

    /**
     * Do nothing.  Do not consume any source characters.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
    }
}
