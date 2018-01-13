package cop5556sp17;

//import java.io.PrintWriter;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

//import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
//import org.objectweb.asm.util.TraceClassVisitor;

//import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

//import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
//import static cop5556sp17.AST.Type.TypeName.INTEGER;
//import static cop5556sp17.AST.Type.TypeName.NONE;
//import static cop5556sp17.AST.Type.TypeName.URL;
//import static cop5556sp17.Scanner.Kind.*;

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
		//local variables are visited in visitBlock()
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method

		cw.visitEnd();//end of class
		//generate classfile and return it
		return cw.toByteArray();
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object slotCount) throws Exception {
		assignStatement.getE().visit(this, slotCount);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + " = ");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getExpressionType());
		//CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		assignStatement.getVar().visit(this, slotCount);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object slotCount) throws Exception {
		Chain chain;
		ChainElem chainElem;
		TypeName chainType;
		
		chain = binaryChain.getE0();
		chain.visit(this, false);	//chain elem is on left side on binary chain
		chainType = chain.getChainType();
		switch (chainType) {
		case FILE:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
			//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "readFromFile", "(Ljava/io/File;)Ljava/awt/image/BufferedImage;", false);
			break;
		case URL:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
			//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "readFromURL", "(Ljava/net/URL;)Ljava/awt/image/BufferedImage;", false);
			break;
		default:
			break;
		}
		chainElem = binaryChain.getE1();
		chainElem.visit(this, true);	//chain elem is on right side of binary chain, visit after possible processing for file/url
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object slotCount) throws Exception {
		Expression expression0, expression1;
		Token operator;
		Label label1, label2;
		TypeName expression0Type, expression1Type;

		expression0 = binaryExpression.getE0();
		expression1 = binaryExpression.getE1();
		operator = binaryExpression.getOp();
		expression0.visit(this, slotCount);
		expression1.visit(this, slotCount);
		expression0Type = expression0.getExpressionType();
		expression1Type = expression1.getExpressionType();
		switch(operator.kind) {
		case PLUS:
			if (expression0Type == TypeName.INTEGER && expression1Type == TypeName.INTEGER) {
				mv.visitInsn(IADD);
			} else if (expression0Type == IMAGE && expression1Type == IMAGE) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "add", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			}
			break;
		case MINUS:
			if (expression0Type == TypeName.INTEGER && expression1Type == TypeName.INTEGER) {
				mv.visitInsn(ISUB);
			} else if (expression0Type == IMAGE && expression1Type == IMAGE) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "sub", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			}
			break;
		case TIMES:
			if (expression0Type == TypeName.INTEGER && expression1Type == TypeName.INTEGER) {
				mv.visitInsn(IMUL);
			} else if (expression0Type == TypeName.INTEGER && expression1Type == IMAGE) {
				mv.visitInsn(SWAP);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "mul", "(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;", false);
			} else if (expression0Type == IMAGE && expression1Type == TypeName.INTEGER) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "mul", "(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;", false);
			}
			break;
		case DIV:
			if (expression0Type == TypeName.INTEGER && expression1Type == TypeName.INTEGER) {
				mv.visitInsn(IDIV);
			} else if (expression0Type == IMAGE && expression1Type == TypeName.INTEGER) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "div", "(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;", false);
			}
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
			if ((expression0Type == TypeName.INTEGER && expression1Type == TypeName.INTEGER) ||
				(expression0Type == TypeName.BOOLEAN && expression1Type == TypeName.BOOLEAN)) {
				label1 = new Label();
				mv.visitJumpInsn(IF_ICMPNE, label1);
				mv.visitInsn(ICONST_1);
				label2 = new Label();
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(label2);
			} else {
				label1 = new Label();
				mv.visitJumpInsn(IF_ACMPNE, label1);
				mv.visitInsn(ICONST_1);
				label2 = new Label();
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(label2);
			}
			break;
		case NOTEQUAL:
			if ((expression0Type == TypeName.INTEGER && expression1Type == TypeName.INTEGER) ||
				(expression0Type == TypeName.BOOLEAN && expression1Type == TypeName.BOOLEAN)) {
				label1 = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, label1);
				mv.visitInsn(ICONST_1);
				label2 = new Label();
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(label2);
			} else {
				label1 = new Label();
				mv.visitJumpInsn(IF_ACMPEQ, label1);
				mv.visitInsn(ICONST_1);
				label2 = new Label();
				mv.visitJumpInsn(GOTO, label2);
				mv.visitLabel(label1);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(label2);
			}
			break;
		case AND:
			mv.visitInsn(IAND);
			break;
		case OR:
			mv.visitInsn(IOR);
			break;
		case MOD:
			if (expression0Type == TypeName.INTEGER && expression1Type == TypeName.INTEGER) {
				mv.visitInsn(IREM);
			} else if (expression0Type == IMAGE && expression1Type == TypeName.INTEGER) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "mod", "(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;", false);
			}
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object slotCount) throws Exception {
		Label blockStart, blockEnd;
		ArrayList<Dec> decList;
		ArrayList<Statement> statementList;
		String variableName, variableTypeDesc;
		int variableSlot;	//slot number in local variable table	
	
		variableSlot = (Integer)slotCount;
		decList = block.getDecs();
		for (Dec dec : decList) {
			dec.visit(this, variableSlot++);	//visit each dec to set assigned slot number and initialize some local variables
		}
		blockStart = new Label();
		mv.visitLabel(blockStart);
		statementList = block.getStatements();
		for (Statement statement : statementList) {
			statement.visit(this, variableSlot);	//pass variableSlot as current slot count to all statements, only used by the blocks of ifStatement and whileStatement
			if (statement instanceof BinaryChain) {	//if statement is a binary chain, it always leaves a value on top of stack
				mv.visitInsn(POP);	//get rid of the value on top of stack
			}
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
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object slotCount) throws Exception {
		int value;

		value = booleanLitExpression.getValue() == true ? 1 : 0;		
		mv.visitLdcInsn(value);	//load constant
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object slotCount) {
		Token token;
		
		token = constantExpression.getFirstToken();
		switch (token.kind) {
		case KW_SCREENWIDTH:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
			//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFrame", "getScreenWidth", "()I", false);
			break;
		case KW_SCREENHEIGHT:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
			//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFrame", "getScreenHeight", "()I", false);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitDec(Dec dec, Object slotCount) throws Exception {
		TypeName decType;
		int variableSlot;	//slot number in local variable table
		
		variableSlot = (Integer)slotCount;
		dec.setSlot(variableSlot);
		//initialize local variable value
		decType = dec.getDecType();
		switch (decType) {
		case IMAGE:
		case FRAME:
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, variableSlot);
			break;
		default:
			break;
		}
		return null;
	}

	//assume filter operation changes source image itself rather than return a newly created image
	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object rightSide) throws Exception {
		Tuple tuple;
		Token token;
		
		tuple = filterOpChain.getArg();
		tuple.visit(this, rightSide);
		token = filterOpChain.getFirstToken();
		mv.visitInsn(DUP);		//duplicate the image reference on top of stack and then make a copy of its content
		mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
		//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "copyImage", "(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
		mv.visitInsn(SWAP);		//swap to make the copy as source and the original as destination, so the original image is changed
		switch (token.kind) {
		case OP_BLUR:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);
			//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFilterOps", "blurOp", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			break;
		case OP_GRAY:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
			//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFilterOps", "grayOp", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			break;
		case OP_CONVOLVE:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig, false);
			//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFilterOps", "convolveOp", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object rightSide) throws Exception {
		Tuple tuple;
		Token token;
		
		tuple = frameOpChain.getArg();
		tuple.visit(this, rightSide);
		token = frameOpChain.getFirstToken();
		switch (token.kind) {
		case KW_SHOW:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc, false);
			//mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "showImage", "()Lcop5556sp17/PLPRuntimeFrame;", false);
			break;
		case KW_HIDE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc, false);
			//mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "hideImage", "()Lcop5556sp17/PLPRuntimeFrame;", false);
			break;
		case KW_XLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc, false);
			//mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "getXVal", "()I", false);
			break;
		case KW_YLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc, false);
			//mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "getYVal", "()I", false);
			break;
		case KW_MOVE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc, false);
			//mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "moveFrame", "(II)Lcop5556sp17/PLPRuntimeFrame;", false);
			break;
		default:
			break;		
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object rightSide) throws Exception {
		String variableName, variableTypeDesc;
		Dec dec;
		Boolean isOnRightSide;
		TypeName decType;
		int variableSlot;	//slot number in local variable table
		
		dec = identChain.getDec();
		decType = dec.getDecType();
		variableName = dec.getIdent().getText();
		variableTypeDesc = decType.getJVMTypeDesc();
		variableSlot = dec.getSlot();	//slot of ParamDec is not set, hence cannot be used for instance variable
		isOnRightSide = (Boolean)rightSide;
		if (isOnRightSide) {	//ident is on right side of binary chain, store value on top of stack
			switch(decType) {
			case INTEGER:	//integer could be either instance variable or local variable
				mv.visitInsn(DUP);	//duplicate the value to ensure chain operation always leaves one value on top of stack				
				if (dec instanceof ParamDec) {	//instance variable
					mv.visitVarInsn(ALOAD, 0);	//load "this"
					mv.visitInsn(SWAP);			//swap the two values in stack to make "this" (just loaded) in front of the expression value
					mv.visitFieldInsn(PUTFIELD, className, variableName, variableTypeDesc);
				} else {	//local variable
					mv.visitVarInsn(ISTORE, variableSlot);
				}
				break;
			case FILE:		//file can only be instance variable
				mv.visitVarInsn(ALOAD, 0);	//load "this"
				mv.visitFieldInsn(GETFIELD, className, variableName, variableTypeDesc);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "write", "(Ljava/awt/image/BufferedImage;Ljava/io/File;)Ljava/awt/image/BufferedImage;", false);
				break;
			case IMAGE:		//image can only be local variable
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, variableSlot);
				break;
			case FRAME:		//image can only be local variable
				mv.visitVarInsn(ALOAD, variableSlot);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFrame", "createOrSetFrame", "(Ljava/awt/image/BufferedImage;Lcop5556sp17/PLPRuntimeFrame;)Lcop5556sp17/PLPRuntimeFrame;", false);
				mv.visitInsn(DUP);	//duplicate value before ASTORE consumes it
				mv.visitVarInsn(ASTORE, variableSlot);
				break;
			default:
				break;
			}
		} else {	//ident is on left side of binary chain, load to stack
			if (dec instanceof ParamDec) {	//instance variable
				mv.visitVarInsn(ALOAD, 0);	//load "this"
				mv.visitFieldInsn(GETFIELD, className, variableName, variableTypeDesc);
			} else {	//local variable
				switch(decType) {
				case INTEGER:
					mv.visitVarInsn(ILOAD, variableSlot);
					break;
				case IMAGE:
				case FRAME:
					mv.visitVarInsn(ALOAD, variableSlot);
					break;
				default:
					break;
				}
			}
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object slotCount) throws Exception {
		String variableName, variableTypeDesc;
		Dec dec;
		TypeName decType;
		int variableSlot;	//slot number in local variable table
		
		dec = identExpression.getDec();
		decType = dec.getDecType();
		variableName = dec.getIdent().getText();
		variableTypeDesc = decType.getJVMTypeDesc();
		variableSlot = dec.getSlot();	//slot of ParamDec is not set, hence cannot be used for instance variable
		if (dec instanceof ParamDec) {	//instance variable
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitFieldInsn(GETFIELD, className, variableName, variableTypeDesc);
		} else {	//local variable
			switch(decType) {
			case INTEGER:
			case BOOLEAN:
				mv.visitVarInsn(ILOAD, variableSlot);
				break;
			case IMAGE:
			case FRAME:
				mv.visitVarInsn(ALOAD, variableSlot);
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
		decType = dec.getDecType();
		variableName = dec.getIdent().getText();
		variableTypeDesc = decType.getJVMTypeDesc();
		variableSlot = dec.getSlot();	//slot of ParamDec is not set, hence cannot be used for instance variable
		if (dec instanceof ParamDec) {	//instance variable (class field)
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitInsn(SWAP);			//swap the two values in stack to make "this" (just loaded) in front of the expression value
			mv.visitFieldInsn(PUTFIELD, className, variableName, variableTypeDesc);
		} else {	//local variable
			switch(decType) {
			case INTEGER:
			case BOOLEAN:
				mv.visitVarInsn(ISTORE, variableSlot);
				break;
			case IMAGE:
				//assignment operation of image is copying value, not copying reference
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "copyImage", "(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
				mv.visitVarInsn(ASTORE, variableSlot);
				break;
			case FRAME:
				mv.visitVarInsn(ASTORE, variableSlot);
				break;
			default:
				break;
			}
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object slotCount) throws Exception {
		Expression expression;
		Block block;
		Label after;
		
		expression = ifStatement.getE();
		expression.visit(this, slotCount);	//visit expression leaves the value of expression on top of stack
		after = new Label();
		mv.visitJumpInsn(IFEQ, after);
		block = ifStatement.getB();
		block.visit(this, slotCount);
		mv.visitLabel(after);	//visit label after block to get the actual line number which the execution may jump to
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object rightSide) throws Exception {
		Tuple tuple;
		Token token;
		
		tuple = imageOpChain.getArg();
		tuple.visit(this, rightSide);
		token = imageOpChain.getFirstToken();
		switch (token.kind) {
		case OP_WIDTH:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth", "()I", false);
			break;
		case OP_HEIGHT:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getHeight", "()I", false);
			break;
		case KW_SCALE:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
			//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "scale", "(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;", false);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object slotCount) throws Exception {
		int value;

		value = intLitExpression.getValue();
		mv.visitLdcInsn(value);	//load constant
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object argIndex) throws Exception {
		FieldVisitor fv;
		String variableName, variableTypeDesc;
		TypeName decType;
		int index;

		decType = paramDec.getDecType();
		variableName = paramDec.getIdent().getText();
		variableTypeDesc = decType.getJVMTypeDesc();
		//add field
		fv = cw.visitField(0, variableName, variableTypeDesc, null, null);
		fv.visitEnd();
		//set field value
		index = (Integer)argIndex;
		switch(decType) {
		case INTEGER:
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitVarInsn(ALOAD, 1);	//load "args"
			mv.visitLdcInsn(index);		//load constant
			mv.visitInsn(AALOAD);		//load object from array
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);	//convert string to integer
			mv.visitFieldInsn(PUTFIELD, className, variableName, variableTypeDesc);
			break;
		case BOOLEAN:
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitVarInsn(ALOAD, 1);	//load "args"
			mv.visitLdcInsn(index);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);	//convert string to boolean
			mv.visitFieldInsn(PUTFIELD, className, variableName, variableTypeDesc);
			break;
		case FILE:
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);	//load "args"
			mv.visitLdcInsn(index);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTFIELD, className, variableName, variableTypeDesc);
			break;
		case URL:
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitVarInsn(ALOAD, 1);	//load "args"
			mv.visitLdcInsn(index);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);	//in case professor changes the signature of the getURL()
			//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "getURL", "([Ljava/lang/String;I)Ljava/net/URL;", false);
			mv.visitFieldInsn(PUTFIELD, className, variableName, variableTypeDesc);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object slotCount) throws Exception {
		Expression expression;
		
		expression = sleepStatement.getE();
		expression.visit(this, slotCount);	//visit expression leaves the value of expression on top of stack
		mv.visitInsn(I2L);	//convert int to long
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object rightSide) throws Exception {
		List<Expression> expressionList;
		
		expressionList = tuple.getExprList();
		for (Expression expression: expressionList) {
			expression.visit(this, rightSide);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object slotCount) throws Exception {
		Expression expression;
		Block block;
		Label guard, body;
		
		guard = new Label();
		mv.visitJumpInsn(GOTO, guard);
		body = new Label();
		mv.visitLabel(body);
		block = whileStatement.getB();
		block.visit(this, slotCount);
		mv.visitLabel(guard);	//visit label to get the actual line number which the execution may jump to
		expression = whileStatement.getE();
		expression.visit(this, slotCount);	//visit expression leaves the value of expression on top of stack
		mv.visitJumpInsn(IFNE, body);
		return null;
	}

}
