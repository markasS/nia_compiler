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
public class ValueTACGenerator extends TACGenerator {

    protected Variable value;

    private VariableTypes valueType;

    @Override
    public void generateCode(SyntaxTreeItem syntaxTreeItem, int indent) {
        SyntaxTreeItem specificValue = syntaxTreeItem.getDescendants().getFirst();
        String valueType = specificValue.getRule().getName();
        ValueTACGenerator specificValueGenerator = (ValueTACGenerator) TACGeneratorsMap.getMap().get(valueType);
        if (specificValue.getLexemValue() == null)   {
            generateCodeForValue (specificValueGenerator, indent, specificValue);
            value = specificValueGenerator.getValue();
            if (value == null)  {
                Variable tmpVariable = generateTmpVariable(getValueType());
                value = processFuncCall(tmpVariable, true);
            }
        }
        else value = new Variable(this.getValueType(),specificValue.getLexemValue(), null, null);
    }

    @Override
    public String getRuleName() {
        return TACGeneratorsMap.VALUE_NAME;
    }

    protected void generateCodeForValue (ValueTACGenerator valueTACGenerator, int indent, SyntaxTreeItem descendant) {
        valueTACGenerator.setMethodName(this.getMethodName());
        valueTACGenerator.setValueType(this.getValueType());
        valueTACGenerator.generateCode(descendant,indent);

    }

    protected ValueTACGenerator initValueTACGenerator (String tacGeneratorName) {
        ValueTACGenerator tacGenerator = (ValueTACGenerator) TACGeneratorsMap.getMap().get(tacGeneratorName);
        tacGenerator.setValueType(getValueType());
        tacGenerator.setMethodName(getMethodName());
        return tacGenerator;
    }

    public Variable getValue() {
        return value;
    }

    public VariableTypes getValueType() {
        return valueType;
    }

    public void setValueType(VariableTypes valueType) {
        this.valueType = valueType;
    }
}
