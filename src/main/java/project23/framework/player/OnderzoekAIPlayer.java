package project23.framework.player;

import project23.framework.ConfigData;
import project23.framework.board.Board;
import project23.framework.board.BoardPiece;
import project23.util.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class OnderzoekAIPlayer extends AIPlayer {

    private final BoardEvaluator boardEvaluator;
    private final int startDepth;

    public OnderzoekAIPlayer(Board board, int id, String name, BoardEvaluator boardEvaluator, int startDepth) {
        super(board, id, name);

        this.boardEvaluator = boardEvaluator;
        this.startDepth = startDepth;
    }

    @Override
    public void requestMove() {
        List<BoardPiece> validMoves = board.getValidMoves(this);
        if (validMoves.size() == 0) {
            Logger.info("Skipping");
            board.makeMove(this, null);
            return;
        } else if (validMoves.size() == 1) {
            Logger.info("Executing only possible move");
            board.makeMove(this, validMoves.get(0));
            return;
        }

        final AtomicReference<BoardPiece> bestMoveReference = new AtomicReference<>(null);

        Thread minimaxThread = new Thread(() -> {
            for (int depth = startDepth; ; depth++) {
                Logger.info("Digging at depth " + depth);

                BoardPiece bestMove = miniMax(depth);
                Logger.info("Done digging at depth " + depth);

                synchronized (bestMoveReference) {
                    bestMoveReference.set(bestMove);
                }
            }
        }, "minimax-digger");
        minimaxThread.start();

        new Thread(() -> {
            try {
                Thread.sleep(ConfigData.getInstance().getMinimaxThinkingTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            minimaxThread.stop();

            BoardPiece bestMove;
            synchronized (bestMoveReference) {
                bestMove = bestMoveReference.get();
            }

            List<BoardPiece> availableMoves = board.getValidMoves();
            if(bestMove == null && !availableMoves.isEmpty()) {
                Logger.info("Couldn't finish minimax tree at depth " + startDepth + ". Picking a random move.");
                bestMove = availableMoves.get((int) (Math.random() * availableMoves.size()));
            }else{
                if(!availableMoves.contains(bestMove)) {
                    Logger.info("Best move isn't a valid move!");
                }
            }

            board.makeMove(this, bestMove);
        }, "minimax-supervisor").start();
    }

    private BoardPiece miniMax(int depth) {
        float bestMoveScore = Float.NEGATIVE_INFINITY;
        BoardPiece bestMove = null;

        for (BoardPiece move : board.getValidMoves(this)) {
            float score = miniMax(board, depth, this, move.getX(), move.getY());
            if (score > bestMoveScore) {
                bestMoveScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private float miniMax(Board _board, int depth, Player player, int moveX, int moveY) {
        // Clone the board
        Board board;
        try {
            board = _board.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        board.setDisableRequestMove(true);

        // Execute the move
        if (moveX == -1 && moveY == -1) {
            board.makeMove(player, null);
        } else {
            board.makeMove(player, moveX, moveY);
        }

        boolean gameOver = board.calculateIsGameOver();
        if (depth == 0 || gameOver) {
            return boardEvaluator.evaluateBoard(board, depth, this);
        }

        Player playerToMove = board.getCurrentPlayer();
        boolean lookForMax = playerToMove == this;
        float extremeVal = lookForMax ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;

        List<BoardPiece> validMoves = board.getValidMoves(playerToMove);
        if (validMoves.isEmpty()) {
            validMoves.add(null);
        }

        for (BoardPiece _boardPiece : validMoves) {
            int x, y;
            if (_boardPiece != null) {
                x = _boardPiece.getX();
                y = _boardPiece.getY();
            } else {
                x = y = -1;
            }

            float val = miniMax(board, depth - 1, playerToMove, x, y);

            if (lookForMax) {
                if (val > extremeVal) extremeVal = val;
            } else {
                if (val < extremeVal) extremeVal = val;
            }
        }

        return extremeVal;
    }

    @Override
    public boolean isShowValidMoves() {
        return true;
    }
}
