package project23.othello.player;

import project23.framework.board.Board;
import project23.framework.player.BoardEvaluator;
import project23.framework.player.Player;

public class OthelloBoardEvaluator implements BoardEvaluator {

    /**
     * Weights for every project23.othello piece.
     * https://github.com/Jules-Lion/kurwa/blob/master/Dokumentation/An%20Analysis%20of%20Heuristics%20in%20Othello.pdf
     * The weight matrix is extract from the paper called "An Analysis of Heuristic in Othello"
     */
    private static final float[][] PIECE_WEIGHTS = {
            {4, -3, 2, 2, 2, 2, -3, 4},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            {2, -1, 1, 0, 0, 1, -1, 2},
            {2, -1, 0, 1, 1, 0, -1, 2},
            {2, -1, 0, 1, 1, 0, -1, 2},
            {2, -1, 1, 0, 0, 1, -1, 2},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            {4, -3, 2, 2, 2, 2, -3, 4}
    };

    @Override
    public float evaluateBoard(Board board, int treeDepth, Player asWho) {
        int selfPieces = 0, otherPieces = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Player owner = board.getBoardPiece(x, y).getOwner();
                if (owner == asWho) {
                    selfPieces += PIECE_WEIGHTS[x][y];
                } else if (owner != null) {
                    otherPieces += PIECE_WEIGHTS[x][y];
                }
            }
        }

        float value = 0;
        if (selfPieces + otherPieces != 0) {
            value = (float) (selfPieces - otherPieces) / (selfPieces + otherPieces);
        }

        selfPieces += board.getValidMoves(asWho).size();
        otherPieces += board.getValidMoves(board.getGameManager().getOtherPlayer(asWho)).size();

        if (selfPieces + otherPieces != 0) {
            value += (float) (selfPieces - otherPieces) / (selfPieces + otherPieces);
        }

        return value;
    }
}
