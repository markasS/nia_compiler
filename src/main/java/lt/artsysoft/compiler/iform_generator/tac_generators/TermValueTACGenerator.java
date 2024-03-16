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
public class TermValueTACGenerator extends ValueTACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        SyntaxTreeItem parValue = syntaxTreeItem.getDescendants().getFirst();
        ValueTACGenerator parValueGenerator = initValueTACGenerator(TACGeneratorsMap.VALUE_PAR_NAME);
        parValueGenerator.generateCode(parValue, indent);
        Variable valueParResult = parValueGenerator.getValue();

        value = valueParResult;
        for (int i=2; i < syntaxTreeItem.getDescendants().size(); i=i+2 ) {
            Variable localTerm = value;
            SyntaxTreeItem descendant = syntaxTreeItem.getDescendants().get(i);
            parValueGenerator.generateCode(descendant, indent);
            Variable result = parValueGenerator.getValue();
            // t3 = t1*t2 or a*t1 (if t1 is function)
            Variable tmpVariable = generateTmpVariable(getValueType());
            TACodeItem termCodeItem = new TACodeItem(TACOperations.MULT, localTerm, result, tmpVariable, indent);
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
