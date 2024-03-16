package lt.artsysoft.compiler.beans;


import lt.artsysoft.compiler.beans.classifiers.LexemTypes;

/**
 * A language element describing non-programmer-generated content that can
 * be found in the source code of a program. A {@link Lexer} will try to find these among the code
 * as to ensure that is can be allowed to compile safely.
 *
 */
public class Lexem extends NamedElement implements Rule, Cloneable {
	
	private String value;
	private String uid;
	private LexemTypes type;
    private int lineInCode;
	
	public Lexem() 
	{
		super();
	}
	
	
	public Lexem(String value, String name, String uid, LexemTypes type) {
		super(name);
		this.value = value;
		this.uid = uid;
		this.type = type;
	}

    public Lexem copyLexem() {
        return new Lexem(this.value,this.name,this.uid,this.getType());
    }

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public LexemTypes getType() {
		return type;
	}
	public void setType(LexemTypes type) {
		this.type = type;
	}
	
	
	public String toString() {
		return uid + " - " + value/* + "(" + super.toString() + ", uid: " + uid + ")"*/;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Lexem) {
			return this.uid.equals(((Lexem)obj).uid);
		} else {
			return super.equals(obj);
		}
	}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getLineInCode() {
        return lineInCode;
    }

    public void setLineInCode(int lineInCode) {
        this.lineInCode = lineInCode;
    }
}
