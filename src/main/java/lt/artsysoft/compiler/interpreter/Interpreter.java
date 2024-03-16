package lt.artsysoft.compiler.interpreter;

import lt.artsysoft.compiler.beans.Function;
import lt.artsysoft.compiler.beans.TACodeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.iform_generator.TACOperations;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;
import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.12.17
 * Time: 22.10
 * To change this template use File | Settings | File Templates.
 */
public class Interpreter {

    private static LinkedList<String> parametersStack = new LinkedList<>();

    private static TreeMap<String, TreeMap<String,Variable>> firstVarsInRecursion = new TreeMap<>();

    private static LinkedList<TACodeItem> codeItems = TACodeItemsList.getList();

    public static InterpreterResult interpretFunction (int startAddress, String functionName) {
        int i = startAddress;
        boolean functionEnd = false;
        InterpreterResult returnResult = new InterpreterResult();
        returnResult.setReturnsValue(false);
        while (!functionEnd) {
            TACodeItem currentItem = codeItems.get(i);
            TACOperations currentOperation = currentItem.getOperation();
            if (currentOperation == null)  {
                Object currentValue = ((Variable) currentItem.getOperand1()).getCurrentValue();
                updateCodeItemResult(functionName, currentItem, currentValue);
            }
            else {
                switch (currentOperation) {
                    case MAIN: case FUNC_BEGIN:
                        break;
                    case FUNC_END:
                        functionEnd = true;
                        break;
                    case CALL:
                        String invokedFunctionName = (String) currentItem.getOperand1();
                        InterpreterResult functionResult;
                        if (FunctionDefinitionsTable.getReservedFunctions().contains(invokedFunctionName)) {
                            int paramsCount = (Integer) currentItem.getOperand2();
                            functionResult = callReservedFunctionOperation(invokedFunctionName, functionName, paramsCount);
                        }
                        else functionResult = callFunctionOperation(invokedFunctionName, functionName);
                        if (functionResult.isReturnsValue() && currentItem.getResult() != null)
                            updateCodeItemResult(functionName, currentItem, functionResult.getResult());
                        break;
                    case PARAM:
                        parametersStack.addLast(currentItem.getOperand1().toString());
                        break;
                    case RETURN:
                        returnResult.setReturnsValue(true);
                        returnResult.setResult(InterpreterHelper.prepareParam(currentItem.getOperand1(), functionName));
                        return  returnResult;
                    case IF_ZERO:
                        String expression = InterpreterHelper.prepareParam (currentItem.getOperand1(), functionName);
                        if (!Boolean.parseBoolean(expression))
                            i = VariablesTable.getLabelsTable().get(currentItem.getOperand2().toString()) - 1;
                        break;
                    case GOTO:
                        i = VariablesTable.getLabelsTable().get(currentItem.getOperand1()+":") - 1;
                        break;
                    case NOT:
                        String paramBool = InterpreterHelper.prepareParam (currentItem.getOperand1(), functionName);
                        updateCodeItemResult(functionName, currentItem, InterpreterHelper.notOperation(paramBool));
                        break;
                    case MULT: case PLUS: case MINUS:
                    case GREATER: case GREATER_EQ: case LESS: case LESS_EQ: case EQUAL: case NOT_EQUAL:
                    case CONJUNCTION: case DISJUNCTION:
                        String param1 =  InterpreterHelper.prepareParam(currentItem.getOperand1(), functionName);
                        String param2 =  InterpreterHelper.prepareParam(currentItem.getOperand2(), functionName);
                        Object result = processOperation (currentOperation, param1, param2);
                        updateCodeItemResult(functionName, currentItem, result);
                }
            }
            i++;
        }
        return returnResult;
    }

