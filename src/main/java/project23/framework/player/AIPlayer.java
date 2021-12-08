package project23.framework.player;

import project23.framework.board.Board;

/**
 * This abstract class is a subclass of Player and stores an AIPlayer. implements MoveRequestable.
 */
public abstract class AIPlayer extends Player implements MoveRequestable {

    public AIPlayer(Board board, int id, String name) {
        super(board, id, name);
    }

    public AIPlayer(Board board, int id) {
        super(board, id);
    }
}
