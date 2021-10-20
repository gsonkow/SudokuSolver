package main;

import java.util.*;
/**
 * Methods and constructor to define and solve a game of Sudoku.
 * @author Gabriel (Gabe) Sonkowsky
 */
public class SudokuSolver {
	private static final int[][] TEST_BOARD = {{5,3,0,0,7,0,0,0,0},
											   {6,0,0,1,9,5,0,0,0},
											   {0,9,8,0,0,0,0,6,0},
											   {8,0,0,0,6,0,0,0,3},
											   {4,0,0,8,0,3,0,0,1},
										  	   {7,0,0,0,2,0,0,0,6},
											   {0,6,0,0,0,0,2,8,0},
											   {0,0,0,4,1,9,0,0,5},
											   {0,0,0,0,8,0,0,7,9}};
	
	private static final int[] VALID_NUMBERS = {1,2,3,4,5,6,7,8,9};
								
								 
	
	private int[][] gameBoard,
	                originBoard;
	
	/**
	 * Instantiates a new Sudoku board to be solved. TODO: throws exception when not 9x9
	 * @param board to be solved
	 */
	public SudokuSolver(int[][] board) {
		this.gameBoard = deepCopyIntMatrix(board);
		this.originBoard = deepCopyIntMatrix(board);
	}
	
	/**
	 * Alternative constructor with no arguments that sets the board to a default value.
	 */
	public SudokuSolver() {
		this.gameBoard = deepCopyIntMatrix(TEST_BOARD);
		this.originBoard = deepCopyIntMatrix(TEST_BOARD);
	}
	
	/**
	 * Places a valid value at a valid location on the gameBoard, and then returns the updated board.
	 * @param val to insert
	 * @param x column, starting from the top
	 * @param y row, starting from the top
	 * @return the updated gameBoard
	 */
	public int[][] insertValAt(int val, int x, int y) {
		if (val < 1 || val > 9 || this.originBoard[y][x] != 0) {
			throw new IllegalArgumentException("val must be between 1-9 and be placed within a valid space");
		}
		this.gameBoard[y][x] = val;
		return this.gameBoard;
	}
	
