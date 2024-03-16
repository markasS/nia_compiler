package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 15.58
 * To change this template use File | Settings | File Templates.
 */
public class FuncBodyTACGenerator extends TACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        TACodeItem beginFuncLabel = new TACodeItem(TACOperations.FUNC_BEGIN, null, null, null, indent);
        beginFuncLabel.setPrefix(true);
        TACodeItemsList.getList().addLast(beginFuncLabel);
        TACGenerator statementGenerator = TACGeneratorsMap.getMap().get(TACGeneratorsMap.STATEMENT_NAME);
        statementGenerator.setMethodName(this.getMethodName());
        for (SyntaxTreeItem statement : syntaxTreeItem.getDescendants()) {
            statementGenerator.generateCode(statement, indent);
        }
        TACodeItem endFuncLabel = new TACodeItem(TACOperations.FUNC_END, null, null, null, indent);
        endFuncLabel.setPrefix(true);
        TACodeItemsList.getList().addLast(endFuncLabel);
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.STATEMENT_NAME;
    }
}
