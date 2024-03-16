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
public class ElseTACGenerator extends TACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        //GOTO L1;
        TACodeItem elseEndItem = generateLabel();
        String gotoValue = elseEndItem.getOperand1().toString();
        TACodeItem gotoCodeItem = new TACodeItem(TACOperations.GOTO, gotoValue.substring(0,gotoValue.length()-1), null, null, indent);
        gotoCodeItem.setPrefix(true);
        TACodeItemsList.getList().addLast(gotoCodeItem);
        VariablesTable.getLabelsTable().put(elseEndItem.getOperand1().toString(),TACodeItemsList.getList().size()-1 );
        //L0:
        TACodeItemsList.getList().addLast(endItem);
        VariablesTable.getLabelsTable().put(endItem.getOperand1().toString(),TACodeItemsList.getList().size()-1 );

        //repeating statements  - last else
        if (syntaxTreeItem.getDescendants().get(1).getLexemValue() != null) {
            //generate statements
            TACGenerator statementGenerator = TACGeneratorsMap.getMap().get(TACGeneratorsMap.STATEMENT_NAME);
            statementGenerator.setMethodName(this.getMethodName());
            for ( int i = 2; i < syntaxTreeItem.getDescendants().size()-1; i++) {
                SyntaxTreeItem statement = syntaxTreeItem.getDescendants().get(i);
                statementGenerator.generateCode(statement, indent);
            }
        }
        // if else
        else {
            IfTACGenerator ifTacGenerator =  (IfTACGenerator) TACGeneratorsMap.getMap().get(TACGeneratorsMap.IF_NAME);
            ifTacGenerator.setMethodName(getMethodName());
            SyntaxTreeItem ifItem = syntaxTreeItem.getDescendants().get(1);
            ifTacGenerator.setEndItem(elseEndItem);
            ifTacGenerator.generateCode(ifItem, indent);
            endItem = ifTacGenerator.getEndItem();
            TACodeItemsList.getList().addLast(endItem);
            VariablesTable.getLabelsTable().put(endItem.getOperand1().toString(),TACodeItemsList.getList().size()-1 );
        }
        TACodeItemsList.getList().addLast(elseEndItem);
        VariablesTable.getLabelsTable().put(elseEndItem.getOperand1().toString(),TACodeItemsList.getList().size()-1 );
    }

    private TACodeItem endItem;

    public void setEndItem(TACodeItem endItem) {
        this.endItem = endItem;
    }

    public TACodeItem getEndItem() {
        return endItem;
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.ELSE_NAME;
    }
}
