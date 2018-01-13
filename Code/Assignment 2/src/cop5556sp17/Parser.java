package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.Token;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	void parse() throws SyntaxException {
		program();
		matchEOF();
		return;
	}

	void program() throws SyntaxException {
		//TODO
		match(IDENT);
		if (couldBeParamDec()) {
			paramDec();
			while (t.isKind(COMMA)) {
				match(COMMA);
				paramDec();
			}
		}
		block();
	}

	void paramDec() throws SyntaxException {
		//TODO
		switch (t.kind) {
		case KW_URL:
		case KW_FILE:
		case KW_INTEGER:
		case KW_BOOLEAN:
			match(t.kind);
			match(IDENT);
			break;
		default:
			throw new SyntaxException(composeTokenInformation() +  " Expect paramDec.");
		}
	}

	void block() throws SyntaxException {
		//TODO
		match(LBRACE);
		while (!t.isKind(RBRACE)) {
			if (couldBeDec()) {
				dec();
			} else {
				statement();
			}
		}
		match(RBRACE);
	}
	
	void dec() throws SyntaxException {
		//TODO
		switch (t.kind) {
		case KW_INTEGER:
		case KW_BOOLEAN:
		case KW_IMAGE:
		case KW_FRAME:
			match(t.kind);
			match(IDENT);
			break;
		default:
			throw new SyntaxException(composeTokenInformation() +  " Expect dec.");
		}
	}

	void statement() throws SyntaxException {
		//TODO
		if (t.isKind(OP_SLEEP)) {
			match(OP_SLEEP);
			expression();
			match(SEMI);
		} else if (isWhileStatementStart(t)) {
			whileStatement();
		} else if (isIfStatementStart(t)) {
			ifStatement();
		} else if (couldBeChain()) {
			chain();
			match(SEMI);
		} else if (shouldBeAssign()) {
			assign();
			match(SEMI);
		} else {
			throw new SyntaxException(composeTokenInformation() +  " Expect statement.");
		}	
	}
	
	void assign() throws SyntaxException {
		match(IDENT);
		match(ASSIGN);
		expression();
	}
	
	void chain() throws SyntaxException {
		//TODO
		chainElem();
		arrowOp();
		chainElem();
		while (isArrowOp(t)) {
			arrowOp();
			chainElem();
		}
	}
	
	void whileStatement() throws SyntaxException {
		match(KW_WHILE);
		match(LPAREN);
		expression();
		match(RPAREN);
		block();
	}
	
	void ifStatement() throws SyntaxException {
		match(KW_IF);
		match(LPAREN);
		expression();
		match(RPAREN);
		block();
	}
	
	void chainElem() throws SyntaxException {
		//TODO
		if (t.isKind(IDENT)) {
			match(IDENT);
		} else if (isFilterOp(t)) {
			filterOp();
			arg();
		} else if (isFrameOp(t)) {
			frameOp();
			arg();
		} else if (isImageOp(t)) {
			imageOp();
			arg();
		} else {
			throw new SyntaxException(composeTokenInformation() +  " Expect chainElem.");
		}
	}
	
	void arg() throws SyntaxException {
		//TODO
		if (t.isKind(LPAREN)) {
			match(LPAREN);
			expression();
			while (t.isKind(COMMA)) {
				match(COMMA);
				expression();
			}
			match(RPAREN);
		} else {
			//¦Å do nothing
		}
	}

	void expression() throws SyntaxException {
		//TODO
		term();
		while (isRelOp(t)) {
			relOp();
			term();
		}
	}

	void term() throws SyntaxException {
		//TODO
		elem();
		while (isWeakOp(t)) {
			weakOp();
			elem();
		}
	}

	void elem() throws SyntaxException {
		//TODO
		factor();
		while (isStrongOp(t)) {
			strongOp();
			factor();
		}
	}

	void factor() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			consume();
		}
			break;
		case INT_LIT: {
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			consume();
		}
			break;
		case LPAREN: {
			consume();
			expression();
			match(RPAREN);
		}
			break;
		default:
			throw new SyntaxException(composeTokenInformation() +  " Expect factor.");
		}
	}

	void arrowOp() throws SyntaxException {
		switch (t.kind) {
		case ARROW:
		case BARARROW:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeTokenInformation() +  " Expect arrowOp.");
		}
	}
	
	void filterOp() throws SyntaxException {
		switch (t.kind) {
		case OP_BLUR:
		case OP_GRAY:
		case OP_CONVOLVE:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeTokenInformation() +  " Expect filterOp.");
		}
	}
	
	void frameOp() throws SyntaxException {
		switch (t.kind) {
		case KW_SHOW:
		case KW_HIDE:
		case KW_MOVE:
		case KW_XLOC:
		case KW_YLOC:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeTokenInformation() +  " Expect frameOp.");
		}
	}
	
	void imageOp() throws SyntaxException {
		switch (t.kind) {
		case OP_WIDTH:
		case OP_HEIGHT:
		case KW_SCALE:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeTokenInformation() +  " Expect imageOp.");
		}
	
	}
	
	void relOp() throws SyntaxException {
		switch (t.kind) {
		case LT:
		case LE:
		case GT:
		case GE:
		case EQUAL:
		case NOTEQUAL:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeTokenInformation() +  " Expect relOp.");
		}
	}
	
	void weakOp() throws SyntaxException {
		switch (t.kind) {
		case PLUS:
		case MINUS:
		case OR:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeTokenInformation() + " Expect weakOp.");
		}
	}
	
	void strongOp() throws SyntaxException {
		switch (t.kind) {
		case TIMES:
		case DIV:
		case AND:
		case MOD:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeTokenInformation() + " Expect strongOp.");
		}
	}
	
	private final boolean couldBeParamDec() {
		switch (t.kind) {
		case KW_URL:
		case KW_FILE:
		case KW_INTEGER:
		case KW_BOOLEAN:
			return true;
		default:
			return false;
		}
	}
	
	private final boolean couldBeDec() {
		switch(t.kind) {
		case KW_INTEGER:
		case KW_BOOLEAN:
		case KW_IMAGE:
		case KW_FRAME:
			return true;
		default:
			return false;
		}
	}
	
	private final boolean shouldBeAssign() {
		return t.isKind(IDENT) && scanner.peek().isKind(ASSIGN);
	}
	
	private final boolean couldBeChain() {
		return (t.isKind(IDENT) && !scanner.peek().isKind(ASSIGN)) || isFilterOp(t) || isFrameOp(t) || isImageOp(t);
	}
	
	public boolean isWhileStatementStart(Token token) {
		return token.isKind(KW_WHILE);
	}
	
	public boolean isIfStatementStart(Token token) {
		return token.isKind(KW_IF);
	}
	
	public boolean isArrowOp(Token token) {
		switch (token.kind) {
		case ARROW:
		case BARARROW:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isFilterOp(Token token) {
		switch (token.kind) {
		case OP_BLUR:
		case OP_GRAY:
		case OP_CONVOLVE:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isFrameOp(Token token) {
		switch (token.kind) {
		case KW_SHOW:
		case KW_HIDE:
		case KW_MOVE:
		case KW_XLOC:
		case KW_YLOC:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isImageOp(Token token) {
		switch (token.kind) {
		case OP_WIDTH:
		case OP_HEIGHT:
		case KW_SCALE:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isRelOp(Token token) {
		switch (token.kind) {
		case LT:
		case LE:
		case GT:
		case GE:
		case EQUAL:
		case NOTEQUAL:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isWeakOp(Token token) {
		switch (token.kind) {
		case PLUS:
		case MINUS:
		case OR:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isStrongOp(Token token) {
		switch (token.kind) {
		case TIMES:
		case DIV:
		case AND:
		case MOD:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		
		if (kind == IDENT) {
			throw new SyntaxException(composeTokenInformation() + " Expect IDENT.");
		} else if (kind == INT_LIT) {
			throw new SyntaxException(composeTokenInformation() + " Expect INT_LIT.");
		} else {
			throw new SyntaxException(composeTokenInformation() + " Expect \"" + kind.getText() + "\".");
		}
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	@SuppressWarnings("unused")
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		for (Kind kind : kinds) {
			if (t.isKind(kind)) {
				return consume();
			}
		}
		
		String message = composeTokenInformation() + " Expect one of following: ";
		for (Kind kind : kinds) {
			if (kind == IDENT) {
				message += "IDENT, ";
			} else if (kind == INT_LIT) {
				message += "INT_LIT, ";
			} else {
				message += kind.getText() + ", ";
			}
		}
		throw new SyntaxException(message.substring(0, message.length() - 2) + ".");
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

	//compose basic token information
	private final String composeTokenInformation() {
		return "Incorrect token \"" + t.getText() + "\" at " + t.getPositionText() + ".";
	}
}
