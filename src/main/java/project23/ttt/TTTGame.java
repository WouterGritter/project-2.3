package project23.ttt;

import javafx.scene.paint.Color;
import project23.framework.ConfigData;
import project23.framework.Game;
import project23.framework.GameManager;
import project23.framework.GameType;
import project23.framework.board.Board;
import project23.framework.player.Player;
import project23.ttt.board.TTTBoard;
import project23.ttt.player.TTTMinimaxAIPlayer;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TTTGame extends Game {

    private final String[] boardPieceNames = {"Noughts (O)", "Crosses (X)"};
    private final List<URL> boardPieceIcons = Arrays.asList(
            getClass().getResource("/images/boardPieces/ttt_o.png"),
            getClass().getResource("/images/boardPieces/ttt_x.png"));
    private final boolean showPiecesCount = false;
    private final Color colors = Color.rgb(245, 245, 245);

    @Override
    public BiFunction<Board, Integer, Player> createAIPlayerFactory() {
        return (board, id) -> new TTTMinimaxAIPlayer(
                board,
                id,
                Game.AI_NAME,
                ConfigData.getInstance().getAIDifficulty());
    }

    @Override
    public Function<GameManager, Board> createBoardFactory() {
        return TTTBoard::new;
    }

    @Override
    public Color getBoardBackgroundColor() {
        return colors;
    }

    @Override
    public List<URL> getBoardPieceIcons() {
        return boardPieceIcons;
    }

    @Override
    public String[] getBoardPieceNames() {
        return boardPieceNames;
    }

    @Override
    public GameType getGameType() {
        return GameType.TIC_TAC_TOE;
    }

    @Override
    public boolean showPiecesCount() {
        return showPiecesCount;
    }
}
