package lt.artsysoft.compiler.iform_generator.tac_generators;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.iform_generator.TACGeneratorsMap;
import lt.artsysoft.compiler.iform_generator.TACOperations;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.8
 * Time: 16.34
 * To change this template use File | Settings | File Templates.
 */
public class ConjOrDisjValueTACGenerator extends ValueTACGenerator {
    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        SyntaxTreeItem secondOperand = syntaxTreeItem.getDescendants().getLast();
        ValueTACGenerator valueOrNotTACGenerator = initValueTACGenerator(TACGeneratorsMap.VALUE_OR_NOT_NAME);
        generateCodeForValue(valueOrNotTACGenerator, indent, secondOperand);
        value = valueOrNotTACGenerator.getValue();
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.RETURN_NAME;
    }
}
