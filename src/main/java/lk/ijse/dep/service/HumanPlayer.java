package lk.ijse.dep.service;

public class HumanPlayer extends Player{    // HumanPlayer class is inherited the Player class
    public HumanPlayer(Board newBoard) {    // Here, the argument passed from the BoardController class is taken as the parameter
        super(newBoard);    // Call to the Player class's parameterized constructor
    }

    @Override
    public void movePiece(int col) {    // This parameter is the human clicked column index

        //check whether the move is legal
        boolean isMoveLegal = board.isLegalMove(col);   // Here, board reference is come from Player class and call to the BoardImpl class's method

        if(isMoveLegal) {   // If it is a legal move -> (isMoveLegal == true)

            // Update board
            board.updateMove(col, Piece.BLUE);  // Call to the BoardImpl class's method

            // Update boardUI
            board.getBordUI().update(col,true); // Call to the BoardController class's method

            Winner winner = board.findWinner(); // Call to the BoardImpl class's method

            // Notify to the UI about the winner
            if(winner.getWinningPiece()!= Piece.EMPTY){ // Check if there is a winner
                board.getBordUI().notifyWinner(winner); // Call to the BoardController class's method
            }
            else{
                // Check if the game is tied
                if (!board.existLegalMoves()){  // If no exist legal moves
                    board.getBordUI().notifyWinner(new Winner(Piece.EMPTY)); // Call to the BoardController class's method
                }
            }
        }
    }
}
