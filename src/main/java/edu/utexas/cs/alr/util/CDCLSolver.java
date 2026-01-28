package edu.utexas.cs.alr.util;

import edu.utexas.cs.alr.ast.Expr;

import java.util.*;

<<<<<<< HEAD

=======
/**
 * CDCL (Conflict-Driven Clause Learning) SAT solver.
 */
>>>>>>> 083e919ec84aabbae83a8fbef034fc236701dc6d
public class CDCLSolver {
    private List<Set<Long>> clauses;
    private ImplicationGraph graph;
    private Set<Long> allVariables;
    
    public CDCLSolver(Expr cnfExpr) {
        this.clauses = CNFConverter.toClauses(cnfExpr);
        this.graph = new ImplicationGraph();
        this.allVariables = new HashSet<>();
        
        // Collect all variables
        for (Set<Long> clause : clauses) {
            for (Long lit : clause) {
                allVariables.add(Math.abs(lit));
            }
        }
    }
    
    /**
     * Solve the SAT problem using CDCL algorithm.
     */
    public boolean solve() {
        graph.reset();
        
        while (true) {
            // Unit propagation
            Set<Long> conflictClause = unitPropagate();
            
            if (conflictClause != null) {
                // Conflict detected
                if (graph.getCurrentLevel() == 0) {
                    // Conflict at level 0 means UNSAT
                    return false;
                }
                
                // Conflict analysis and learning
                Set<Long> learnedClause = analyzeConflict(conflictClause);
                int backtrackLevel = computeBacktrackLevel(learnedClause);
                
                // Add learned clause
                clauses.add(learnedClause);
                
                // Backtrack
                graph.backtrack(backtrackLevel);
                
                // If learned clause is unit, propagate it
                if (learnedClause.size() == 1) {
                    Long unitLit = learnedClause.iterator().next();
                    graph.makeImplication(unitLit, learnedClause);
                }
            } else {
                // No conflict, check if all variables are assigned
                if (graph.getAssignedVariables().size() == allVariables.size()) {
                    return true; // SAT
                }
                
                // Make a decision
                Long unassignedVar = findUnassignedVariable();
                if (unassignedVar == null) {
                    return true; // All variables assigned
                }
                
                // Try positive literal first
                graph.makeDecision(unassignedVar);
            }
        }
    }
    
    /**
     * Perform unit propagation.
     * Returns a conflicting clause if conflict is detected, null otherwise.
     */
    private Set<Long> unitPropagate() {
        boolean changed = true;
        
        while (changed) {
            changed = false;
            
            for (Set<Long> clause : clauses) {
                Long unitLit = findUnitLiteral(clause);
                
                if (unitLit != null) {
                    long var = Math.abs(unitLit);
                    
                    // Check if already assigned with opposite value
                    Long currentAssignment = graph.getAssignment(var);
                    if (currentAssignment != null) {
                        if (!currentAssignment.equals(unitLit)) {
                            // Conflict detected
                            return clause;
                        }
                        // Already satisfied, skip
                        continue;
                    }
                    
                    // Make implication
                    graph.makeImplication(unitLit, clause);
                    changed = true;
                } else {
                    // Check if clause is falsified
                    if (isClauseFalsified(clause)) {
                        return clause; // Conflict
                    }
                }
            }
        }
        
        return null; // No conflict
    }
    
    /**
     * Find a unit literal in a clause (exactly one unassigned literal).
     */
    private Long findUnitLiteral(Set<Long> clause) {
        Long unitLit = null;
        int unassignedCount = 0;
        
        for (Long lit : clause) {
            long var = Math.abs(lit);
            Long assignment = graph.getAssignment(var);
            
            if (assignment == null) {
                // Unassigned
                unassignedCount++;
                if (unassignedCount > 1) {
                    return null; // Not a unit clause
                }
                unitLit = lit;
            } else if (assignment.equals(lit)) {
                // Clause is satisfied
                return null;
            }
            // Otherwise, literal is falsified, continue
        }
        
        return (unassignedCount == 1) ? unitLit : null;
    }
    
