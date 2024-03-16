package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
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
public class ValueParValueTACGenerator extends ValueTACGenerator {


    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        value = null;
        SyntaxTreeItem parValue = null;
        boolean negativeResult = false;
        ValueTACGenerator specificValueGenerator;
        String valueType;
        String firstLexemValue;
        if (syntaxTreeItem.getLexemValue() != null)
            firstLexemValue= syntaxTreeItem.getLexemValue();
        else firstLexemValue = syntaxTreeItem.getDescendants().getFirst().getLexemValue();
        if (firstLexemValue != null) {
            if (firstLexemValue.equals("(")) {
                parValue = syntaxTreeItem.getDescendants().get(1);
                if (parValue.getLexemValue() != null) {
                    if (parValue.getLexemValue().equals("-")) {
                         parValue = syntaxTreeItem.getDescendants().get(2);
                         negativeResult = true;
                    }
                else value = new Variable(getValueType(),parValue.getLexemValue(), null, getMethodName()); //identifier  in braces
                }
            }
            else {
                value = new Variable(getValueType(),firstLexemValue, null, getMethodName()); //identifier  without braces
            }
        }
        else {
            if (syntaxTreeItem.getLexemValue() != null)
                parValue= syntaxTreeItem;
            else parValue = syntaxTreeItem.getDescendants().getFirst();
        }
         //constant
        if (value == null) {
            valueType = parValue.getRule().getName();
            specificValueGenerator = (ValueTACGenerator) TACGeneratorsMap.getMap().get(valueType);
            generateCodeForValue(specificValueGenerator, indent, parValue);
            value = specificValueGenerator.getValue();
        }
        //if function call occurs
        if (value == null)  {
            Variable tmpVariable = generateTmpVariable(getValueType());
            value = processFuncCall(tmpVariable, true);
        }
        if (negativeResult)
            value =negativeResult();
    }

    private Variable negativeResult() {
          Variable tmpVariable = generateTmpVariable(getValueType());
          Variable minus = new Variable(this.getValueType(),"-1", "-1", null);
          TACodeItem termCodeItem = new TACodeItem(TACOperations.MULT, minus, value, tmpVariable, 1);
          TACodeItemsList.getList().add(termCodeItem);
          VariablesTable.getTable().get(getMethodName()).put(tmpVariable.getName(), tmpVariable);
          return tmpVariable;
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.VALUE_PAR_NAME;
    }
}
