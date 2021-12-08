package project23.framework;

import javafx.scene.paint.Color;
import project23.framework.board.Board;
import project23.framework.player.LocalPlayer;
import project23.framework.player.Player;
import project23.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Returns relevant Board-objects, Player-objects (and more?)
 */
public abstract class Game {

    public static final String AI_NAME = "Computer";

    private boolean online = false;
    private String helpText;

    public abstract Function<GameManager, Board> createBoardFactory();

    public abstract BiFunction<Board, Integer, Player> createAIPlayerFactory();

    public BiFunction<Board, Integer, Player> createLocalPlayerFactory() {
        return ((board, id) -> new LocalPlayer(board, id, ConfigData.getInstance().getPlayerName()));
    }

    /**
     * creates a GameManager.
     *
     * @return the gameManger
     */
    public GameManager createGameManager() {
        if (online) {
            try {
                ConnectedGameManager cgm = new ConnectedGameManager(
                        createBoardFactory(),
                        ConfigData.getInstance().getServerIP(),
                        ConfigData.getInstance().getServerPort(),
                        createAIPlayerFactory()
                );

                cgm.setSelfName(ConfigData.getInstance().getPlayerName());
                cgm.login();

                return cgm;
            } catch (IOException e) {
                Logger.error("Couldn't connect to server!");
                e.printStackTrace();
            }

            return null;
        } else {
            return new GameManager(
                    createBoardFactory(),
                    createLocalPlayerFactory(),
                    createAIPlayerFactory()
            );
        }
    }

    public abstract Color getBoardBackgroundColor();

    public abstract List<URL> getBoardPieceIcons();

    public abstract String[] getBoardPieceNames();

    public abstract GameType getGameType();

    public abstract boolean showPiecesCount();

    // Aanroepen in GameMenu (lobby of lokale wedstrijd)
    public void setOnline(boolean isOnline) {
        this.online = isOnline;
    }

    public boolean isOnline() {
        return online;
    }

    public String getHelpText() {
        if (helpText == null) {
            loadHelpFile();
        }
        return helpText;
    }

    // Moet aangeroepen worden door child-klassen
    protected void loadHelpFile() {
        StringBuilder helpTextBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(
                "/helpfiles/" + getGameType().serverName + "-help.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                helpTextBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        helpText = helpTextBuilder.toString();
    }
}
