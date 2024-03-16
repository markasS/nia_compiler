package lt.artsysoft.compiler.beans;

import java.util.Collection;
import java.util.List;

public abstract interface ComplexRule extends Rule {
	
	public List<Rule> getAlternatives();
	
	public void setAlternatives(Collection<? extends Rule> rules);

}
