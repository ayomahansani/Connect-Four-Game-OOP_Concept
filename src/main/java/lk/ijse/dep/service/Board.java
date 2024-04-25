package lk.ijse.dep.service;

public interface Board {    //A blueprint of a behavior -> before JDK 1.8,only abstract methods can have in an interface
    int NUM_OF_COLS = 6;    //public static final int NUM_OF_COLS = 6;
    int NUM_OF_ROWS = 5;    //public static final int NUM_OF_ROWS = 5;

    BoardUI getBordUI(); //public abstract BoardUI getBordUI();

    int findNextAvailableSpot(int col); //public abstract int findNextAvailableSpot(int col);

    boolean isLegalMove(int col);   //public abstract boolean isLegalMove(int col);

    boolean existLegalMoves();  //public abstract boolean existLegalMoves();

    void updateMove(int col, Piece move);   //public abstract void updateMove(int col, Piece move);

    void updateMove(int col, int row, Piece move);  //public abstract void updateMove(int col, int row, Piece move);

    Winner findWinner();    //public abstract Winner findWinner();

}
