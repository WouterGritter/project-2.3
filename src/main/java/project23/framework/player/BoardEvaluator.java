package project23.framework.player;

import project23.framework.board.Board;

public interface BoardEvaluator {
    float evaluateBoard(Board board, int treeDepth, Player asWho);
}
