package lk.ijse.dep.service;

public class BoardImpl implements Board {   //Board interface is implemented to the BoardImpl class
    private final Piece[][] pieces;
    private final BoardUI boardUI;

    public BoardImpl(BoardUI boardUI) {
        this.boardUI = boardUI;

        pieces = new Piece[NUM_OF_COLS][NUM_OF_ROWS];   //pieces = new Piece[6][5];

        // To initialize all the pieces of the 2D array to Piece.EMPTY
        for (int col = 0; col < NUM_OF_COLS; col++) {
            for (int row = 0; row < NUM_OF_ROWS; row++) {
                pieces[col][row] = Piece.EMPTY;
            }
        }
    }

    // In here, we must override all methods in Board interface because Board interface is implemented to this class

    @Override
    public BoardUI getBordUI() {
        return this.boardUI; // Return the boardUI object's memory location
    }

    @Override
    public int findNextAvailableSpot(int col) { // This parameter is a human clicked column

        for (int row = 0; row < NUM_OF_ROWS; row++) {
            if (pieces[col][row].equals(Piece.EMPTY)) {
                return row; // Return the first empty space in the specified column
            }
        }
        return -1;  // If there are no free spaces in the specific column
    }

    @Override
    public boolean isLegalMove(int col) {

        int row = findNextAvailableSpot(col);

        if (row == -1) {
            return false;   // If the current move is an illegal move
        }
        return true;    // If the current move is a legal move
    }

    @Override
    public boolean existLegalMoves() {

        // Firstly, we need to check if there have at least one spot in a column
        for (int col = 0; col < NUM_OF_COLS; col++) {
            if (isLegalMove(col)) {
                return true;    // If there is a single spot available
            }
        }
        // No legal moves found in any column
        return false;
    }

    @Override
    public void updateMove(int col, Piece move) {

        int row = findNextAvailableSpot(col);

        if (row != -1) {
            pieces[col][row] = move;    // Assign GREEN or BLUE
        }
    }

    @Override
    public void updateMove(int col, int row, Piece move) {
        pieces[col][row] = move;
    }

    @Override
    public Winner findWinner() {

        L1:
        for (int col = 0; col < NUM_OF_COLS; col++) {
            L2:
            for (int row = 0; row < NUM_OF_ROWS; row++) {

                Piece currentPiece = pieces[col][row];

                if (currentPiece.equals(Piece.EMPTY)) {
                    continue L2;
                }

                // For horizontally checking
                if (col + 3 < NUM_OF_COLS && currentPiece == pieces[col + 1][row] && currentPiece == pieces[col + 2][row] && currentPiece == pieces[col + 3][row]) {
                    return new Winner(currentPiece, col, row, col + 3, row);    // Return a Winner type object
                }

                // For vertically checking
                if (row + 3 < NUM_OF_ROWS && currentPiece == pieces[col][row + 1] && currentPiece == pieces[col][row + 2] && currentPiece == pieces[col][row + 3]) {
                    return new Winner(currentPiece, col, row, col, row + 3);    // Return a Winner type object
                }

            }
        }
        // When cannot find winner
        return new Winner(Piece.EMPTY); // Return a Winner type object
    }
}
