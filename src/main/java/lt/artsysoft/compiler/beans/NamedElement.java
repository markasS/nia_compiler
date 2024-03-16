package lt.artsysoft.compiler.beans;

public abstract class NamedElement {

	String name;
	
	public NamedElement() {
		name = "";
	}
	
	public NamedElement(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	/**
	 * Returns the named element's name
	 */
	public String toString() {
		return name;
	}
}
