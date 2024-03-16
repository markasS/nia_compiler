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
public class ControlStatementTACGenerator extends TACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        SyntaxTreeItem specificStatement = syntaxTreeItem.getDescendants().getFirst();
        String statementType = specificStatement.getRule().getName();
        TACGenerator specificStatementGenerator = TACGeneratorsMap.getMap().get(statementType);
        specificStatementGenerator.setMethodName(this.getMethodName());
        specificStatementGenerator.generateCode(specificStatement,indent);
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.CONTROL_STATEMENT_NAME;
    }
}
