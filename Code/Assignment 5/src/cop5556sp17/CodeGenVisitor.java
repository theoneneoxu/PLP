package cop5556sp17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		int argIndex = 0;
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec paramDec : params) {
			paramDec.visit(this, argIndex++);			
		}
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label runStart = new Label();
		mv.visitLabel(runStart);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		//pass 1 as local variable table slot count due to "this" has taken slot 0
		program.getB().visit(this, 1);
		mv.visitInsn(RETURN);
		Label runEnd = new Label();
		mv.visitLabel(runEnd);
		mv.visitLocalVariable("this", classDesc, null, runStart, runEnd, 0);
//TODO  visit the local variables
		//local variables are visited in visitBlock()
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method

		cw.visitEnd();//end of class
		//generate classfile and return it
		return cw.toByteArray();
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + " = ");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getExpressionType());
		//CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		Expression expression0, expression1;
		Token operator;
		Label label1, label2;
		
		expression0 = binaryExpression.getE0();
		expression1 = binaryExpression.getE1();
		operator = binaryExpression.getOp();
		expression0.visit(this, arg);
		expression1.visit(this, arg);
		//Neo: for now, only integer and boolean are considered
		switch(operator.kind) {
		case PLUS:
			mv.visitInsn(IADD);
			break;
		case MINUS:
			mv.visitInsn(ISUB);
			break;
		case TIMES:
			mv.visitInsn(IMUL);
			break;
		case DIV:
			mv.visitInsn(IDIV);
			break;
		case LT:
			label1 = new Label();
			mv.visitJumpInsn(IF_ICMPGE, label1);
			mv.visitInsn(ICONST_1);
			label2 = new Label();
			mv.visitJumpInsn(GOTO, label2);
			mv.visitLabel(label1);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(label2);
			break;
		case LE:
			label1 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, label1);
			mv.visitInsn(ICONST_1);
			label2 = new Label();
			mv.visitJumpInsn(GOTO, label2);
			mv.visitLabel(label1);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(label2);
			break;
		case GT:
			label1 = new Label();
			mv.visitJumpInsn(IF_ICMPLE, label1);
			mv.visitInsn(ICONST_1);
			label2 = new Label();
			mv.visitJumpInsn(GOTO, label2);
			mv.visitLabel(label1);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(label2);
			break;
		case GE:
			label1 = new Label();
			mv.visitJumpInsn(IF_ICMPLT, label1);
			mv.visitInsn(ICONST_1);
			label2 = new Label();
			mv.visitJumpInsn(GOTO, label2);
			mv.visitLabel(label1);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(label2);
			break;
		case EQUAL:
			label1 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, label1);
			mv.visitInsn(ICONST_1);
			label2 = new Label();
			mv.visitJumpInsn(GOTO, label2);
			mv.visitLabel(label1);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(label2);
			break;
		case NOTEQUAL:
			label1 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, label1);
			mv.visitInsn(ICONST_1);
			label2 = new Label();
			mv.visitJumpInsn(GOTO, label2);
			mv.visitLabel(label1);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(label2);
			break;
		case AND:
			mv.visitInsn(IAND);
			break;
		case OR:
			mv.visitInsn(IOR);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		Label blockStart, blockEnd;
		ArrayList<Dec> decList;
		ArrayList<Statement> statementList;
		String variableName, variableTypeDesc;
		int variableSlot;	//slot number in local variable table	
	
		variableSlot = (Integer)arg;
		decList = block.getDecs();
		for (Dec dec : decList) {
			dec.visit(this, variableSlot++);	//visit each dec to set assigned slot number
		}
		blockStart = new Label();
		mv.visitLabel(blockStart);
		statementList = block.getStatements();
		for (Statement statement : statementList) {
			statement.visit(this, variableSlot);	//pass variableSlot as current slot count to all statements, only used by the blocks of ifStatement and whileStatement
		}
		blockEnd = new Label();
		mv.visitLabel(blockEnd);
		//add variables of current block to jvm local variable table
		for (Dec dec : decList) {
			variableName = dec.getIdent().getText();
			variableTypeDesc = dec.getDecType().getJVMTypeDesc();
			variableSlot = dec.getSlot();
			mv.visitLocalVariable(variableName, variableTypeDesc, null, blockStart, blockEnd, variableSlot);
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		int value;

		value = booleanLitExpression.getValue() == true ? 1 : 0;		
		mv.visitLdcInsn(value);	//load constant
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitDec(Dec dec, Object arg) throws Exception {
		int variableSlot;	//slot number in local variable table
		
		variableSlot = (Integer)arg;
		dec.setSlot(variableSlot);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		String variableName, variableTypeDesc;
		Dec dec;
		TypeName decType;
		int variableSlot;	//slot number in local variable table	
		
		dec = identExpression.getDec();
		if (dec instanceof ParamDec) {	//instance variable (class field)
			variableName = dec.getIdent().getText();
			variableTypeDesc = dec.getDecType().getJVMTypeDesc();
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitFieldInsn(GETFIELD, className, variableName, variableTypeDesc);
		} else {	//local variable
			variableSlot = dec.getSlot();
			decType = dec.getDecType();
			switch(decType) {
			case INTEGER:
			case BOOLEAN:
				mv.visitVarInsn(ILOAD, variableSlot);
				break;
			case IMAGE:
			case FRAME:
			case URL:
			case FILE:
				mv.visitVarInsn(ALOAD, variableSlot);
				break;
			case NONE:
				break;
			default:
				break;
			}
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identLValue, Object arg) throws Exception {
		String variableName, variableTypeDesc;
		Dec dec;
		TypeName decType;
		int variableSlot;	//slot number in local variable table	
		
		dec = identLValue.getDec();
		if (dec instanceof ParamDec) {	//instance variable (class field)
			variableName = dec.getIdent().getText();
			variableTypeDesc = dec.getDecType().getJVMTypeDesc();
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitInsn(SWAP);			//swap the two values in stack to make "this" (just loaded) in front of the expression value
			mv.visitFieldInsn(PUTFIELD, className, variableName, variableTypeDesc);
		} else {	//local variable
			variableSlot = dec.getSlot();
			decType = dec.getDecType();
			switch(decType) {
			case INTEGER:
			case BOOLEAN:
				mv.visitVarInsn(ISTORE, variableSlot);
				break;
			case IMAGE:
			case FRAME:
			case URL:
			case FILE:
				mv.visitVarInsn(ASTORE, variableSlot);
				break;
			case NONE:
				break;
			default:
				break;
			}
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		Expression expression;
		Block block;
		Label after;
		
		expression = ifStatement.getE();
		expression.visit(this, arg);	//visit expression leaves the value of expression on top of stack
		after = new Label();
		mv.visitJumpInsn(IFEQ, after);
		block = ifStatement.getB();
		block.visit(this, arg);
		mv.visitLabel(after);	//visit label after block to get the actual line number which the execution may jump to
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		int value;

		value = intLitExpression.getValue();
		mv.visitLdcInsn(value);	//load constant
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		FieldVisitor fv;
		String variableName, variableTypeDesc;
		TypeName decType;
		int argIndex;
		
		decType = paramDec.getDecType();
		variableName = paramDec.getIdent().getText();
		variableTypeDesc = decType.getJVMTypeDesc();
		//add field
		fv = cw.visitField(0, variableName, variableTypeDesc, null, null);
		fv.visitEnd();
		//set field value
		argIndex = (Integer)arg;
		mv.visitVarInsn(ALOAD, 0);	//load "this"
		mv.visitVarInsn(ALOAD, 1);	//load "args"
		mv.visitLdcInsn(argIndex);	//load constant
		mv.visitInsn(AALOAD);
		switch(decType) {
		case INTEGER:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);	//convert string to integer
			break;
		case BOOLEAN:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);	//convert string to boolean
			break;
		case IMAGE:
			break;
		case FRAME:
			break;
		case URL:
			break;
		case FILE:
			break;
		case NONE:
			break;
		default:
			break;
		}
		mv.visitFieldInsn(PUTFIELD, className, variableName, variableTypeDesc);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		Expression expression;
		Block block;
		Label guard, body;
		
		guard = new Label();
		mv.visitJumpInsn(GOTO, guard);
		body = new Label();
		mv.visitLabel(body);
		block = whileStatement.getB();
		block.visit(this, arg);
		mv.visitLabel(guard);	//visit label to get the actual line number which the execution may jump to
		expression = whileStatement.getE();
		expression.visit(this, arg);	//visit expression leaves the value of expression on top of stack
		mv.visitJumpInsn(IFNE, body);
		return null;
	}

}
