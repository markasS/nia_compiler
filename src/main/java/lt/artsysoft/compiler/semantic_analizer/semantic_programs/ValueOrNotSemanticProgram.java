package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class ValueOrNotSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		if (item.getDescendants().size() == 1) {
			return SemanticsAnalysisHelper.checkItem(item.getDescendants().getFirst());
		}
		SyntaxTreeItem valuePar = item.getDescendants().get(0);
		SemanticsCheckResult finalCheck = null;
		if (valuePar.getRule().getName().contains("$exclMark")) {
			valuePar = item.getDescendants().get(1);
			finalCheck = SemanticsAnalysisHelper.checkItem(valuePar);
			if (!finalCheck.isCorrect()) {
				return finalCheck;
			}
			finalCheck = checkToBeType(valuePar, VariableTypes.BOOLEAN);
			if (!finalCheck.isCorrect()) {
				return finalCheck;
			}
		} else {
			int partIndex = 0;
			if (valuePar.getRule().getName().contains("$lPar")) {
				partIndex++;
			}
			valuePar = item.getDescendants().get(partIndex);
			finalCheck = SemanticsAnalysisHelper.checkItem(valuePar);
			if (!finalCheck.isCorrect()) {
				return finalCheck;
			} 
			VariableTypes type1 = null, type2 = null;
			String typeString = checkToBeAType(valuePar);
			if (typeString.contains(" ")) {
				return SemanticsAnalysisHelper.createFailureCheckResult(typeString, valuePar.getRule());
			}
			type1 = VariableTypes.valueOf(typeString);
			valuePar = item.getDescendants().get(partIndex + 2);
			finalCheck = SemanticsAnalysisHelper.checkItem(valuePar);
			if (!finalCheck.isCorrect()) {
				return finalCheck;
			}
			typeString = checkToBeAType(valuePar);
			if (typeString.contains(" ")) {
				return SemanticsAnalysisHelper.createFailureCheckResult(typeString, valuePar.getRule());
			}
			type2 = VariableTypes.valueOf(typeString);
			
			if (!(type1.equals(type2) || bothNumeric(type1, type2))) {
				return SemanticsAnalysisHelper.createFailureCheckResult(
						SemanticAnalysisErrors.raiseComparisonTypeError(
								SemanticAnalizer.getSyntaxItemStartLine(valuePar), type1.toString(), type2.toString()),
							item.getRule());
			}
			if (type1.equals(VariableTypes.STRING)) {
				Lexem comparison = SemanticAnalizer.getLexemAtTreeItem(item.getDescendants().get(partIndex + 1));
				if (!(comparison.getUid().equals("$exclEq") || comparison.getUid().equals("$dEq"))) {
					return SemanticsAnalysisHelper.createFailureCheckResult(
							SemanticAnalysisErrors.raiseInvalidOperationError(SemanticAnalizer.getSyntaxItemStartLine(valuePar),
									comparison.getValue(), "==> or <!="), 
							item.getRule());
				}
			}
			
		}
		
		return finalCheck;
	}
	
	private boolean bothNumeric(VariableTypes type1, VariableTypes type2) {
		boolean is1Numeric = type1.equals(VariableTypes.DOUBLE) || type1.equals(VariableTypes.INTEGER);
		boolean is2Numeric = type2.equals(VariableTypes.DOUBLE) || type2.equals(VariableTypes.INTEGER);
		return is1Numeric && is2Numeric;
	}

	private String checkToBeAType(SyntaxTreeItem valuePar) throws InvalidAttributesException  {
		SyntaxTreeItem value = valuePar.getDescendants().get(0).getRule().getName().contains("$lPar")?
				valuePar.getDescendants().get(1) :
				valuePar.getDescendants().get(0);
		if (value.getRule().getName().contains("$id") ||
				value.getRule().getName().equals("constant")) {
			value = SemanticsAnalysisHelper.createTreeItemWrapper(value);
		}
		String valueTypeString = SemanticsAnalysisHelper.extractValueReturnType(value);
		return valueTypeString;
	}
	
	private SemanticsCheckResult checkToBeType(SyntaxTreeItem valuePar, VariableTypes type) throws InvalidAttributesException {
		SyntaxTreeItem value = valuePar.getDescendants().get(0).getRule().getName().contains("$lPar")?
				valuePar.getDescendants().get(1) :
				valuePar.getDescendants().get(0);
		if (value.getRule().getName().contains("$id") ||
				value.getRule().getName().equals("constant")) {
			value = SemanticsAnalysisHelper.createTreeItemWrapper(value);
		}
		String valueTypeString = SemanticsAnalysisHelper.extractValueReturnType(value);
		if (!type.equals(VariableTypes.valueOf(valueTypeString))) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseTypeError(
							SemanticAnalizer.getSyntaxItemStartLine(valuePar), 
							type.toString(),
							valueTypeString),
					valuePar.getRule());
		}
		return SemanticsAnalysisHelper.createSuccessCheckResult();
	}

	@Override
	public String getRuleName() {
		return "value_or_not";
	}

}
