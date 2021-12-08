package project23.framework.player;

import project23.framework.board.Board;
import project23.framework.board.BoardPiece;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A test class (intended to use in development) which implements a {@link LocalPlayer} through the command line.
 */
@Deprecated
public class ConsoleLocalPlayer extends LocalPlayer implements MoveRequestable {

    public ConsoleLocalPlayer(Board board, int id, String name) {
        super(board, id, name);
    }

    public ConsoleLocalPlayer(Board board, int id) {
        super(board, id);
    }

    @Override
    public void requestMove() {
        System.out.println("Please enter the coordinates of the move you want to make.");
        System.out.println("1,1 is top-left, " + board.getWidth() + "," + board.getHeight() + " is bottom-right.");

        List<BoardPiece> validMoves = board.getValidMoves();
        BoardPiece exampleMove = validMoves.get((int) (Math.random() * validMoves.size()));
        String exampleMoveStr = (exampleMove.getX() + 1) + "," + (exampleMove.getY() + 1);
        System.out.println("An example of a valid move: " + exampleMoveStr);

        int x = -1;
        int y = -1;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (x == -1 || y == -1) {
            String line;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            int _x, _y;
            try {
                String[] parts = line.split(",");

                _x = Integer.parseInt(parts[0]) - 1;
                _y = Integer.parseInt(parts[1]) - 1;
            } catch (Exception ignored) {
                System.out.println("Invalid syntax! Example: " + exampleMoveStr);
                continue;
            }

            if (_x < 0 || _x >= board.getWidth() || _y < 0 || _y >= board.getHeight()) {
                System.out.println("Coordinates out of bounds! Please make sure the coordinates are within 1,1 and " + board.getWidth() + "," + board.getHeight());
                continue;
            }

            if (!validMoves.contains(board.getBoardPiece(_x, _y))) {
                System.out.println("That is an invalid move! An example of a valid move is " + exampleMoveStr);
                continue;
            }

            x = _x;
            y = _y;
        }

        executeMove(x, y);
    }
}
