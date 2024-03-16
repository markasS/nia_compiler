package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class ReturnSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		
		SyntaxTreeItem retVal = item.getDescendants().get(1);
        if (!retVal.getRule().getName().equals("value")) {
            retVal = SemanticsAnalysisHelper.createTreeItemWrapper(retVal);
        }
		SemanticsCheckResult returnCheck = SemanticsAnalysisHelper.checkItem(retVal);
		if (!returnCheck.isCorrect()) {
			return returnCheck;
		}
		//value not wrong itself, time to typematch
		VariableTypes functionReturnType = SemanticsAnalysisHelper.getCurrentFunction().getReturnType();
		String returnValueType = SemanticsAnalysisHelper.extractValueReturnType(retVal);
		if (returnValueType.contains(" ")) {
			return SemanticsAnalysisHelper.createFailureCheckResult(returnValueType, retVal.getRule());
		}
		if (!functionReturnType.equals(VariableTypes.valueOf(returnValueType))) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseTypeError(SemanticAnalizer.getSyntaxItemStartLine(retVal), 
							functionReturnType.toString(), returnValueType), item.getRule());
		}
		
		return returnCheck;
	}

	
	@Override
	public String getRuleName() {
		return "return";
	}

}
