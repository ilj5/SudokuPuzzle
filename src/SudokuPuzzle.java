import java.util.*;

public class SudokuPuzzle {
    private int[][] puzzle;
    private Random random;

    // Difficulty Enum
    public enum Difficulty {
        EASY(50),
        STANDARD(40),
        HARD(30),
        EXTREME(20);

        private final int initialFilledCells;

        Difficulty(int initialFilledCells) {
            this.initialFilledCells = initialFilledCells;
        }

        public int getInitialFilledCells() {
            return initialFilledCells;
        }
    }

    // Constructor to generate and validate a puzzle
    public SudokuPuzzle(Difficulty difficulty) {
        random = new Random();
        puzzle = generatePuzzle(difficulty);
    }

    // Generate a complete, valid Sudoku board
    private int[][] generateSolvedBoard() {
        int[][] board = new int[9][9];
        boolean flag = solveSudoku(board, 0, 0);
        while (!flag) {
            flag = solveSudoku(board, 0, 0);
        }
        return board;
    }

    // Recursive backtracking solver to fill the board
    private boolean solveSudoku(int[][] board, int row, int col) {
        // If we've filled the entire board, we're done
        if (row == 9) {
            return true;
        }

        // Move to next row when current row is complete
        if (col == 9) {
            return solveSudoku(board, row + 1, 0);
        }

        // Skip already filled cells
        if (board[row][col] != 0) {
            return solveSudoku(board, row, col + 1);
        }

        // Try numbers 1-9
        ArrayList<Integer> numbers = getShuffledNumbers();
        for (int num : numbers) {
            if (isValidMove(board, row, col, num)) {
                board[row][col] = num;

                // Recursively try to fill next cell
                if (solveSudoku(board, row, col + 1)) {
                    return true;
                }

                // Backtrack if solution not found
                board[row][col] = 0;
            }
        }

        return false;
    }

    // Check if a number can be placed in a specific cell
    private boolean isValidMove(int[][] board, int row, int col, int num) {
        // Check row
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }

