package wci.frontend.subc.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.subc.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;
//import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
//import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;


/**
 * <h1>VariableDeclarationsParser</h1>
 *
 * <p>Parse SubC variable declarations.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class VariableDeclarationsParser extends DeclarationsParser
{
    private Definition definition;  // how to define the identifier
    protected TypeSpec type;
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public VariableDeclarationsParser(SubCParserTD parent)
    {
        super(parent);
    }

    /**
     * Setter.
     * @param definition the definition to set.
     */
    protected void setDefinition(Definition definition)
    {
        this.definition = definition;
    }

    // Synchronization set for a variable identifier.
    static final EnumSet<SubCTokenType> IDENTIFIER_SET =
        DeclarationsParser.VAR_START_SET.clone();
    static {
        IDENTIFIER_SET.add(IDENTIFIER);
        IDENTIFIER_SET.add(SEMICOLON);
    }

    // Synchronization set for the start of the next definition or declaration.
    static final EnumSet<SubCTokenType> NEXT_START_SET =
        DeclarationsParser.ROUTINE_START_SET.clone();
    static {
        NEXT_START_SET.add(IDENTIFIER);
        NEXT_START_SET.add(SEMICOLON);
    }

    /**
     * Parse variable declarations.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
    public void parse(Token token)
        throws Exception
    {
        //ICodeNode variableDeclarationNode = ICodeFactory.createICodeNode(VARIABLE_DECLARE);
        // Parse the type specification.
        type = parseTypeSpec(token);
        token = synchronize(IDENTIFIER_SET);

        // Loop to parse a sequence of variable declarations
        // separated by semicolons.
        if (token.getType() == IDENTIFIER) {

            // Parse the identifier sublist and its type specification.
            parseIdentifierSublist(token);

            token = currentToken();
            TokenType tokenType = token.getType();

            // Look for one or more semicolons after a definition.
            if (tokenType == SEMICOLON) {
                token = nextToken();  // consume the ;
            }

            // If at the start of the next definition or declaration,
            // then missing a semicolon.
            else if (NEXT_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_SEMICOLON, this);
            }

            //token = synchronize(IDENTIFIER_SET);
        }
        //return variableDeclarationNode;
    }

    // Synchronization set to start a sublist identifier.
    static final EnumSet<SubCTokenType> IDENTIFIER_START_SET =
        EnumSet.of(IDENTIFIER, COMMA);

    // Synchronization set to follow a sublist identifier.
    private static final EnumSet<SubCTokenType> IDENTIFIER_FOLLOW_SET =
        EnumSet.of(COLON, SEMICOLON);
    static {
        IDENTIFIER_FOLLOW_SET.addAll(DeclarationsParser.VAR_START_SET);
    }

    // Synchronization set for the , token.
    private static final EnumSet<SubCTokenType> COMMA_SET =
        EnumSet.of(COMMA, COLON, IDENTIFIER, SEMICOLON);

    /**
     * Parse a sublist of identifiers and their type specification.
     * @param token the current token.
     * @return the sublist of identifiers in a declaration.
     * @throws Exception if an error occurred.
     */
    protected ArrayList<SymTabEntry> parseIdentifierSublist(Token token)
        throws Exception
    {
        ArrayList<SymTabEntry> sublist = new ArrayList<SymTabEntry>();

        do {
            token = synchronize(IDENTIFIER_START_SET);
            SymTabEntry id = parseIdentifier(token);

            if (id != null) {
                sublist.add(id);
            }

            token = synchronize(COMMA_SET);
            TokenType tokenType = token.getType();

            // Look for the comma.
            if (tokenType == COMMA) {
                token = nextToken();  // consume the comma

                if (IDENTIFIER_FOLLOW_SET.contains(token.getType())) {
                    errorHandler.flag(token, MISSING_IDENTIFIER, this);
                }
            }
            else if (IDENTIFIER_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_COMMA, this);
            }
        } while (!IDENTIFIER_FOLLOW_SET.contains(token.getType()));

        // Assign the type specification to each identifier in the list.
        for (SymTabEntry variableId : sublist) {
            variableId.setTypeSpec(type);
        }

        return sublist;
    }

    /**
     * Parse an identifier.
     * @param token the current token.
     * @return the symbol table entry of the identifier.
     * @throws Exception if an error occurred.
     */
    private SymTabEntry parseIdentifier(Token token)
        throws Exception
    {
        SymTabEntry id = null;

        if (token.getType() == IDENTIFIER) {
            String name = token.getText().toLowerCase();
            id = symTabStack.lookupLocal(name);

            // Enter a new identifier into the symbol table.
            if (id == null) {
                id = symTabStack.enterLocal(name);
                id.setDefinition(definition);
                id.appendLineNumber(token.getLineNumber());
            }
            else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }


            token = nextToken();   // consume the identifier token
            if (token.getType() == EQUALS) {
                token = nextToken();  // consume the =
                // Parse the constant value.
                Object value = parseConstant(token);
                id.setAttribute(CONSTANT_VALUE, value);
            }
            else{
                id.setAttribute(CONSTANT_VALUE, 0);
            }

        }
        else {
            errorHandler.flag(token, MISSING_IDENTIFIER, this);
        }

        return id;
    }

    // Synchronization set for starting a constant.
    static final EnumSet<SubCTokenType> CONSTANT_START_SET =
        EnumSet.of(IDENTIFIER, INTEGER, REAL, PLUS, MINUS, STRING, SEMICOLON, COMMA);
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
            if(type.getIdentifier().getName()!="integer"){
                errorHandler.flag(token, INCOMPATIBLE_ASSIGNMENT, this);
            }
            Integer value = (Integer) token.getValue();
            nextToken();  // consume the number
            return sign == MINUS ? -value : value;
        }

        case REAL: {
            if(type.getIdentifier().getName()!="real"){
                errorHandler.flag(token, INCOMPATIBLE_ASSIGNMENT, this);
            }
            Float value = (Float) token.getValue();
            nextToken();  // consume the number
            return sign == MINUS ? -value : value;
        }

        case STRING: {
            if(type.getIdentifier().getName()!="char"){
                errorHandler.flag(token, INCOMPATIBLE_ASSIGNMENT, this);
            }
            if (sign != null) {
                errorHandler.flag(token, INVALID_CONSTANT, this);
            }

            nextToken();  // consume the string
            return (String) token.getValue();
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




    // Synchronization set for the : token.
    private static final EnumSet<SubCTokenType> COLON_SET =
        EnumSet.of(COLON, SEMICOLON);

    /**
     * Parse the type specification.
     * @param token the current token.
     * @return the type specification.
     * @throws Exception if an error occurs.
     */
    protected TypeSpec parseTypeSpec(Token token)
        throws Exception
    {
        // Synchronize on the : token.
        // token = synchronize(COLON_SET);
        // if (token.getType() == COLON) {
        //     token = nextToken(); // consume the :
        // }
        // else {
        //     errorHandler.flag(token, MISSING_COLON, this);
        // }

        // Parse the type specification.
        TypeSpecificationParser typeSpecificationParser =
            new TypeSpecificationParser(this);
        TypeSpec type = typeSpecificationParser.parse(token);

        return type;
    }
}
