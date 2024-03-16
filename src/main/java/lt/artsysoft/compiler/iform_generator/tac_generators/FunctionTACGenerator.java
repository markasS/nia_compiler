package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.Function;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;
import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.6
 * Time: 21.26
 * To change this template use File | Settings | File Templates.
 */
public class FunctionTACGenerator extends TACGenerator {

    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {

        String functionName = syntaxTreeItem.getDescendants().getFirst().getDescendants().get(1).getLexemValue();
        TACodeItem funcLabel = generateLabel (functionName);
        Function function = FunctionDefinitionsTable.getTable().get(functionName);
        function.setBeginAddress(TACodeItemsList.getList().size());
        funcLabel.setPrefix(true);
        TACodeItemsList.getList().addLast(funcLabel);
        TACGenerator funcBodyGenerator = TACGeneratorsMap.getMap().get(TACGeneratorsMap.FUNCTION_BODY_NAME);
        funcBodyGenerator.setMethodName(functionName);
        SyntaxTreeItem funcBodyTreeItem = getSyntaxTreeItemDescendant(syntaxTreeItem, TACGeneratorsMap.FUNCTION_BODY_NAME);
        if (funcBodyTreeItem != null) {
            funcBodyGenerator.generateCode(funcBodyTreeItem, ++indent);
        }
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.MAIN_RULE_NAME;
    }
}
