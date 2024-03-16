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
public class LoopTACGenerator extends TACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        TACodeItem startItem = generateLabel();
        TACodeItemsList.getList().addLast(startItem);
        VariablesTable.getLabelsTable().put(startItem.getOperand1().toString(),TACodeItemsList.getList().size()-1 );
        //generate expression
        ValueTACGenerator expressionTacGenerator = (ValueTACGenerator) TACGeneratorsMap.getMap().get(TACGeneratorsMap.EXPRESSION_NAME);
        expressionTacGenerator.setMethodName(getMethodName());
        SyntaxTreeItem expression = syntaxTreeItem.getDescendants().get(2);
        expressionTacGenerator.generateCode(expression, indent);
        Variable expressionValue = expressionTacGenerator.getValue();


        //create if statement with goto
        TACodeItem endItem = generateLabel();
        VariablesTable.getLabelsTable().put(endItem.getOperand1().toString(),TACodeItemsList.getList().size()-1 );
        TACodeItem ifzCodeItem = new TACodeItem(TACOperations.IF_ZERO, expressionValue, endItem.getOperand1(), null, indent);
        TACodeItemsList.getList().add(ifzCodeItem);

        if (syntaxTreeItem.getDescendants().get(5).getLexemValue() == null) {
            //generate statements
            TACGenerator statementGenerator = TACGeneratorsMap.getMap().get(TACGeneratorsMap.STATEMENT_NAME);
            statementGenerator.setMethodName(this.getMethodName());
            for ( int i = 5; i < syntaxTreeItem.getDescendants().size()-1; i++) {
                SyntaxTreeItem statement = syntaxTreeItem.getDescendants().get(i);
                statementGenerator.generateCode(statement, indent);
            }
        }
        String gotoValue = startItem.getOperand1().toString();
        TACodeItem gotoCodeItem = new TACodeItem(TACOperations.GOTO, gotoValue.substring(0,gotoValue.length()-1), null, null, indent);
        gotoCodeItem.setPrefix(true);
        TACodeItemsList.getList().addLast(gotoCodeItem);
        TACodeItemsList.getList().addLast(endItem);
        VariablesTable.getLabelsTable().put(endItem.getOperand1().toString(),TACodeItemsList.getList().size()-1 );
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.LOOP_NAME;
    }
}
