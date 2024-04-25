package lk.ijse.dep.service;

public abstract class Player {  // Must have at least one abstract method
    protected Board board;  // Protected properties -> can access only within same package or other package's subclasses

    public Player(Board board) {    // Here, the argument passed from the HumanPlayer class or AiPlayer class is taken as the parameter
        this.board = board;
    }

    public abstract void movePiece(int col);    // Only method definition , no implementation

}
