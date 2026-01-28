package edu.utexas.cs.alr.util;

import edu.utexas.cs.alr.ast.Expr;

public class SatUtil {
    public static boolean checkSAT(Expr expr)
    {
        CDCLSolver solver = new CDCLSolver(expr);
        return solver.solve();
    }
}
