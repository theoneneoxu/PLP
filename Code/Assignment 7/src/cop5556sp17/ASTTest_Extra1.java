package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.*;

public class ASTTest_Extra1 {

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
	
	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  (3, ident0, ident4 < 3) ";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass()); // test class
		Tuple arg = (Tuple) ast;
		List<Expression> expList = arg.getExprList();
		
		assertEquals(3, expList.size());
		
		assertEquals(IntLitExpression.class, expList.get(0).getClass());
		assertEquals(IdentExpression.class, expList.get(1).getClass());
		assertEquals(BinaryExpression.class, expList.get(2).getClass());
	}
	
	@Test
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  {ident1 -> show(3); integer ident2 ident3 <- 3+5; boolean ident5 } ";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.block();
		assertEquals(Block.class, ast.getClass()); // test class
		Block b = (Block) ast;
		List<Dec> decList = b.getDecs();
		List<Statement> stateList =  b.getStatements();
		
		assertEquals(2, decList.size());
		assertEquals(2, stateList.size());
		
		// ?
		assertEquals(KW_INTEGER, decList.get(0).getType().kind);
		assertEquals(IDENT, decList.get(0).getIdent().kind);
		assertEquals(KW_BOOLEAN, decList.get(1).getType().kind);
		assertEquals(IDENT, decList.get(1).getIdent().kind);
		
		assertEquals(BinaryChain.class, stateList.get(0).getClass());
		assertEquals(IdentChain.class, ((BinaryChain)stateList.get(0)).getE0().getClass());
		assertEquals(ARROW, ((BinaryChain)stateList.get(0)).getArrow().kind);
		assertEquals(FrameOpChain.class, ((BinaryChain)stateList.get(0)).getE1().getClass());
		
		assertEquals(AssignmentStatement.class, stateList.get(1).getClass());
		assertEquals(IdentLValue.class, ((AssignmentStatement)stateList.get(1)).getVar().getClass());
		assertEquals(BinaryExpression.class, ((AssignmentStatement)stateList.get(1)).getE().getClass());
	}
	
	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = " program0 url ident0, file ident1, integer ident2 {ident1 -> show(3); integer ident2 ident3 <- 3+5; boolean ident5 } ";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.parse();
		assertEquals(Program.class, ast.getClass()); // test class
		Program prom = (Program) ast;
		ArrayList<ParamDec> PDList = prom.getParams();
		assertEquals(3, PDList.size());
		
		assertEquals("program0", prom.getName());
		assertEquals(ParamDec.class, PDList.get(0).getClass());
		assertEquals(ParamDec.class, PDList.get(1).getClass());
		assertEquals(ParamDec.class, PDList.get(2).getClass());
		assertEquals(Block.class, prom.getB().getClass());
	}
	
	@Test
	public void testElem0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "ident1 * ident2 / 3345 & true % false / screenwidth * screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.elem();
		assertEquals(BinaryExpression.class, ast.getClass()); // test class
		
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(TIMES, be.getOp().kind);
	}
	
	@Test
	public void testTerm0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "ident1 * ident2 + 3783 & 2893 - screenwidth / screenheight | true * false";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.term();
		assertEquals(BinaryExpression.class, ast.getClass()); // test class
		
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(BinaryExpression.class, be.getE1().getClass());
		assertEquals(OR, be.getOp().kind);
	}
	
	@Test
	public void testExpression0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "ident1 * ident2 + 3783 & 2893 < true * false | screenwidth / screenheight <= true * false | screenwidth / screenheight > true * false | screenwidth / screenheight >= true * false | screenwidth / screenheight == true * false | screenwidth / screenheight != true * false | screenwidth / screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass()); // test class
		
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(BinaryExpression.class, be.getE1().getClass());
		assertEquals(NOTEQUAL, be.getOp().kind);
	}

	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url ident321";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.paramDec();
		assertEquals(ParamDec.class, ast.getClass()); // test class
		
		Dec d = (Dec) ast;
		assertEquals(KW_URL, d.getType().kind);
		assertEquals(IDENT, d.getIdent().kind);
	}
	
	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "integer ident0";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.dec();
		assertEquals(Dec.class, ast.getClass()); // test class
		
		Dec d = (Dec) ast;
		assertEquals(KW_INTEGER, d.getType().kind);
		assertEquals(IDENT, d.getIdent().kind);
	}
}
