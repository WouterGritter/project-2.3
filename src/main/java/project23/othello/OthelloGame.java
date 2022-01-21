package project23.othello;

import javafx.scene.paint.Color;
import project23.framework.ConfigData;
import project23.framework.Game;
import project23.framework.GameManager;
import project23.framework.GameType;
import project23.framework.board.Board;
import project23.framework.player.MinimaxAIPlayer;
import project23.framework.player.Player;
import project23.othello.board.OthelloBoard;
import project23.othello.player.OthelloBoardEvaluator;
import project23.util.Logger;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OthelloGame extends Game {

    private final String[] boardPieceNames = {"Black", "White"};
    private final List<URL> boardPieceIcons = Arrays.asList(getClass().getResource(
            "/images/boardPieces/othello_black.png"), getClass().getResource("/images/boardPieces/othello_white.png"));
    private final boolean showPiecesCount = true;
    private final Color colors = Color.rgb(0, 153, 0);

    @Override
    public BiFunction<Board, Integer, Player> createLocalPlayerFactory() {
        Logger.info("Overriding local player factory to an AI player for the research experiments.");

        // TODO: Return our newly implemented AI player.
        return (board, id) -> new MinimaxAIPlayer(
                board,
                id,
                "Research resit AI",
                MinimaxAIPlayer.AIDifficulty.EASY,
                new OthelloBoardEvaluator(),
                6
        );
    }

    @Override
    public BiFunction<Board, Integer, Player> createAIPlayerFactory() {
        return (board, id) -> new MinimaxAIPlayer(
                board,
                id,
                Game.AI_NAME,
                ConfigData.getInstance().getAIDifficulty(),
                new OthelloBoardEvaluator(),
                6
        );
    }

    @Override
    public Function<GameManager, Board> createBoardFactory() {
        return OthelloBoard::new;
    }

    /**
     * @return Color
     */
    @Override
    public Color getBoardBackgroundColor() {
        return colors;
    }

    /**
     * returns a list of icons for the boardpiece
     *
     * @return List<URL>
     */
    @Override
    public List<URL> getBoardPieceIcons() {
        return boardPieceIcons;
    }

    /**
     * names of the boardpieces.
     *
     * @return String[] boardpiecenames
     */
    @Override
    public String[] getBoardPieceNames() {
        return boardPieceNames;
    }

    /**
     * framework.GameType
     *
     * @return GameType.OTHELLO
     */
    @Override
    public GameType getGameType() {
        return GameType.OTHELLO;
    }

    /**
     * if it should show the pieces count on the GUI or not.
     *
     * @return boolean
     */
    @Override
    public boolean showPiecesCount() {
        return showPiecesCount;
    }
}
