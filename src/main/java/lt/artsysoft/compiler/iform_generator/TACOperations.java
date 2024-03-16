package lt.artsysoft.compiler.iform_generator;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.58
 * To change this template use File | Settings | File Templates.
 */
public enum TACOperations {

    MAIN ("main:"),
    FUNC_BEGIN("BeginFunc;"),
    FUNC_END("EndFunc;"),
    CALL ("call"),
    PARAM ("param"),
    MULT ("*"),
    PLUS ("+"),
    MINUS ("-"),
    NOT ("not"),
    GREATER (">"),
    GREATER_EQ (">="),
    LESS ("<"),
    LESS_EQ ("<="),
    EQUAL ("=="),
    NOT_EQUAL ("!="),
    CONJUNCTION ("&&"),
    DISJUNCTION ("||"),
    LABEL (""),
    IF_ZERO ("IfZ"),
    GOTO ("Goto"),
    RETURN ("Return"),
    ;

    private String commandText;
    TACOperations(String commandText) {
        this.commandText = commandText;
    }
    public String getCommandText() {
        return commandText;
    }
}