    /**
     * Check if a clause is falsified (all literals are false).
     */
    private boolean isClauseFalsified(Set<Long> clause) {
        for (Long lit : clause) {
            long var = Math.abs(lit);
            Long assignment = graph.getAssignment(var);
            
            if (assignment == null) {
                // Has unassigned literal, not falsified
                return false;
            }
            
            if (assignment.equals(lit)) {
                // Has satisfied literal, not falsified
                return false;
            }
        }
        
        // All literals are falsified
        return true;
    }
    
    /**
     * Analyze conflict and learn a new clause using First UIP strategy.
     */
    private Set<Long> analyzeConflict(Set<Long> conflictClause) {
        // The learned clause starts as the negation of the conflict clause
        // In the conflict clause, all literals are false, so we negate them
        Set<Long> learnedClause = new HashSet<>();
        for (Long lit : conflictClause) {
            learnedClause.add(-lit);
        }
        
        // Count how many literals from current level are in the learned clause
        int currentLevelCount = countLiteralsAtLevel(learnedClause, graph.getCurrentLevel());
        
        // Resolve until we have exactly one literal from current level (First UIP)
        while (currentLevelCount > 1) {
            // Find a literal at current level that has a reason (not a decision)
            Long toResolve = null;
            for (Long lit : learnedClause) {
                long var = Math.abs(lit);
                if (graph.getDecisionLevel(var) == graph.getCurrentLevel()) {
                    Set<Long> reason = graph.getReason(var);
                    if (reason != null) {
                        // This is an implied literal, resolve with its reason
                        toResolve = var;
                        break;
                    }
                }
            }
            
            if (toResolve == null) {
                // All remaining are decision variables, can't resolve further
                break;
            }
            
            // Get the assignment for this variable to determine which literal to remove
            Long assignment = graph.getAssignment(toResolve);
            if (assignment == null) {
                break;
            }
            
            // Remove the literal from learned clause
            learnedClause.remove(assignment);
            learnedClause.remove(-assignment);
            
            // Resolve with the reason clause
            Set<Long> reasonClause = graph.getReason(toResolve);
            
            // Add all literals from reason clause (except the one we're resolving)
            for (Long reasonLit : reasonClause) {
                long reasonVar = Math.abs(reasonLit);
                if (reasonVar != toResolve) {
                    learnedClause.add(-reasonLit);
                }
            }
            
            // Recalculate count
            currentLevelCount = countLiteralsAtLevel(learnedClause, graph.getCurrentLevel());
        }
        
        return learnedClause;
    }
    
    /**
     * Count how many literals in a clause are at a specific decision level.
     */
    private int countLiteralsAtLevel(Set<Long> clause, int level) {
        int count = 0;
        for (Long lit : clause) {
            long var = Math.abs(lit);
            if (graph.getDecisionLevel(var) == level) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Compute the backtrack level from a learned clause.
     */
    private int computeBacktrackLevel(Set<Long> learnedClause) {
        if (learnedClause.size() <= 1) {
            return 0;
        }
        
        // Find the second highest decision level
        List<Integer> levels = new ArrayList<>();
        for (Long lit : learnedClause) {
            int level = graph.getDecisionLevel(Math.abs(lit));
            if (level >= 0) {
                levels.add(level);
            }
        }
        
        if (levels.isEmpty()) {
            return 0;
        }
        
        Collections.sort(levels, Collections.reverseOrder());
        
        if (levels.size() == 1) {
            return 0;
        }
        
        return levels.get(1); // Second highest level
    }
    
    /**
     * Find an unassigned variable.
     */
    private Long findUnassignedVariable() {
        for (Long var : allVariables) {
            if (!graph.isAssigned(var)) {
                return var;
            }
        }
        return null;
    }
}
