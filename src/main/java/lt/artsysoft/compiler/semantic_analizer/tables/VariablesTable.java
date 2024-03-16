package lt.artsysoft.compiler.semantic_analizer.tables;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import lt.artsysoft.compiler.beans.Variable;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.11.29
 * Time: 13.56
 * To change this template use File | Settings | File Templates.
 */
public class VariablesTable {

    public static final String TMP_MAP = "1TMPMap";

    private static TreeMap<String, TreeMap<String, Variable>> definedVariables;
    
    public static  TreeMap<String, TreeMap<String, Variable>> getTable() {
    	if (definedVariables == null) {
    		definedVariables = new TreeMap<String,  TreeMap<String, Variable>>();
            definedVariables.put(TMP_MAP, new TreeMap<String, Variable>());
    	}
    	return definedVariables;
    	
    }

	public static void mapFunction(String function) {
		if (getTable().get(function) == null) {
			definedVariables.put(function, new TreeMap<String, Variable>());
		}
		
	}

    private static HashMap<String, Integer> labelsMap;

    public static  HashMap<String, Integer> getLabelsTable() {
        if (labelsMap == null) {
            labelsMap = new HashMap<>();
        }
        return labelsMap;
    }

    public static void print() {
        Iterator iterator = definedVariables.get("цел").values().iterator();
        while (iterator.hasNext()) {
            Variable function = (Variable) iterator.next();
            System.out.println(function.getName() +" " + function.getCurrentValue() +" "
                    + function.getType() + " " +function.getInFunction());
        }
    }
}
