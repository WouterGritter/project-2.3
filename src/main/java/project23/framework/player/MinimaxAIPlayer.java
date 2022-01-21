package project23.framework.player;

import project23.framework.ConfigData;
import project23.framework.ConnectedGameManager;
import project23.framework.board.Board;
import project23.framework.board.BoardObserver;
import project23.framework.board.BoardPiece;
import project23.util.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MinimaxAIPlayer extends AIPlayer implements BoardObserver {

    private final AIDifficulty difficulty;
    private final BoardEvaluator boardEvaluator;
    private final int startDepth;

    private final Object minimaxSessionLock = new Object();
    private UUID minimaxSession;

    private final Object bestMoveLock = new Object();
    private BoardPiece bestMove;
    private float bestMoveValue;

    private final AtomicBoolean anyEndedInNonGameOver = new AtomicBoolean();
    private final AtomicInteger highestDepth = new AtomicInteger();
    private final Set<UUID> runningThreads = new HashSet<>();

    public MinimaxAIPlayer(Board board, int id, String name, AIDifficulty difficulty, BoardEvaluator boardEvaluator, int startDepth) {
        super(board, id, name);

        this.difficulty = difficulty;
        this.boardEvaluator = boardEvaluator;
        this.startDepth = startDepth;

        board.registerObserver(this);
    }

    /**
     * Only show valid moves when this AI player is part of a ConnectedGameManager
     */
    @Override
    public boolean isShowValidMoves() {
        return (board.getGameManager() instanceof ConnectedGameManager);
    }

    /**
     * Request a move from the AI player
     */
    @Override
    public void requestMove() {
        List<BoardPiece> validMoves = board.getValidMoves(this);
        if (validMoves.size() == 0) {
            board.makeMove(this, null);
            return;
        } else if (validMoves.size() == 1) {
            board.makeMove(this, validMoves.get(0));
            return;
        }

        switch (difficulty) {
            case EASY:
                executeRandomMove();
                break;
            case MEDIUM:
                if (Math.random() > 0.5) {
                    executeRandomMove();
                } else {
                    executeMinimaxMove();
                }
                break;
            case HARD:
                executeMinimaxMove();
                break;
            default:
                throw new IllegalStateException("Invalid AI difficulty '" + difficulty + "'!");
        }
    }

    /**
     * Executes a random move to the board
     */
    public void executeRandomMove() {
        List<BoardPiece> validMoves = board.getValidMoves(this);

        BoardPiece randomMove = null;
        if (!validMoves.isEmpty()) {
            randomMove = validMoves.get((int) (Math.random() * validMoves.size()));
        }

        board.makeMove(this, randomMove);
    }

    /**
     * Check all valid moves using minimax, and execute it using {@link Board#makeMove(Player, BoardPiece)}
     */
    public void executeMinimaxMove() {
        UUID session = UUID.randomUUID();
        synchronized (minimaxSessionLock) {
            minimaxSession = session;
        }

        synchronized (bestMoveLock) {
            bestMove = null;
            bestMoveValue = Float.NEGATIVE_INFINITY;
        }

        synchronized (highestDepth) {
            highestDepth.set(0);
        }

        performAsyncMinimax(session, startDepth);

        Thread watchdogThread = new Thread(() -> {
            try {
                Thread.sleep(ConfigData.getInstance().getMinimaxThinkingTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onMinimaxDone(session);
        });
        watchdogThread.setDaemon(true);
        watchdogThread.start();
    }

    /**
     * Picks the best move out of the minimax tree.
     * Chooses a random valid move if minimax could not come up with a best move.
     *
     * @param session current threading session
     */
    private void onMinimaxDone(UUID session) {
        synchronized (minimaxSessionLock) {
            if (minimaxSession != session) {
                return;
            }

            minimaxSession = null;
        }

        BoardPiece bestMove;
        float bestMoveValue;
        synchronized (bestMoveLock) {
            bestMove = this.bestMove;
            bestMoveValue = this.bestMoveValue;
        }

        List<BoardPiece> validMoves = board.getValidMoves(this);
        if (!validMoves.isEmpty()) {
            if (bestMove == null) {
                Logger.error(
                        "Minimax couldn't come up with a best move, but there are more than 0 valid moves! Sending a random move..");
                bestMove = validMoves.get((int) (Math.random() * validMoves.size()));
            }

            if (!validMoves.contains(bestMove)) {
                Logger.error("Minimax came up with a best move, but it isn't a valid move! Sending a random move..");
                bestMove = validMoves.get((int) (Math.random() * validMoves.size()));
            }
        }

        int highestDepthValue;
        synchronized (highestDepth) {
            highestDepthValue = highestDepth.get();
        }

        boolean anyEndedInNonGameOverValue;
        synchronized (anyEndedInNonGameOver) {
            anyEndedInNonGameOverValue = anyEndedInNonGameOver.get();
        }

        Logger.info("Found best move " + bestMove + " with a value of " + bestMoveValue + " at a depth of " + highestDepthValue + ".");

        StringBuilder certaintyMessage = new StringBuilder();
        if (Math.abs(bestMoveValue) < 0.8f) {
            certaintyMessage.append("I can't tell who will win if the opponent plays perfectly.");
        } else {
            certaintyMessage.append("We are ");
            if (anyEndedInNonGameOverValue) {
                if (Math.abs(bestMoveValue) > 2.0f) {
                    certaintyMessage.append("most likely");
                } else {
                    certaintyMessage.append("probably");
                }
            } else {
                certaintyMessage.append("definitely");
            }

            certaintyMessage.append(" going to ");

            if (bestMoveValue > 0) {
                certaintyMessage.append("win");
            } else if (bestMoveValue == 0) {
                certaintyMessage.append("tie");
            } else {
                certaintyMessage.append("lose");
            }

            certaintyMessage.append(" if the opponent plays perfectly.");
        }

        Logger.info(certaintyMessage.toString());

        board.makeMove(this, bestMove);
    }

    /**
     * Executes minimax algorithm on async threads
     *
     * @param session current thread session
     * @param depth   minimax tree depth
     */
    private void performAsyncMinimax(UUID session, int depth) {
        synchronized (minimaxSessionLock) {
            if (minimaxSession != session) {
                return;
            }
        }

        synchronized (runningThreads) {
            // Just to be sure!
            runningThreads.clear();
        }

        synchronized (anyEndedInNonGameOver) {
            anyEndedInNonGameOver.set(false);
        }

        List<BoardPiece> validMoves = board.getValidMoves(this);

        for (BoardPiece boardPiece : validMoves) {
            int x = boardPiece.getX();
            int y = boardPiece.getY();

            final UUID threadUuid = UUID.randomUUID();
            synchronized (runningThreads) {
                runningThreads.add(threadUuid);
            }

            Thread thread = new Thread(() -> {
                synchronized (minimaxSessionLock) {
                    if (minimaxSession != session) {
                        return;
                    }
                }

                float moveValue = miniMax(session, board, depth, this, x, y);

                synchronized (bestMoveLock) {
                    if (moveValue > bestMoveValue) {
                        bestMove = boardPiece;
                        bestMoveValue = moveValue;
                    }
                }

                boolean isEmpty;
                synchronized (runningThreads) {
                    runningThreads.remove(threadUuid);
                    isEmpty = runningThreads.isEmpty();
                }

                if (isEmpty) {
                    // We're DONE!
                    synchronized (minimaxSessionLock) {
                        if (minimaxSession != session) {
                            return;
                        }
                    }

                    synchronized (highestDepth) {
                        if (depth > highestDepth.get()) {
                            highestDepth.set(depth);
                        }
                    }

                    boolean anyEndedInNonGameOverValue;
                    synchronized (anyEndedInNonGameOver) {
                        anyEndedInNonGameOverValue = anyEndedInNonGameOver.get();
                    }

                    if (anyEndedInNonGameOverValue) {
                        // We can still go higher!
                        Logger.info("Done with minimax at a depth of " + depth + ", but we still have time. Going deeper!");
                        performAsyncMinimax(session, depth + 1);
                    } else {
                        Logger.info("All minimax ends ended in a game-over. Aborting early at a depth of " + depth + "!");
                        onMinimaxDone(session);
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * returns the highest value move when the end is reached because either a lack of valid moves,
     * the end of a node or the maximum search depth is reached.
     *
     * @param _board a playing board.
     * @param depth  depth of the nodes to look into.
     * @return int value of the board.
     */
    private float miniMax(UUID session, Board _board, int depth, Player player, int moveX, int moveY) {
        synchronized (minimaxSessionLock) {
            if (minimaxSession != session) {
                return 0;
            }
        }

        // Clone the board
        Board board;
        try {
            board = _board.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return 0;
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
            // end reached.
            if (!gameOver) {
                synchronized (anyEndedInNonGameOver) {
                    anyEndedInNonGameOver.set(true);
                }
            }

            return boardEvaluator.evaluateBoard(board, depth, this);
        }

        Player playerToMove = board.getCurrentPlayer();
        boolean lookForMax = playerToMove == this;
        float extremeVal = lookForMax ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;

        List<BoardPiece> validMoves = board.getValidMoves(playerToMove);
        if (validMoves.isEmpty()) { /* && board.canPass(playerToMove) */
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

            float val = miniMax(session, board, depth - 1, playerToMove, x, y);

            if (lookForMax) {
                if (val > extremeVal) extremeVal = val;
            } else {
                if (val < extremeVal) extremeVal = val;
            }
        }

        return extremeVal;
    }

    public AIDifficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public void onPlayerMoved(Player who, BoardPiece where) {
    }

    @Override
    public void onPlayerMoveFinalized(Player previous, Player current) {
    }

    @Override
    public void onGameStart(Player startingPlayer) {
    }

    @Override
    public void onPlayerWon(Player who) {
        synchronized (minimaxSessionLock) {
            minimaxSession = null;
        }
    }

    public enum AIDifficulty {
        EASY,
        MEDIUM,
        HARD;

        /**
         * @return The display-name of this difficulty.
         */
        public String displayName() {
            return String.valueOf(name().charAt(0)).toUpperCase() + name().substring(1).toLowerCase();
        }

        /**
         * @param name The display-name to lookup
         * @return The {@link AIDifficulty} which has the specified (display) name
         */
        public static AIDifficulty fromDisplayName(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }

            return null;
        }
    }
}
