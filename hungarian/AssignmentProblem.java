/**
 * Implementation of the Hungarian algorithm to solve the Assignment problem
 * @author Hiba Azhari
 */

package hungarian;

import java.util.*;
public class AssignmentProblem {

    // DATA STRUCTURES

    public int[][] originalMatrix;
    public int[][] reducedMatrix;
    public Set<Integer> crossedCols;
    public Set<Integer> crossedRows;
    public Map<Integer,Integer> assignment; // maps rows to assigned cols

    // ADDITIONAL ATTRIBUTES

    public boolean maximize; // indicates whether to maximize or minimize cost
    public int maxValue; // largest cost in originalMatrix

    public int score = 0; // total cost of assignment
    public int[][] assignments; // stores coordinates of assignments
    public int[] costs; // stores costs of assignments
    
    // CONSTRUCTOR

    /**
     * @param matrix contains main matrix input
     * @param maximize determines whether it is a maximization problem (true)
     *                 or minimization (false)
     */
    public AssignmentProblem(int[][] matrix, boolean maximize) {
        // initialize the problem
        originalMatrix = matrix;
        this.maximize = maximize;
        crossedCols = new HashSet<Integer>();
        crossedRows = new HashSet<Integer>();
        assignment = new HashMap<Integer,Integer>();
        
        score = 0;
        assignments = new int[originalMatrix.length][2];
        costs = new int[originalMatrix.length];
    }

    // FOR MAXIMIZATION PROBLEM:

    /**
     * find maximum value in matrix
     */
    public void getMaxValue() {
        maxValue = Integer.MIN_VALUE;
        // for each element in the matrix
        for (int i = 0; i < originalMatrix.length; i ++) {
            for (int j = 0; j < originalMatrix[0].length; j++) {
                // save it as maxValue if it is greater than maxValue
                maxValue = Math.max(originalMatrix[i][j], maxValue);
            }
        }
    }

    /**
     * subtract all values from maximum value in matrix
     */
    public void maximize() {
        // for each element in the matrix
        for (int i = 0; i < originalMatrix.length; i ++) {
            for (int j = 0; j < originalMatrix[0].length; j++) {
                // sub from maxValue and replace matrix element with result
                originalMatrix[i][j] = maxValue - originalMatrix[i][j];
            }
        }
    }

    // MAIN FUNCTIONS

    /**
     * checks whether an assignment is possible through recursion
     * if possible, stored in 'assignment'
     * @param i represents current row in matrix
     * @return whether an assignment is possible (true/false)
     */
    public boolean assignmentPossible(int i) {
        // if last row is reached, assignment is possible (true)
        if (i == reducedMatrix.length) return true;
        // for each element in row i
        for (int j = 0; j < reducedMatrix[i].length; j++) {
            // if it == '0' and this col has not been assigned
            if (reducedMatrix[i][j] == 0 && !assignment.containsKey(j)) {
                // assign it and check if its possible to assign the rest
                assignment.put(j,i);
                if (assignmentPossible(i+1)) return true;
                // if not, backtrack by removing the assignment
                assignment.remove(j);
            }
        }
        return false;
    }

    /**
     * STEP 1
     * subtracts min value in each row from all values in row
     */
    public void reduceRows() {
        int[][] reducedMatrix = new int[originalMatrix.length][originalMatrix[0].length];
        // for each row
        for (int i = 0; i < originalMatrix.length; i++) {
            int least = Integer.MAX_VALUE;
            // traverse elements to find least
            for (int j = 0; j < originalMatrix[0].length; j++) {
                least = Math.min(least, originalMatrix[i][j]);
            }
            // traverse again to subtract least from each
            for (int j = 0; j < originalMatrix[0].length; j++) {
                reducedMatrix[i][j] = originalMatrix[i][j] - least;
            }
        }
        this.reducedMatrix = reducedMatrix;
    }

    /**
     * STEP 2
     * subtracts min value in each column from all values in column
     */
    public void reduceCols() {
        // for each col
        for (int j = 0; j < originalMatrix[0].length; j++) {
            int least = Integer.MAX_VALUE;
            // traverse elements to find least
            for (int i = 0; i < originalMatrix.length; i++) {
                least = Math.min(least, reducedMatrix[i][j]);
            }
            // traverse again to subtract least from each
            for (int i = 0; i < originalMatrix.length; i++) {
                reducedMatrix[i][j] = reducedMatrix[i][j] - least;
            }
        }
    }

