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
public class ConditionTACGenerator extends TACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        //generate if
        IfTACGenerator ifTacGenerator =  (IfTACGenerator) TACGeneratorsMap.getMap().get(TACGeneratorsMap.IF_NAME);
        ifTacGenerator.setMethodName(getMethodName());
        SyntaxTreeItem ifItem = syntaxTreeItem.getDescendants().getFirst();
        ifTacGenerator.generateCode(ifItem, indent);

        if (syntaxTreeItem.getDescendants().size() > 1) {
            ElseTACGenerator elseTacGenerator = (ElseTACGenerator) TACGeneratorsMap.getMap().get(TACGeneratorsMap.ELSE_NAME);
            elseTacGenerator.setMethodName(getMethodName());
            elseTacGenerator.setEndItem(ifTacGenerator.getEndItem());
            for (int i = 1; i < syntaxTreeItem.getDescendants().size(); i++) {
                SyntaxTreeItem elseTreeItem = syntaxTreeItem.getDescendants().get(i);
                elseTacGenerator.generateCode(elseTreeItem, indent);
            }
        }
        else {
            TACodeItemsList.getList().addLast(ifTacGenerator.getEndItem());
            VariablesTable.getLabelsTable().put(ifTacGenerator.getEndItem().getOperand1().toString(),TACodeItemsList.getList().size()-1 );
        }
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.CONDITION_NAME;
    }
}
