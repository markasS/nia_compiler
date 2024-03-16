package lt.artsysoft.compiler.interpreter;

import lt.artsysoft.compiler.analyzer.AnalyzerConfParseHelper;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.25
 * Time: 13.24
 * To change this template use File | Settings | File Templates.
 */
public class InterpreterHelper {

    public static TreeMap<String, Variable> copyVariablesMap (TreeMap<String, Variable> mapToCopy) {
        TreeMap<String, Variable>  copy =  new TreeMap<>();
        for(Map.Entry<String,Variable> entry : mapToCopy.entrySet()){
            copy.put(entry.getKey(), new Variable(entry.getValue()));
        }
        return copy;
    }

    protected static boolean notOperation(String param1) {
        return !Boolean.parseBoolean(param1);
    }

    protected static boolean conjunctionOperation(String param1, String param2) {
        return Boolean.parseBoolean(param1) && Boolean.parseBoolean(param2);
    }

    protected static boolean disjunctionOperation(String param1, String param2) {
        return Boolean.parseBoolean(param1) || Boolean.parseBoolean(param2);
    }

    protected static Object sumOperation(String param1, String param2) {
        if (!isNumeric(param1) || !isNumeric(param2)) {
            param1 = removeQuotes(param1);
            param2 = removeQuotes(param2);
            return param1 + param2;
        }
        else if (param1.lastIndexOf(".") != -1 || param2.lastIndexOf(".") != -1) {
            return Double.parseDouble(param1) + Double.parseDouble(param2);
        }
        else {
            return Integer.parseInt(param1) + Integer.parseInt(param2);
        }
    }

    private static String removeQuotes  (String param) {
        String returnValue = param;
        if ((param.startsWith("\"") && param.endsWith("\"")) ||
                (param.startsWith("'") && param.endsWith("'")))
        {
            returnValue = param.substring(1, param.length() - 1);
        }
        return returnValue;
    }

    protected static Object minusOperation(String param1, String param2) {
        if (param1.lastIndexOf(".") != -1 || param2.lastIndexOf(".") != -1) {
            return Double.parseDouble(param1) - Double.parseDouble(param2);
        }
        else {
            return Integer.parseInt(param1) - Integer.parseInt(param2);
        }
    }

    protected static boolean greaterOperation(String param1, String param2) {
        if (param1.lastIndexOf(".") != -1 || param2.lastIndexOf(".") != -1) {
            return Double.parseDouble(param1) > Double.parseDouble(param2);
        }
        else {
            return Integer.parseInt(param1) > Integer.parseInt(param2);
        }
    }

    protected static boolean greaterEqOperation(String param1, String param2) {
        if (param1.lastIndexOf(".") != -1 || param2.lastIndexOf(".") != -1) {
            return Double.parseDouble(param1) >= Double.parseDouble(param2);
        }
        else {
            return Integer.parseInt(param1) >= Integer.parseInt(param2);
        }
    }

    protected static boolean equalOperation(String param1, String param2) {
        if (param1.equals("true") || param1.equals("false")) {
            return Boolean.parseBoolean(param1) == Boolean.parseBoolean(param2);
        }
        else if (param1.lastIndexOf(".") != -1 || param2.lastIndexOf(".") != -1) {
            return Double.parseDouble(param1) == Double.parseDouble(param2);
        }
        else {
        	try {
        		return Integer.parseInt(param1) == Integer.parseInt(param2);
        	} catch (NumberFormatException ex) {
        		return param1.equals(param2);
        	}
        }
    }

    protected static boolean notEqualOperation(String param1, String param2) {
    	if (param1.equals("true") || param1.equals("false")) {
            return Boolean.parseBoolean(param1) != Boolean.parseBoolean(param2);
        }
        else if (param1.lastIndexOf(".") != -1 || param2.lastIndexOf(".") != -1) {
            return Double.parseDouble(param1) != Double.parseDouble(param2);
        }
        else {
        	try {
        		return Integer.parseInt(param1) != Integer.parseInt(param2);
        	} catch (NumberFormatException ex) {
        		return !param1.equals(param2);
        	}
        }
    }

