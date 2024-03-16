package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.34
 * To change this template use File | Settings | File Templates.
 */
public class InitializationTACGenerator extends TACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        SyntaxTreeItem declaration = syntaxTreeItem.getDescendants().getFirst();
        String variableName = declaration.getDescendants().getLast().getLexemValue();
        internalGenerateCode (syntaxTreeItem, indent, variableName);
    }

    protected void internalGenerateCode (SyntaxTreeItem syntaxTreeItem, int indent, String variableName) {
        Variable variableInTable = VariablesTable.getTable().get(getMethodName()).get(variableName);
        ValueTACGenerator valueGenerator = (ValueTACGenerator) TACGeneratorsMap.getMap().get(TACGeneratorsMap.VALUE_NAME);
        valueGenerator.setMethodName(getMethodName());
        valueGenerator.setValueType(variableInTable.getType());
        //generating assignment value in tmp value table block
        valueGenerator.generateCode(syntaxTreeItem.getDescendants().getLast(), indent);
        //last generated value in tmp block is assignment value
        Variable result = valueGenerator.getValue();
        TACodeItem initLabel;
        if (result != null) {
            variableInTable.setCurrentValue(result.getCurrentValue());
            initLabel = new TACodeItem(null, result, null, variableInTable , indent);
            TACodeItemsList.getList().addLast(initLabel);
        }
        // function call
        else {
            processFuncCall(variableInTable, false);
        }

    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.INITIALIZATION_NAME;
    }
}
