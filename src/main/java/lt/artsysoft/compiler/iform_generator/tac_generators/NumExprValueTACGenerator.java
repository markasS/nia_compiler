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
public class NumExprValueTACGenerator extends ValueTACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        SyntaxTreeItem firstTerm = syntaxTreeItem.getDescendants().getFirst();
        ValueTACGenerator termGenerator =initValueTACGenerator (TACGeneratorsMap.TERM_NAME);
        termGenerator.generateCode(firstTerm,indent);
        value = termGenerator.getValue();
        for (int i = 1; i < syntaxTreeItem.getDescendants().size(); i++) {
            Variable localValue = value;
            SyntaxTreeItem plusOrMinusItem = syntaxTreeItem.getDescendants().get(i);
            String plusOrMinus = plusOrMinusItem.getDescendants().getFirst().getLexemValue();
            TACOperations operation = (plusOrMinus.equals("+")) ? TACOperations.PLUS : TACOperations.MINUS;

            SyntaxTreeItem nextTermItem = plusOrMinusItem.getDescendants().get(1);
            termGenerator.generateCode(nextTermItem,indent);
            Variable result = termGenerator.getValue();

            Variable tmpValue = generateTmpVariable (getValueType());
            VariablesTable.getTable().get(tmpValue.getInFunction()).put(tmpValue.getName(),tmpValue);
            TACodeItem termCodeItem = new TACodeItem(operation, localValue, result, tmpValue, indent);
            TACodeItemsList.getList().add(termCodeItem);
            value = tmpValue;
        }

    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.NUM_EXPR_NAME;
    }
}
