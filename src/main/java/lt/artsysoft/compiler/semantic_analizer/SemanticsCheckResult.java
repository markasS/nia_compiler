package lt.artsysoft.compiler.semantic_analizer;

import lt.artsysoft.compiler.beans.StandardRule;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.11.29
 * Time: 14.03
 * <br/>The result of running a <code>SemanticProgram</code> for a given rule
 */
public class SemanticsCheckResult {

    private String errorMessage;

    private boolean isCorrect;

    private StandardRule invalidRule;

    public SemanticsCheckResult(String errorMessage, boolean isCorrect,
			StandardRule invalidRule) {
		this.errorMessage = errorMessage;
		this.isCorrect = isCorrect;
		this.invalidRule = invalidRule;
	}
    
    public SemanticsCheckResult() {
    
    }

	public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public StandardRule getInvalidRule() {
        return invalidRule;
    }

    public void setInvalidRule(StandardRule invalidRule) {
        this.invalidRule = invalidRule;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
    
    @Override
    public String toString() {
    	if (invalidRule != null) {
    		return errorMessage + " From rule: " + invalidRule.getName();
    	} else {
    		return errorMessage;
    	}
    }
}
