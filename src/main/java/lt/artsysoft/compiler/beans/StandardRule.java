package lt.artsysoft.compiler.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StandardRule extends NamedElement implements ComplexRule {
	
	String errorMessage;
	ArrayList<Rule> alternatives;
	
	public StandardRule() {
		this(null);
	}
	
	public StandardRule(String name) {
		this(name, null, "Error at unknown rule " + name);
	}
	
	public StandardRule(String name, String error) {
		this(name, null, error);
	}
	
	public StandardRule(String name, Collection<? extends Rule> alts, String errorMessage) {
		super(name);
		alternatives = new ArrayList<>();
		if (alts != null) {
			alternatives.addAll(alts);
		}
		this.errorMessage = errorMessage;
	}
	
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String message) {
		errorMessage = message;
	}

	@Override
	public List<Rule> getAlternatives() {
		return alternatives;
	}

	@Override
	public void setAlternatives(Collection<? extends Rule> rules) {
		alternatives = new ArrayList<>(rules);
	}
	
	@Override
	public String toString() {
		if (name != null) {
			String prefix = "<" + name + ">";
			StringBuilder ruleDesc = new StringBuilder(prefix);
			if (!alternatives.isEmpty()) {
				ruleDesc.append("::=");
				for (int i = 0; i < alternatives.size(); i++) {
					ruleDesc.append(alternatives.get(i).toString());
					if (i < alternatives.size() - 1 &&
							(alternatives.get(i) instanceof StandardRule && ((StandardRule)alternatives.get(i)).name == null)) {
						ruleDesc.append("|");
					}
				}
			}
			
			return ruleDesc.toString();
		} else {
			StringBuilder ruleDesc = new StringBuilder();
			if (!alternatives.isEmpty()) {
				for (int i = 0; i < alternatives.size(); i++) {
					ruleDesc.append(alternatives.get(i).toString());
					if (i < alternatives.size() - 1 &&
							(alternatives.get(i) instanceof StandardRule && ((StandardRule)alternatives.get(i)).name == null)) {
						ruleDesc.append("|");
					}
				}
			}
			
			return ruleDesc.toString();
		}
	}

}
