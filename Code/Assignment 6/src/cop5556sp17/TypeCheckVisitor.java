package cop5556sp17;

import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
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
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		Chain chain;
		ChainElem chainElem;
		Token operator, chainElemFirstToken;
		TypeName binaryChainType, chainType, chainElemType;

		chain = binaryChain.getE0();
		chainElem = binaryChain.getE1();
		operator = binaryChain.getArrow();
		chain.visit(this, arg);
		chainElem.visit(this, arg);
		chainType = chain.getChainType();
		chainElemType = chainElem.getChainType();
		chainElemFirstToken = chainElem.getFirstToken();
		switch (operator.kind) {
		case ARROW:
			switch (chainType) {
			case URL:
			case FILE:
				if (chainElemType == IMAGE) {
					binaryChainType = IMAGE;
				} else {
					throw new TypeCheckException("Incorrect type " + chainElemType.toString() + " of chain elem starting with \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect IMAGE.");
				}
				break;
			case FRAME:
				if (chainElem instanceof FrameOpChain) {
					switch (chainElemFirstToken.kind) {
					case KW_XLOC:
					case KW_YLOC:
						binaryChainType = INTEGER;
						break;
					case KW_SHOW:
					case KW_HIDE:
					case KW_MOVE:
						binaryChainType = FRAME;
						break;
					default:
						throw new TypeCheckException("Incorrect token \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect one of following: xloc, yloc, show, hide, move.");
					}	
				} else {
					throw new TypeCheckException("Incorrect class " + chainElem.getClass().getSimpleName() + " of chain elem starting with \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect FrameOpChain.");
				}
				break;
			case IMAGE:
				if (chainElemType == FRAME) {
					binaryChainType = FRAME;
				} else if (chainElemType == FILE) {
					binaryChainType = NONE;
				} else if (chainElem instanceof ImageOpChain) {
					switch (chainElemFirstToken.kind) {
					case OP_WIDTH:
					case OP_HEIGHT:
						binaryChainType = INTEGER;
						break;
					case KW_SCALE:
						binaryChainType = IMAGE;
						break;
					default:
						throw new TypeCheckException("Incorrect token \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect one of following: width, height, scale.");
					}
				} else if (chainElem instanceof FilterOpChain) {
					switch (chainElemFirstToken.kind) {
					case OP_BLUR:
					case OP_GRAY:
					case OP_CONVOLVE:
						binaryChainType = IMAGE;
						break;
					default:
						throw new TypeCheckException("Incorrect token \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect one of following: blur, gray, convolve.");
					}
				} else if (chainElem instanceof IdentChain) {
					if (chainElemType == IMAGE) {	//updated in assignment 6
						binaryChainType = IMAGE;
					} else {
						throw new TypeCheckException("Incorrect type " + chainElemType.toString() + " of chain elem starting with \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect IMAGE.");
					}
				} else {
					throw new TypeCheckException("Incorrect type " + chainElemType.toString() + " or class " + chainElem.getClass().getSimpleName() + " of chain elem starting with \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect one of following type or class: FRAME, FILE, ImageOpChain, FilterOpChain, IdentChain.");					
				}
				break;
			case INTEGER:	//added in assignment 6
				if (chainElem instanceof IdentChain) {
					if (chainElemType == INTEGER) {
						binaryChainType = INTEGER;
					} else {
						throw new TypeCheckException("Incorrect type " + chainElemType.toString() + " of chain elem starting with \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect INTEGER.");
					}
				} else {
					throw new TypeCheckException("Incorrect class " + chainElem.getClass().getSimpleName() + " of chain elem starting with \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect IdentChain.");
				}
				break;
			default:
				throw new TypeCheckException("Incorrect type " + chainType.toString() + " of chain before \"" + operator.getText() + " " + chainElemFirstToken.getText() + "\" at " + chain.getFirstToken().getPositionText() + ". Expect one of following: URL, FILE, FRAME, IMAGE, INTEGER.");
			}
			break;
		case BARARROW:
			if (chainType == IMAGE) {
				if (chainElem instanceof FilterOpChain) {
					switch (chainElemFirstToken.kind) {
					case OP_BLUR:
					case OP_GRAY:
					case OP_CONVOLVE:
						binaryChainType = IMAGE;
						break;
					default:
						throw new TypeCheckException("Incorrect token \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect one of following: blur, gray, convolve.");
					}
				} else {
					throw new TypeCheckException("Incorrect class " + chainElem.getClass().getSimpleName() + " of chain elem starting with \"" + chainElemFirstToken.getText() + "\" at " + chainElemFirstToken.getPositionText() + ". Expect FilterOpChain.");					
				}
			} else {
				throw new TypeCheckException("Incorrect type " + chainType.toString() + " of chain before \"" + operator.getText() + " " + chainElemFirstToken.getText() + "\" at " + chain.getFirstToken().getPositionText() + ". Expect IMAGE.");	
			}
			break;
		default: 
			throw new TypeCheckException("Incorrect operator \"" + operator.getText() + "\" at " + operator.getPositionText() + ". Cannot use the operator in binary chain.");
		}
		binaryChain.setChainType(binaryChainType);
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		Expression expression0, expression1;
		Token operator;
		TypeName binaryExpressionType, expression0Type, expression1Type;
		
		expression0 = binaryExpression.getE0();
		expression1 = binaryExpression.getE1();
		operator = binaryExpression.getOp();
		expression0.visit(this, arg);
		expression1.visit(this, arg);
		expression0Type = expression0.getExpressionType();
		expression1Type = expression1.getExpressionType();
		switch(operator.kind) {
		case PLUS:
		case MINUS:
			if (expression0Type == INTEGER && expression1Type == INTEGER) {
				binaryExpressionType = INTEGER;
			} else if (expression0Type == IMAGE && expression1Type == IMAGE) {
				binaryExpressionType = IMAGE;
			} else {
				throw new TypeCheckException("Cannot use operator \"" + operator.getText() + "\" between expression type " + expression0Type.toString() + " and " + expression1Type.toString() + " at " + operator.getPositionText() + ".");
			}
			break;
		case TIMES:
			if (expression0Type == INTEGER && expression1Type == INTEGER) {
				binaryExpressionType = INTEGER;
			} else if (expression0Type == INTEGER && expression1Type == IMAGE) {
				binaryExpressionType = IMAGE;
			} else if (expression0Type == IMAGE && expression1Type == INTEGER) {
				binaryExpressionType = IMAGE;
			} else {
				throw new TypeCheckException("Cannot use operator \"" + operator.getText() + "\" between expression type " + expression0Type.toString() + " and " + expression1Type.toString() + " at " + operator.getPositionText() + ".");
			}			
			break;
		case DIV:
			if (expression0Type == INTEGER && expression1Type == INTEGER) {
				binaryExpressionType = INTEGER;
			} else if (expression0Type == IMAGE && expression1Type == INTEGER) {	//added in assignment 6
				binaryExpressionType = IMAGE;
			} else {
				throw new TypeCheckException("Cannot use operator \"" + operator.getText() + "\" between expression type " + expression0Type.toString() + " and " + expression1Type.toString() + " at " + operator.getPositionText() + ".");
			}
			break;
		case LT:
		case LE:
		case GT:
		case GE:
			if (expression0Type == INTEGER && expression1Type == INTEGER) {
				binaryExpressionType = BOOLEAN;
			} else if (expression0Type == BOOLEAN && expression1Type == BOOLEAN) {
				binaryExpressionType = BOOLEAN;
			} else {
				throw new TypeCheckException("Cannot use operator \"" + operator.getText() + "\" between expression type " + expression0Type.toString() + " and " + expression1Type.toString() + " at " + operator.getPositionText() + ".");
			}
			break;
		case EQUAL:
		case NOTEQUAL:
			if (expression0Type == expression1Type) {
				binaryExpressionType = BOOLEAN;
			} else {
				throw new TypeCheckException("Cannot use operator \"" + operator.getText() + "\" between expression type " + expression0Type.toString() + " and " + expression1Type.toString() + " at " + operator.getPositionText() + ".");
			}
			break;
		case AND:
		case OR:
			if (expression0Type == BOOLEAN && expression1Type == BOOLEAN) {
				binaryExpressionType = BOOLEAN;
			} else {
				throw new TypeCheckException("Cannot use operator \"" + operator.getText() + "\" between expression type " + expression0Type.toString() + " and " + expression1Type.toString() + " at " + operator.getPositionText() + ".");				
			}
			break;
		case MOD:	//added in assignment 6
			if (expression0Type == INTEGER && expression1Type == INTEGER) {
				binaryExpressionType = INTEGER;				
			} else if (expression0Type == IMAGE && expression1Type == INTEGER) {
				binaryExpressionType = IMAGE;
			} else {
				throw new TypeCheckException("Cannot use operator \"" + operator.getText() + "\" between expression type " + expression0Type.toString() + " and " + expression1Type.toString() + " at " + operator.getPositionText() + ".");								
			}
			break;
		default: 
			throw new TypeCheckException("Cannot use operator \"" + operator.getText() + "\" between expression type " + expression0Type.toString() + " and " + expression1Type.toString() + " at " + operator.getPositionText() + ".");
		}
		binaryExpression.setExpressionType(binaryExpressionType);
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symtab.enterScope();
		for (Dec dec : block.getDecs()) {
			dec.visit(this, arg);
		}
		for (Statement statement : block.getStatements()) {
			statement.visit(this, arg);
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		booleanLitExpression.setExpressionType(BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		Tuple tuple;

		tuple = filterOpChain.getArg();
		tuple.visit(this, arg);		
		if (tuple.getExprList().size() != 0) {
			throw new TypeCheckException("Incorrect tuple of filter op chain at " + tuple.getFirstToken().getPositionText() + ". Expect no tuple.");
		}
		filterOpChain.setChainType(IMAGE);
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		Tuple tuple;
		Token token;
		TypeName type;
		int legalTupleSize;

		tuple = frameOpChain.getArg();
		tuple.visit(this, arg);		
		token = frameOpChain.getFirstToken();
		switch (token.kind) {
		case KW_SHOW:
		case KW_HIDE:
			legalTupleSize = 0;
			type = NONE;
			break;
		case KW_XLOC:
		case KW_YLOC:
			legalTupleSize = 0;
			type = INTEGER;
			break;
		case KW_MOVE:
			legalTupleSize = 2;
			type = NONE;
			break;
		default:
			throw new TypeCheckException("Incorrect operator of frame op chain. Compilier error.");
		}
		if (tuple.getExprList().size() != legalTupleSize) {
			throw new TypeCheckException("Incorrect tuple of frame op chain at " + tuple.getFirstToken().getPositionText() + ". Expect " + (legalTupleSize == 0 ? "no tuple." : legalTupleSize + " tuples."));
		}
		frameOpChain.setChainType(type);
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		Token ident;
		Dec dec;
		TypeName type;
		
		ident = identChain.getFirstToken();
		dec = symtab.lookup(ident.getText());
		if (dec == null) {
			throw new TypeCheckException("Undeclared identifier \"" + ident.getText() + "\" at " + ident.getPositionText() + ".");
		}
		type = Type.getTypeName(dec.getType());
		identChain.setChainType(type);
		identChain.setDec(dec);
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		Token ident;
		Dec dec;
		TypeName type;
		
		ident = identExpression.getFirstToken();
		dec = symtab.lookup(ident.getText());
		if (dec == null) {
			throw new TypeCheckException("Undeclared identifier \"" + ident.getText() + "\" at " + ident.getPositionText() + ".");
		}
		type = Type.getTypeName(dec.getType());
		identExpression.setExpressionType(type);
		identExpression.setDec(dec);
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		Expression expression;
		Block block;

		expression = ifStatement.getE();
		block = ifStatement.getB();
		expression.visit(this, arg);
		block.visit(this, arg);
		if (expression.getExpressionType() != BOOLEAN) {
			throw new TypeCheckException("Incorrect type " + expression.getExpressionType().toString() + " of expression in if statement at " + expression.getFirstToken().getPositionText() + ". Expect BOOLEAN.");
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		intLitExpression.setExpressionType(INTEGER);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		Expression expression;

		expression = sleepStatement.getE();
		expression.visit(this, arg);
		if (expression.getExpressionType() != INTEGER) {
			throw new TypeCheckException("Incorrect type " + expression.getExpressionType().toString() + " of expression in sleep statement at " + expression.getFirstToken().getPositionText() + ". Expect INTEGER.");
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		Expression expression;
		Block block;

		expression = whileStatement.getE();
		block = whileStatement.getB();
		expression.visit(this, arg);
		block.visit(this, arg);
		if (expression.getExpressionType() != BOOLEAN) {
			throw new TypeCheckException("Incorrect type " + expression.getExpressionType().toString() + " of expression in while statement at " + expression.getFirstToken().getPositionText() + ". Expect BOOLEAN.");
		}
		return null;
	}

	@Override
	public Object visitDec(Dec dec, Object arg) throws Exception {
		Token ident;
		TypeName type;

		ident = dec.getIdent();
		type = Type.getTypeName(dec.getType());
		dec.setDecType(type);
		if (symtab.insert(ident.getText(), dec) == false) {
			throw new TypeCheckException("Duplicate declaration of identifier \"" + ident.getText() + "\" at " + ident.getPositionText() + ". \"" + ident.getText() + "\" is already declared at " + symtab.lookup(ident.getText()).getIdent().getPositionText() + ".");
		}
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ParamDec paramDec: program.getParams()) {
			paramDec.visit(this, arg);
		}
		program.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		IdentLValue identLValue;
		Expression expression;
				
		identLValue = assignStatement.getVar();
		expression = assignStatement.getE();
		identLValue.visit(this, arg);
		expression.visit(this, arg);
		if (identLValue.getIdentLValueType() != expression.getExpressionType()) {
			throw new TypeCheckException("Incorrect type " + expression.getExpressionType().toString() + " of expression in assignment statement at " + expression.getFirstToken().getPositionText() + ". Expect " + identLValue.getIdentLValueType().toString() + ".");
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg) throws Exception {
		Token ident;
		Dec dec;
		TypeName type;

		ident = identLValue.getFirstToken();
		dec = symtab.lookup(ident.getText());
		if (dec == null) {
			throw new TypeCheckException("Undeclared identifier \"" + ident.getText() + "\" at " + ident.getPositionText() + ".");
		}
		type = Type.getTypeName(dec.getType());
		identLValue.setIdentLValueType(type);
		identLValue.setDec(dec);
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		Token ident;
		TypeName type;
				
		ident = paramDec.getIdent();
		type = Type.getTypeName(paramDec.getType());
		paramDec.setDecType(type);
		if (symtab.insert(ident.getText(), paramDec) == false) {
			throw new TypeCheckException("Duplicate declaration of identifier \"" + ident.getText() + "\" at " + ident.getPositionText() + ". \"" + ident.getText() + "\" is already declared at " + symtab.lookup(ident.getText()).getIdent().getPositionText() + ".");
		}
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		constantExpression.setExpressionType(INTEGER);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		Tuple tuple;
		Token token;
		TypeName type;
		int legalTupleSize;
		
		tuple = imageOpChain.getArg();
		tuple.visit(this, arg);		
		token = imageOpChain.getFirstToken();
		switch (token.kind) {
		case OP_WIDTH:
		case OP_HEIGHT:
			legalTupleSize = 0;
			type = INTEGER;
			break;
		case KW_SCALE:
			legalTupleSize = 1;
			type = IMAGE;
			break;
		default:
			throw new TypeCheckException("Incorrect operator of image op chain. Compilier error.");
		}
		if (tuple.getExprList().size() != legalTupleSize) {
			throw new TypeCheckException("Incorrect tuple of image op chain at " + tuple.getFirstToken().getPositionText() + ". Expect " + (legalTupleSize == 0 ? "no tuple." : legalTupleSize + " tuple."));
		}
		imageOpChain.setChainType(type);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		for (Expression expression: tuple.getExprList()) {
			expression.visit(this, arg);
			if (expression.getExpressionType() != INTEGER) {
				throw new TypeCheckException("Incorrect type " + expression.getExpressionType().toString() + " of expression in tuple at " + expression.getFirstToken().getPositionText() + ". Expect INTEGER.");
			}
		}
		return null;
	}

}