	/**
	 * Checks if the gameBoard is breaking the rules
	 * @return if the gameBoard is valid
	 */
	public boolean checkValid() {
		int[] checkList = new int[9];
		//rows
		for (int i = 0; i < this.gameBoard.length; i++) {
			checkList = fillRow(i).clone();
			if (!numValid(checkList)) {
				return false;
			}
		}
		//columns
		for (int i = 0; i < this.gameBoard.length; i++) {
			checkList = fillCol(i).clone();
			if (!numValid(checkList)) {
				return false;
			}
		}
		//3x3s
		for (int i = 0; i < this.gameBoard.length / 3; i++) {
			for (int k = 0; k < this.gameBoard.length / 3; k++) {
				checkList = fillBox(k, i).clone();
				if (!numValid(checkList)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Helper function to fill a array with the values stored in a specified row
	 * @param y index of the row to take values
	 * @return int[] array filled with the the values stored in the specified row
 	 */
	private int[] fillRow(int y) {
		int[] rowList = new int[9];
		for (int x = 0; x < this.gameBoard.length; x++) {
			rowList[x] = this.gameBoard[y][x];
		}
		return rowList;
	}
	
	/**
	 * Helper function to fill a array with the values stored in a specified column
	 * @param x index of the column to take values
	 * @return int[] array filled with the the values stored in the specified column
	 */
	private int[] fillCol(int x) {
		int[] colList = new int[9];
		for (int y = 0; y < this.gameBoard.length; y++) {
			colList[y] = this.gameBoard[y][x];
		}
		return colList;
	}
	
	/**
	 * Helper function to fill a array with the values stored in a specified box
	 * @param x position of the box, relative to other boxes (3x3)
	 * @param y position of the box, relative to other boxes (3x3)
	 * @return int[] array filled with the values stored in the specified box
	 */
	private int[] fillBox(int x, int y) {
		int[] boxList = new int[9];
		for (int i = 0; i < this.gameBoard.length / 3; i++) {
			boxList[(i * 3)] = this.gameBoard[(y * 3) + i][(x * 3)];
			boxList[(i * 3) + 1] = this.gameBoard[(y * 3) + i][(x * 3) + 1];
			boxList[(i * 3) + 2] = this.gameBoard[(y * 3) + i][(x * 3) + 2];
		}
		return boxList;
	}
	
	/**
	 * checks if list contains 1-9 or a 0
	 * @param check list to check
	 * @return bool if list contains 1-9 or a 0
	 */
	private static boolean numValid(int[] check) {
		Arrays.sort(check);
		if (!Arrays.equals(check, VALID_NUMBERS) && check[0] != 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks what numbers are available after the values already on the
	 * Sudoku board are taken
	 * @param row of Values unavailable
	 * @param col of Values unavailable
	 * @param box of Values unavailable
	 * @return int[] containing available numbers
	 */
	private int[] numAvailable(int[] row, int[] col, int[] box) {
		Set<Integer> used = new HashSet<Integer>();
		int index = 0;
		
		for (int i = 0; i < row.length; i++) {
			if (row[i] != 0) 
				used.add(row[i]);
			if (col[i] != 0) 
				used.add(col[i]);
			if (box[i] != 0) 
				used.add(box[i]);
		}
		
		int[] available = new int[9 - used.size()];
		//available[0] = 0;
		
		for (int val : VALID_NUMBERS) {
			if (!used.contains(val)) {
				available[index] = val;
				index++;
			}
		}
		Arrays.sort(available);
		return available;
	}
	
	/**
	 * Updates a gameBoard with its solution
	 * @param depth Should be 0 when used
	 * @return if it was solved
	 */
	public boolean solve(int depth) {
		if (!this.checkValid() && depth == 0) {
			throw new IllegalArgumentException("must begin with valid board");
		}
		int[] available;
		boolean found = false;
		for (int y = 0; y < this.gameBoard.length; y++) {
			for (int x = 0; x < this.gameBoard.length; x++) {
				 if (this.gameBoard[y][x] == 0) {
					 found = true;
					 available = numAvailable(fillRow(y), fillCol(x), fillBox((int) Math.floor(x / 3.0),(int) Math.floor(y / 3.0)));
					 for (int i = 0; i < available.length; i++) { 
						 this.gameBoard[y][x] = available[i];
						 if (this.solve(depth++)) {
							 return true;
						 }
					 }
				 }
			}
		}
		return !found;
	}
	
	/**
	 * Returns gameBoard formatted as a Sudoku board
	 * @return gameBoard formatted as a Sudoku board
	 */
	@Override
	public String toString() {
		String ret = "";
		for (int[] i : this.gameBoard) {
			ret+="{";
			for (int k = 0; k < i.length - 1; k++) {
				ret+= i[k] + ", ";
			}
			ret+= i[8] + "}\n";
		}
		return ret;
	}
	
	/**
	 * Creates a deep clone of a 2d int array
	 * @see https://stackoverflow.com/questions/9106131/how-to-clone-a-multidimensional-array-in-java/9106176
	 * @param input 2d array to be cloned
	 * @return a deep clone of a 2d int array
	 */
	private static int[][] deepCopyIntMatrix(int[][] input) {
	    if (input == null) {
	        throw new IllegalArgumentException("null input");
	    }
	    int[][] result = new int[input.length][];
	    for (int r = 0; r < input.length; r++) {
	        result[r] = input[r].clone();
	    }
	    return result;
	}
	
	public static void main(String[] args) {
		SudokuSolver bdy = new SudokuSolver();
		System.out.println(bdy.toString());
		System.out.println("----------------------------");
		bdy.solve(0);
		System.out.println(bdy.toString());
	}
	
	
}