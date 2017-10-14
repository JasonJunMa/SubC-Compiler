package wci.frontend.subc;

import java.util.Hashtable;
import java.util.HashSet;

import wci.frontend.TokenType;

/**
 * <h1>PascalTokenType</h1>
 *
 * <p>Pascal token types.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum SubCTokenType implements TokenType
{
        // 32 Reserved words.
        AUTO,DOUBLE,INT,STRUCT,BREAK,ELSE,LONG,SWITCH,
        CASE,ENUM,REGISTER,TYPEDEF,CHAR,EXTERN,RETURN,UNION,
        CONST,FLOAT,SHORT,UNSIGNED,CONTINUE,FOR,SIGNED,VOID,
        DEFAULT,GOTO,SIZEOF,VOLATILE,DO,IF,STATIC,WHILE,

        // Special symbols.
        PLUS("+"),PLUS_EQUAL("+="), INCREMENT("++"),MINUS("-"), MINUS_EQUAL("-="),DECREMENT("--"), STAR("*"), STAR_EAUQL("*="),
        SLASH("/"),SLASH_EQUAL("/="),ASSIGN("="),EQUALS("=="),NOT("!"),NOT_EQUALS("!="),
        LESS_THAN("<"), LESS_EQUALS("<="),GREATER_EQUALS(">="), GREATER_THAN(">"),AMPERSAND("&"),AND("&&"),
        VERTICAL_BAR("|"),OR("||"),MOD("%"),MOD_EQUALS("%="),BACKSLASH("\\"),
        DOT("."), COMMA(","), SEMICOLON(";"), COLON(":"), QUOTE("'"),DOUBLE_QUOTE("\""),NUMBER_SIGN("#"),
        TILDE("~"),LEFT_PAREN("("), RIGHT_PAREN(")"),QUESTION("?"),
        LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_BRACE("{"), RIGHT_BRACE("}"),UNDERSCORE("_"),APOSTROPHE("`"),UP_ARROW("^"),

        IDENTIFIER, INTEGER, REAL, STRING,
        ERROR, END_OF_FILE;

        private static final int FIRST_RESERVED_INDEX = AUTO.ordinal();
        private static final int LAST_RESERVED_INDEX  = WHILE.ordinal();

        private static final int FIRST_SPECIAL_INDEX = PLUS.ordinal();
        private static final int LAST_SPECIAL_INDEX  = UP_ARROW.ordinal();

        private String text; // token text

        /**
         * Constructor.
         */
        SubCTokenType()
        {
                this.text = this.toString().toLowerCase();
        }

        /**
         * Constructor.
         * @param text the token text.
         */
        SubCTokenType(String text)
        {
                this.text = text;
        }

        /**
         * Getter.
         * @return the token text.
         */
        public String getText()
        {
                return text;
        }

        // Set of lower-cased Pascal reserved word text strings.
        public static HashSet<String> RESERVED_WORDS = new HashSet<String>();
        static {
                SubCTokenType values[] = SubCTokenType.values();
                for (int i = FIRST_RESERVED_INDEX; i <= LAST_RESERVED_INDEX; ++i) {
                        RESERVED_WORDS.add(values[i].getText().toLowerCase());
                }
        }

        // Hash table of Pascal special symbols.  Each special symbol's text
        // is the key to its Pascal token type.
        public static Hashtable<String, SubCTokenType> SPECIAL_SYMBOLS =
                new Hashtable<String, SubCTokenType>();
        static {
                SubCTokenType values[] = SubCTokenType.values();
                for (int i = FIRST_SPECIAL_INDEX; i <= LAST_SPECIAL_INDEX; ++i) {
                        SPECIAL_SYMBOLS.put(values[i].getText(), values[i]);
                }
        }
}
