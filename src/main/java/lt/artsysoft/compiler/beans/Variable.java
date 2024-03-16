package lt.artsysoft.compiler.beans;

import lt.artsysoft.compiler.beans.classifiers.VariableTypes;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.11.29
 * Time: 13.48
 * To change this template use File | Settings | File Templates.
 */
public class Variable  {

	
	public Variable() {
		
	}

    public Variable (Variable variable) {
        this.type = variable.getType();
        this.name = variable.getName();
        this.currentValue = variable.getCurrentValue();
        this.inFunction = variable.getInFunction();
        this.temp = variable.isTemp();
        this.isParam = variable.isParam();
    }
	
    public Variable(VariableTypes type, String name, Object currentValue,
			String inFunction) {
		this.type = type;
		this.name = name;
		this.currentValue = currentValue;
		this.inFunction = inFunction;
	}

    public Variable(VariableTypes type, String name, Object currentValue, String inFunction, boolean temp) {
        this.type = type;
        this.name = name;
        this.currentValue = currentValue;
        this.inFunction = inFunction;
        this.temp = temp;
    }

    private VariableTypes type;

    private String name;

    private Object currentValue;
    
    private String inFunction;

    private boolean temp;
    
    private boolean isParam;

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VariableTypes getType() {
        return type;
    }

    public void setType(VariableTypes type) {
        this.type = type;
    }

	public String getInFunction() {
		return inFunction;
	}

	public void setInFunction(String inFunction) {
		this.inFunction = inFunction;
	}

    public String toString() {
        return name;
    }

	public boolean isParam() {
		return isParam;
	}

	public void setParam(boolean isParam) {
		this.isParam = isParam;
	}
}
