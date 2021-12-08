package project23.gui.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import project23.gui.MainWindow;
import project23.gui.model.GameLobbyModel;

import java.net.URL;
import java.util.ResourceBundle;

public class GameLobbyController extends Controller<GameLobbyModel> implements Initializable {

    @FXML
    private ListView challengerList;
    @FXML
    private CheckBox isAIBox;
    @FXML
    private Label messageLabel;

    /**
     * Switches back to the main menu view
     */
    public void pressBackToMainMenu(ActionEvent event) {
        model.logout();
        mainWindow.switchView(MainWindow.ViewEnum.MAINMENU);
    }

    /**
     * refreshes the playerList from the server
     */
    public void pressRefresh(ActionEvent event) {
        model.refreshPlayerList();
    }

    /**
     * When the challenge button is pressed, choose a player to challenge.
     * If playerlist is not empty
     */
    public void pressChallengePlayer() {
        ObservableList<Integer> indices = challengerList.getSelectionModel().getSelectedIndices();
        if (!indices.isEmpty()) {
            model.challengePlayer(indices.get(0));
        } else {
            model.setInfoMessage("Please select a player to challenge.");
            model.updateView();
        }
    }

    /**
     * This is the functionality of the old "Play online" button, switches to the Play online view
     */
    public void pressSubscribe() {
        model.prepareOnlineGame();
        mainWindow.switchView(MainWindow.ViewEnum.GAME);
    }

    /**
     * Set the AI to current value of the AI checkbox in model
     * Also when the checkbox changes
     * Sets the labelNode to be used for showInfoMessage()
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set default value
        model.setAI(isAIBox.isSelected());
        model.setLabelNode(messageLabel);

        // Listen for changes
        isAIBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            model.setAI(newValue);
        });
    }
}
