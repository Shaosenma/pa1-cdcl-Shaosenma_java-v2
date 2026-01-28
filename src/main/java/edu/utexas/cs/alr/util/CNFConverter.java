package edu.utexas.cs.alr.util;

import edu.utexas.cs.alr.ast.*;

import java.util.*;

/**
 * Utility class to convert CNF expressions to clause lists.
 */
public class CNFConverter {
    /**
     * Convert a CNF expression to a list of clauses.
     * Each clause is represented as a set of literals (positive for variables, negative for negated variables).
     */
    public static List<Set<Long>> toClauses(Expr cnfExpr) {
        List<Set<Long>> clauses = new ArrayList<>();
        Set<Long> vars = new HashSet<>();
        
        Stack<Expr> stack = new Stack<>();
        stack.push(cnfExpr);
        
        while (!stack.isEmpty()) {
            Expr e = stack.pop();
            
            if (!ExprUtils.canBeCNF(e)) {
                throw new RuntimeException("Expr is not in CNF.");
            }
            
            switch (e.getKind()) {
                case AND:
                    AndExpr andExpr = (AndExpr) e;
                    stack.push(andExpr.getLeft());
                    stack.push(andExpr.getRight());
                    break;
                case NEG:
                    if (!ExprUtils.isLiteral(e)) {
                        throw new RuntimeException("Expr is not in CNF.");
                    }
                    VarExpr childVarExpr = (VarExpr) ((NegExpr) e).getExpr();
                    Set<Long> negClause = new HashSet<>();
                    negClause.add(-childVarExpr.getId());
                    clauses.add(negClause);
                    vars.add(childVarExpr.getId());
                    break;
                case VAR:
                    VarExpr varExpr = (VarExpr) e;
                    Set<Long> posClause = new HashSet<>();
                    posClause.add(varExpr.getId());
                    clauses.add(posClause);
                    vars.add(varExpr.getId());
                    break;
                case OR:
                    Set<Long> clause = ExprUtils.getLiteralsForClause((OrExpr) e, vars);
                    clauses.add(clause);
                    break;
                default:
                    assert false;
            }
        }
        
        return clauses;
    }
}