    /**
     * STEP 3
     * "cross out" rows and column containing zeros but adding their indices
     * to 'crossedRows' and 'crossedCols'
     */
    public void crossOutZeros() {
        // for each row
        for (int i = 0; i < reducedMatrix.length; i++) {
            // currently no zero position found, assume one zero exists
            int pos = -1;
            boolean oneZero = true;
            // for each element
            for (int j = 0; j < reducedMatrix[0].length; j++) {
                // skip crossed cols
                // if a zero is found and it is the first, record its position
                if (crossedCols.contains(j)) continue;
                if (reducedMatrix[i][j] == 0) {
                    if (pos < 0) pos = j;
                    else oneZero = false;
                }
            }
            // if the row had a 0 and it is the only one, cross its col
            if (oneZero && pos >= 0) crossedCols.add(pos);
        }
        // for each col
        for (int j = 0; j < reducedMatrix[0].length; j++) {
            // skip if crossed
            // currently no zero position found, assume one zero exists
            if (crossedCols.contains(j)) continue;
            int pos = -1;
            boolean oneZero = true;
            // for each element
            for (int i = 0; i < reducedMatrix.length; i++) {
                // skip crossed rows
                // if a zero is found and it is the first, record its position
                if (crossedRows.contains(i)) continue;
                if (reducedMatrix[i][j] == 0) {
                    if (pos < 0) pos = i;
                    else oneZero = false;
                }
            }
            // if the col had a 0 and it is the only one, cross its row
            if (oneZero && pos >= 0) crossedRows.add(pos);
        }
    }

    /**
     * STEP 4
     * finds smallest uncrossed value in matrix
     * subtracts it from other uncrossed values
     * adds it to values at intersections
     */
    public void addSubLeastUncrossed() {
        int least = Integer.MAX_VALUE;
        // for each uncrossed element
        for (int i = 0; i < reducedMatrix.length; i++) {
            if (!crossedRows.contains(i)) {
                for (int j = 0; j < reducedMatrix[0].length; j++) {
                    if (!crossedCols.contains(j)) {
                        // set it to least if < least
                        least = Math.min(least,reducedMatrix[i][j]);
                    }
                }
            }
        }
        // for each element
        for (int i = 0; i < reducedMatrix.length; i++) {
            for (int j = 0; j < reducedMatrix[0].length; j++) {
                if (crossedRows.contains(i)){
                    // += least if both row and col are crossed
                    if (crossedCols.contains(j)) reducedMatrix[i][j] += least;
                } else {
                    // -= least if it is uncrossed
                    if (!crossedCols.contains(j)) reducedMatrix[i][j] -= least;
                }
            }
        }
    }

    /**
     * STEP 5
     * retrieves the assignment from 'assignment'
     * gets the costs from the original matrix to be stored in 'costs'
     * calculates total cost to be stored in 'score'
     */
    public void calcAssignment() {
        // for each row
        for (int i = 0; i < originalMatrix.length; i++) {
            // get its assigned col from assignment map
            // add coordinates to assignments
            assignments[i][0] = assignment.get(i);
            assignments[i][1] = i;
            // get cost from originalMatrix and add to costs
            costs[i] = originalMatrix[assignments[i][0]][assignments[i][1]];
            // update total score
            score += costs[i];
        }
    }

    /**
     * contains all steps to solve problem
     * calls ui functions to update user
     */
    public void solve() {
        // step 0: check if maximization problem
        // do necessary calc
        if (maximize) {
            getMaxValue();
            maximize();
            UI.update(5);
            UI.printMatrix(originalMatrix);
        }
        // step 1: reduce rows
        UI.update(0);
        reduceRows();
        UI.printMatrix(reducedMatrix);
        // step 2: reduce cols
        UI.update(1);
        reduceCols();
        UI.printMatrix(reducedMatrix);
        // if no assignment is possible
        while (!assignmentPossible(0)) {
            crossedRows.clear();
            crossedCols.clear();
            // step 3: cross out zeros with minimum lines
            UI.update(2);
            crossOutZeros();
            UI.printMatrix(reducedMatrix, crossedRows, crossedCols);
            // step 4: find least element, subtract from uncrossed, add to intersections
            UI.update(3);
            addSubLeastUncrossed();
            UI.printMatrix(reducedMatrix);
        }
        // step 5: get assignment
        UI.update(4); 
        UI.printMatrix(reducedMatrix, assignment); 
        if (maximize) maximize(); 
        UI.printMatrix(originalMatrix);
        calcAssignment();
        UI.printResult(score, assignments, costs);
    }
}
