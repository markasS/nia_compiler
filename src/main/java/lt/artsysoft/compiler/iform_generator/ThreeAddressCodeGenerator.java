package lt.artsysoft.compiler.iform_generator;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.iform_generator.tac_generators.*;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.6
 * Time: 20.08
 * To change this template use File | Settings | File Templates.
 */
public class ThreeAddressCodeGenerator {

    public static void init(SyntaxTreeItem rootSyntaxTreeItem) {
        HashMap<String,TACGenerator>  tacGeneratorsMap = TACGeneratorsMap.getMap();
        TACGenerator programTACGenerator = new ProgramTACGenerator();
        tacGeneratorsMap.put(TACGeneratorsMap.PROGRAM_RULE_NAME, programTACGenerator);
        tacGeneratorsMap.put(TACGeneratorsMap.MAIN_RULE_NAME, new MainTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.FUNCTION_BODY_NAME, new FuncBodyTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.STATEMENT_NAME, new StatementTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.RETURN_NAME, new ReturnTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.FUNC_CALL_NAME, new FuncCallTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.INITIALIZATION_NAME, new InitializationTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.DECLARATION_NAME, new DeclarationTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.ASSIGNMENT_NAME, new AssignmentTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.NUM_EXPR_NAME, new NumExprValueTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.CONTROL_STATEMENT_NAME, new ControlStatementTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.VALUE_NAME, new ValueTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.CONSTANT_NAME, new ConstantValueTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.VALUE_PAR_NAME, new ValueParValueTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.PARAM_LIST_NAME, new ParamListValueTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.TERM_NAME, new TermValueTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.VALUE_OR_NOT_NAME, new ValueOrNotValueTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.CONJ_OR_DISJ_NAME, new ConjOrDisjValueTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.EXPRESSION_NAME, new ExpressionValueTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.LOOP_NAME, new LoopTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.IF_NAME, new IfTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.CONDITION_NAME, new ConditionTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.ELSE_NAME, new ElseTACGenerator());
        tacGeneratorsMap.put(TACGeneratorsMap.FUNCTION_RULE_NAME, new FunctionTACGenerator());
        programTACGenerator.generateCode(rootSyntaxTreeItem, 0);
    }

    public static void print() {
        for (TACodeItem taCodeItem : TACodeItemsList.getList()) {
            System.out.println(taCodeItem.toString());
        }
    }

}
