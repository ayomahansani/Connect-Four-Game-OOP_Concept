package lk.ijse.dep.service;

public interface BoardUI {  //A blueprint of a behavior -> before JDK 1.8,only abstract methods can have in an interface
    void update(int col, boolean isHuman);  //public abstract void update(int col, boolean isHuman);

    void notifyWinner(Winner winner);   //public abstract void notifyWinner(Winner winner);
}
