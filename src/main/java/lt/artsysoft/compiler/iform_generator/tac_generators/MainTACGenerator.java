package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.6
 * Time: 21.26
 * To change this template use File | Settings | File Templates.
 */
public class MainTACGenerator extends TACGenerator {

    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        TACodeItem mainLabel = new TACodeItem(TACOperations.MAIN, null, null, null, indent);
        mainLabel.setPrefix(true);
        TACodeItemsList.getList().addLast(mainLabel);
        TACGenerator funcBodyGenerator = TACGeneratorsMap.getMap().get(TACGeneratorsMap.FUNCTION_BODY_NAME);
        funcBodyGenerator.setMethodName("main");
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
