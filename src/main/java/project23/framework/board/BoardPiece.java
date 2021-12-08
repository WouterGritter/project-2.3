package project23.framework.board;

import project23.framework.player.Player;

import java.util.Objects;

public class BoardPiece implements Cloneable {

    private final int x, y;
    private Player owner;

    /**
     * Constructs a BoardPiece with an owner.
     *
     * @param x     The X-coordinate of this piece.
     * @param y     The X-coordinate of this piece.
     * @param owner The current owner of this piece.
     */
    public BoardPiece(int x, int y, Player owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;
    }

    /**
     * Constructs a BoardPiece without an owner.
     *
     * @param x The X-coordinate of this piece.
     * @param y The X-coordinate of this piece.
     */
    public BoardPiece(int x, int y) {
        this(x, y, null);
    }

    // Auto-generated equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardPiece that = (BoardPiece) o;
        return x == that.x &&
                y == that.y;
    }

    // Auto-generated hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public BoardPiece clone() throws CloneNotSupportedException {
        return (BoardPiece) super.clone();
    }

    @Override
    public String toString() {
        return "BoardPiece{" +
                "x=" + x +
                ", y=" + y +
                ", owner=" + owner +
                '}';
    }

    /**
     * @return The X-coordinate of this piece.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The Y-coordinate of this piece.
     */
    public int getY() {
        return y;
    }

    /**
     * @return The current owner or <code>null</code> if there is no owner.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * @return Whether this piece has an owner or not.
     */
    public boolean hasOwner() {
        return owner != null;
    }

    /**
     * @param owner The player to change the owner to.
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Clears the owner of this piece. Equivalent to <code>boardPiece.setOwner(null)</code>
     */
    public void clearOwner() {
        this.owner = null;
    }
}
