package cop5556sp17;

import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.WhileStatement;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}

	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}

	//Neo
	@Test
	public void testIdentExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "id1 \n id2 \n id3 \n id4";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
		assertEquals(IDENT, ast.getFirstToken().kind);
		assertEquals("id1", ast.getFirstToken().getText());
		ast = parser.term();
		assertEquals(IdentExpression.class, ast.getClass());
		assertEquals(IDENT, ast.getFirstToken().kind);
		assertEquals("id2", ast.getFirstToken().getText());
		ast = parser.elem();
		assertEquals(IdentExpression.class, ast.getClass());
		assertEquals(IDENT, ast.getFirstToken().kind);
		assertEquals("id3", ast.getFirstToken().getText());
		ast = parser.factor();
		assertEquals(IdentExpression.class, ast.getClass());
		assertEquals(IDENT, ast.getFirstToken().kind);
		assertEquals("id4", ast.getFirstToken().getText());
	}

	@Test
	public void testIntLitExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "120 \n 0 \n 888 \n 12345678";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
		assertEquals(INT_LIT, ast.getFirstToken().kind);
		assertEquals(120, ast.getFirstToken().intVal());
		ast = parser.term();
		assertEquals(IntLitExpression.class, ast.getClass());
		assertEquals(INT_LIT, ast.getFirstToken().kind);
		assertEquals(0, ast.getFirstToken().intVal());
		ast = parser.elem();
		assertEquals(IntLitExpression.class, ast.getClass());
		assertEquals(INT_LIT, ast.getFirstToken().kind);
		assertEquals(888, ast.getFirstToken().intVal());
		ast = parser.factor();
		assertEquals(IntLitExpression.class, ast.getClass());
		assertEquals(INT_LIT, ast.getFirstToken().kind);
		assertEquals(12345678, ast.getFirstToken().intVal());
	}

	@Test
	public void testBooleanExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true \n false \n true \n false";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BooleanLitExpression.class, ast.getClass());
		assertEquals(KW_TRUE, ast.getFirstToken().kind);
		assertEquals("true", ast.getFirstToken().getText());
		ast = parser.term();
		assertEquals(BooleanLitExpression.class, ast.getClass());
		assertEquals(KW_FALSE, ast.getFirstToken().kind);
		assertEquals("false", ast.getFirstToken().getText());
		ast = parser.elem();
		assertEquals(BooleanLitExpression.class, ast.getClass());
		assertEquals(KW_TRUE, ast.getFirstToken().kind);
		assertEquals("true", ast.getFirstToken().getText());
		ast = parser.factor();
		assertEquals(BooleanLitExpression.class, ast.getClass());
		assertEquals(KW_FALSE, ast.getFirstToken().kind);
		assertEquals("false", ast.getFirstToken().getText());
	}

	@Test
	public void testConstantExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "screenwidth \n screenheight \n screenwidth \n screenheight";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(ConstantExpression.class, ast.getClass());
		assertEquals(KW_SCREENWIDTH, ast.getFirstToken().kind);
		assertEquals("screenwidth", ast.getFirstToken().getText());
		ast = parser.term();
		assertEquals(ConstantExpression.class, ast.getClass());
		assertEquals(KW_SCREENHEIGHT, ast.getFirstToken().kind);
		assertEquals("screenheight", ast.getFirstToken().getText());
		ast = parser.elem();
		assertEquals(ConstantExpression.class, ast.getClass());
		assertEquals(KW_SCREENWIDTH, ast.getFirstToken().kind);
		assertEquals("screenwidth", ast.getFirstToken().getText());
		ast = parser.factor();
		assertEquals(ConstantExpression.class, ast.getClass());
		assertEquals(KW_SCREENHEIGHT, ast.getFirstToken().kind);
		assertEquals("screenheight", ast.getFirstToken().getText());
	}
	
	@Test
	public void testBinaryExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "id1 > id2 \n id1 + 120 \n (id1 != (20 - 3)) * (((id3)) & 50) \n id1 >= (id2 * 10 - id3)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression)ast;
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(GT, be.getOp().kind);
		ast = parser.term();
		assertEquals(BinaryExpression.class, ast.getClass());
		be = (BinaryExpression)ast;
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
		ast = parser.elem();
		assertEquals(BinaryExpression.class, ast.getClass());
		be = (BinaryExpression)ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(BinaryExpression.class, be.getE1().getClass());
		assertEquals(TIMES, be.getOp().kind);
		be = (BinaryExpression)(((BinaryExpression)ast).getE0());
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(BinaryExpression.class, be.getE1().getClass());	
		assertEquals(NOTEQUAL, be.getOp().kind);
		be = (BinaryExpression)(((BinaryExpression)ast).getE1());
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());	
		assertEquals(AND, be.getOp().kind);
		be = (BinaryExpression)(((BinaryExpression)(((BinaryExpression)ast).getE0())).getE1());		
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());	
		assertEquals(MINUS, be.getOp().kind);
		ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		be = (BinaryExpression)ast;
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(BinaryExpression.class, be.getE1().getClass());
		assertEquals(GE, be.getOp().kind);
		be = (BinaryExpression)(((BinaryExpression)ast).getE1());
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(MINUS, be.getOp().kind);
		be = (BinaryExpression)(((BinaryExpression)(((BinaryExpression)ast).getE1())).getE0());	
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(TIMES, be.getOp().kind);
	}

	@Test
	public void testSleepStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep id1; \n sleep (id1 * 50);";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(SleepStatement.class, ast.getClass());
		SleepStatement s = (SleepStatement)ast;
		assertEquals(OP_SLEEP, s.getFirstToken().kind);
		assertEquals(IdentExpression.class, s.getE().getClass());
		ast = parser.statement();
		assertEquals(SleepStatement.class, ast.getClass());
		s = (SleepStatement)ast;
		assertEquals(OP_SLEEP, s.getFirstToken().kind);
		assertEquals(BinaryExpression.class, s.getE().getClass());
		BinaryExpression be = (BinaryExpression)(s.getE());
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(TIMES, be.getOp().kind);
	}

	@Test
	public void testAssignmentStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "id1 <- 50; \n id2 <- (50 / true)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(AssignmentStatement.class, ast.getClass());
		AssignmentStatement a = (AssignmentStatement)ast;
		assertEquals(IDENT, a.var.getFirstToken().kind);
		assertEquals(IntLitExpression.class, a.getE().getClass());
		ast = parser.assign();
		assertEquals(AssignmentStatement.class, ast.getClass());
		a = (AssignmentStatement)ast;
		assertEquals(IDENT, a.var.getFirstToken().kind);
		assertEquals(BinaryExpression.class, a.getE().getClass());
		BinaryExpression be = (BinaryExpression)(a.getE());
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(BooleanLitExpression.class, be.getE1().getClass());
		assertEquals(DIV, be.getOp().kind);
	}

	@Test
	public void testWhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while (a > 20) {} \n while (true) {sleep 5;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(WhileStatement.class, ast.getClass());
		WhileStatement w = (WhileStatement)ast;
		assertEquals(KW_WHILE, w.getFirstToken().kind);
		assertEquals(BinaryExpression.class, w.getE().getClass());
		BinaryExpression be = (BinaryExpression)(w.getE());
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(GT, be.getOp().kind);
		Block b = (Block)(w.getB());
		assertEquals(0, b.getDecs().size());
		assertEquals(0, b.getStatements().size());
		ast = parser.whileStatement();
		assertEquals(WhileStatement.class, ast.getClass());
		w = (WhileStatement)ast;
		assertEquals(KW_WHILE, w.getFirstToken().kind);
		assertEquals(BooleanLitExpression.class, w.getE().getClass());
		b = (Block)(w.getB());
		assertEquals(0, b.getDecs().size());
		assertEquals(1, b.getStatements().size());
		assertEquals(SleepStatement.class, b.getStatements().get(0).getClass());
	}

	@Test
	public void testIfStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if (a > 20) {} \n if (true) {sleep 5;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(IfStatement.class, ast.getClass());
		IfStatement i = (IfStatement)ast;
		assertEquals(KW_IF, i.getFirstToken().kind);
		assertEquals(BinaryExpression.class, i.getE().getClass());
		BinaryExpression be = (BinaryExpression)(i.getE());
		assertEquals(IdentExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(GT, be.getOp().kind);
		Block b = (Block)(i.getB());
		assertEquals(0, b.getDecs().size());
		assertEquals(0, b.getStatements().size());
		ast = parser.ifStatement();
		assertEquals(IfStatement.class, ast.getClass());
		i = (IfStatement)ast;
		assertEquals(KW_IF, i.getFirstToken().kind);
		assertEquals(BooleanLitExpression.class, i.getE().getClass());
		b = (Block)(i.getB());
		assertEquals(0, b.getDecs().size());
		assertEquals(1, b.getStatements().size());
		assertEquals(SleepStatement.class, b.getStatements().get(0).getClass());
	}

	@Test
	public void testTuple() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(id1 + id2) \n (id1, 120, true, screenwidth, (id1 + 5) % 3)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
		Tuple t = (Tuple)ast;
		assertEquals(1, t.getExprList().size());
		assertEquals(BinaryExpression.class, t.getExprList().get(0).getClass());
		ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
		t = (Tuple)ast;
		assertEquals(5, t.getExprList().size());
		assertEquals(IdentExpression.class, t.getExprList().get(0).getClass());
		assertEquals(IntLitExpression.class, t.getExprList().get(1).getClass());
		assertEquals(BooleanLitExpression.class, t.getExprList().get(2).getClass());
		assertEquals(ConstantExpression.class, t.getExprList().get(3).getClass());
		assertEquals(BinaryExpression.class, t.getExprList().get(4).getClass());
		//test ¦Å
		input = "";
		scanner = new Scanner(input);
		scanner.scan();
		parser = new Parser(scanner);
		ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
		t = (Tuple)ast;
		assertEquals(0, t.getExprList().size());
	}

	@Test
	public void testIdentChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "id1";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(IdentChain.class, ast.getClass());
	}

	@Test
	public void testFilterOpChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blur \n gray (id1, id2 + 3, (id1 + id2) <= 50)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(FilterOpChain.class, ast.getClass());
		FilterOpChain foc = (FilterOpChain)ast;
		assertEquals(OP_BLUR, foc.getFirstToken().kind);
		assertEquals(0, foc.getArg().getExprList().size());
		ast = parser.chainElem();
		assertEquals(FilterOpChain.class, ast.getClass());
		foc = (FilterOpChain)ast;
		assertEquals(OP_GRAY, foc.getFirstToken().kind);
		assertEquals(3, foc.getArg().getExprList().size());
	}

	@Test
	public void testFrameOpChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "show \n xloc (id1, id2 + 3, (id1 + id2) <= 50)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(FrameOpChain.class, ast.getClass());
		FrameOpChain foc = (FrameOpChain)ast;
		assertEquals(KW_SHOW, foc.getFirstToken().kind);
		assertEquals(0, foc.getArg().getExprList().size());
		ast = parser.chainElem();
		assertEquals(FrameOpChain.class, ast.getClass());
		foc = (FrameOpChain)ast;
		assertEquals(KW_XLOC, foc.getFirstToken().kind);
		assertEquals(3, foc.getArg().getExprList().size());
	}

	@Test
	public void testImageOpChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "width \n scale (id1, id2 + 3, (id1 + id2) <= 50)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(ImageOpChain.class, ast.getClass());
		ImageOpChain ioc = (ImageOpChain)ast;
		assertEquals(OP_WIDTH, ioc.getFirstToken().kind);
		assertEquals(0, ioc.getArg().getExprList().size());
		ast = parser.chainElem();
		assertEquals(ImageOpChain.class, ast.getClass());
		ioc = (ImageOpChain)ast;
		assertEquals(KW_SCALE, ioc.getFirstToken().kind);
		assertEquals(3, ioc.getArg().getExprList().size());
	}

	@Test
	public void testBinaryChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "id1 -> convolve (id2) \n id1 |-> yloc (1 + a, screenheight) -> scale";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
		BinaryChain bc = (BinaryChain)ast;
		assertEquals(IdentChain.class, bc.getE0().getClass());
		assertEquals(FilterOpChain.class, bc.getE1().getClass());
		assertEquals(ARROW, bc.getArrow().kind);
		assertEquals(OP_CONVOLVE, ((FilterOpChain)(bc.getE1())).getFirstToken().kind);
		assertEquals(1, ((FilterOpChain)(bc.getE1())).getArg().getExprList().size());
		assertEquals(IdentExpression.class, ((FilterOpChain)(bc.getE1())).getArg().getExprList().get(0).getClass());
		ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
		bc = (BinaryChain)ast;
		assertEquals(BinaryChain.class, bc.getE0().getClass());
		assertEquals(ImageOpChain.class, bc.getE1().getClass());
		assertEquals(ARROW, bc.getArrow().kind);
		assertEquals(KW_SCALE, ((ImageOpChain)(bc.getE1())).getFirstToken().kind);
		assertEquals(0, ((ImageOpChain)(bc.getE1())).getArg().getExprList().size());
		bc = (BinaryChain)(bc.getE0());
		assertEquals(IdentChain.class, bc.getE0().getClass());
		assertEquals(FrameOpChain.class, bc.getE1().getClass());
		assertEquals(BARARROW, bc.getArrow().kind);
		assertEquals(KW_YLOC, ((FrameOpChain)(bc.getE1())).getFirstToken().kind);
		assertEquals(2, ((FrameOpChain)(bc.getE1())).getArg().getExprList().size());
		assertEquals(BinaryExpression.class, ((FrameOpChain)(bc.getE1())).getArg().getExprList().get(0).getClass());
		assertEquals(ConstantExpression.class, ((FrameOpChain)(bc.getE1())).getArg().getExprList().get(1).getClass());
	}

	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "integer id1 \n boolean id2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.dec();
		assertEquals(Dec.class, ast.getClass());
		Dec d = (Dec)ast;
		assertEquals(KW_INTEGER, d.getFirstToken().kind);
		assertEquals(IDENT, d.getIdent().kind);
		assertEquals("id1", d.getIdent().getText());
		ast = parser.dec();
		assertEquals(Dec.class, ast.getClass());
		d = (Dec)ast;
		assertEquals(KW_BOOLEAN, d.getFirstToken().kind);
		assertEquals(IDENT, d.getIdent().kind);
		assertEquals("id2", d.getIdent().getText());
	}

	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "file id1 \n url id2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.paramDec();
		assertEquals(ParamDec.class, ast.getClass());
		ParamDec pd = (ParamDec)ast;
		assertEquals(KW_FILE, pd.getFirstToken().kind);
		assertEquals(IDENT, pd.getIdent().kind);
		assertEquals("id1", pd.getIdent().getText());
		ast = parser.paramDec();
		assertEquals(ParamDec.class, ast.getClass());
		pd = (ParamDec)ast;
		assertEquals(KW_URL, pd.getFirstToken().kind);
		assertEquals(IDENT, pd.getIdent().kind);
		assertEquals("id2", pd.getIdent().getText());
	}

	@Test
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{} \n {integer id1 id1 <- 10; sleep id2; boolean id2 while(a > b) {a <- b;} blur (id1) -> hide (id1 + id2);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.block();
		assertEquals(Block.class, ast.getClass());
		Block b = (Block)ast;
		assertEquals(0, b.getDecs().size());
		assertEquals(0, b.getStatements().size());
		ast = parser.block();
		assertEquals(Block.class, ast.getClass());
		b = (Block)ast;
		assertEquals(2, b.getDecs().size());
		assertEquals(4, b.getStatements().size());
		assertEquals(AssignmentStatement.class, b.getStatements().get(0).getClass());
		assertEquals(SleepStatement.class, b.getStatements().get(1).getClass());
		assertEquals(WhileStatement.class, b.getStatements().get(2).getClass());
		assertEquals(BinaryChain.class, b.getStatements().get(3).getClass());
	}

	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "p1 {} \n p2 file id1, url id2, integer id3 {integer id1 a <- 10; while (a < 100) {a <- a + 1;} image i1 blur (id1) -> hide (id1 + id2);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class, ast.getClass());
		Program p = (Program)ast;
		assertEquals(IDENT, p.getFirstToken().kind);
		assertEquals("p1", p.getFirstToken().getText());
		assertEquals(0, p.getParams().size());
		assertEquals(0, p.getB().getDecs().size());
		assertEquals(0, p.getB().getStatements().size());
		ast = parser.program();
		assertEquals(Program.class, ast.getClass());
		p = (Program)ast;
		assertEquals(IDENT, p.getFirstToken().kind);
		assertEquals("p2", p.getFirstToken().getText());
		assertEquals(3, p.getParams().size());
		assertEquals(2, p.getB().getDecs().size());
		assertEquals(3, p.getB().getStatements().size());
	}

	@Test
	public void testASTNode() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "p2 file id1, url id2, integer id3 {integer id1 a <- 10; while (a < 100) {a <- a + 1;} image i1 blur (id1) -> hide (id1 + id2);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(Program.class, ast.getClass());
	}
}
