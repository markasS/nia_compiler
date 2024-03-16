package lt.artsysoft.compiler.semantic_analizer;

import java.util.List;

public class SemanticAnalysisErrors {
	
	
	private static String typeError = "%d: Types mismatch! Expected: <%s> found: <%s>";
	private static String invalidOperationError = "%d: The operation <%s> is unnaplicable within the given context!\n"
			+ "An example of an acceptable one would be <%s>";
	private static String badStartOfConcatenationError = "%d: Cannot start concatenation with <SYMBOL>, first operand must be <STRING>!";
	private static String functionExistsError = "%d: Function <%s> is already defined!";
	private static String variableExistsError = "%d: Variable <%s> is already defined in function <%s>!";
	private static String variableUndefinedError = "%d: Variable <%s> has not been defined in function <%s>!";
	private static String functionUndefinedError = "%d: Function <%s> has not been defined!";
	private static String functionParamTypeMismatchError = "%d: Function <%s> actual parameter list differs from expected:"
			+ " expected type <%s> at parameter <%d>, found <%s>";
	private static String functionParamsCountMismatchError = "%d: Funtion <%s> actual parameters list length differs "
			+ "from factual: expected <%d>, found <%d>";
	private static String readParamNonVariableError = "%d: The call to $read has  non-modifiable parameters at <%s>";
	private static String comparisonTypeError = "%d: Comparing different types! First operand is of type <%s>, second - <%s>";
	private static String assignToReadWriteError = "%d: Attempting to assign variable <%s> to value of $read or $write!";
	private static String declareVariableInConditionalStatementError = "%d: Attempting to declare a variable in conditional statement! This is "
			+ "\nunsupported by the current compiler version, please move your declarations outside the condition.";
	private static String declareVariableInLoopStatementError = "%d: Attempting to declare a variable in loop statement! This is "
			+ "\nunsupported by the current compiler version, please move your declarations outside the loop.";
	
	public static String raiseTypeError(int lineNumber, String expected, String found) {
		return String.format(typeError, lineNumber, expected, found);
	}
	
	public static String raiseDeclareVariableInConditionalStatementError(int lineNumber) {
		return String.format(declareVariableInConditionalStatementError, lineNumber);
	}

	public static String raiseDeclareVariableInLoopStatementError(int lineNumber) {
		return String.format(declareVariableInLoopStatementError, lineNumber);
	}

	public static String raiseBadStartOfConcatentationError(int lineNumber)	{
		return String.format(badStartOfConcatenationError, lineNumber);
	}
	
	public static String raiseAssignToReadWriteError(int lineNumber, String varName) {
		return String.format(assignToReadWriteError, lineNumber, varName);
	}
	
	public static String raiseComparisonTypeError(int lineNumber, String first, String second) {
		return String.format(comparisonTypeError, lineNumber, first, second);
	}
	
	public static String raiseFunctionExistsError(int lineNumber, String name) {
		return String.format(functionExistsError, lineNumber, name);
	}
	
	public static String raiseVariableExistsError(int lineNumber, String name, String funcName) {
		return String.format(variableExistsError, lineNumber, name, funcName);
	}

	public static String raiseFunctionUndefinedError(int lineNumber, String name) {
		return String.format(functionUndefinedError, lineNumber, name);
	}
	
	public static String raiseVariableUndefinedError(int lineNumber, String name, String funcName) {
		return String.format(variableUndefinedError, lineNumber, name, funcName);
	}
	
	public static String raiseFunctionParamTypeMismatchError(int lineNumber, String name, String expectedType, int paramIndex, String foundType) {
		return String.format(functionParamTypeMismatchError, lineNumber, name, expectedType, paramIndex, foundType);
	}
	
	public static String raiseFunctionParamsCountMismatchError(int lineNumber, String name, int expected, int actual) {
		return String.format(functionParamsCountMismatchError, lineNumber, name, expected, actual);
	}
	
	public static String raiseReadParamNonVariableError(int lineNumber, List<Integer> paramIndexes) {
		return String.format(readParamNonVariableError, lineNumber, paramIndexes);
	}

	public static String raiseInvalidOperationError(int lineNumber, String operation, String acceptable) {
		return String.format(invalidOperationError, lineNumber, operation, acceptable);
	}
}