    protected static boolean lessOperation(String param1, String param2) {
        if (param1.lastIndexOf(".") != -1 || param2.lastIndexOf(".") != -1) {
            return Double.parseDouble(param1) <= Double.parseDouble(param2);
        }
        else {
            return Integer.parseInt(param1) < Integer.parseInt(param2);
        }
    }

    protected static boolean lessEqOperation(String param1, String param2) {
        if (param1.lastIndexOf(".") != -1 || param2.lastIndexOf(".") != -1) {
            return Double.parseDouble(param1) <= Double.parseDouble(param2);
        }
        else {
            return Integer.parseInt(param1) <= Integer.parseInt(param2);
        }
    }

    protected static Object multiplyOperation(String param1, String param2) {
        if (param1.lastIndexOf(".") != -1 || param2.lastIndexOf(".") != -1) {
            return Double.parseDouble(param1) * Double.parseDouble(param2);
        }
        else {
            return Integer.parseInt(param1) * Integer.parseInt(param2);
        }
    }

    public static void writeFuncOperation(String functionName, LinkedList<String> parametersStack, int size) {
        if (parametersStack.isEmpty()) {
            System.out.println();
        }
        else {
            for (int i = parametersStack.size()-size; i < parametersStack.size(); i++ ) {
                String param = prepareParamFromName (parametersStack.get(i), functionName);
                param = removeQuotes(param);
                System.out.println(param);
            }
        }
    }

    public static void readFuncOperation(String functionName, LinkedList<String> parametersStack, int size) {
        if (parametersStack.isEmpty()) {
            inputStr();
        }
        else {
            for (int i = parametersStack.size()-size; i < parametersStack.size(); i++ ) {
                Variable currentVar = VariablesTable.getTable().get(functionName).get(parametersStack.get(i));
                String userInput = inputStr();
                currentVar.setCurrentValue(userInput);
            }
        }
    }

    public static String substringFuncOperation(String functionName, LinkedList<String> parametersStack) {
        int from = Integer.parseInt(prepareParamFromName(parametersStack.get(parametersStack.size()-2), functionName));
        int to = Integer.parseInt(prepareParamFromName(parametersStack.getLast(), functionName));
        String string = prepareParamFromName (parametersStack.get(parametersStack.size()-3), functionName);
        string = removeQuotes(string);
        String substring = string.substring(from,to);
        return substring;
    }

    public static String toStringFuncOperation(String functionName, LinkedList<String> parametersStack) {
        String original = prepareParamFromName (parametersStack.getLast(), functionName);
        if (original.equals("true") || original.equals("false")) {
        	original = AnalyzerConfParseHelper.getLexemBank().get("$" + original).getValue();
        }
        
        return original;
        
    }

    private static String inputStr() {
        String aLine = null;
        BufferedReader input =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            aLine = input.readLine();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return aLine;
    }

    private static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }


    protected static String prepareParam (Object operand, String function) {
        if (VariablesTable.getTable().get(function).containsKey(((Variable) operand).getName())) {
            Variable varOperand = (Variable) operand;
            return VariablesTable.getTable().get(function).get(varOperand.getName()).getCurrentValue().toString();
        }
        else {
        	stripCodeMarkers((Variable)operand);
            return ((Variable) operand).getCurrentValue().toString();
        }
    }

    private static void stripCodeMarkers(Variable operand) {
		
		String fullVal = operand.getCurrentValue().toString();
		if (fullVal.startsWith("\"") || fullVal.startsWith("'"))
			fullVal = fullVal.substring(1, fullVal.length() - 1);
		operand.setCurrentValue(fullVal);
		
		
	}

	protected static String prepareParamFromName (String name, String function) {
        String parameterValue;
        if (VariablesTable.getTable().get(function).containsKey(name))
            parameterValue = VariablesTable.getTable().get(function).get(name).getCurrentValue().toString();
        else parameterValue = name;
        return parameterValue;
    }

    protected static void removeFromParametersStack (LinkedList<String> parametersStack, int nrOfItems) {
        for (int i = 0; i < nrOfItems; i++) {
            parametersStack.removeLast();
        }
    }


}
