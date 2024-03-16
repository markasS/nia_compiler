package lt.artsysoft.compiler.beans;

import lt.artsysoft.compiler.iform_generator.TACOperations;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.6
 * Time: 20.14
 * To change this template use File | Settings | File Templates.
 */
public class TACodeItem {

    private TACOperations operation;

    private Object operand1;

    private Object operand2;

    private Variable result;

    private int identLevel;

    private boolean prefix = false;

    public TACodeItem(TACOperations operation, Object operand1, Object operand2, Variable result, int identLevel) {
        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.result = result;
        this.identLevel = identLevel;
    }

    public Object getOperand2() {
        return operand2;
    }

    public void setOperand2(Object operand2) {
        this.operand2 = operand2;
    }

    public Object getOperand1() {
        return operand1;
    }

    public void setOperand1(Object operand1) {
        this.operand1 = operand1;
    }

    public TACOperations getOperation() {
        return operation;
    }

    public void setOperation(TACOperations operation) {
        this.operation = operation;
    }

    public Variable getResult() {
        return result;
    }

    public void setResult(Variable result) {
        this.result = result;
    }

    public int getIdentLevel() {
        return identLevel;
    }

    public void setIdentLevel(int identLevel) {
        this.identLevel = identLevel;
    }

    public boolean isPrefix() {
        return prefix;
    }

    public void setPrefix(boolean prefix) {
        this.prefix = prefix;
    }

    public String toString() {
        if (operation != null && operation.equals(TACOperations.IF_ZERO))
            return  toStringConditional();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < identLevel; i++) {
            sb.append('\t');
        }
        if (result != null) sb.append(result.toString() + " =");
        if (prefix) {
            if (!operation.getCommandText().isEmpty()) sb.append(operation.getCommandText()+ " " );
            if (operand1 != null) sb.append( operand1.toString());
            if (operand2 != null) sb.append(", " + operand2.toString());
            if ((operand1 != null || operand2 != null) && !operation.getCommandText().isEmpty()) sb.append(";");
        }
        else {
            if (operand1 != null) sb.append(" " + operand1.toString());
            if (operation != null) sb.append(" " + operation.getCommandText());
            if (operand2 != null) sb.append(" " + operand2.toString());
            if (operation != null &&  !operation.equals(TACOperations.LABEL)) sb.append(";");
        }
        //sb.append("\n");
        return sb.toString();
    }

    private String toStringConditional() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < identLevel; i++) {
            sb.append('\t');
        }
        sb.append(TACOperations.IF_ZERO.getCommandText());
        sb.append(" " + operand1.toString() + " ");
        sb.append(TACOperations.GOTO.getCommandText());
        sb.append(" " + operand2.toString().substring(0,operand2.toString().length()-1));
        sb.append(";");
        return  sb.toString();
    }
}
