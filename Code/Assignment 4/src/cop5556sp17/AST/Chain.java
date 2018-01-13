package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;


public abstract class Chain extends Statement {
	
	public TypeName type; //added in assignment 4

	public TypeName getChainType(){ //added in assignment 4
		return type;
	}
	
	public void setChainType(TypeName type){ //added in assignment 4
		this.type = type;
	}
	
	public Chain(Token firstToken) {
		super(firstToken);
	}

}
