package lt.artsysoft.compiler.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RepeatRule implements ComplexRule {

	List<Rule> alternatives;
	boolean repeatsOnce;
	
	public RepeatRule(boolean repeatsOnce, Collection<? extends Rule> alts) {
		alternatives = new ArrayList<Rule>(alts);
		this.repeatsOnce = repeatsOnce;
	}
	
	public RepeatRule(boolean repeatsOnce) {
		this(repeatsOnce, null);
	}
	
	public RepeatRule() {
		this(false);
	}
	
	@Override
	public List<Rule> getAlternatives() {
		return alternatives;
	}

	@Override
	public void setAlternatives(Collection<? extends Rule> rules) {
		alternatives = new ArrayList<Rule>(rules);
		
	}

	
	public boolean doesRepeatOnce() {
		return repeatsOnce;
	}
	
	public void setRepeatsOnce(boolean repeat) {
		this.repeatsOnce = repeat;
	}
	
	@Override
	public String toString() {
		String syntaxStart = repeatsOnce ? "[" : "{";
		String syntaxEnd = repeatsOnce ? "]" : "}";
		StringBuilder altsBuild = new StringBuilder();
		for (int i = 0; i < alternatives.size(); i++) {
			altsBuild.append(alternatives.get(i).toString());
			if (i < alternatives.size() - 1 &&
					(alternatives.get(i) instanceof StandardRule && ((StandardRule)alternatives.get(i)).name == null)) {
				altsBuild.append("|");
			}
		}
		String rule = syntaxStart + altsBuild.toString() + syntaxEnd;
		
		return rule;
	}
	
}
