package project23.framework;

import project23.framework.board.Board;
import project23.framework.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class manages a game. Including project23.connection, board and players.
 */
public class GameManager {

    protected final List<BiFunction<Board, Integer, ? extends Player>> playerSuppliers = new ArrayList<>();

    protected Board board;
    protected final List<Player> players = new ArrayList<>();

    private boolean isInitialized = false;

    /**
     * constructor, initializes project23.connection, board and players.
     *
     * @param boardSupplier The board supplier
     */
    public GameManager(Function<GameManager, ? extends Board> boardSupplier,
                       BiFunction<Board, Integer, ? extends Player>... playerSuppliers) {
        this.board = boardSupplier.apply(this);
        this.playerSuppliers.addAll(Arrays.asList(playerSuppliers));
    }

    /**
     * Initializes the GameManager
     */
    public void initialize() {
        if (isInitialized) {
            throw new IllegalStateException("The GameManager is already initialized! Please reset it first.");
        }

        reset();

        players.clear();
        for (int i = 0; i < playerSuppliers.size(); i++) {
            Player player = playerSuppliers.get(i).apply(board, i);
            players.add(player);
        }

        isInitialized = true;
    }

    public void requestStart() {
        _start(null);
    }

    public void forfeit() {
        board.forceWin(null);
    }

    /**
     * Destroy the internal objects, the GameManager will never be able to be used after this!
     */
    public void destroy() {
    }

    protected void _start(Player startingPlayer) {
        if (!isInitialized) {
            initialize();
        }

        board._start(startingPlayer);
    }

    /**
     * getter for board.
     *
     * @return Board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter for the array players
     *
     * @return List<Player>
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Getter for the number of players
     *
     * @return The number of players
     */
    public int getNumPlayers() {
        return players.size();
    }

    /**
     * getter for an individual player from the array players.
     *
     * @param id the index of the player in the arraylist players.
     * @return Player player.
     */
    public Player getPlayer(int id) {
        if (id < 0 || id >= players.size()) {
            throw new IllegalArgumentException("Invalid player ID!");
        }

        return players.get(id);
    }

    public Player getPlayer(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Get the other player
     *
     * @param notThis not this player, but the other one
     * @return the other one
     */
    public Player getOtherPlayer(Player notThis) {
        if (players.size() > 2) {
            throw new IllegalStateException(
                    "There are more than 2 players, please use GameManager#getOtherPlayers instead!");
        }

        for (Player other : players) {
            if (other != notThis) {
                return other;
            }
        }

        return null;
    }

    public List<Player> getOtherPlayers(Player notThis) {
        List<Player> result = new ArrayList<>(this.players);
        result.removeIf(other -> other == notThis);

        return result;
    }

    /**
     * Resets the board and players
     */
    public void reset() {
        board.reset();
        players.clear();
        isInitialized = false;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public GameType getGameType() {
        return board.getGameType();
    }
}
