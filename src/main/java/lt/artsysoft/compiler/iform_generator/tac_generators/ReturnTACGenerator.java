package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;
import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.34
 * To change this template use File | Settings | File Templates.
 */
public class ReturnTACGenerator extends TACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        ValueTACGenerator valueGenerator = (ValueTACGenerator) TACGeneratorsMap.getMap().get(TACGeneratorsMap.VALUE_NAME);
        VariableTypes variablesTypes;
        if (!getMethodName().equals("main"))  {
            variablesTypes = FunctionDefinitionsTable.getTable().get(getMethodName()).getReturnType();
            valueGenerator.setValueType(variablesTypes);
        }
        else variablesTypes = VariableTypes.BOOLEAN;
        valueGenerator.setMethodName(getMethodName());
        //generating assignment value in tmp value table block
        valueGenerator.generateCode(syntaxTreeItem.getDescendants().getLast(), indent);
        Variable result = valueGenerator.getValue();
        TACodeItem initLabel;

        if (result != null) {
            initLabel = new TACodeItem(TACOperations.RETURN, result, null, null , indent);
            initLabel.setPrefix(true);
            TACodeItemsList.getList().addLast(initLabel);
        }
        // function call
        else {
            Variable tmpVar = this.generateTmpVariable(variablesTypes);
            TACodeItem termCodeItem = TACodeItemsList.getList().getLast();
            termCodeItem.setResult(tmpVar);
            //TACodeItemsList.getList().add(termCodeItem);
            initLabel = new TACodeItem(TACOperations.RETURN, tmpVar, null, null , indent);
            initLabel.setPrefix(true);
            TACodeItemsList.getList().addLast(initLabel);
        }
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.RETURN_NAME;
    }
}
