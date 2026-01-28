package edu.utexas.cs.alr.util;

import java.util.*;

/**
 * Implication graph for CDCL algorithm.
 * Tracks decision levels, assignments, and reasons for implications.
 */
public class ImplicationGraph {
    // Assignment: variable -> literal value (positive or negative)
    private final Map<Long, Long> assignment = new HashMap<>();
    
    // Decision level for each variable
    private final Map<Long, Integer> decisionLevel = new HashMap<>();
    
    // Reason clause for each assignment (null for decision variables)
    private final Map<Long, Set<Long>> reason = new HashMap<>();
    
    // Current decision level
    private int currentLevel = 0;
    
    // Decision variables at each level
    private final List<Long> decisions = new ArrayList<>();
    
    /**
     * Get the current decision level.
     */
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    /**
     * Get the assignment for a variable, or null if unassigned.
     */
    public Long getAssignment(Long var) {
        return assignment.get(var);
    }
    
    /**
     * Check if a variable is assigned.
     */
    public boolean isAssigned(Long var) {
        return assignment.containsKey(var);
    }
    
    /**
     * Get the decision level for a variable.
     */
    public int getDecisionLevel(Long var) {
        return decisionLevel.getOrDefault(var, -1);
    }
    
    /**
     * Get the reason clause for an assignment.
     */
    public Set<Long> getReason(Long var) {
        return reason.get(var);
    }
    
    /**
     * Check if a variable is a decision variable (no reason clause).
     */
    public boolean isDecision(Long var) {
        return reason.get(var) == null && isAssigned(var);
    }
    
    /**
     * Make a decision assignment.
     */
    public void makeDecision(Long literal) {
        currentLevel++;
        long var = Math.abs(literal);
        assignment.put(var, literal);
        decisionLevel.put(var, currentLevel);
        reason.put(var, null); // Decision has no reason
        decisions.add(literal);
    }
    
    /**
     * Make an implication assignment (unit propagation).
     */
    public void makeImplication(Long literal, Set<Long> reasonClause) {
        long var = Math.abs(literal);
        assignment.put(var, literal);
        decisionLevel.put(var, currentLevel);
        reason.put(var, reasonClause);
    }
    
    /**
     * Backtrack to a specific decision level.
     */
    public void backtrack(int level) {
        List<Long> toRemove = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : decisionLevel.entrySet()) {
            if (entry.getValue() > level) {
                toRemove.add(entry.getKey());
            }
        }
        
        for (Long var : toRemove) {
            assignment.remove(var);
            decisionLevel.remove(var);
            reason.remove(var);
        }
        
        // Remove decisions beyond this level
        while (decisions.size() > level) {
            decisions.remove(decisions.size() - 1);
        }
        
        currentLevel = level;
    }
    
    /**
     * Get all assigned variables.
     */
    public Set<Long> getAssignedVariables() {
        return new HashSet<>(assignment.keySet());
    }
    
    /**
     * Get the decision variable at a specific level.
     */
    public Long getDecisionAtLevel(int level) {
        if (level > 0 && level <= decisions.size()) {
            return decisions.get(level - 1);
        }
        return null;
    }
    
    /**
     * Reset the graph.
     */
    public void reset() {
        assignment.clear();
        decisionLevel.clear();
        reason.clear();
        currentLevel = 0;
        decisions.clear();
    }
    
    /**
     * Get all variables at a specific decision level.
     */
    public Set<Long> getVariablesAtLevel(int level) {
        Set<Long> vars = new HashSet<>();
        for (Map.Entry<Long, Integer> entry : decisionLevel.entrySet()) {
            if (entry.getValue() == level) {
                vars.add(entry.getKey());
            }
        }
        return vars;
    }
}
