package project23.ttt.board;

import project23.framework.GameManager;
import project23.framework.GameType;
import project23.framework.board.Board;
import project23.framework.board.BoardPiece;
import project23.framework.player.Player;

import java.util.ArrayList;
import java.util.List;

public class TTTBoard extends Board {

    public TTTBoard(GameManager gameManager) {
        super(gameManager, 3, 3);
    }

    @Override
    public int getMinPlayers() {
        return 2;
    }

    @Override
    public int getMaxPlayers() {
        return 2;
    }

    @Override
    public boolean isShowValidMoves() {
        return false;
    }

    @Override
    public List<BoardPiece> getValidMoves(Player asWho) {
        List<BoardPiece> result = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BoardPiece boardPiece = getBoardPiece(x, y);
                if (!boardPiece.hasOwner()) {
                    // This board piece isn't assigned to any player, so this is a valid move.
                    result.add(boardPiece);
                }
            }
        }

        return result;
    }

    @Override
    public void _executeMove(Player asWho, BoardPiece piece) {
        piece.setOwner(asWho);
    }

    @Override
    public boolean calculateIsGameOver() {
        return getValidMoves().isEmpty() || calculateWinner() != null;
    }

    @Override
    public Player calculateWinner() {
        Player player;

        // Check horizontal lines
        for (int y = 0; y < height; y++) {
            player = getPlayerOwningLine(0, y, 1, 0);
            if (player != null) return player;
        }

        // Check vertical lines
        for (int x = 0; x < width; x++) {
            player = getPlayerOwningLine(x, 0, 0, 1);
            if (player != null) return player;
        }

        // Check diagonals
        player = getPlayerOwningLine(0, 0, 1, 1);
        if (player != null) return player;

        player = getPlayerOwningLine(width - 1, 0, -1, 1);
        if (player != null) return player;

        return null;
    }

    @Override
    public void prepareBoard(Player startPlayer) { /* Start with an empty board */ }

    @Override
    public GameType getGameType() {
        return GameType.TIC_TAC_TOE;
    }

    private Player getPlayerOwningLine(int sx, int sy, int dx, int dy) {
        if (dx == 0 && dy == 0) {
            throw new IllegalArgumentException("dx and dy cannot both be 0.");
        }

        int lineSize;
        if (dx == 0) {
            // No movement along x-axis, so checking vertical lines (from 0 - height)
            lineSize = height;
        } else if (dy == 0) {
            // No movement along y-axis, so checking horizontal lines (from 0 - width)
            lineSize = width;
        } else {
            // Movement along both axes, so checking diagonal lines (from 0 - min(width, height))
            lineSize = Math.min(width, height);
        }

        Player player = null;
        for (int i = 0; i < lineSize; i++) {
            int x = sx + i * dx;
            int y = sy + i * dy;

            BoardPiece piece = getBoardPiece(x, y);
            if (piece.getOwner() == null) {
                // One or more lines isn't owned by a player, so there is no player who owns the whole line.
                return null;
            }

            if (player == null) {
                // This is the first piece that we're checking!
                player = piece.getOwner();
            } else if (piece.getOwner() != player) {
                // This piece owner isn't the same owner as all the other pieces on this line, so there is no player who owns the whole line.
                return null;
            }
        }

        // We made it through the whole for-loop while not returning null, so 'player' owns the whole line.
        return player;
    }
}
