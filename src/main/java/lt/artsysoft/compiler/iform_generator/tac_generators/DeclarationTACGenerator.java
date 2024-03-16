package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.34
 * To change this template use File | Settings | File Templates.
 */
public class DeclarationTACGenerator extends TACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        String variableName = syntaxTreeItem.getDescendants().getLast().getLexemValue();
        Variable variableInTable = VariablesTable.getTable().get(getMethodName()).get(variableName);
        if (variableInTable.getCurrentValue() == null) {
            switch(variableInTable.getType()) {
                case INTEGER:
                     variableInTable.setCurrentValue(new Integer(0));
                case DOUBLE:
                    variableInTable.setCurrentValue(new Double(0));
                case STRING:
                    variableInTable.setCurrentValue(new String());
                case BOOLEAN:
                    variableInTable.setCurrentValue(new Boolean(false));
                case SYMBOL:
                    variableInTable.setCurrentValue(new Character('\u0000'));
            }
        }
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.DECLARATION_NAME;
    }
}