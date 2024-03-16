package lt.artsysoft.compiler.semantic_analizer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.Function;
import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.StandardRule;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.semantic_programs.SemanticProgram;
import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

public class SemanticsAnalysisHelper {
	
	public static String inFunction = null;
	
	public static boolean inCondition = false;
	
	public static boolean inLoop = false;
	
	public static SemanticsCheckResult createSuccessCheckResult() {
		return new SemanticsCheckResult("OK", true, null);
	}
	
	public static SemanticsCheckResult createFailureCheckResult(String error, StandardRule rule) {
		return new SemanticsCheckResult(error, false, rule);
	}
	
	
	public static SemanticsCheckResult checkItem(SyntaxTreeItem item) throws InvalidAttributesException {
		SemanticProgram checker = SemanticAnalizer.semanticCheckers.get(item.getRule().getName());
		if (checker == null) {
			throw new IllegalArgumentException("The rule " + item.getRule().getName() + " has no semantics program associated with it!");
		}
		
		return checker.checkSemantics(item);
	}
	
	
	public static SemanticsCheckResult checkForExistingVar(SyntaxTreeItem containsId) throws InvalidAttributesException {
		Lexem theId = SemanticAnalizer.getLexemAtTreeItem(containsId);
		AbstractMap<String, Variable> funcMap = VariablesTable.getTable().get(SemanticsAnalysisHelper.inFunction);
		Variable definedVar = funcMap.get(theId.getValue());
		if (definedVar == null) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseVariableUndefinedError(
							SemanticAnalizer.getSyntaxItemStartLine(containsId),
							theId.getValue(),
							SemanticsAnalysisHelper.inFunction),
							containsId.getRule());
		}
		return createSuccessCheckResult();
	}
	
	public static SemanticsCheckResult checkDescendants(List<SyntaxTreeItem> items) throws InvalidAttributesException {
		List<SemanticsCheckResult> results = new ArrayList<>();
		boolean allOk = true;
		int i = 0;
		while(allOk && i < items.size()) {
			SyntaxTreeItem item = items.get(i);
			results.add(checkItem(item));

			allOk = allOk && results.get(i).isCorrect();
            i++;

		}
		
		if (allOk) {
			return createSuccessCheckResult();
		} else {
			i--;
			return createFailureCheckResult(results.get(i).getErrorMessage(), results.get(i).getInvalidRule());
		}
	}

	public static VariableTypes extractTypeName(SyntaxTreeItem typeItem) throws InvalidAttributesException {
		Lexem typeLexem = SemanticAnalizer.getLexemAtTreeItem(typeItem);
		
		return convertLexemTypeToEnum(typeLexem.getUid());
	}

	public static String extractIdValue(SyntaxTreeItem idItem) throws InvalidAttributesException {
		Lexem idLexem = SemanticAnalizer.getLexemAtTreeItem(idItem);
		
		return idLexem.getValue();
	}

	public static List<String> exctractTypesList(SyntaxTreeItem typedParamsListItem) throws InvalidAttributesException {
		List<String> typesList = new ArrayList<String>();
		//just closing parenthesis, no params
		if (typedParamsListItem.getDifference() == 0) {
			return typesList;
		}
		//params, its business time
		for (int i = 0; i < typedParamsListItem.getDescendants().size(); i+= 2) {
            SyntaxTreeItem aDeclaration = typedParamsListItem.getDescendants().get(i);
			VariableTypes typeName = extractTypeName(aDeclaration.getDescendants().get(0));
			typesList.add(typeName.toString());
		}
		
		return typesList;
	}
	
	public static SemanticsCheckResult writeParamsToVariableDefs(SyntaxTreeItem typedParamsListItem) throws InvalidAttributesException {
		//just closing parenthesis, no params
		if (typedParamsListItem.getDifference() == 0) {
			return createSuccessCheckResult();
		}
		//params, its business time
		for (int i = 0; i < typedParamsListItem.getDescendants().size(); i+= 2) {
            SyntaxTreeItem aDeclaration = typedParamsListItem.getDescendants().get(i);
			VariableTypes typeName = extractTypeName(aDeclaration.getDescendants().get(0));
			String varName = extractIdValue(aDeclaration.getDescendants().get(1));
            if (VariablesTable.getTable().get(inFunction).get(varName) != null) {
                return createFailureCheckResult(SemanticAnalysisErrors.raiseVariableExistsError(SemanticAnalizer.getSyntaxItemStartLine(typedParamsListItem), varName, inFunction), typedParamsListItem.getRule());
            }
			Variable var = new Variable();
			var.setName(varName);
			var.setInFunction(inFunction);
			var.setType(typeName);
			var.setParam(true);
			VariablesTable.getTable().get(inFunction).put(varName, var);
		}

        return createSuccessCheckResult();
	}
	
	public static String exractValueParReturnType(SyntaxTreeItem valueParItem) throws InvalidAttributesException {
		if (valueParItem.getRule().getName().contains("$id")) {
			valueParItem = createTreeItemWrapper(valueParItem);
		}
		SyntaxTreeItem firstChild = valueParItem.getDescendants().get(0);
		if (firstChild.getRule().getName().contains("$lPar")) {
			firstChild = valueParItem.getDescendants().get(1);
		}
		if (firstChild.getRule().getName().contains("$minus")) {
			firstChild = valueParItem.getDescendants().get(2);
		}
		if (firstChild.getRule().getName().contains("$id")) {
			
			Lexem id = SemanticAnalizer.getLexemAtTreeItem(firstChild);
			Variable var = VariablesTable.getTable().get(inFunction).get(id.getValue());
			if (var == null) {
				Function func = FunctionDefinitionsTable.getTable().get(id.getValue());
				if (func == null) {
					return SemanticAnalysisErrors.raiseVariableUndefinedError(SemanticAnalizer.getSyntaxItemStartLine(firstChild), id.getValue(), inFunction);
				} else {
					return func.getReturnType().toString();
				}
			} else {
				return var.getType().toString();
			}
		} else {
			if (firstChild.getRule().getName().contains("constant") || firstChild.getRule().getName().contains("$number")) {
				firstChild = createTreeItemWrapper(firstChild);
			}
			return extractValueReturnType(firstChild);
		}
	}

	public static String extractValueReturnType(SyntaxTreeItem valueItem) throws InvalidAttributesException {
		String[] analyzed_names = {"$id", "func_call", "constant", "$number", "expression", "numExpr", "term"};
		boolean ruleReady = false;
		for (String name: analyzed_names) {
			ruleReady = ruleReady || valueItem.getRule().getName().contains(name);
		}
		SyntaxTreeItem valueContents = null;
		if (!ruleReady) {
			valueContents = valueItem.getDescendants().getFirst();
		} else {
			valueContents = valueItem;
		}
		
		//the value is an id
		if (valueContents.getRule().getName().contains("$id")) {
			Lexem theId = SemanticAnalizer.getLexemAtTreeItem(valueContents);
			Variable definedVar = VariablesTable.getTable().get(inFunction).get(theId.getValue());
			if (definedVar == null) {
				return SemanticAnalysisErrors.raiseVariableUndefinedError(SemanticAnalizer.getSyntaxItemStartLine(valueContents), theId.getValue(), inFunction);
			}
			
			return definedVar.getType().toString();
		}
		//value is a function call
		if (valueContents.getRule().getName().equals("func_call")) {
			Lexem funcName = SemanticAnalizer.getLexemAtTreeItem(valueContents.getDescendants().get(0));
			//function call is for read/write
			if (funcName.getUid().equals("$write") || funcName.getUid().equals("$read")) {
				return "VOID";
			}
			
			Function definedFunc = FunctionDefinitionsTable.getTable().get(funcName.getValue());
			
			if (definedFunc == null) {
				return SemanticAnalysisErrors.raiseFunctionUndefinedError(SemanticAnalizer.getSyntaxItemStartLine(valueContents), funcName.getValue());
			}
			
			return definedFunc.getReturnType().toString();
		}
		//value is a contant
		if (valueContents.getRule().getName().contains("$number")) {
			Lexem contLexem = SemanticAnalizer.getLexemAtTreeItem(valueContents);
			String returnType = contLexem.getValue().contains(".")?
					VariableTypes.DOUBLE.toString() : VariableTypes.INTEGER.toString();
			
			return returnType;
		}
		if (valueContents.getRule().getName().equals("constant")) {
			Lexem contLexem = SemanticAnalizer.getLexemAtTreeItem(valueContents);

			switch(contLexem.getUid()) {
				case "$number":
					String returnType = contLexem.getValue().contains(".")?
							VariableTypes.DOUBLE.toString() : VariableTypes.INTEGER.toString();
					
					return returnType;
					
				case "$string":
					
					return VariableTypes.STRING.toString();

				case "$char":
					
					return VariableTypes.SYMBOL.toString();
				case "$true":
				case "$false":
					return VariableTypes.BOOLEAN.toString();
			}
		}
		//value is an expression
		if (valueContents.getRule().getName().equals("expression")) {
			return VariableTypes.BOOLEAN.toString();
		}
		//value is term
		if (valueContents.getRule().getName().equals("term")) {
			return SemanticsAnalysisHelper.extractTermType(valueContents);
		}
		//value is numExpr
		if (valueContents.getRule().getName().equals("numExpr")) {
			String returnType = expressionValuesTypes(valueContents);
			return returnType;
		}
		return null;
	}
	
	private static String expressionValuesTypes(SyntaxTreeItem numExprItem) throws InvalidAttributesException {
		SyntaxTreeItem termItem = numExprItem.getDescendants().get(0);
		String termType = extractTermType(termItem);
		if (VariableTypes.SYMBOL.toString().equals(termType)) {
			return SemanticAnalysisErrors.raiseBadStartOfConcatentationError(SemanticAnalizer.getSyntaxItemStartLine(termItem));
		}
		boolean isDouble = VariableTypes.DOUBLE.toString().equals(termType);
		boolean stringTypeDeadlock = false;
		if (!isDouble) {
			stringTypeDeadlock = VariableTypes.STRING.toString().equals(termType);
		}
		for (int i = 1; i < numExprItem.getDescendants().size(); i++) {
			SyntaxTreeItem plusOrMinusItem = numExprItem.getDescendants().get(i);
			String plusMinusType = extractTermType(plusOrMinusItem.getDescendants().get(1));
			if (!stringTypeDeadlock) {
				isDouble = isDouble || VariableTypes.DOUBLE.toString().equals(plusMinusType);
			} else {
				if (!(VariableTypes.STRING.toString().equals(plusMinusType)
						|| VariableTypes.SYMBOL.toString().equals(plusMinusType))) {
					return SemanticAnalysisErrors.raiseTypeError(SemanticAnalizer.getSyntaxItemStartLine(plusOrMinusItem), VariableTypes.STRING.toString()
							+ "> or <" + VariableTypes.SYMBOL.toString(), plusMinusType);
				}
				if (!plusOrMinusItem.getDescendants().get(0).getRule().getName().contains("$plus")) {
					String operation = SemanticAnalizer.getLexemAtTreeItem(plusOrMinusItem.getDescendants().get(0)).getValue();
					return SemanticAnalysisErrors.raiseInvalidOperationError(SemanticAnalizer.getSyntaxItemStartLine(plusOrMinusItem), operation, "+");
				}
			}
			
		}
		
		if (stringTypeDeadlock) {
			return VariableTypes.STRING.toString();
		} else {
			if (isDouble) {
				return VariableTypes.DOUBLE.toString();
			} else {
				return VariableTypes.INTEGER.toString();
			}
		}
	}

	private static String extractTermType(SyntaxTreeItem termItem) throws InvalidAttributesException {
		String overallType = VariableTypes.INTEGER.toString();
		for (int i = 0; i < termItem.getDescendants().size(); i+= 2) {
			SyntaxTreeItem varPar = termItem.getDescendants().get(i);
			
			String type = exractValueParReturnType(varPar);
			if (type.contains(" ")) {
				return type;
			}
			
			
			
			if (!(VariableTypes.DOUBLE.toString().equals(type)
					|| VariableTypes.INTEGER.toString().equals(type)
					|| VariableTypes.STRING.toString().equals(type)
					|| VariableTypes.SYMBOL.toString().equals(type)
					|| VariableTypes.BOOLEAN.toString().equals(type))) {
				return SemanticAnalysisErrors.raiseTypeError(
						SemanticAnalizer.getSyntaxItemStartLine(varPar), 
						VariableTypes.DOUBLE + "> or <"
						+ VariableTypes.INTEGER+ "> or <"
						+ VariableTypes.STRING + "> or <"
						+ VariableTypes.SYMBOL, type);
				
			} else {
				overallType = overallType.equals(VariableTypes.DOUBLE.toString())? overallType : type;
			}
			
			
		}
		
		return overallType;
	}

	public static SyntaxTreeItem createTreeItemWrapper(SyntaxTreeItem wrapee) {
		SyntaxTreeItem wrapper = new SyntaxTreeItem(wrapee.getRule(), wrapee.getFromLexemNr(), wrapee.getToLexemNr());
		wrapper.getDescendants().add(wrapee);
		
		return wrapper;
	}
	
	public static String normalizeNumericExpr(SyntaxTreeItem numericExpressionItem) {
		String normalizedNumExpr = "";
		
		Lexem[] numExprLexems = SemanticAnalizer.getLexemsInItem(numericExpressionItem);
		for (Lexem aLexem: numExprLexems) {
			normalizedNumExpr += aLexem.getValue();
		}
		return normalizedNumExpr;
	}

	public static Function getCurrentFunction() {
		if (inFunction == null || inFunction.isEmpty())
			return null;
		else {
			return FunctionDefinitionsTable.getTable().get(inFunction);
		}
	}

	public static VariableTypes convertLexemTypeToEnum(String typeUid) {
		switch(typeUid) {
		case "$Integer_Type":
			return VariableTypes.INTEGER;
		case "$Decimal_Type":
			return VariableTypes.DOUBLE;
		case "$Bool_Type":
			return VariableTypes.BOOLEAN;
		case "$Symbol_Type":
			return VariableTypes.SYMBOL;
		case "$String_Type":
			return VariableTypes.STRING;
		}
		return null;
	}

	public static boolean isNumericType(String typeString) {
		
		return VariableTypes.INTEGER.toString().equals(typeString) 
				|| VariableTypes.DOUBLE.toString().equals(typeString);
	}


}
