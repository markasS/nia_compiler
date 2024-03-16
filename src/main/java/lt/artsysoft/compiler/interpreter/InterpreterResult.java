package lt.artsysoft.compiler.interpreter;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.18
 * Time: 00.37
 * To change this template use File | Settings | File Templates.
 */
public class InterpreterResult {


    private Object result;

    private boolean returnsValue;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isReturnsValue() {
        return returnsValue;
    }

    public void setReturnsValue(boolean returnsValue) {
        this.returnsValue = returnsValue;
    }
}
