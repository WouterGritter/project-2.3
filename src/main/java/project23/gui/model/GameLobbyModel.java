package project23.gui.model;

import javafx.application.Platform;
import project23.framework.*;
import project23.gui.MainWindow;

import java.util.List;

public class GameLobbyModel extends Model implements ConnectedGameManagerObserver {

    private List<String> currentlyShowingPlayers;

    private ChallengeRequest lastChallengeRequest;
    private boolean isAI;
    private GameModel gameModel;
    private MainWindow mainWindow;
    private ConnectedGameManager cgm;

    public GameLobbyModel(GameModel gameModel, MainWindow mainWindow) {
        this.gameModel = gameModel;
        this.mainWindow = mainWindow;
    }

    public void setAI(boolean AI) {
        isAI = AI;
    }

    /**
     * Requests a refresh of the playerlist. This, in turn, runs #onPlayerListReceive
     */
    public void refreshPlayerList() {
        cgm.getClient().sendGetPlayerlistMessage();
    }

    /**
     * Returns the players in the lobby, and keeps a local copy (needed for {@link #challengePlayer(int)})
     *
     * @return players in lobby
     */
    public List<String> getLobbyPlayers() {
        this.currentlyShowingPlayers = cgm.getLobbyPlayers();
        return currentlyShowingPlayers;
    }

    /**
     * challenges a player, specified by index from the list items in the game lobby
     *
     * @param playerListIndex the selected index
     */
    public void challengePlayer(int playerListIndex) {
        String playername = currentlyShowingPlayers.get(playerListIndex);
        cgm.challengePlayer(playername);
        setInfoMessage("Challenged " + playername);
        updateView();
    }

    /**
     * Starts an online match, nothing to do with challenging
     */
    public void prepareOnlineGame() {
        gameModel.prepareNewGame();
        cgm.requestStart();
    }

    public void logout() {
        cgm.destroy();
    }

    /**
     * Shows any server errors
     *
     * @param errorMessage the error message
     */
    @Override
    public void onServerError(String errorMessage) {
        Platform.runLater(() -> {
            setDialogMessageAndTitle(errorMessage, "Error");
            updateView();
        });
    }

    /**
     * Updates the list of lobby players upon receiving playerlist update
     */
    @Override
    public void onPlayerListReceive() {
        Platform.runLater(this::updateView);
    }

    /**
     * Shows challenge messages, as long as player is not playing
     *
     * @param challengeRequest the challenge request
     */
    @Override
    public void onChallengeRequestReceive(ChallengeRequest challengeRequest) {
        if (cgm.getBoard().getBoardState() != BoardState.PLAYING && challengeRequest.getGameType()
                .equals(cgm.getGameType())) {
            this.lastChallengeRequest = challengeRequest;
            Platform.runLater(this::updateView); // zodat melding wordt weergegeven
        }
    }

    /**
     * Returns the latest challenge request, and then clears the variable so it cannot be requested again
     *
     * @return
     */
    public ChallengeRequest getLastChallengeRequest() {
        ChallengeRequest lastChallengeRequestTmp = lastChallengeRequest;
        lastChallengeRequest = null;
        return lastChallengeRequestTmp;
    }

    /**
     * Accepts specified request
     *
     * @param request the challengerequest
     */
    public void acceptMatch(ChallengeRequest request) {
        cgm.acceptChallengeRequest(request);
    }

    /**
     * Switches to game view when game has started
     */
    @Override
    public void onPostGameStart() {
        Platform.runLater(() -> mainWindow.switchView(MainWindow.ViewEnum.GAME));
    }

    /**
     * Sets whether AI or the user should play the match
     */
    @Override
    public void onPreGameStart() {
        cgm.updateSelfPlayerSupplier(isAI ? ConfigData.getInstance().getCurrentGame().createAIPlayerFactory() :
                                             ConfigData.getInstance().getCurrentGame().createLocalPlayerFactory());
    }

    /**
     * Sets local gamemanager, so it does not need to be queried from ConfigData all the time
     * Also registers this object as listener for {@link ConnectedGameManagerObserver}.
     */
    public void prepareGameManager() {
        this.cgm = (ConnectedGameManager) ConfigData.getInstance().getGameManager();
        cgm.registerObserver(this);
    }
}
