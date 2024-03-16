package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.34
 * To change this template use File | Settings | File Templates.
 */
public class ValueOrNotValueTACGenerator extends ValueTACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        LinkedList<SyntaxTreeItem> descendants = syntaxTreeItem.getDescendants();
        SyntaxTreeItem firstElement = descendants.getFirst();
        ValueTACGenerator valueParTACGenerator = initValueTACGenerator(TACGeneratorsMap.VALUE_PAR_NAME);
        //negation !
        if (firstElement.getLexemValue() != null && firstElement.getLexemValue().equals("!")) {
            SyntaxTreeItem secondElement = descendants.get(1);
            valueParTACGenerator.generateCode(secondElement, indent);
            Variable resultValuePar = valueParTACGenerator.getValue();
            Variable tmpVariable = generateTmpVariable(getValueType());
            TACodeItem termCodeItem = new TACodeItem(TACOperations.NOT, resultValuePar, null , tmpVariable, indent);
            termCodeItem.setPrefix(true);
            TACodeItemsList.getList().add(termCodeItem);
            VariablesTable.getTable().get(getMethodName()).put(tmpVariable.getName(), tmpVariable);
            value = tmpVariable;
        }
        //comparison <, >, <=, !=
        else if (descendants.size() == 1) {
            valueParTACGenerator.generateCode(firstElement, indent);
            value = valueParTACGenerator.getValue();
        }
        else {
            int firstOpIndex = 1;
            int secondOpIndex = 2;
            if (firstElement.getLexemValue() != null && firstElement.getLexemValue().equals("(")) {
                firstOpIndex++; secondOpIndex++;
                firstElement =  descendants.get(1);
            }
            String operationItem = descendants.get(firstOpIndex).getDescendants().getFirst().getLexemValue();
            SyntaxTreeItem secondOperand = descendants.get(secondOpIndex);
            valueParTACGenerator.generateCode(firstElement, indent);
            Variable resultValuePar1 = valueParTACGenerator.getValue();
            valueParTACGenerator.generateCode(secondOperand, indent);
            Variable resultValuePar2 = valueParTACGenerator.getValue();
            Variable tmpVariable = generateTmpVariable(getValueType());
            TACOperations comparison = getComparisonOperation(operationItem);
            TACodeItem termCodeItem = new TACodeItem(comparison, resultValuePar1, resultValuePar2, tmpVariable, indent);
            TACodeItemsList.getList().add(termCodeItem);
            VariablesTable.getTable().get(getMethodName()).put(tmpVariable.getName(), tmpVariable);
            value = tmpVariable;
        }
    }

    private TACOperations getComparisonOperation (String comparison) {
        TACOperations result = null;
        switch (comparison) {
            case ">" :
                result = TACOperations.GREATER;
                break;
            case ">=":
                result =  TACOperations.GREATER_EQ;
                break;
            case "<":
                result =  TACOperations.LESS;
                break;
            case "<=":
                result =  TACOperations.LESS_EQ;
                break;
            case "!=":
                result =  TACOperations.NOT_EQUAL;
                break;
            case "==":
                result =  TACOperations.EQUAL;
                break;
        }
        return result;
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.VALUE_OR_NOT_NAME;
    }
}
