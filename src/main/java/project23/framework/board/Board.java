package project23.framework.board;

import project23.framework.BoardState;
import project23.framework.ConfigData;
import project23.framework.GameManager;
import project23.framework.GameType;
import project23.framework.player.MoveRequestable;
import project23.framework.player.Player;

import java.util.*;

public abstract class Board implements Cloneable {

    protected final GameManager gameManager;
    protected final int width, height;
    protected BoardPiece[] pieces;

    private Set<BoardObserver> observers = new HashSet<>();

    private BoardState boardState = BoardState.WAITING;
    private int currentPlayerId;
    private Player winner;

    private boolean disableRequestMove = false;

    /**
     * Constructs a new Board
     *
     * @param gameManager The GameManager which owns this board.
     * @param width       The width (in tiles) of this board.
     * @param height      The height (in tiles) of this board.
     */
    public Board(GameManager gameManager, int width, int height) {
        this.gameManager = gameManager;
        this.width = width;
        this.height = height;

        this.pieces = new BoardPiece[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pieces[x + y * width] = new BoardPiece(x, y);
            }
        }
    }

    /**
     * @return The minimum amount of players required to start a game on this board.
     */
    public abstract int getMinPlayers();

    /**
     * @return The maximum amount of players required to start a game on this board.
     */
    public abstract int getMaxPlayers();

    /**
     * @return Whether this board type should show valid moves
     */
    public abstract boolean isShowValidMoves();

    /**
     * An implementation-specific getter for calculating all valid moves.
     *
     * @return All valid moves.
     */
    public abstract List<BoardPiece> getValidMoves(Player asWho);

    /**
     * An implementation-specific method for executing a move on the board.
     * No argument-checking needs to be done in this method, because it is protected and only gets called internally from the super-class Board.
     *
     * @param asWho The player which executed the move.
     * @param piece The piece the player wants to affect.
     */
    public abstract void _executeMove(Player asWho, BoardPiece piece);

    /**
     * An implementation-specific method for calculating if the game is currently over.
     *
     * @return Whether the game is currently over or not.
     */
    public abstract boolean calculateIsGameOver();

    /**
     * An implementation-specific method for calculating the winner of the game.
     *
     * @return The winner of the game, or <code>null</code> if the game ended in a draw.
     */
    public abstract Player calculateWinner();

    /**
     * Prepares the board.
     *
     * @param startPlayer The player who will start.
     */
    public abstract void prepareBoard(Player startPlayer);

    public abstract GameType getGameType();

    public List<BoardPiece> getValidMoves() {
        return getValidMoves(getCurrentPlayer());
    }

    /**
     * this method requests a playermove from the board if all players have been initialized.
     * <p>
     * SHOULDN'T BE CALLED EXTERNALLY! Use GameManager#start!
     */
    public void _start(Player startingPlayer) {
        if (boardState != BoardState.WAITING) {
            throw new IllegalStateException("The game cannot start in this state! (Current state: " + boardState + ")");
        }

        if (gameManager.getNumPlayers() < getMinPlayers() || gameManager.getNumPlayers() > getMaxPlayers()) {
            throw new IllegalStateException("The number of players must be between " + getMinPlayers() + " and " + getMaxPlayers() + ", and is currently " + gameManager.getNumPlayers() + "!");
        }

        // Reset pieces
        for (BoardPiece piece : pieces) {
            piece.clearOwner();
        }

        if (startingPlayer == null) {
            startingPlayer = gameManager.getPlayer((int) (Math.random() * gameManager.getNumPlayers()));
        }

        currentPlayerId = startingPlayer.getID();

        prepareBoard(startingPlayer);

        boardState = BoardState.PLAYING;

        for (BoardObserver o : observers) {
            o.onGameStart(startingPlayer);
        }

        if (!ConfigData.getInstance().getCurrentGame().isOnline()) {
            requestPlayerMove();
        }
    }

    /**
     * resets the gameboard to be able to start a new match after clearing.
     */
    public void reset() {
        if (boardState == BoardState.PLAYING) {
            gameManager.forfeit();
        }

        // Reset pieces
        for (BoardPiece piece : pieces) {
            piece.clearOwner();
        }

        boardState = BoardState.WAITING;

        // Doesn't really matter, because this will be overridden when the start method gets called..
        currentPlayerId = 0;

        winner = null;
    }

    /**
     * Executes a RAW MOVE on the board. This does NOT finalize the raw turn. The caller of this method
     * should finalize their own turn by executing {@link Board#finalizeRawMove()}.
     *
     * @param player The player which executed the move.
     * @param piece  The piece the player wants to affect.
     */
    public void makeRawMove(Player player, BoardPiece piece) {
        if (getCurrentPlayer() != player) {
            throw new IllegalArgumentException("It's not that player's turn yet!");
        }

        if (!getValidMoves(player).contains(piece)) {
            throw new IllegalArgumentException("That is not a valid move!");
        }

        // All good, now actually execute the move on the board!
        _executeMove(player, piece);

        // Make sure all observers know of this state-change!
        observers.forEach(o -> o.onPlayerMoved(player, piece));
    }

    /**
     * See {@link Board#makeRawMove(Player, BoardPiece)}
     */
    public void makeRawMove(Player player, int x, int y) {
        makeRawMove(player, getBoardPiece(x, y));
    }

    /**
     * Finalizes the current raw move by:
     * 1. Updating the current player
     * 2. Calculating whether the game is over or not
     * 3. Requesting a move from the new current player if the game is not over
     */
    public void finalizeRawMove() {
        if (boardState != BoardState.PLAYING) {
            return;
        }

        Player previousPlayer = getCurrentPlayer();

        // Update currentPlayerId because it's now the next player's turn.
        currentPlayerId++;
        if (currentPlayerId >= gameManager.getPlayers().size()) {
            currentPlayerId = 0;
        }

        // Make sure all observers know of this state-change!
        observers.forEach(o -> o.onPlayerMoveFinalized(previousPlayer, getCurrentPlayer()));

        // See if the game is over after this move
        if (calculateIsGameOver()) {
            // The game is over! Calculate a winner and set the flag!

            Player winner = calculateWinner();
            forceWin(winner);
        }

        if (boardState == BoardState.PLAYING) {
            // The game is not yet over. Request the next move from the new current player.
            requestPlayerMove();
        }
    }

    /**
     * This method attempts to execute a move from a given player, and immediately finalizes the move.
     * See {@link Board#makeRawMove(Player, BoardPiece)}
     * See {@link Board#finalizeRawMove()}
     *
     * @param player The player which executed the move.
     * @param piece  The piece the player wants to affect.
     */
    public void makeMove(Player player, BoardPiece piece) {
        // Make the raw turn, and finalize it immediately!
        if (piece != null) {
            makeRawMove(player, piece);
        }
        finalizeRawMove();
    }

    /**
     * See {@link Board#makeMove(Player, BoardPiece)}
     */
    public void makeMove(Player player, int x, int y) {
        makeMove(player, getBoardPiece(x, y));
    }

    /**
     * Calls {@link MoveRequestable#requestMove()} on the current player, if the current player implements {@link MoveRequestable}
     */
    public void requestPlayerMove() {
        if (disableRequestMove) {
            return;
        }

        Player currentPlayer = gameManager.getPlayer(currentPlayerId);
        if (currentPlayer instanceof MoveRequestable) {
            ((MoveRequestable) currentPlayer).requestMove();
        }
    }

    /**
     * Forces a win for a specific player.
     *
     * @param winner The player who should be considered the winner, or <code>null</code> to indicate a draw.
     */
    public void forceWin(Player winner) {

        if (this.boardState != BoardState.PLAYING) {
            return;
        }

        this.boardState = BoardState.GAME_OVER;
        this.winner = winner;

        new ArrayList<>(observers).forEach(o -> o.onPlayerWon(winner));
    }

    /**
     * makes a clone of this board.
     *
     * @return Board clone of the board.
     * @throws CloneNotSupportedException
     */
    @Override
    public Board clone() throws CloneNotSupportedException {
        Board cloned = (Board) super.clone();

        // Deep-clone pieces
        cloned.pieces = new BoardPiece[this.pieces.length];
        for (int i = 0; i < this.pieces.length; i++) {
            cloned.pieces[i] = this.pieces[i].clone();
        }

        // Reset observers
        cloned.observers = new HashSet<>();

        return cloned;
    }

    /**
     * Gets the board piece on specific coordinates.
     *
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @return The board piece on the specified coordinates.
     */
    public BoardPiece getBoardPiece(int x, int y) {
        return pieces[x + y * width];
    }

    /**
     * Gets the board piece neighbors (including diagonals) of specific coordinates, excluding the piece on the coordinates itself.
     *
     * @param centerX The X-coordinate.
     * @param centerY The Y-coordinate.
     * @return The neighbors of (centerX, centerY) including diagonals, excluding itself.
     */
    public List<BoardPiece> getBoardPieceNeighbors(int centerX, int centerY) {
        List<BoardPiece> result = new ArrayList<>();

        for (int y = centerY - 1; y <= centerY + 1; y++) {
            if (y < 0 || y >= height) continue; // Make sure we don't get out of bounds

            for (int x = centerX - 1; x <= centerX + 1; x++) {
                if (x < 0 || x >= width) continue; // Make sure we don't get out of bounds
                if (x == 0 && y == 0) continue;

                result.add(getBoardPiece(x, y));
            }
        }

        return result;
    }

    /**
     * @return The GameManager which owns this board.
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * @return The width of the board in tiles
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return The height of the board in tiles
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return The player who is currently expected to make a move.
     */
    public Player getCurrentPlayer() {
        if (currentPlayerId < 0 || currentPlayerId >= gameManager.getNumPlayers()) {
            return null;
        }

        return gameManager.getPlayer(currentPlayerId);
    }

    /**
     * @param currentPlayer Sets the current player who is expected to make a move.
     */
    public void setCurrentPlayer(Player currentPlayer) {
        currentPlayerId = currentPlayer.getID();
    }

    /**
     * returns the state of the board. WAITING, PLAYING or GAME_OVER
     * from the enum BoardState.
     *
     * @return boardState
     */
    public BoardState getBoardState() {
        return boardState;
    }

    /**
     * @return The winner of the game, or <code>null</code> if the game ended in a draw.
     * @throws IllegalStateException when the game is not over yet.
     */
    public Player getWinner() {
        if (boardState != BoardState.GAME_OVER) {
            throw new IllegalStateException("The game isn't over yet!");
        }

        return winner;
    }

    /**
     * @param piece The piece to check
     * @return Whether the specified piece is a valid move or not.
     */
    public boolean isValidMove(Player asWho, BoardPiece piece) {
        return getValidMoves(asWho).contains(piece);
    }

    /**
     * @param boardPiece the boardpiece to check
     * @see z#isValidMove(Player asWho, int x, int y)
     */
    public boolean isValidMove(BoardPiece boardPiece) {
        return isValidMove(getCurrentPlayer(), boardPiece);
    }

    /**
     * @param x The X-coordinate of the piece to check
     * @param y The Y-coordinate of the piece to check
     * @return Whether the specified piece is a valid move or not.
     */
    public boolean isValidMove(Player asWho, int x, int y) {
        return isValidMove(asWho, getBoardPiece(x, y));
    }

    /**
     * @param x The X-coordinate of the piece to check
     * @param y The Y-coordinate of the piece to check
     * @see #isValidMove(Player asWho, int x, int y)
     */
    public boolean isValidMove(int x, int y) {
        return isValidMove(getCurrentPlayer(), x, y);
    }

    /**
     * Registers a BoardObserver.
     *
     * @param observer The BoardObserver to register.
     */
    public void registerObserver(BoardObserver observer) {
        observers.add(observer);
    }

    /**
     * Unregisters a BoardObserver.
     *
     * @param observer The BoardObserver to unregister.
     */
    public void unregisterObserver(BoardObserver observer) {
        observers.remove(observer);
    }

    /**
     * getter for the boolean disableRequestMove, this is for previewing move,
     * not actually executing a move and sending it if this boolean is true.
     *
     * @return boolean, getter for disableRequestMove
     */
    public boolean isDisableRequestMove() {
        return disableRequestMove;
    }

    /**
     * @param disableRequestMove, setter for disableRequestMove
     */
    public void setDisableRequestMove(boolean disableRequestMove) {
        this.disableRequestMove = disableRequestMove;
    }

    /**
     * counts the pieces on the board for the players.
     *
     * @return Map<Player, Integer> , key is player, value is the amount of pieces for that player.
     */
    public Map<Player, Integer> piecesCount() {
        Map<Player, Integer> map = new HashMap<>();
        gameManager.getPlayers().forEach(player -> map.put(player, 0));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BoardPiece tempBoardPiece = getBoardPiece(x, y);
                if (tempBoardPiece.hasOwner()) {
                    int count = map.getOrDefault(tempBoardPiece.getOwner(), 0);
                    count++;
                    map.put(tempBoardPiece.getOwner(), count);
                }
            }
        }

        return map;
    }
}
