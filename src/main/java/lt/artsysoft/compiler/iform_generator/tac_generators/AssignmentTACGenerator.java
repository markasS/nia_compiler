package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.34
 * To change this template use File | Settings | File Templates.
 */
public class AssignmentTACGenerator extends InitializationTACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        String variableName = syntaxTreeItem.getDescendants().getFirst().getLexemValue();
        internalGenerateCode(syntaxTreeItem, indent, variableName);
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.ASSIGNMENT_NAME;
    }
}
