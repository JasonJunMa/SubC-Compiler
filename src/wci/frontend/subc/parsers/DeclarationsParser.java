package wci.frontend.subc.parsers;

import java.util.EnumSet;

//import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import wci.frontend.*;
import wci.frontend.subc.*;
import wci.intermediate.*;

import static wci.frontend.subc.SubCTokenType.*;
import static wci.frontend.subc.SubCErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;

import wci.intermediate.SymTabEntry;
import wci.intermediate.symtabimpl.DefinitionImpl;

/**
 * <h1>DeclarationsParser</h1>
 *
 * <p>Parse SubC declarations.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class DeclarationsParser extends SubCParserTD {
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public DeclarationsParser(SubCParserTD parent) {
        super(parent);
    }

    static final EnumSet<SubCTokenType> DECLARATION_START_SET = EnumSet.of(CONST, TYPEDEF, INT, DOUBLE, FLOAT, CHAR,VOID,
            IDENTIFIER);

    static final EnumSet<SubCTokenType> TYPE_START_SET = DECLARATION_START_SET.clone();
    static {
        TYPE_START_SET.remove(CONST);
    }

    static final EnumSet<SubCTokenType> VAR_START_SET = TYPE_START_SET.clone();
    static {
        VAR_START_SET.remove(TYPEDEF);
    }

    static final EnumSet<SubCTokenType> ROUTINE_START_SET = VAR_START_SET.clone();
    static {

    }

    /**
     * Parse declarations.
     * To be overridden by the specialized declarations parser subclasses.
     * @param token the initial token.
     * @param parentId the symbol table entry of the parent routine's name.
     * @return null
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {

        do {
            token = synchronize(DECLARATION_START_SET);
            SymTabEntry varType = symTabStack.lookup(token.getText());
            if (token.getType() == CONST) {
                token = nextToken(); // consume CONST
                ConstantDefinitionsParser constantDefinitionsParser = new ConstantDefinitionsParser(this);
                constantDefinitionsParser.parse(token, parentId);
            } else if (VAR_START_SET.contains(token.getType()) && varType != null
                    && varType.getDefinition() == DefinitionImpl.TYPE) {
                VariableDeclarationsParser variableParser = new VariableDeclarationsParser(this);
                variableParser.setDefinition(VARIABLE);
                variableParser.parse(token, parentId);
            } else {
                token = synchronize(ROUTINE_START_SET);
                DeclaredRoutineParser routineParser = new DeclaredRoutineParser(this);
                routineParser.parse(token, parentId);
            }

            token = currentToken();
        } while (!(token instanceof EofToken));

        return null;
    }

    /**
    * Parse the type specification.
    * @param token the current token.
    * @return the type specification.
    * @throws Exception if an error occurs.
    */
    protected TypeSpec parseTypeSpec(Token token) throws Exception {
        // Parse the type specification.
        TypeSpecificationParser typeSpecificationParser = new TypeSpecificationParser(this);
        TypeSpec type = typeSpecificationParser.parse(token);

        return type;
    }
}
