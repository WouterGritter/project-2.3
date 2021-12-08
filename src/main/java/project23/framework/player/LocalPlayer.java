package project23.framework.player;

import project23.framework.board.Board;
import project23.framework.board.BoardPiece;

import java.util.List;

/**
 * this class is a subclass of Player and stores a local player.
 */
public class LocalPlayer extends Player implements MoveRequestable {

    public LocalPlayer(Board board, int id, String name) {
        super(board, id, name);
    }

    public LocalPlayer(Board board, int id) {
        super(board, id);
    }

    /**
     * Executes a move as the local player.
     *
     * @param x The X-coordinate to move to
     * @param y The Y-coordinate to move to
     */
    public void executeMove(int x, int y) {
        board.makeMove(this, x, y);
    }

    @Override
    public void requestMove() {
        List<BoardPiece> validMoves = board.getValidMoves();
        if (validMoves.isEmpty()) {
            board.makeMove(this, null);
        }
    }

    /**
     * @return if valid moves should be displayed on the board or not.
     */
    @Override
    public boolean isShowValidMoves() {
        return true;
    }
}
