package project23.othello.player;

import project23.framework.ConfigData;
import project23.framework.board.Board;
import project23.framework.board.BoardObserver;
import project23.framework.board.BoardPiece;
import project23.framework.player.AIPlayer;
import project23.framework.player.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GPUOthelloAI extends AIPlayer implements BoardObserver {

    private static final String PROGRAM_NAME = "gpu-othello";
    private final static int MAX_MEMORY_KB = 16777216;

    private Process p;
    private BufferedReader br;
    private BufferedReader stderr;
    private BufferedWriter bw;

    private BoardPiece opponentsMove = null;

    public GPUOthelloAI(Board board, int id, String name) {
        super(board, id, name);

        board.registerObserver(this);
    }

    private void init(String side) {
        try {
            // String cmd = "ulimit -m " + MAX_MEMORY_KB + " -v " + MAX_MEMORY_KB + ";";
            String cmd = "";
            cmd += "./" + PROGRAM_NAME + " " + side;
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            p = pb.start();

            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

            // Wait for message that program is done initialization.
            br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printStdErr() {
        try {
            while (stderr.ready()) {
                System.out.println(stderr.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BoardPiece calculateMove() {
        printStdErr();

        int millisLeft = ConfigData.getInstance().getMinimaxThinkingTime();

        String line;
        try {
            if (opponentsMove == null) {
                bw.write("-1 -1 " + millisLeft + "\n");
            } else {
                bw.write(opponentsMove.getX() + " " + opponentsMove.getY()
                        + " " + millisLeft + "\n");
            }
            bw.flush();

            while (!br.ready()) {
                printStdErr();

                try {
                    int exitvalue = p.exitValue();
                    // If we got an exit value (versus exception), it means
                    // the program has ended; don't wait for input.
                    break;
                } catch (IllegalThreadStateException e) {
                    // Program still running, don't do anything.
                }
                Thread.yield();
                Thread.sleep(100);
            }
            printStdErr();

            line = br.readLine();
            if (line == null || line.equals("-1 -1")) {
                return null;
            } else {
                String[] parts = line.split(" ");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                return board.getBoardPiece(x, y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void requestMove() {
        BoardPiece move = calculateMove();
        System.out.println("making move " + move);

        board.makeMove(this, move);

        opponentsMove = null;
    }

    @Override
    public void onPlayerWon(Player who) {
    }

    @Override
    public void onGameStart(Player startingPlayer) {
        String side = this.getID() == 0 ? "Black" : "White";

        System.out.println("startingPlayer.getID() = " + startingPlayer.getID());
        System.out.println("side = " + side);

        init(side);
    }

    @Override
    public void onPlayerMoved(Player who, BoardPiece where) {
        if(who != this) {
            opponentsMove = where;
        }
    }

    @Override
    public void onPlayerMoveFinalized(Player previous, Player current) {
    }

    @Override
    public boolean isShowValidMoves() {
        return true;
    }
}
