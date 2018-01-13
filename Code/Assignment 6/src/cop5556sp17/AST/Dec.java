package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public class Dec extends ASTNode {
	
	final Token ident;
	public TypeName type;	//added in assignment 4
	public int slot;		//added in assignment 5, slot number in local variable table; for ParamDec is corresponding index in args
	
	//get the slot number of this variable in local variable table
	public int getSlot() {	//added in assignment 5
		return slot;
	}

	//store the assigned slot number in local variable table
	public void setSlot(int slot) {	//added in assignment 5
		this.slot = slot;
	}

	public TypeName getDecType(){ //added in assignment 4
		return type;
	}
	
	public void setDecType(TypeName type){ //added in assignment 4
		this.type = type;
	}

	public Dec(Token firstToken, Token ident) {
		super(firstToken);
		this.ident = ident;
	}

	public Token getType() {
		return firstToken;
	}

	public Token getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return "Dec [ident=" + ident + ", firstToken=" + firstToken + "]";
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ident == null) ? 0 : ident.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Dec)) {
			return false;
		}
		Dec other = (Dec) obj;
		if (ident == null) {
			if (other.ident != null) {
				return false;
			}
		} else if (!ident.equals(other.ident)) {
			return false;
		}
		return true;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDec(this,arg);
	}

}
