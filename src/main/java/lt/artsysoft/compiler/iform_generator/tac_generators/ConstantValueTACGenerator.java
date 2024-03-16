package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.34
 * To change this template use File | Settings | File Templates.
 */
public class ConstantValueTACGenerator extends ValueTACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        SyntaxTreeItem constantSTI = syntaxTreeItem.getDescendants().getFirst();
        String lexemValue = constantSTI.getLexemValue();
        if (lexemValue != null) {
            String currentValue = lexemValue;
            value = new Variable(this.getValueType(),lexemValue, currentValue, null);
        }
        //if logical constant
        else {
            SyntaxTreeItem logicalConstant = constantSTI.getDescendants().getFirst();
            boolean currentValue;
            if (logicalConstant.getLexemValue().equals("ЛОЖНО")) {
                currentValue = false;
            }
            else currentValue = true;
            value = new Variable(this.getValueType(),logicalConstant.getLexemValue(),currentValue, null);
        }
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.CONSTANT_NAME;
    }
}
