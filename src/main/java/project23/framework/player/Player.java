package project23.framework.player;

import project23.framework.board.Board;

/**
 * This abstract class stores a player, including the Board.
 */
public abstract class Player {

    protected final Board board;
    protected final int id;
    protected String name;

    /**
     * constructor
     *
     * @param board the Board.
     * @param name  the player-name.
     */
    public Player(Board board, int id, String name) {
        this.name = name;
        this.id = id;
        this.board = board;
    }

    /**
     * constructor without a name
     *
     * @param board the Board.
     */
    public Player(Board board, int id) {
        this(board, id, "UNNAMED");
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * @return Whether or not to show valid moves on the screen.
     */
    public abstract boolean isShowValidMoves();

    /**
     * getter for the board
     *
     * @return Board board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return The player ID
     */
    public int getID() {
        return id;
    }

    /**
     * @return Name of this player
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for player name
     *
     * @param name The name of this player
     */
    public void setName(String name) {
        this.name = name;
    }
}
