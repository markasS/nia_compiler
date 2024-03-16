package lt.artsysoft.compiler.iform_generator.tac_generators;

import java.util.LinkedList;


import lt.artsysoft.compiler.beans.*;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;
import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.6
 * Time: 20.13
 * To change this template use File | Settings | File Templates.
 */
public abstract class TACGenerator {

    private String methodName;

    /**
     * Generates Three Address code according to rule passed as a parameter and  level of identation
     * @param syntaxTreeItem
     * @param indent
     */
     public abstract void generateCode(SyntaxTreeItem syntaxTreeItem, int indent);

    /**
     * Returns name of associated rule
     * @return the name of the rule this generator is used for (key in generators map)
     */
    public abstract String getRuleName();

    protected SyntaxTreeItem getSyntaxTreeItemDescendant(SyntaxTreeItem syntaxTreeItem, String descendantName) {
        LinkedList<SyntaxTreeItem> descendants  = syntaxTreeItem.getDescendants();
        for (SyntaxTreeItem descendant : descendants) {
            StandardRule rule = descendant.getRule();
            if (rule.getName().equals(descendantName)) {
                return descendant;
            }
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    protected LinkedList<SyntaxTreeItem>  getSyntaxTreeItemDescendantsList(SyntaxTreeItem syntaxTreeItem, String descendantName) {
        LinkedList<SyntaxTreeItem> descendants  = syntaxTreeItem.getDescendants();
        LinkedList<SyntaxTreeItem> foundItems = new LinkedList<>();
        for (SyntaxTreeItem descendant : descendants) {
            StandardRule rule = descendant.getRule();
            if (rule.getName().equals(descendantName)) {
                foundItems.add(descendant);
            }
        }
        return foundItems;  //To change body of created methods use File | Settings | File Templates.
    }

    protected void assignVariable (Variable variable,VariableTypes varType, Object value) {
    switch(varType) {
        case INTEGER:
            variable.setCurrentValue((Integer) value);
        case DOUBLE:
            variable.setCurrentValue((Double) value);
        case STRING:
            variable.setCurrentValue((String) value);
        case BOOLEAN:
            variable.setCurrentValue((Boolean) value);
        case SYMBOL:
            variable.setCurrentValue((Character)value);
        }
    }

    protected Variable generateTmpVariable (VariableTypes varType) {
        StringBuilder tmpValue = new StringBuilder();
        tmpValue.append("_t");
        int i = 0;
        //tmpValue.append(i);
        boolean containsSameInTmp, containsSameInMethod;
        containsSameInTmp = VariablesTable.getTable().get(VariablesTable.TMP_MAP).containsKey(tmpValue.toString());
        containsSameInMethod = VariablesTable.getTable().get(methodName).containsKey(tmpValue.toString());
        String current = new String(tmpValue);
        int charToDelete = 1;
        while ((containsSameInTmp || containsSameInMethod))  {
            tmpValue = new StringBuilder(current);
            tmpValue.append(++i);
            containsSameInTmp = VariablesTable.getTable().get(VariablesTable.TMP_MAP).containsKey(tmpValue.toString());
            containsSameInMethod = VariablesTable.getTable().get(methodName).containsKey(tmpValue.toString());
        }
        return new Variable(varType, tmpValue.toString(), null, methodName, true );
    }

    protected TACodeItem generateLabel () {
        StringBuilder tmpValue = new StringBuilder();
        tmpValue.append("_L");
        int i = 0;
        tmpValue.append(i);
        boolean containsSameInLabels = VariablesTable.getLabelsTable().containsKey(tmpValue.toString() + ":");

        while (containsSameInLabels)  {
            tmpValue.deleteCharAt(tmpValue.length() - 1);
            tmpValue.append(++i);
            containsSameInLabels = VariablesTable.getLabelsTable().containsKey(tmpValue.toString() + ":");
        }
        tmpValue.append(":");

        TACodeItem result = new TACodeItem(TACOperations.LABEL, tmpValue, null, null, 0);
        result.setPrefix(true);
        return result;
    }

    protected TACodeItem generateLabel (String name) {
        StringBuilder tmpValue = new StringBuilder();
        tmpValue.append(name);
        tmpValue.append(":");
        TACodeItem result = new TACodeItem(TACOperations.LABEL, tmpValue, null, null, 0);
        result.setPrefix(true);
        return result;
    }

    protected Variable processFuncCall(Variable tmpVariable, boolean saveToTable)  {
        TACodeItem termCodeItem = TACodeItemsList.getList().getLast();
        termCodeItem.setResult(tmpVariable);
        //TACodeItemsList.getList().add(termCodeItem);
        //put to variables table with current value == function address (codeItemNumber)
        setFuncAddressToVariable(tmpVariable, termCodeItem.getOperand1().toString());
        if (saveToTable)
            VariablesTable.getTable().get(getMethodName()).put(tmpVariable.getName(), tmpVariable);
        return tmpVariable;
    }

    protected void setFuncAddressToVariable(Variable variable, String functionName) {
        Function function = FunctionDefinitionsTable.getTable().get(functionName);
        variable.setCurrentValue(function.getBeginAddress());
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
