package project23.ttt.player;

import project23.framework.board.Board;
import project23.framework.player.MinimaxAIPlayer;
import project23.framework.player.Player;

public class TTTMinimaxAIPlayer extends MinimaxAIPlayer {

    public TTTMinimaxAIPlayer(Board board, int id, String name, AIDifficulty difficulty) {
        super(board, id, name, difficulty);
    }

    public TTTMinimaxAIPlayer(Board board, int id, AIDifficulty difficulty) {
        super(board, id, difficulty);
    }

    @Override
    protected float evaluateBoard(Board board, int treeDepth) {
        Player winner = board.calculateWinner();
        if (winner == this) {
            // Win for self
            return 10 + treeDepth;
        } else if (winner != null) {
            // Win for other
            return -10 - treeDepth;
        } else {
            // Draw or no win
            return 0;
        }
    }

    @Override
    public int getStartDepth() {
        return 7;
    }
}