        // Check column
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }

        // Check 3x3 box
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int i = boxRowStart; i < boxRowStart + 3; i++) {
            for (int j = boxColStart; j < boxColStart + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    // Shuffle numbers to add randomness to board generation
    private ArrayList<Integer> getShuffledNumbers() {
        ArrayList<Integer> numbers = new ArrayList<>();
        int r;
        for (int i = 0; i < 9; i++) {
            do {
                r = random.nextInt(9) + 1;
            } while (numbers.contains(r));

            numbers.add(r);
        }
        return numbers;
    }

    // Generate puzzle by removing cells based on difficulty
    private int[][] generatePuzzle(Difficulty difficulty) {
        // Generate a solved board
        int[][] solvedBoard = generateSolvedBoard();

        // Create puzzle by removing cells
        int[][] puzzleBoard = new int[9][9];

        // Copy the solved board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                puzzleBoard[i][j] = solvedBoard[i][j];
            }
        }

        // Remove cells based on difficulty
        int cellsToRemove = 81 - difficulty.getInitialFilledCells();

        while (cellsToRemove > 0) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if (puzzleBoard[row][col] != 0) {
                puzzleBoard[row][col] = 0;
                cellsToRemove--;
            }
        }

        return puzzleBoard;
    }

    // Check if the puzzle is solvable
    private boolean isSolvable() {
        return isSolvable(copyPuzzle());
    }

    // Recursive method to check if puzzle is solvable
    private boolean isSolvable(int[][] board) {
        // Find an empty cell
        int[] emptyCell = findEmptyCell(board);

        int row = emptyCell[0];
        int col = emptyCell[1];

        // If no empty cell, puzzle is solved
        if (row == -1 && col == -1) {
            return true;
        }



        // Try numbers 1-9
        for (int num = 1; num <= 9; num++) {
            if (isValidMove(board, row, col, num)) {
                // Try this number
                board[row][col] = num;

                // Recursively try to solve rest of the puzzle
                if (isSolvable(board)) {
                    return true;
                }

                // If not solvable, backtrack so it would be considered as empty
                board[row][col] = 0;
            }
        }

        // No solution found
        return false;
    }

    // Find an empty cell in the board
    private int[] findEmptyCell(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    return new int[]{row, col};
                }
            }
        }
        return new int[]{-1, -1};
    }

    // Create a deep copy of the puzzle
    private int[][] copyPuzzle() {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                copy[i][j] = puzzle[i][j];
            }
        }
        return copy;
    }

    // Validate the entire puzzle
    public boolean isValid() {
        return validateRows() &&
                validateColumns() &&
                validateBoxes() &&
                validateRowSums() &&
                validateColumnSums() &&
                validateBoxSums();
    }

    // Validate no repetitions in rows
    private boolean validateRows() {
        for (int row = 0; row < 9; row++) {
            ArrayList<Integer> rowSet = new ArrayList<>();
            for (int col = 0; col < 9; col++) {
//                if (puzzle[row][col] == 0) continue;
                if (!rowSet.contains(puzzle[row][col]) || (puzzle[row][col] < 1) || (puzzle[row][col] > 9)) {
                    return false;
                }
                rowSet.add(puzzle[row][col]);
            }
        }
        return true;
    }

    // Validate no repetitions in columns
    private boolean validateColumns() {
        for (int col = 0; col < 9; col++) {
            ArrayList<Integer> colSet = new ArrayList<>();
            for (int row = 0; row < 9; row++) {
//                if (puzzle[row][col] == 0) continue;
                if (!colSet.contains(puzzle[row][col]) || puzzle[row][col] < 1 || puzzle[row][col] > 9) {
                    return false;
                }
                colSet.add(puzzle[row][col]);
            }
        }
        return true;
    }

    // Validate no repetitions in 3x3 boxes
    private boolean validateBoxes() {
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                ArrayList<Integer> boxSet = new ArrayList<>();
                for (int row = boxRow * 3; row < boxRow * 3 + 3; row++) {
                    for (int col = boxCol * 3; col < boxCol * 3 + 3; col++) {
//                        if (puzzle[row][col] == 0) continue;
                        if (!boxSet.contains(puzzle[row][col]) || puzzle[row][col] < 1 || puzzle[row][col] > 9) {
                            return false;
                        }
                        boxSet.add(puzzle[row][col]);
                    }
                }
            }
        }
        return true;
    }

    // Validate row sums
    private boolean validateRowSums() {
        for (int row = 0; row < 9; row++) {
            int rowSum = 0;
            for (int col = 0; col < 9; col++) {
                if (puzzle[row][col] != 0) {
                    rowSum += puzzle[row][col];
                }
            }
            if (rowSum > 45) {
                return false;
            }
        }
        return true;
    }

    // Validate column sums
    private boolean validateColumnSums() {
        for (int col = 0; col < 9; col++) {
            int colSum = 0;
            for (int row = 0; row < 9; row++) {
                if (puzzle[row][col] != 0) {
                    colSum += puzzle[row][col];
                }
            }
            if (colSum > 45) {
                return false;
            }
        }
        return true;
    }

    // Validate box sums
    private boolean validateBoxSums() {
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                int boxSum = 0;
                for (int row = boxRow * 3; row < boxRow * 3 + 3; row++) {
                    for (int col = boxCol * 3; col < boxCol * 3 + 3; col++) {
                        if (puzzle[row][col] != 0) {
                            boxSum += puzzle[row][col];
                        }
                    }
                }
                if (boxSum > 45) {
                    return false;
                }
            }
        }
        return true;
    }

    // Getter for the puzzle
    public int[][] getPuzzle() {
        return puzzle;
    }

    public void setNewNumber(int row, int col, int num) {
        puzzle[row][col] = num;
    }

    // Print puzzle utility method
    public void printPuzzle() {
        for (int[] row : puzzle) {
            for (int cell : row) {
                System.out.print(cell == 0 ? ". " : cell + " ");
            }
            System.out.println();
        }
    }

    // Main method to demonstrate usage
    public static void main(String[] args) {
        // Generate puzzles of different difficulties
        SudokuPuzzle easyPuzzle = new SudokuPuzzle(Difficulty.EXTREME);
        System.out.println("Easy Puzzle:");
        easyPuzzle.printPuzzle();
        System.out.println("\nPuzzle is valid: " + easyPuzzle.isValid());
        System.out.println("Puzzle is solvable: " + easyPuzzle.isSolvable());
    }


//    throw new IllegalStateException("Could not generate a valid Sudoku board");
}