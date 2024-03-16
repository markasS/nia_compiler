package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class InitializationSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		
		SyntaxTreeItem declaration = item.getDescendants().get(0);
		SemanticsCheckResult declCheck = SemanticsAnalysisHelper.checkItem(declaration);
		if (!declCheck.isCorrect()) {
			return declCheck;
		}
		//ignore the assign lexem
		SyntaxTreeItem initValue = item.getDescendants().get(2);
		SemanticsCheckResult initCheck = SemanticsAnalysisHelper.checkItem(initValue);
		if (!initCheck.isCorrect()) {
			return initCheck;
		}
		VariableTypes declaredType = SemanticsAnalysisHelper.extractTypeName(declaration.getDescendants().get(0));
		String stringVal = SemanticsAnalysisHelper.extractValueReturnType(initValue);
		if (stringVal.contains(" ")) {
			return SemanticsAnalysisHelper.createFailureCheckResult(stringVal, declaration.getRule());
		}
		VariableTypes actualType = VariableTypes.valueOf(stringVal);
		if (!declaredType.equals(actualType)) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseTypeError(
							SemanticAnalizer.getSyntaxItemStartLine(declaration),
							declaredType.toString(),
							actualType.toString()),
					initValue.getRule());
		}
		
		return SemanticsAnalysisHelper.createSuccessCheckResult();
	}

	@Override
	public String getRuleName() {
		return "initialization";
	}

}
