package project23.framework.player;

import project23.connection.ServerPlayerCommunicationListener;
import project23.framework.board.Board;
import project23.util.Logger;

/**
 * This class is a subclass of Player and stores a ServerPlayer.
 */
public class ServerPlayer extends Player implements ServerPlayerCommunicationListener {

    public ServerPlayer(Board board, int id, String name) {
        super(board, id, name);
    }

    public ServerPlayer(Board board, int id) {
        super(board, id);
    }

    /**
     * method for after receiving move from the server.
     *
     * @param whoPlayer the name of player whose turn it is.
     * @param move      the number of the move as string.
     */
    @Override
    public void turnReceive(String whoPlayer, String move) {
        if (whoPlayer.equals(getName())) {
            try {
                int intMove = Integer.parseInt(move);

                if (intMove < 0 || intMove >= (board.getWidth() * board.getHeight())) {
                    Logger.error("ServerPlayer received an out of bounds move from the server! Move nr: " + intMove + ". Discarding move..");
                    return;
                }

                int x = intMove % getBoard().getWidth();
                int y = intMove / getBoard().getWidth();

                if (!board.isValidMove(x, y)) {
                    Logger.error("ServerPlayer received an invalid move from the server! Move coordinates: (" + x + ", " + y + "). Discarding move..");
                    return;
                }

                board.makeRawMove(this, x, y);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * calls board.finalizeRawMove if its our turn, requests a move from opponent otherwise.
     */
    @Override
    public void finalizeTurn() {
        if (board.getCurrentPlayer() == this) {
            board.finalizeRawMove();
        } else {
            board.requestPlayerMove();
        }
    }

    /**
     * @return if the valid moves should be displayed on the GUI.
     */
    @Override
    public boolean isShowValidMoves() {
        return false;
    }
}
