package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.WhileStatement;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import java.util.ArrayList;

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
	ASTNode parse() throws SyntaxException {
		ASTNode astNode;
		
		astNode = program();
		matchEOF();
		return astNode;
	}

	Program program() throws SyntaxException {
		Token firstToken;
		ParamDec paramDec;
		ArrayList<ParamDec> paramDecList = new ArrayList<ParamDec>();
		Block block;
		Program program;

		firstToken = t;
		match(IDENT);
		if (couldBeParamDec()) {
			paramDec = paramDec();
			paramDecList.add(paramDec);
			while (t.isKind(COMMA)) {
				match(COMMA);
				paramDec = paramDec();
				paramDecList.add(paramDec);
			}
		}
		block = block();
		program = new Program(firstToken, paramDecList, block);
		return program;
	}

	ParamDec paramDec() throws SyntaxException {
		Token firstToken, ident;
		ParamDec paramDec;
		
		firstToken = t;
		switch (t.kind) {
		case KW_URL:
		case KW_FILE:
		case KW_INTEGER:
		case KW_BOOLEAN:
			match(t.kind);
			ident = match(IDENT);
			break;
		default:
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect paramDec.");
		}
		paramDec = new ParamDec(firstToken, ident);
		return paramDec;
	}

	Block block() throws SyntaxException {
		Token firstToken;
		Dec dec;
		ArrayList<Dec> decList = new ArrayList<Dec>();
		Statement statement;
		ArrayList<Statement> statementList = new ArrayList<Statement>();
		Block block;
		
		firstToken = t;
		match(LBRACE);
		while (!t.isKind(RBRACE)) {
			if (couldBeDec()) {
				dec = dec();
				decList.add(dec);
			} else {
				statement = statement();
				statementList.add(statement);
			}
		}
		match(RBRACE);
		block = new Block(firstToken, decList, statementList);
		return block;
	}
	
	Dec dec() throws SyntaxException {
		Token firstToken, ident;
		Dec dec;

		firstToken = t;
		switch (t.kind) {
		case KW_INTEGER:
		case KW_BOOLEAN:
		case KW_IMAGE:
		case KW_FRAME:
			match(t.kind);
			ident = match(IDENT);
			break;
		default:
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect dec.");
		}
		dec = new Dec(firstToken, ident);
		return dec;
	}

	Statement statement() throws SyntaxException {
		Token firstToken;
		Expression expression;
		Statement statement = null;//initialize to null in case some compilers give error of non-initialized variable, though Eclipse does not give error
		
		firstToken = t;
		if (t.isKind(OP_SLEEP)) {
			match(OP_SLEEP);
			expression = expression();
			match(SEMI);
			statement = new SleepStatement(firstToken, expression);
		} else if (isWhileStatementStart(t)) {
			statement = whileStatement();
		} else if (isIfStatementStart(t)) {
			statement = ifStatement();
		} else if (couldBeChain()) {
			statement = chain();
			match(SEMI);
		} else if (shouldBeAssign()) {
			statement = assign();
			match(SEMI);
		} else {
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect statement.");
		}
		return statement;
	}
	
	AssignmentStatement assign() throws SyntaxException {
		Token firstToken, ident;
		IdentLValue variable;
		Expression expression;
		AssignmentStatement assignmentStatement;
		
		firstToken = t;
		ident = match(IDENT);
		match(ASSIGN);
		expression = expression();
		variable = new IdentLValue(ident);
		assignmentStatement = new AssignmentStatement(firstToken, variable, expression);
		return assignmentStatement;
	}
	
	Chain chain() throws SyntaxException {
		Token firstToken, operator;
		ChainElem chainElem;
		Chain chain;

		firstToken = t;
		//if the parsing fails then the problem is probably on the first token
		try {
			chain = chainElem();
			operator = arrowOp();
			chainElem = chainElem();
		} catch (SyntaxException e) {
			throw new SyntaxException("Incorrect statement starts with \"" + firstToken.getText() + "\" at " + firstToken.getPositionText() + ".");
		}
		chain = new BinaryChain(firstToken, chain, operator, chainElem);
		while (isArrowOp(t)) {
			operator = arrowOp();
			chainElem = chainElem();
			chain = new BinaryChain(firstToken, chain, operator, chainElem);
		}
		return chain;
	}
	
	WhileStatement whileStatement() throws SyntaxException {
		Token firstToken;
		Expression expression;
		Block block;
		WhileStatement whileStatement;
		
		firstToken = t;
		match(KW_WHILE);
		match(LPAREN);
		expression = expression();
		match(RPAREN);
		block = block();
		whileStatement = new WhileStatement(firstToken, expression, block);
		return whileStatement;
	}
	
	IfStatement ifStatement() throws SyntaxException {
		Token firstToken;
		Expression expression;
		Block block;
		IfStatement ifStatement;
		
		firstToken = t;
		match(KW_IF);
		match(LPAREN);
		expression = expression();
		match(RPAREN);
		block = block();
		ifStatement = new IfStatement(firstToken, expression, block);
		return ifStatement;
	}
	
	ChainElem chainElem() throws SyntaxException {
		Token firstToken;
		Tuple tuple;
		ChainElem chainElem = null;//initialize to null in case some compilers give error of non-initialized variable, though Eclipse does not give error
		
		firstToken = t;
		if (t.isKind(IDENT)) {
			match(IDENT);
			chainElem = new IdentChain(firstToken);
		} else if (isFilterOp(t)) {
			filterOp();
			tuple = arg();
			chainElem = new FilterOpChain(firstToken, tuple);
		} else if (isFrameOp(t)) {
			frameOp();
			tuple = arg();
			chainElem = new FrameOpChain(firstToken, tuple);
		} else if (isImageOp(t)) {
			imageOp();
			tuple = arg();
			chainElem = new ImageOpChain(firstToken, tuple);
		} else {
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect chainElem.");
		}
		return chainElem;
	}
	
	Tuple arg() throws SyntaxException {
		Token firstToken;
		Expression expression;
		ArrayList<Expression> expressionList = new ArrayList<Expression>();
		Tuple tuple;
		
		firstToken = t;
		if (t.isKind(LPAREN)) {
			match(LPAREN);
			expression = expression();
			expressionList.add(expression);
			while (t.isKind(COMMA)) {
				match(COMMA);
				expression = expression();
				expressionList.add(expression);
			}
			match(RPAREN);
		} else {
			//empty, do nothing
		}
		tuple = new Tuple(firstToken, expressionList);
		return tuple;
	}

	Expression expression() throws SyntaxException {
		Token firstToken, operator;
		Expression expression, rightExpression;

		firstToken = t;
		expression = term();
		while (isRelOp(t)) {
			operator = relOp();
			rightExpression = term();
			expression = new BinaryExpression(firstToken, expression, operator, rightExpression);
		}
		return expression;
	}

	Expression term() throws SyntaxException {
		Token firstToken, operator;
		Expression expression, rightExpression;
		
		firstToken = t;
		expression = elem();
		while (isWeakOp(t)) {
			operator = weakOp();
			rightExpression = elem();
			expression = new BinaryExpression(firstToken, expression, operator, rightExpression);
		}
		return expression;
	}

	Expression elem() throws SyntaxException {
		Token firstToken, operator;
		Expression expression, rightExpression;
		
		firstToken = t;
		expression = factor();
		while (isStrongOp(t)) {
			operator = strongOp();
			rightExpression = factor();
			expression = new BinaryExpression(firstToken, expression, operator, rightExpression);
		}
		return expression;
	}

	Expression factor() throws SyntaxException {
		Token firstToken;
		Expression expression = null;//initialize to null in case some compilers give error of non-initialized variable, though Eclipse does not give error
		
		firstToken = t;
		switch (t.kind) {
		case IDENT:
			match(t.kind);
			expression = new IdentExpression(firstToken);
			break;
		case INT_LIT:
			match(t.kind);
			expression = new IntLitExpression(firstToken);
			break;
		case KW_TRUE:
		case KW_FALSE:
			match(t.kind);
			expression = new BooleanLitExpression(firstToken);
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT:
			match(t.kind);
			expression = new ConstantExpression(firstToken);
			break;
		case LPAREN:
			match(t.kind);
			expression = expression();
			match(RPAREN);
			break;
		default:
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect factor.");
		}
		return expression;
	}

	Token arrowOp() throws SyntaxException {
		Token operator;
		
		operator = t;
		switch (t.kind) {
		case ARROW:
		case BARARROW:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect arrowOp.");
		}
		return operator;
	}
	
	Token filterOp() throws SyntaxException {
		Token operator;
		
		operator = t;
		switch (t.kind) {
		case OP_BLUR:
		case OP_GRAY:
		case OP_CONVOLVE:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect filterOp.");
		}
		return operator;
	}
	
	Token frameOp() throws SyntaxException {
		Token operator;
		
		operator = t;
		switch (t.kind) {
		case KW_SHOW:
		case KW_HIDE:
		case KW_MOVE:
		case KW_XLOC:
		case KW_YLOC:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect frameOp.");
		}
		return operator;
	}
	
	Token imageOp() throws SyntaxException {
		Token operator;
		
		operator = t;
		switch (t.kind) {
		case OP_WIDTH:
		case OP_HEIGHT:
		case KW_SCALE:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect imageOp.");
		}
		return operator;
	}
	
	Token relOp() throws SyntaxException {
		Token operator;
		
		operator = t;
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
			throw new SyntaxException(composeIncorrectTokenInformation() +  " Expect relOp.");
		}
		return operator;
	}
	
	Token weakOp() throws SyntaxException {
		Token operator;
		
		operator = t;
		switch (t.kind) {
		case PLUS:
		case MINUS:
		case OR:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeIncorrectTokenInformation() + " Expect weakOp.");
		}
		return operator;
	}
	
	Token strongOp() throws SyntaxException {
		Token operator;
		
		operator = t;
		switch (t.kind) {
		case TIMES:
		case DIV:
		case AND:
		case MOD:
			match(t.kind);
			break;
		default:
			throw new SyntaxException(composeIncorrectTokenInformation() + " Expect strongOp.");
		}
		return operator;
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
			throw new SyntaxException(composeIncorrectTokenInformation() + " Expect IDENT.");
		} else if (kind == INT_LIT) {
			throw new SyntaxException(composeIncorrectTokenInformation() + " Expect INT_LIT.");
		} else {
			throw new SyntaxException(composeIncorrectTokenInformation() + " Expect \"" + kind.getText() + "\".");
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
		for (Kind kind : kinds) {
			if (t.isKind(kind)) {
				return consume();
			}
		}
		
		String message = composeIncorrectTokenInformation() + " Expect one of following: ";
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

	//compose incorrect token information of current token
	private final String composeIncorrectTokenInformation() {
		return "Incorrect token \"" + t.getText() + "\" at " + t.getPositionText() + ".";
	}
	
	//compose incorrect token information of given token
	@SuppressWarnings("unused")
	private final String composeIncorrectTokenInformation(Token t) {
		return "Incorrect token \"" + t.getText() + "\" at " + t.getPositionText() + ".";
	}
}
