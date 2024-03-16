package lt.artsysoft.compiler.beans;

import java.util.List;

import lt.artsysoft.compiler.beans.classifiers.VariableTypes;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.11.29
 * Time: 13.50
 * To change this template use File | Settings | File Templates.
 */
public class Function {

	public Function() {
		
	}
	
    public Function(VariableTypes returnType, String name, List<VariableTypes> paramTypes) {
		this.returnType = returnType;
		this.name = name;
		this.paramTypes = paramTypes;
	}


	private VariableTypes returnType;

    private String name;
    
    private List<VariableTypes> paramTypes;

    private Integer beginAddress;
    /**
     * Map with key - name of parameter in function
     */
    //private TreeMap<String, Variable> paramsList;


    public VariableTypes getReturnType() {
        return returnType;
    }


    public void setReturnType(VariableTypes returnType) {
        this.returnType = returnType;
    }

//    public TreeMap<String, Variable> getParamsList() {
//        return paramsList;
//    }
//
//    public void setParamsList(TreeMap<String, Variable> paramsList) {
//        this.paramsList = paramsList;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public List<VariableTypes> getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(List<VariableTypes> paramTypes) {
		this.paramTypes = paramTypes;
	}

    public Integer getBeginAddress() {
        return beginAddress;
    }

    public void setBeginAddress(Integer beginAddress) {
        this.beginAddress = beginAddress;
    }
}