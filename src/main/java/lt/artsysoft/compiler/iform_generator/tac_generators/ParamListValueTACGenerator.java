package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.34
 * To change this template use File | Settings | File Templates.
 */
public class ParamListValueTACGenerator extends TACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        LinkedList<SyntaxTreeItem> params = syntaxTreeItem.getDescendants();
        int localNumberOfParams = 0;
        for (int i = 0; i < params.size(); i=i+2) {
            localNumberOfParams++;
            SyntaxTreeItem param = params.get(i);
            ValueTACGenerator valueTACGenerator = (ValueTACGenerator) TACGeneratorsMap.getMap().get(TACGeneratorsMap.VALUE_NAME);
            valueTACGenerator.setMethodName(this.getMethodName());
            valueTACGenerator.generateCode(param, indent);
            Object value = valueTACGenerator.getValue();
            if (!VariablesTable.getTable().get(getMethodName()).containsKey(value.toString())) {
                Variable tmpVariable = generateTmpVariable(valueTACGenerator.getValueType());
                TACodeItem assignCodeItem = new TACodeItem(null, value, null, tmpVariable, indent);
                TACodeItemsList.getList().add(assignCodeItem);
                VariablesTable.getTable().get(getMethodName()).put(tmpVariable.getName(), tmpVariable);
                value = tmpVariable;
            }
            TACodeItem paramCodeItem = new TACodeItem(TACOperations.PARAM,value , null, null, indent);
            paramCodeItem.setPrefix(true);
            TACodeItemsList.getList().addLast(paramCodeItem);

        }
        numberOfParams = localNumberOfParams;
    }

    int numberOfParams;

    public int getNumberOfParams() {
        return numberOfParams;
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.PARAM_LIST_NAME;
    }
}