    private static Object processOperation(TACOperations operation, String param1, String param2) {
        Object result = null;
        switch (operation) {
            case MULT:
                result = InterpreterHelper.multiplyOperation(param1, param2);
                break;
            case PLUS:
                result = InterpreterHelper.sumOperation(param1, param2);
                break;
            case MINUS:
                result = InterpreterHelper.minusOperation(param1, param2);
                break;
            case GREATER:
                result = InterpreterHelper.greaterOperation(param1, param2);
                break;
            case GREATER_EQ:
                result = InterpreterHelper.greaterEqOperation(param1, param2);
                break;
            case EQUAL:
                result = InterpreterHelper.equalOperation(param1, param2);
                break;
            case NOT_EQUAL:
                result = InterpreterHelper.notEqualOperation(param1, param2);
                break;
            case LESS_EQ:
                result = InterpreterHelper.lessEqOperation(param1, param2);
                break;
            case LESS:
                result = InterpreterHelper.lessOperation(param1, param2);
                break;
            case CONJUNCTION:
                result = InterpreterHelper.conjunctionOperation(param1, param2);
                break;
            case DISJUNCTION:
                result = InterpreterHelper.disjunctionOperation(param1, param2);
                break;
        }
        return result;
    }

    private static InterpreterResult callReservedFunctionOperation(String reservedFunctionName, String invokingFunctionName, int paramCount) {
        InterpreterResult result = new InterpreterResult();
        switch (reservedFunctionName) {
            case "ПИСАТЬ":
                InterpreterHelper.writeFuncOperation(invokingFunctionName, parametersStack, paramCount);
                result.setReturnsValue(false);
                break;
            case "ЧИТАТЬ":
                InterpreterHelper.readFuncOperation(invokingFunctionName, parametersStack, paramCount);
                result.setReturnsValue(false);
                break;
            case "ДОЛЯ":
                String substring = InterpreterHelper.substringFuncOperation(invokingFunctionName, parametersStack);
                result.setReturnsValue(true);
                result.setResult(substring);
                break;
            case "В_СТРОКУ":
                String toString = InterpreterHelper.toStringFuncOperation(invokingFunctionName, parametersStack);
                result.setReturnsValue(true);
                result.setResult(toString);
                break;
        }
        if (!parametersStack.isEmpty()) InterpreterHelper.removeFromParametersStack(parametersStack, paramCount);
        return result;
    };

    private static InterpreterResult callFunctionOperation(String functionName, String invokingFunctionName)  {
        TreeMap<String, Variable> invokingFunctionVars = InterpreterHelper.copyVariablesMap(VariablesTable.getTable().get(invokingFunctionName));
        TreeMap<String, Variable> currFunctionVars;
        //recursive call
        if (functionName.equals(invokingFunctionName)) {
            currFunctionVars = InterpreterHelper.copyVariablesMap(firstVarsInRecursion.get(functionName));
            //put new variables map for new function call
            VariablesTable.getTable().put(invokingFunctionName, currFunctionVars);
        }
        else {
            currFunctionVars = VariablesTable.getTable().get(functionName);
            firstVarsInRecursion.put(functionName, InterpreterHelper.copyVariablesMap(currFunctionVars));
        }
        Function currentFunction = FunctionDefinitionsTable.getTable().get(functionName);
        //fill function params
        Iterator entries = currFunctionVars.entrySet().iterator();
        for (int i = 0; i < currentFunction.getParamTypes().size(); i++) {
            Variable localFunctionVar;
            do {
                localFunctionVar = ((Map.Entry<String, Variable>) entries.next()).getValue();
            } while (localFunctionVar.isTemp() || !localFunctionVar.isParam());
            int index = parametersStack.size() -  currentFunction.getParamTypes().size() + i;
            Variable invokingFunctionVar = invokingFunctionVars.get(parametersStack.get(index));
            //put current value as value
            localFunctionVar.setCurrentValue(invokingFunctionVar.getCurrentValue());
            VariablesTable.getTable().get(functionName).put(localFunctionVar.getName(), localFunctionVar);
        }
        InterpreterHelper.removeFromParametersStack(parametersStack, currentFunction.getParamTypes().size() );
        InterpreterResult result = interpretFunction (currentFunction.getBeginAddress(), functionName);
        if (functionName.equals(invokingFunctionName)) {
            //return outer function call variables map
            VariablesTable.getTable().put(invokingFunctionName,invokingFunctionVars);
        }
        return result;
    }

    private static void updateCodeItemResult(String inFunction, TACodeItem currentItem, Object value) {
        currentItem.getResult().setCurrentValue( value);
        VariablesTable.getTable().get(inFunction).get(currentItem.getResult().getName()).setCurrentValue(value);
    }
}
