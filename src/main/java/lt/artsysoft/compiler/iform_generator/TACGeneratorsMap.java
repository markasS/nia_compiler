package lt.artsysoft.compiler.iform_generator;

import java.util.HashMap;

import lt.artsysoft.compiler.iform_generator.tac_generators.TACGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.6
 * Time: 21.02
 * To change this template use File | Settings | File Templates.
 */
public class TACGeneratorsMap {

    public static final String PROGRAM_RULE_NAME = "program";
    public static final String MAIN_RULE_NAME = "main";
    public static final String FUNCTION_RULE_NAME = "function";
    public static final String FUNCTION_BODY_NAME = "func_body";
    public static final String STATEMENT_NAME = "statement";
    public static final String DECLARATION_NAME = "declaration";
    public static final String INITIALIZATION_NAME = "initialization";
    public static final String FUNC_CALL_NAME = "func_call";
    public static final String CONTROL_STATEMENT_NAME = "control_statement";
    public static final String ASSIGNMENT_NAME = "assignment";
    public static final String RETURN_NAME = "return";
    public static final String VALUE_NAME = "value";
    public static final String CONSTANT_NAME = "constant";
    public static final String NUM_EXPR_NAME = "numExpr";
    public static final String EXPRESSION_NAME = "expression";
    public static final String ID_NAME = "id";
    public static final String PARAM_LIST_NAME = "param_list";
    public static final String TERM_NAME = "term";
    public static final String VALUE_PAR_NAME = "value_par";
    public static final String VALUE_OR_NOT_NAME = "value_or_not";
    public static final String CONJ_OR_DISJ_NAME ="conj_or_disj";
    public static final String LOOP_NAME ="loop";
    public static final String CONDITION_NAME ="condition";
    public static final String IF_NAME ="if";
    public static final String ELSE_NAME ="else";

    private static HashMap<String, TACGenerator> taCodeGenerators = new HashMap<>();

    public static HashMap<String, TACGenerator> getMap() {
        if (taCodeGenerators == null) {
            taCodeGenerators = new HashMap<>();
        }

        return taCodeGenerators;
    }
}
