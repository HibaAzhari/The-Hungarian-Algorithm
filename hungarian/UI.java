/**
 * UI for Hungarian algorithm implementation
 * @author Hiba Azhari
 */

package hungarian;

import java.util.Scanner;
import java.util.*;
public class UI {

    public static Scanner in = new Scanner(System.in);

    /**
     * get matrix dimensions from user in range 1 - 30
     * @return matrix dimensions
     */
    public static int[] initMatrix() {
        System.out.println("\n\n--- Assignment Problem Simulation ---");
        System.out.print("\nPlease enter number of jobs/operators (1 - 30): ");
        int assign = 0;
        while (assign < 1 || assign > 30) {
            if (in.hasNextInt()) {
                assign = in.nextInt();
                if (assign < 1 || assign > 30) {
                    System.out.print("Please enter a valid input (1 - 30):");
                    in.nextLine();
                }
            } else {
                System.out.print("Please enter a valid input (1 - 30):");
                in.nextLine();
            }
        }
        return new int[]{assign,assign};
    }

    /**
     * ask whether user want to maximize or minimize cost
     * @return
     */
    public static boolean maximize() {
        System.out.print("Maximize(M) or Minimize(m) score?");
        String userIn = in.next();
        while (!userIn.equals("M") && !userIn.equals("m")) {
            System.out.print("Please enter a valid input (M/m):");
            userIn = in.next();
        }
        if (userIn.equals("M")) return true;
        return false;
    }

    /**
     * transform numbers into their string form for printing in matrix
     * @param i number
     * @return "  i"
     */
    public static String stringify(int i) {
        if(i == -1) return "  #";
        if(i < 10) return "  "+ i;
        else return " "+ i ;
    }

    /**
     * build the header of the matrix
     * @param cols
     * @return
     */
    public static String header(int cols) {
        String output = "          ";
        for (int i = 0; i < cols/2 - 1; i++) {
            output += " ";
        }
        output += "Operators";
        for (int i = 0; i < cols/2 - 2; i++) {
            output += " ";
        }
        output += "\n         ";
        for (int i = 0; i < cols; i++) output += stringify(i+1);
        return output;
    }

    /**
     * print matrix row by row
     * @param matrix
     */
    public static void printMatrix(int[][] matrix) {
        String[] output = new String[matrix.length];
        for (int i = 0 ; i < matrix.length; i++) {
            if ( i == matrix.length/2) output[i] = " Jobs ";
            else output[i] = "      ";
            output[i] += stringify(i+1);
            for (int j = 0; j < matrix[0].length; j++) {
                output[i] += stringify(matrix[i][j]);
            }
        }
        System.out.println();
        System.out.println(header(matrix[0].length));
        for (String s : output) {
            System.out.println(s);
        }
        System.out.println();
    }

    /**
     * print matrix with rows and columns crossed out
     * @param matrix to be printed
     * @param crossedRows set of row indices
     * @param crossedCols set of column indices
     */
    public static void printMatrix(int[][] matrix, Set<Integer> crossedRows, Set<Integer> crossedCols) {
        String[] output = new String[matrix.length+1];
        for (int i = 0 ; i < matrix.length; i++) {
            if ( i == matrix.length/2) output[i] = " Jobs ";
            else output[i] = "      ";
            output[i] += stringify(i+1);
            for (int j = 0; j < matrix[0].length; j++) {
                output[i] += stringify(matrix[i][j]);
            }
            if (crossedRows.contains(i)) output[i] += "  X";
        }
        output[matrix.length] = "         ";
        for (int j = 0; j < matrix[0].length; j++) {
            if (crossedCols.contains(j)) output[matrix.length] += "  X";
            else output[matrix.length] += "   ";
        }
        System.out.println();
        System.out.println(header(matrix[0].length));
        for (String s : output) {
            System.out.println(s);
        }
        System.out.println();
    }

    /**
     * print matrix with assignments starred
     * @param matrix to be printed
     * @param assignment map of column to row indices of assignments
     */
    public static void printMatrix(int[][] matrix, Map<Integer,Integer> assignment) {
        String[] output = new String[matrix.length];
        for (int i = 0 ; i < matrix.length; i++) {
            if ( i == matrix.length/2) output[i] = " Jobs ";
            else output[i] = "      ";
            output[i] += stringify(i+1);
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0
                        && assignment.containsKey(j)
                        && assignment.get(j) == i) {
                    output[i] += " *0";
                }
                else output[i] += stringify(matrix[i][j]);
            }
        }
        System.out.println();
        System.out.println(header(matrix[0].length));
        for (String s : output) {
            System.out.println(s);
        }
        System.out.println();
    }

    /**
     * get assignment costs from user
     * @param r number of rows (jobs)
     * @param c number of columns (operators)
     * @return matrix with costs
     */
    public static int[][] getCostInput(int r, int c) {
        int[][] matrix = new int[r][c];
        for (int[] a : matrix) Arrays.fill(a, -1);
        
        printMatrix(matrix);
        
        for (int j = 0; j < c; j++) {
            
            System.out.println("Please enter job costs for operator "+ (j+1) +"\n");
            for (int i = 0; i < r; i++) {
                System.out.print("Job "+ (i+1) +" cost (OP "+ (j+1) +"):");
                int input = in.nextInt();
                matrix[i][j] = input;
            }
            printMatrix(matrix);
        }
        return matrix;
    }

    /**
     * print updates to user based on update code entered
     * @param code
     */
    public static void update(int code) {
        switch (code) {
            case 0:
            System.out.println("\nFirst the rows are reduced: ");
            break;
            case 1:
            System.out.println("\nThen columns are reduced: ");
            break;
            case 2:
            System.out.println("\nIf no assignment is possible, zeros are" +
                    " crossed out with minimum number of lines: ");
            break;
            case 3:
            System.out.println("\nThen the smallest uncrossed value is found, " +
                    "subtracted from all other uncrossed \n values and added to " +
                    "values at intersections:");
            break;
            case 4:
            System.out.println("\nAn assignment is now possible. The result is:\n");
            break;
            case 5:
            System.out.println("\nTo maximize, all numbers must be subtracted from" +
                    " the largest number in the matrix:\n");
        }
    }

    /**
     * print final solution to assignment problem
     * @param score total cost
     * @param assignments array of int[row index, col index] for each assignment
     * @param costs array of assignment costs
     */
    public static void printResult(int score, int[][] assignments, int[] costs) {
        for (int[] i : assignments) {
            System.out.println("Operator: "+ (i[1]+1) +" => Job: "+ (i[0]+1));
        }
        System.out.print("\nTotal Cost = ");
        for (int i = 0; i < costs.length; i++) {
            if (i == costs.length-1) System.out.print(costs[i]);
            else System.out.print( costs[i] +" + ");
        }
        System.out.println(" = "+ score );
    }

    /**
     * main function
     * @param args
     */
     public static void main(String[] args) {
        int[] dimensions = UI.initMatrix();
        int[][] matrix = UI.getCostInput(dimensions[0], dimensions[1]);
        hungarian.AssignmentProblem problem = new hungarian.AssignmentProblem(matrix, UI.maximize());
        problem.solve();
    }
}