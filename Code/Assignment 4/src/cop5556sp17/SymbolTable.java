package cop5556sp17;

import java.util.HashMap;
import java.util.Stack;

import cop5556sp17.AST.Dec;

public class SymbolTable {

	private class SymbolAttribute {
	
		private int scope;
		private Dec dec;
		
		SymbolAttribute(int scope, Dec dec) {
			this.scope = scope;
			this.dec = dec;
		}
		
		public int getScope() {
			return scope;
		}
		
		public Dec getDec() {
			return dec;
		}
	}
	
	private HashMap<String, Stack<SymbolAttribute>> symbolTable;
	private Stack<Integer> scopeStack;
	private int scopeSequence;
	
	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		scopeStack.push(++scopeSequence); //for the project language, ParamDecs are considered not in the same scope as Decs in program block
	}
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		scopeStack.pop();
	}
	
	//return: true - insert done; false - identifier already exists for current scope, insert failed.
	public boolean insert(String ident, Dec dec){
		Stack<SymbolAttribute> symbolAttributeStack;

		symbolAttributeStack = symbolTable.get(ident);
		if (symbolAttributeStack == null) {
			symbolAttributeStack = new Stack<SymbolAttribute>();
			symbolAttributeStack.push(new SymbolAttribute(getCurrentScope(), dec));
			symbolTable.put(ident, symbolAttributeStack);
		} else {
			if (symbolAttributeStack.peek().getScope() == getCurrentScope()) { //identifier already exists for current scope
				return false;
			}
			symbolAttributeStack.push(new SymbolAttribute(getCurrentScope(), dec));
		}
		return true;
	}
	
	//return: null - no visible identifier for current scope; Dec - matched declaration for the identifier.
	public Dec lookup(String ident){
		SymbolAttribute symbolAttribute;
		Stack<SymbolAttribute> symbolAttributeStack;

		symbolAttributeStack = symbolTable.get(ident);
		if (symbolAttributeStack != null) {
			//iterate backwards to find the identifier with scope number closest to the top of the scope stack
			for (int i = symbolAttributeStack.size() - 1; i >= 0; i--) {
				symbolAttribute = symbolAttributeStack.get(i);
				if (scopeStack.contains(symbolAttribute.getScope())) {
					return symbolAttribute.getDec();
				}
			}
		}
		return null;
	}
		
	public SymbolTable() {
		symbolTable = new HashMap<String, Stack<SymbolAttribute>>();
		scopeStack = new Stack<Integer>();
		scopeStack.push(0); //initialize scope stack with a 0 in it to avoid accessing to empty stack before entering Block
		scopeSequence = 0;
	}

	@Override
	public String toString() {
		return "Scope Sequence: " + scopeSequence + ". Scope Stack: " + scopeStack + ". Symbol Table: " + symbolTable + ".";
	}
	
	public int getCurrentScope() {
		return scopeStack.peek();
	}
}
