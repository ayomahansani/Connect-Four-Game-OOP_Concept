package lk.ijse.dep.service;

import lk.ijse.dep.controller.BoardController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AiPlayer extends Player {  // AiPlayer class is inherited the Player class -----> AiPlayer class has static inner classes named Tree,Node,State,MonteCarloTreeSearch,Uct

    public AiPlayer(Board newBoard) {   // Here, the argument passed from the BoardController class is taken as the parameter
        super(newBoard);    // Call to the Player class's parameterized constructor
    }

    private static final int ITERATIONS = 1000;
    private static final double C = 1.4;

    @Override
    public void movePiece(int col) {

        if (col == -1) {
            int randomCol;
            int bestMove = findBestMove();

            // Update the board with the AI player's move (Piece.GREEN)
            board.updateMove(bestMove,Piece.GREEN);
            // Notify the UI about the move
            board.getBordUI().update(bestMove,false);


        /*
            do {
                // Pick a random integer value between 0 and 5
                //Math.random() returns a pseudorandom double value greater than or equal to 0.0 and less than 1.0.So the result has to be multiplied by 5

                col = (int) (Math.random() * board.NUM_OF_COLS);    //Math.random() * 5

            } while (!board.isLegalMove(col));

            // Update the board with the AI player's move (Piece.GREEN)
            board.updateMove(col, Piece.GREEN);

            // Notify the UI about the move
            board.getBordUI().update(col, false);
        */


            Winner winner = board.findWinner(); // Call to the BoardImpl class's method

            if (winner.getWinningPiece() != Piece.EMPTY) {  // Check if there is a winner
                //notify to the UI about the winner
                board.getBordUI().notifyWinner(winner); // Call to the BoardController class's method
            } else {
                // Check if the game is tied
                if (!board.existLegalMoves()) { // If there are no legal moves
                    board.getBordUI().notifyWinner(new Winner(Piece.EMPTY));    // Call to the BoardController class's method
                }
            }

        }

    }

    private int findBestMove() {
        int bestCol = -1;
        double bestScore = Double.NEGATIVE_INFINITY;

        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(); // Create a single instance of MCTS

        for (int col = 0; col < board.NUM_OF_COLS; col++) {

            if (board.isLegalMove(col)) {   // If there is a legal move in a specific column
                double score = mcts.findNextMove(board, col);
                double heuristicValue = calculateColumnHeuristicValues(col, board);

                // Consider the column heuristic value when selecting the best move
                score += heuristicValue;

                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            }
        }

        return bestCol; // Return the best column index(-1)
    }



    private static class Tree { // Static Inner(nested) Class -> Can access this class without creating an object of the outer class & can be accessed by outer class name
        private Node root;

        public Tree() {
            this.root = new Node(new State());
        }

        public void setRoot(Node root) {
            this.root = root;
        }

        public Node getRoot() {
            return root;
        }
    }



    private static class Node { // Static Inner(nested) Class -> Can access this class without creating an object of the outer class & can be accessed by outer class name

        private State state;
        private Node parent;
        List<Node> childArray;

        public Node() {

        }

        public Node(State state) {
            this.state = state;
            this.childArray = new ArrayList<>();
            this.parent = null;

        }

        public void setState(State state) {
            this.state = state;
        }

        public State getState() {
            return state;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public Node getParent() {
            return parent;
        }

        public void setChildArray(List<Node> childArray) {
            this.childArray = childArray;
        }

        public List<Node> getChildArray() {
            return childArray;
        }

        public Node getRandomeChildNode() {
            if(childArray.isEmpty()){
                return null;
            }
            int children = new Random().nextInt(childArray.size());

            return childArray.get(children);

        }

    }



    private static class State {    // Static Inner(nested) Class -> Can access this class without creating an object of the outer class & can be accessed by outer class name
        private Board board;
        private double bestScore;
        private int visitCount;

        public State() {

        }

        public State(Board board) {
            this.bestScore = 0.0;
            this.visitCount = 0;
            this.board = board;
        }

        public State(State state) { // Copy constructor
            this.board = new BoardImpl((BoardController) state.getBoard().getBordUI()); // Create a copy of the board
            this.visitCount = state.getVisitCount();
            this.bestScore = state.getBestScore();
        }

        public Board getBoard() {
            return board;
        }

        public void setBoard(Board board) {
            this.board = board;
        }

        public int getVisitCount() {
            return visitCount;
        }

        public void setVisitCount(int visitCount) {
            this.visitCount = visitCount;
        }

        public double getBestScore() {
            return bestScore;
        }

        public void setBestScore(double bestScore) {
            this.bestScore = bestScore;
        }


        public void incrementVisit() {
            visitCount++;
        }

        public void addScore(double score) {
            bestScore += score;
        }

        public List<State> getPossibleState() {
            List<State> possibleMoves = new ArrayList<>();
            for (int col = 0; col < board.NUM_OF_COLS; col++) {
                if (board.isLegalMove(col)) {
                    State state = new State(this);  // Create new state and copy the current state to board
                    state.getBoard().updateMove(col, Piece.GREEN);  // Apply the move to the  state's board
                    possibleMoves.add(state);

                }
            }
            return possibleMoves;
        }

    }



    private static class MonteCarloTreeSearch { // Static Inner(nested) Class -> Can access this class without creating an object of the outer class & can be accessed by outer class name

        private Tree tree;

        public MonteCarloTreeSearch() {
            this.tree = new Tree();
        }

        public double findNextMove(Board board, int col) {

            Node rootNode = new Node(new State(board)); // Create a new root node with the current board state
            tree.setRoot(rootNode);

            for (int i = 0; i < ITERATIONS; i++) {

                // ********** Selection **********
                // ---> Step 1: Select the most promising node using the UCT (Upper Confidence Bound for Trees) algorithm
                Node promisingNode = selectPromisingNode(rootNode);

                // ---> Step 2: If the promising node has legal moves, expand it by creating child nodes
                if (promisingNode.getState().getBoard().existLegalMoves()) {
                    expandNode(promisingNode);
                }

                // ********** Expansion **********
                // ---> Step 3: Choose a node to explore
                Node nodeToExplore = promisingNode;
                if(!promisingNode.getChildArray().isEmpty()){
                    nodeToExplore = promisingNode.getRandomeChildNode();
                }

                // ********** Simulation **********
                // ---> Step 4: Simulate a random play (Monte Carlo simulation) from the chosen node
                double playOutResult = simulateRandomePlay(nodeToExplore);
                backPropogation(nodeToExplore, playOutResult);

            }

            // Step 6: After the specified number of iterations, find the best child node using UCT
            Node bestChild = Uct.findBestNodeWithUct(rootNode);

            // ********** Backpropagation **********
            // Step 7: Return the best child node's best score as the recommended move's heuristic value
            return bestChild.getState().getBestScore();
        }

        private void backPropogation(Node nodeToExplore, double playOutResult) {
            Node currentNode = nodeToExplore;
            if(currentNode!=null){
                currentNode.getState().incrementVisit();
                currentNode.getState().addScore(playOutResult);

                currentNode = currentNode.getParent();

            }

        }


    }



    private static double simulateRandomePlay(Node nodeToExplore) {

        Piece currentPlayer = Piece.GREEN;
        Node tempNode = new Node(new State(nodeToExplore.getState())); // Create a copy of the current state

        Board currentBoard = tempNode.getState().getBoard();

        for (int iteration = 0; iteration < ITERATIONS; iteration++) {
            List<Integer> legalMoves = new ArrayList<>();

            for (int col = 0; col < currentBoard.NUM_OF_COLS; col++) {
                if (currentBoard.isLegalMove(col)) {
                    legalMoves.add(col);
                }
            }

            if (legalMoves.isEmpty()) { // If game is tied
                return 0.0;
            }

            int randomMove = legalMoves.get(new Random().nextInt(legalMoves.size()));
            currentBoard.updateMove(randomMove, currentPlayer);

            // Increment the visit count for the current state.
            nodeToExplore.getState().incrementVisit();

            Winner winner = currentBoard.findWinner();

            if (winner.getWinningPiece() != Piece.EMPTY || !currentBoard.existLegalMoves()) {
                // Game is over (win or tie), break out of the loop
                break;
            }

            currentPlayer = (currentPlayer == Piece.GREEN) ? Piece.BLUE : Piece.GREEN; // Switch players
        }

        // Calculate and return the result of the game
        return calculateGameResult(currentBoard);
    }



    private static double calculateGameResult(Board currentBoard) {
        Winner winner = currentBoard.findWinner();

        if (winner.getWinningPiece() == Piece.GREEN) {
            // AI wins
            return 1.0;
        } else if (winner.getWinningPiece() == Piece.BLUE) {
            // Opponent wins
            return -1.0;
        } else if (!currentBoard.existLegalMoves()) {
            // Tie or draw (board is full of no winner)
            return 0.0;
        } else {
            // Game is still ongoing
            return 0.5;
        }
    }



    private static void expandNode(Node promisingNode) {
        State nodeState = promisingNode.getState();

        List<State> possibleStates = nodeState.getPossibleState();
        if (possibleStates == null) {
            return; // Exit if possibleStates is null
        }

        for (State state : possibleStates) {
            if (state != null) {
                Node newNode = new Node(state); // Create a new node using the possible state.
                newNode.setParent(promisingNode); // Set the parent of the new node to the promising node.
                promisingNode.getChildArray().add(newNode);// Add the new node as a child of the promising node.
                //System.out.println("Add");
            }
        }

    }



    private static Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;

        while (!node.getChildArray().isEmpty()) {
            node = Uct.findBestNodeWithUct(node);

            if (node == null) {
                break; // Exit the loop if node is null
            }
        }

        return node;
    }



    private static class Uct {  // Static Inner(nested) Class -> Can access this class without creating an object of the outer class & can be accessed by outer class name

        private static double uctValue(int parentVisit, double childWinScore, int childVisitCount, double explorationParameter) {
            if (childVisitCount == 0) {
                return Double.POSITIVE_INFINITY; // If the child node has not been visited at all, give it the highest exploration priority.
            }

            // Calculate the exploitation component of the UCT value.
            double exploitation = childWinScore / childVisitCount;
            // Calculate the exploration component of the UCT value.
            double exploration = explorationParameter * Math.sqrt(Math.log(parentVisit+1) / childVisitCount+1);
            // Combine the exploitation and exploration components to get the final UCT value
            return exploitation + exploration;
        }

        public static Node findBestNodeWithUct(Node node) {
            Node bestChild = null;
            double bestValue = Double.NEGATIVE_INFINITY;

            // Check if the current node has child nodes.
            if (!node.getChildArray().isEmpty()) {
                // Iterate through each child node.
                for (Node child : node.getChildArray()) {
                    int parentVisit = node.getState().getVisitCount();
                    // System.out.println(parentVisit);
                    int childVisit = child.getState().getVisitCount();
                    // System.out.println(childVisit);

                    // Handle the case where childVisitCount is zero
                    if (childVisit == 0) {
                        double uctValue = Double.POSITIVE_INFINITY; // Set to highest exploration priority
                        if (uctValue > bestValue) {
                            bestValue = uctValue;
                            bestChild = child;
                        }
                    } else {
                        double uctValue = uctValue(parentVisit, child.getState().getBestScore(), childVisit, C);
                        if (uctValue > bestValue) {
                            bestValue = uctValue;
                            bestChild = child;
                        }
                    }
                }
            }

            return bestChild;
        }
    }



    private double calculateColumnHeuristicValues(int col,Board board) {
        double score = 0;

        // Create a temporary board state to simulate a move in the current column
        int row = board.findNextAvailableSpot(col);

        if (row != -1) {
            // Create a copy of the board with the AI's move
            Board tempBoard = new BoardImpl((BoardController) board.getBordUI());
            tempBoard.updateMove(col, row, Piece.GREEN);

            // Evaluate the board based on different factors

            double centerValue = evaluateCenter(col);
            double randomValue = Math.random()*20; // Add some randomness

            score =  centerValue + randomValue;
        }

        return score;
    }


    private double evaluateCenter(int col) {
        int centerColumn = board.NUM_OF_COLS / 2;
        int distanceToCenter = Math.abs(col - centerColumn);
        // The closer to the center, the higher the score
        return 5.0 / (1 + distanceToCenter);
    }
}
