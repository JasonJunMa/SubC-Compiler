package wci.frontend.subc.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.subc.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;
//import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
//import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>ConstantDefinitionsParser</h1>
 *
 * <p>Parse SubC constant definitions.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ConstantDefinitionsParser extends DeclarationsParser
{
    protected TypeSpec constantType;
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public ConstantDefinitionsParser(SubCParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for a constant identifier.
    private static final EnumSet<SubCTokenType> IDENTIFIER_SET =
        DeclarationsParser.TYPE_START_SET.clone();
    static {
        IDENTIFIER_SET.add(IDENTIFIER);
        IDENTIFIER_SET.add(SEMICOLON);
    }

    // Synchronization set for starting a constant.
    static final EnumSet<SubCTokenType> CONSTANT_START_SET =
        EnumSet.of(IDENTIFIER, INTEGER, REAL, PLUS, MINUS, STRING, SEMICOLON, COMMA);

    // Synchronization set for the = token.
    private static final EnumSet<SubCTokenType> EQUALS_SET =
        CONSTANT_START_SET.clone();
    static {
        EQUALS_SET.add(INT);
        EQUALS_SET.add(DOUBLE);
        EQUALS_SET.add(FLOAT);
        EQUALS_SET.add(CHAR);
        EQUALS_SET.add(EQUALS);
        EQUALS_SET.add(SEMICOLON);
    }

    // Synchronization set for the start of the next definition or declaration.
    private static final EnumSet<SubCTokenType> NEXT_START_SET =
        DeclarationsParser.TYPE_START_SET.clone();
    static {
        //NEXT_START_SET.add(SEMICOLON);
        NEXT_START_SET.add(IDENTIFIER);
    }

    /**
     * Parse constant definitions.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
    public void parse(Token token)
        throws Exception
    {
        //ICodeNode constantNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
        constantType =parseTypeSpec(token);
        if(constantType==null){
            constantType = Predefined.integerType;
        }

        token = synchronize(IDENTIFIER_SET);

        // Loop to parse a sequence of constant definitions
        // separated by comma.
        while (token.getType() == IDENTIFIER) {
            String name = token.getText().toLowerCase();
            SymTabEntry constantId = symTabStack.lookupLocal(name);


            // Enter the new identifier into the symbol table
            // but don't set how it's defined yet.
            if (constantId == null) {
                constantId = symTabStack.enterLocal(name);
                constantId.appendLineNumber(token.getLineNumber());
            }
            else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
                constantId = null;
            }


            token = nextToken();  // consume the identifier token
            token = synchronize(EQUALS_SET);
            if (token.getType() == EQUALS) {
                token = nextToken();  // consume the =
            }
            else if(DECLARATION_START_SET.contains(token.getType())){
                // Parse the constant value.
                //Object value = parseConstant(token);

                // Set identifier to be a constant and set its value.
                if (constantId != null) {
                    constantId.setDefinition(CONSTANT);
                    constantId.setAttribute(CONSTANT_VALUE, 0);
                    constantId.setTypeSpec(constantType);
                 }
                break;
            }

            // Parse the constant value.
            Object value = parseConstant(token);

            // Set identifier to be a constant and set its value.
            if (constantId != null) {
                constantId.setDefinition(CONSTANT);
                constantId.setAttribute(CONSTANT_VALUE, value);
                constantId.setTypeSpec(constantType);
            }

            token = currentToken();
            TokenType tokenType = token.getType();

            // Look for one comma after a definition.
            if (tokenType == COMMA) {
                token = nextToken();  // consume the ,
            }
            else if(NEXT_START_SET.contains(tokenType)){
                errorHandler.flag(token, MISSING_COMMA, this);
            }
            token = synchronize(IDENTIFIER_SET);
        }

        if(token.getType() != SEMICOLON)
            errorHandler.flag(token, MISSING_SEMICOLON, this);
        else
            nextToken();

        //return constantNode;
    }


    /**
     * Parse a constant value.
     * @param token the current token.
     * @return the constant value.
     * @throws Exception if an error occurred.
     */
    protected Object parseConstant(Token token)
        throws Exception
    {
        TokenType sign = null;

        // Synchronize at the start of a constant.
        token = synchronize(CONSTANT_START_SET);
        TokenType tokenType = token.getType();

        // Plus or minus sign?
        if ((tokenType == PLUS) || (tokenType == MINUS)) {
            sign = tokenType;
            token = nextToken();  // consume sign
        }

        // Parse the constant.
        switch ((SubCTokenType) token.getType()) {

            case IDENTIFIER: {
                return parseIdentifierConstant(token, sign);
            }

            case INTEGER: {
                Integer value = (Integer) token.getValue();
                nextToken();  // consume the number
                return sign == MINUS ? -value : value;
            }

            case REAL: {
                Float value = (Float) token.getValue();
                nextToken();  // consume the number
                return sign == MINUS ? -value : value;
            }

            case STRING: {
                if (sign != null) {
                    errorHandler.flag(token, INVALID_CONSTANT, this);
                }

                nextToken();  // consume the string
                return (String) token.getValue();
            }

            case COMMA:

            case SEMICOLON: {
                if(constantType == Predefined.integerType){
                    return (int)0;
                }
                else if(constantType == Predefined.realType){
                    return (float)0;
                }
                else if(constantType == Predefined.charType){
                    return null;
                }
            }

            default: {
                errorHandler.flag(token, INVALID_CONSTANT, this);
                return null;
            }
        }
    }

    /**
     * Parse an identifier constant.
     * @param token the current token.
     * @param sign the sign, if any.
     * @return the constant value.
     * @throws Exception if an error occurred.
     */
    protected Object parseIdentifierConstant(Token token, TokenType sign)
        throws Exception
    {
        String name = token.getText().toLowerCase();
        SymTabEntry id = symTabStack.lookup(name);

        nextToken();  // consume the identifier

        // The identifier must have already been defined
        // as an constant identifier.
        if (id == null) {
            errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
            return null;
        }

        Definition definition = id.getDefinition();

        if (definition == CONSTANT) {
            Object value = id.getAttribute(CONSTANT_VALUE);
            id.appendLineNumber(token.getLineNumber());

            if (value instanceof Integer) {
                return sign == MINUS ? -((Integer) value) : value;
            }
            else if (value instanceof Float) {
                return sign == MINUS ? -((Float) value) : value;
            }
            else if (value instanceof String) {
                if (sign != null) {
                    errorHandler.flag(token, INVALID_CONSTANT, this);
                }

                return value;
            }
            else {
                return null;
            }
        }
        else if (definition == ENUMERATION_CONSTANT) {
            Object value = id.getAttribute(CONSTANT_VALUE);
            id.appendLineNumber(token.getLineNumber());

            if (sign != null) {
                errorHandler.flag(token, INVALID_CONSTANT, this);
            }

            return value;
        }
        else if (definition == null) {
            errorHandler.flag(token, NOT_CONSTANT_IDENTIFIER, this);
            return null;
        }
        else {
            errorHandler.flag(token, INVALID_CONSTANT, this);
            return null;
        }
    }

    /**
     * Parse the type specification.
     * @param token the current token.
     * @return the type specification.
     * @throws Exception if an error occurs.
     */
    protected TypeSpec parseTypeSpec(Token token)
        throws Exception
    {
        // Parse the type specification.
        TypeSpecificationParser typeSpecificationParser =
            new TypeSpecificationParser(this);
        TypeSpec type = typeSpecificationParser.parse(token);

        return type;
    }


    /**
     * Return the type of a constant given its value.
     * @param value the constant value
     * @return the type specification.
     */
    protected TypeSpec getConstantType(Object value)
    {
        TypeSpec constantType = null;

        if (value instanceof Integer) {
            constantType = Predefined.integerType;
        }
        else if (value instanceof Float) {
            constantType = Predefined.realType;
        }
        else if (value instanceof String) {
            if (((String) value).length() == 1) {
                constantType = Predefined.charType;
            }
            else {
                constantType = TypeFactory.createStringType((String) value);
            }
        }

        return constantType;
    }

    /**
     * Return the type of a constant given its identifier.
     * @param identifier the constant's identifier.
     * @return the type specification.
     */
    protected TypeSpec getConstantType(Token identifier)
    {
        String name = identifier.getText().toLowerCase();
        SymTabEntry id = symTabStack.lookup(name);

        if (id == null) {
            return null;
        }

        Definition definition = id.getDefinition();

        if ((definition == CONSTANT) || (definition == ENUMERATION_CONSTANT)) {
            return id.getTypeSpec();
        }
        else {
            return null;
        }
    }
}
