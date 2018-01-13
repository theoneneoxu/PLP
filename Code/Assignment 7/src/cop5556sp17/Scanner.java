package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line = " + line + ", posInLine = " + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  	//position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			String tokenText;
			if (kind == Kind.EOF) {
				tokenText = "eof";
			} else {
				tokenText = chars.substring(pos, pos + length);
			}
			return tokenText;
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			int lineNumber = 0;
			int columnNumber = 0;
			for (int i = 0; i < pos; i++) {
				if (chars.charAt(i) == '\n') {
					lineNumber++;
					columnNumber = 0;
				} else {
					columnNumber++;
				}
			}
			return new LinePos(lineNumber, columnNumber);
		}
		
		LinePos getLinePos_v2(){
			int lineNumber = 1;
			int columnNumber = 1;
			for (int i = 0; i < pos; i++) {
				if (chars.charAt(i) == '\n') {
					lineNumber++;
					columnNumber = 1;
				} else {
					if (chars.charAt(i) == '\t') {
						columnNumber += 4;
					} else {
						columnNumber++;
					}
				}
			}
			return new LinePos(lineNumber, columnNumber);
		}
		
		public final String getPositionText() {
			return "line " + getLinePos_v2().line + " column " + getLinePos_v2().posInLine;
			//return "position " + pos + " [line " + getLinePos().line + " column " + getLinePos().posInLine + "]";
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}
		
		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			return Integer.parseInt(chars.substring(pos, pos + length));
		}
		
		public boolean isKind(Kind kind){
			return this.kind == kind;	
		}

		public boolean isKind(Kind... kinds) {
			for (Kind kind: kinds) {
				if (this.kind == kind) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + pos;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Token)) {
				return false;
			}
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (kind != other.kind) {
				return false;
			}
			if (length != other.length) {
				return false;
			}
			if (pos != other.pos) {
				return false;
			}
			return true;
		}

		private Scanner getOuterType() {
			return Scanner.this;
		}
	}

	Scanner(String chars) {
		reservedWordTable = new HashMap<String, Kind>();
		configReservedWordTable();
		tokens = new ArrayList<Token>();
		this.chars = chars;
		tokenNum = 0;
	}

	private final void configReservedWordTable(){
		reservedWordTable.put(Kind.KW_INTEGER.getText(), Kind.KW_INTEGER);
		reservedWordTable.put(Kind.KW_BOOLEAN.getText(), Kind.KW_BOOLEAN);
		reservedWordTable.put(Kind.KW_IMAGE.getText(), Kind.KW_IMAGE);
		reservedWordTable.put(Kind.KW_URL.getText(), Kind.KW_URL);
		reservedWordTable.put(Kind.KW_FILE.getText(), Kind.KW_FILE);
		reservedWordTable.put(Kind.KW_FRAME.getText(), Kind.KW_FRAME);
		reservedWordTable.put(Kind.KW_WHILE.getText(), Kind.KW_WHILE);
		reservedWordTable.put(Kind.KW_IF.getText(), Kind.KW_IF);
		reservedWordTable.put(Kind.OP_SLEEP.getText(), Kind.OP_SLEEP);
		reservedWordTable.put(Kind.KW_SCREENHEIGHT.getText(), Kind.KW_SCREENHEIGHT);
		reservedWordTable.put(Kind.KW_SCREENWIDTH.getText(), Kind.KW_SCREENWIDTH);
		reservedWordTable.put(Kind.OP_GRAY.getText(), Kind.OP_GRAY);
		reservedWordTable.put(Kind.OP_CONVOLVE.getText(), Kind.OP_CONVOLVE);
		reservedWordTable.put(Kind.OP_BLUR.getText(), Kind.OP_BLUR);
		reservedWordTable.put(Kind.KW_SCALE.getText(), Kind.KW_SCALE);
		reservedWordTable.put(Kind.OP_WIDTH.getText(), Kind.OP_WIDTH);
		reservedWordTable.put(Kind.OP_HEIGHT.getText(), Kind.OP_HEIGHT);
		reservedWordTable.put(Kind.KW_XLOC.getText(), Kind.KW_XLOC);
		reservedWordTable.put(Kind.KW_YLOC.getText(), Kind.KW_YLOC);
		reservedWordTable.put(Kind.KW_HIDE.getText(), Kind.KW_HIDE);
		reservedWordTable.put(Kind.KW_SHOW.getText(), Kind.KW_SHOW);
		reservedWordTable.put(Kind.KW_MOVE.getText(), Kind.KW_MOVE);
		reservedWordTable.put(Kind.KW_TRUE.getText(), Kind.KW_TRUE);
		reservedWordTable.put(Kind.KW_FALSE.getText(), Kind.KW_FALSE);
	}
	
	//scan state
	public static enum ScanState{
		START,
		AFTER_EQUAL,				//previous character is =
		AFTER_MINUS,				//previous character is -
		AFTER_PIPE,					//previous character is |
		AFTER_PIPE_MINUS,			//previous two characters are |-
		AFTER_LESSTHAN,				//previous character is <
		AFTER_GREATERTHAN,			//previous character is >
		AFTER_EXCLAMATION,			//previous character is !
		AFTER_SLASH,				//previous character is /
		IN_COMMENT,					//had met /*
		IN_COMMENT_AFTER_STAR,		//had met /* and *
		IN_DIGIT,					//had met int_literal
		IN_IDENTIFIER;				//had met ident_start ::= A..Z|a..z|$|_
	}

	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		//TODO IMPLEMENT THIS!!!!
		char ch;
		int scanPosition = 0;				//current reading position
		int tokenStartPosition = 0;			//possible (not necessarily) start position of current token
		int codeLength = chars.length();	//length of input code
		ScanState scanState = ScanState.START;

		//scan through code
		while (scanPosition <= codeLength) {
			ch = scanPosition < codeLength ? chars.charAt(scanPosition) : ' ';	//set eof as a white space character to finish processing of pending token
			switch (scanState) {
			case START: {
				tokenStartPosition = scanPosition;
				switch (ch) {
				//skip white space characters
				case ' ':
				case '\t':
				case '\r':
				case '\n':
					scanPosition++;
					break;	
				case '0':
					tokens.add(new Token(Kind.INT_LIT, tokenStartPosition, 1));
					scanPosition++;
					break;
				case ';':
					tokens.add(new Token(Kind.SEMI, tokenStartPosition, 1));
					scanPosition++;
					break;
				case ',':
					tokens.add(new Token(Kind.COMMA, tokenStartPosition, 1));
					scanPosition++;
					break;
				case '(':
					tokens.add(new Token(Kind.LPAREN, tokenStartPosition, 1));
					scanPosition++;
					break;
				case ')':
					tokens.add(new Token(Kind.RPAREN, tokenStartPosition, 1));
					scanPosition++;
					break;
				case '{':
					tokens.add(new Token(Kind.LBRACE, tokenStartPosition, 1));
					scanPosition++;
					break;
				case '}':
					tokens.add(new Token(Kind.RBRACE, tokenStartPosition, 1));
					scanPosition++;
					break;
				case '&':
					tokens.add(new Token(Kind.AND, tokenStartPosition, 1));
					scanPosition++;
					break;
				case '+':
					tokens.add(new Token(Kind.PLUS, tokenStartPosition, 1));
					scanPosition++;
					break;
				case '%':
					tokens.add(new Token(Kind.MOD, tokenStartPosition, 1));
					scanPosition++;
					break;
				case '*':
					tokens.add(new Token(Kind.TIMES, tokenStartPosition, 1));
					scanPosition++;
					break;
				case '=':
					scanState = ScanState.AFTER_EQUAL;
					scanPosition++;
					break;
				case '-':
					scanState = ScanState.AFTER_MINUS;
					scanPosition++;
					break;	
				case '|':
					scanState = ScanState.AFTER_PIPE;
					scanPosition++;
					break;	
				case '<':
					scanState = ScanState.AFTER_LESSTHAN;
					scanPosition++;
					break;	
				case '>':
					scanState = ScanState.AFTER_GREATERTHAN;
					scanPosition++;
					break;	
				case '!':
					scanState = ScanState.AFTER_EXCLAMATION;
					scanPosition++;
					break;	
				case '/':
					scanState = ScanState.AFTER_SLASH;
					scanPosition++;
					break;
				default:
					if (isDigit(ch)) {
						scanState = ScanState.IN_DIGIT;
					} else if (isIdentifierStart(ch)) {
						scanState = ScanState.IN_IDENTIFIER;
					} else {
						throw new IllegalCharException("Illegal character '" + ch +"' at " + getPositionText(tokenStartPosition) + ". Unsupported character.");
					}
					scanPosition++;
					break;
				}//switch (ch)
				break;
			}//case START
			case AFTER_EQUAL:
				if (ch == '=') {
					tokens.add(new Token(Kind.EQUAL, tokenStartPosition, 2));
					scanPosition++;
				} else {
					throw new IllegalCharException("Illegal character '=' at " + getPositionText(tokenStartPosition) + ". Unsupported character.");
				}
				scanState = ScanState.START;
				break;
			case AFTER_MINUS:
				if (ch == '>') {
					tokens.add(new Token(Kind.ARROW, tokenStartPosition, 2));
					scanPosition++;
				} else {
					tokens.add(new Token(Kind.MINUS, tokenStartPosition, 1));
				}
				scanState = ScanState.START;
				break;
			case AFTER_PIPE:
				if (ch == '-') {
					scanState = ScanState.AFTER_PIPE_MINUS;
					scanPosition++;
				} else {
					tokens.add(new Token(Kind.OR, tokenStartPosition, 1));
					scanState = ScanState.START;
				}
				break;
			case AFTER_PIPE_MINUS:
				if (ch == '>') {
					tokens.add(new Token(Kind.BARARROW, tokenStartPosition, 3));
					scanPosition++;
				} else {
					tokens.add(new Token(Kind.OR, tokenStartPosition, 1));
					tokens.add(new Token(Kind.MINUS, tokenStartPosition + 1, 1));
				}
				scanState = ScanState.START;
				break;
			case AFTER_LESSTHAN:
				if (ch == '=') {
					tokens.add(new Token(Kind.LE, tokenStartPosition, 2));
					scanPosition++;
				} else if (ch == '-') {
					tokens.add(new Token(Kind.ASSIGN, tokenStartPosition, 2));
					scanPosition++;
				} else {
					tokens.add(new Token(Kind.LT, tokenStartPosition, 1));
				}
				scanState = ScanState.START;
				break;
			case AFTER_GREATERTHAN:
				if (ch == '=') {
					tokens.add(new Token(Kind.GE, tokenStartPosition, 2));
					scanPosition++;
				} else {
					tokens.add(new Token(Kind.GT, tokenStartPosition, 1));
				}
				scanState = ScanState.START;
				break;
			case AFTER_EXCLAMATION:
				if (ch == '=') {
					tokens.add(new Token(Kind.NOTEQUAL, tokenStartPosition, 2));
					scanPosition++;
				} else {
					tokens.add(new Token(Kind.NOT, tokenStartPosition, 1));
				}
				scanState = ScanState.START;
				break;
			case AFTER_SLASH:
				if (ch == '*') {
					scanState = ScanState.IN_COMMENT;
					scanPosition++;
				} else {
					tokens.add(new Token(Kind.DIV, tokenStartPosition, 1));
					scanState = ScanState.START;
				}
				break;
			case IN_COMMENT:
				if (ch =='*') {
					scanState = ScanState.IN_COMMENT_AFTER_STAR;
				}
				scanPosition++;
				break;
			case IN_COMMENT_AFTER_STAR:
				if (ch == '/') {
					scanState = ScanState.START;
				} else if (ch != '*') {
					scanState = ScanState.IN_COMMENT;
				}
				scanPosition++;
				break;
			case IN_DIGIT: {
				if (isDigit(ch)) {
					scanPosition++;
				} else {
					Token tempToken = new Token(Kind.INT_LIT, tokenStartPosition, scanPosition - tokenStartPosition);
					try {
						tempToken.intVal();
					}
					catch (NumberFormatException e) {
						throw new IllegalNumberException("Illegal number " + chars.substring(tokenStartPosition, scanPosition) + " at " + getPositionText(tokenStartPosition) + ". Out of range of int.");
					}
					tokens.add(tempToken);
					scanState = ScanState.START;
				}
				break;
			}//case IN_DIGIT
			case IN_IDENTIFIER: {
				if (isIdentifierPart(ch)) {
					scanPosition++;
				} else {
					Kind kind = reservedWordTable.get(chars.substring(tokenStartPosition, scanPosition));
					if (kind == null) {
						kind = Kind.IDENT;
					}
					tokens.add(new Token(kind, tokenStartPosition, scanPosition - tokenStartPosition));
					scanState = ScanState.START;
				}
				break;
			}//case IN_IDENTIFIER
			default:
				break;
			}//switch (scanState)
		}//while(scanPosition < codeLength)
		//add EOF as the last token
		tokens.add(new Token(Kind.EOF,codeLength,0));
		return this;
	}
	
	//verify if character is digit
	private final boolean isDigit(char ch) {
		if (ch >= '0' && ch <= '9') {
			return true;
		} else {
			return false;
		}
	}

	//verify if character is ident_start
	private final boolean isIdentifierStart(char ch) {
		if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch == '$' || ch == '_') {
			return true;
		} else {
			return false;
		}
	}
	
	//verify if character is ident_part
	private final boolean isIdentifierPart(char ch) {
		if (isIdentifierStart(ch) || isDigit(ch)) {
			return true;
		} else {
			return false;
		}
	}

	private final String getPositionText(int position){
		return new Token(Kind.IDENT, position, 0).getPositionText();
	}
	
	final HashMap<String, Kind> reservedWordTable;
	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;
	
	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	 /*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek() {
	    if (tokenNum >= tokens.size())
	        return null;
	    return tokens.get(tokenNum);
	}

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		return t.getLinePos();
	}


}
