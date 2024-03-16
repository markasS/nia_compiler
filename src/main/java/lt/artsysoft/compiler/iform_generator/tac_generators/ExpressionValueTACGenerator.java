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
public class ExpressionValueTACGenerator extends ValueTACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        //one value_or_not tree item processed
        SyntaxTreeItem specificValue = syntaxTreeItem.getDescendants().getFirst();
        String valueType = specificValue.getRule().getName();
        ValueTACGenerator specificValueGenerator = (ValueTACGenerator) TACGeneratorsMap.getMap().get(valueType);
        generateCodeForValue (specificValueGenerator, indent, specificValue);
        value = specificValueGenerator.getValue();

        for (int i=1; i < syntaxTreeItem.getDescendants().size(); i++) {
            //generate operand and second parameter
            SyntaxTreeItem conjOrDisjTreeItem = syntaxTreeItem.getDescendants().get(i);
            String conjOrDisjOperation = conjOrDisjTreeItem.getDescendants().getFirst().getLexemValue();
            TACOperations operation = (conjOrDisjOperation.equals("&&")) ? TACOperations.CONJUNCTION : TACOperations.DISJUNCTION;
            ValueTACGenerator conjOrDisjTACGenerator = initValueTACGenerator(TACGeneratorsMap.CONJ_OR_DISJ_NAME);
            conjOrDisjTACGenerator.generateCode(conjOrDisjTreeItem, indent);
            Variable result =  conjOrDisjTACGenerator.getValue();

            //save to tam variable;
            Variable tmpVariable = generateTmpVariable(getValueType());
            TACodeItem termCodeItem = new TACodeItem(operation, value, result, tmpVariable, indent);
            TACodeItemsList.getList().add(termCodeItem);
            VariablesTable.getTable().get(getMethodName()).put(tmpVariable.getName(), tmpVariable);
            value = tmpVariable;
        }
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.EXPRESSION_NAME;
    }
}
