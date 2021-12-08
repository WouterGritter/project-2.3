package project23.gui.view;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project23.framework.ChallengeRequest;
import project23.gui.controller.Controller;
import project23.gui.model.GameLobbyModel;

import java.util.ArrayList;
import java.util.List;

public class GameLobbyView extends View<GameLobbyModel> {

    private final ArrayList<Dialog> dialogs;

    /**
     * Makes a new arraylist with challenge dialogs
     *
     * @param parent,       screen nodes (fxml)
     * @param controller,   controller of the nodes
     * @param windowWidth,  width of the window
     * @param windowHeight, height of the window
     */
    public GameLobbyView(Parent parent, Controller controller, int windowWidth, int windowHeight) {
        super(parent, controller, windowWidth, windowHeight);
        this.dialogs = new ArrayList<>();
    }

    /**
     * Shows all the challenges in the lobby and updates playerlist in the lobby
     *
     * @param model, the model to query data from
     */
    @Override
    public void update(GameLobbyModel model) {
        showDialog(model.getDialogMessage(), model.getDialogTitle());
        showChallengeDialog(model);
        showInfoText(model.getInfoMessage(), model.getLabelNode());

        // Update lobby list
        updateGameChallengeList(model.getLobbyPlayers());
    }

    /**
     * shows dialog when a player challenges you
     * Dialog is a pop-up message
     * Makes new dialog, adds buttons, message and icons, shows the dialog
     *
     * @param model, the model to get/set the challenge request
     */
    public void showChallengeDialog(GameLobbyModel model) {
        ChallengeRequest challengeRequest = model.getLastChallengeRequest();
        if (challengeRequest == null) {
            return;
        }

        //creating dialog
        Dialog<String> dialog = new Dialog<>();
        dialogs.add(dialog);
        //add button
        ButtonType accept = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
        ButtonType ignore = new ButtonType("Ignore", ButtonBar.ButtonData.CANCEL_CLOSE);

        //setting dialog content
        dialog.setContentText(challengeRequest.getOpponentName() + " is challenging you to a game of " + challengeRequest.getGameType().displayName + "! Do you accept?");
        //add button to dialogpane
        dialog.getDialogPane().getButtonTypes().add(accept);
        dialog.getDialogPane().getButtonTypes().add(ignore);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dialog.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSS/dialogStyle.css").toExternalForm());
        dialog.setTitle("A new foe has appeared!");
        dialog.initModality(Modality.NONE); // dont block other dialogs
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResource("/images/icon.png").toExternalForm()));

        // Listen for answer
        Button button1 = (Button) dialog.getDialogPane().lookupButton(accept);
        button1.setOnAction(actionEvent -> {
            model.acceptMatch(challengeRequest);
            closeAllDialogs();
        });

        //show dialog
        dialog.show();
    }

    /**
     * CLoses all dialogs
     */
    private void closeAllDialogs() {
        for (Dialog dialog : dialogs) {
            dialog.close();
        }
        dialogs.clear();
    }

    /**
     * Displays list of players which are available in the server
     *
     * @param playerNames, list of players
     */
    private void updateGameChallengeList(List<String> playerNames) {
        ListView challengerList = (ListView) lookup("#challengerList");
        challengerList.getItems().clear();

        for (String playerName : playerNames) {
            challengerList.getItems().add(playerName);
        }
    }
}
