package lt.artsysoft.compiler.semantic_analizer.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lt.artsysoft.compiler.analyzer.AnalyzerConfParseHelper;
import lt.artsysoft.compiler.beans.Function;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;

public class FunctionDefinitionsTable {
	
	private static HashMap<String, Function> definedFunctions;

    private static List<String> reservedFunctions;
	
	public static HashMap<String, Function> getTable() {
        if (definedFunctions == null) {
            definedFunctions = new HashMap<String, Function>();
            reservedFunctions = new ArrayList<String>();
            populateReservedFunctions(definedFunctions);
        }

        return definedFunctions;
    }

    public static List<String> getReservedFunctions() {
        return reservedFunctions;
    }

    private static void populateReservedFunctions(HashMap<String, Function> functionsMap) {
		//add Read
    	Function func = new Function();
    	func.setName(AnalyzerConfParseHelper.getLexemBank().get("$read").getValue());
    	func.setReturnType(VariableTypes.VOID);
        reservedFunctions.add(func.getName());
    	
    	functionsMap.put(func.getName(), func);
    	//add Write
    	func = new Function();
    	func.setName(AnalyzerConfParseHelper.getLexemBank().get("$write").getValue());
    	func.setReturnType(VariableTypes.VOID);
        reservedFunctions.add(func.getName());
    	
    	functionsMap.put(func.getName(), func);
    	//add Substring
    	func = new Function();
    	func.setName(AnalyzerConfParseHelper.getLexemBank().get("$substring").getValue());
    	func.setReturnType(VariableTypes.STRING);
    	List<VariableTypes> paramTypes = new ArrayList<VariableTypes>();
    	paramTypes.add(VariableTypes.STRING);
    	paramTypes.add(VariableTypes.INTEGER);
    	paramTypes.add(VariableTypes.INTEGER);
    	func.setParamTypes(paramTypes);
        reservedFunctions.add(func.getName());
    	
    	functionsMap.put(func.getName(), func);

        //add toString
        func = new Function();
        func.setName(AnalyzerConfParseHelper.getLexemBank().get("$to_string").getValue());
        func.setReturnType(VariableTypes.STRING);
        reservedFunctions.add(func.getName());
        
        functionsMap.put(func.getName(), func);   
	}

	public static void print() {
        Iterator iterator = definedFunctions.values().iterator();
        while (iterator.hasNext()) {
            Function function = (Function) iterator.next();
            System.out.println(function.getName() +" " + function.getReturnType() +" " + function.getBeginAddress()  +
                    " " + function.getParamTypes());
        }
    }
	

}
