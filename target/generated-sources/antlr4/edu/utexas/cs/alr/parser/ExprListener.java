// Generated from edu/utexas/cs/alr/parser/Expr.g4 by ANTLR 4.9
package edu.utexas.cs.alr.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExprParser}.
 */
public interface ExprListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(ExprParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(ExprParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(ExprParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(ExprParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#sexpr}.
	 * @param ctx the parse tree
	 */
	void enterSexpr(ExprParser.SexprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#sexpr}.
	 * @param ctx the parse tree
	 */
	void exitSexpr(ExprParser.SexprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#lneg}.
	 * @param ctx the parse tree
	 */
	void enterLneg(ExprParser.LnegContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#lneg}.
	 * @param ctx the parse tree
	 */
	void exitLneg(ExprParser.LnegContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#lor}.
	 * @param ctx the parse tree
	 */
	void enterLor(ExprParser.LorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#lor}.
	 * @param ctx the parse tree
	 */
	void exitLor(ExprParser.LorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#land}.
	 * @param ctx the parse tree
	 */
	void enterLand(ExprParser.LandContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#land}.
	 * @param ctx the parse tree
	 */
	void exitLand(ExprParser.LandContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#limpl}.
	 * @param ctx the parse tree
	 */
	void enterLimpl(ExprParser.LimplContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#limpl}.
	 * @param ctx the parse tree
	 */
	void exitLimpl(ExprParser.LimplContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#lequiv}.
	 * @param ctx the parse tree
	 */
	void enterLequiv(ExprParser.LequivContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#lequiv}.
	 * @param ctx the parse tree
	 */
	void exitLequiv(ExprParser.LequivContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#parexpr}.
	 * @param ctx the parse tree
	 */
	void enterParexpr(ExprParser.ParexprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#parexpr}.
	 * @param ctx the parse tree
	 */
	void exitParexpr(ExprParser.ParexprContext ctx);
}