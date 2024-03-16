package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.6
 * Time: 20.43
 * To change this template use File | Settings | File Templates.
 */
public class ProgramTACGenerator extends TACGenerator{

    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        LinkedList<SyntaxTreeItem> functions = getSyntaxTreeItemDescendantsList(syntaxTreeItem,TACGeneratorsMap.FUNCTION_RULE_NAME );
        TACGenerator functionTACGenerator = TACGeneratorsMap.getMap().get(TACGeneratorsMap.FUNCTION_RULE_NAME);
        for (SyntaxTreeItem function : functions) {
            functionTACGenerator.generateCode(function, indent);
        }
        SyntaxTreeItem mainTreeItem = getSyntaxTreeItemDescendant(syntaxTreeItem, TACGeneratorsMap.MAIN_RULE_NAME);
        TACGenerator mainTACGenerator = TACGeneratorsMap.getMap().get(TACGeneratorsMap.MAIN_RULE_NAME);
        mainTACGenerator.generateCode(mainTreeItem, indent);
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.PROGRAM_RULE_NAME;
    }

}
