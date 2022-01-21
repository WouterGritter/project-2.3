package project23.ttt.player;

import project23.framework.board.Board;
import project23.framework.player.BoardEvaluator;
import project23.framework.player.Player;

public class TTTBoardEvaluator implements BoardEvaluator {

    @Override
    public float evaluateBoard(Board board, int treeDepth, Player asWho) {
        Player winner = board.calculateWinner();
        if (winner == asWho) {
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
}
