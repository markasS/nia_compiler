package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.Function;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;
import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.34
 * To change this template use File | Settings | File Templates.
 */
public class FuncCallTACGenerator extends ValueTACGenerator {

    private static final String READ_FUNC_NAME = "ЧИТАТЬ";

    private static final String WRITE_FUNC_NAME = "ПИСАТЬ";
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        SyntaxTreeItem paramList = syntaxTreeItem.getDescendants().get(2);
        ParamListValueTACGenerator paramListValueGenerator =  (ParamListValueTACGenerator) TACGeneratorsMap.getMap().get(TACGeneratorsMap.PARAM_LIST_NAME);
        paramListValueGenerator.setMethodName(this.getMethodName());
        paramListValueGenerator.generateCode(paramList, indent);

        //generating function call (after parameters generation
        String functionName = syntaxTreeItem.getDescendants().getFirst().getLexemValue();
        //Function functionInTable = FunctionDefinitionsTable.getTable().get(functionName);
        //function without assignment
        TACodeItem funcCallInvocation = new TACodeItem(TACOperations.CALL, functionName, paramListValueGenerator.numberOfParams,
                    null, indent);
        funcCallInvocation.setPrefix(true);
        TACodeItemsList.getList().addLast(funcCallInvocation);
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.FUNC_CALL_NAME;
    }
}
