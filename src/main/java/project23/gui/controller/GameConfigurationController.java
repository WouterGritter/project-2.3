package project23.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import project23.framework.ConfigData;
import project23.framework.player.MinimaxAIPlayer;
import project23.gui.MainWindow;
import project23.gui.model.GameConfigurationModel;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GameConfigurationController extends Controller<GameConfigurationModel> implements Initializable {

    @FXML
    private Label ipConfirmation;
    @FXML
    private TextField ipAddressField;
    @FXML
    private TextField portField;
    @FXML
    private ComboBox<String> comboBoxDifficulty;
    @FXML
    private TextField thinkingTimeField;

    ObservableList<String> options = FXCollections.observableArrayList(
            Arrays.stream(MinimaxAIPlayer.AIDifficulty.values())
                    .map(MinimaxAIPlayer.AIDifficulty::displayName)
                    .collect(Collectors.toList())
    );

    /**
     * Switches back to the main menu view
     */
    @FXML
    public void pressBackToMainMenu(ActionEvent event) {
        mainWindow.switchView(MainWindow.ViewEnum.MAINMENU);
    }

    /**
     * Sets all the fields to the standard values at the start (so only one time)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        model.setLabelNode(ipConfirmation);
        comboBoxDifficulty.setItems(options);
        ipAddressField.setText(ConfigData.getInstance().getServerIP());
        portField.setText(ConfigData.getInstance().getServerPort() + "");
        comboBoxDifficulty.setValue(ConfigData.getInstance().getAIDifficulty().displayName());
        thinkingTimeField.setText(String.valueOf(ConfigData.getInstance().getMinimaxThinkingTime()));
    }

    /**
     * When OK is pressed IP, port, difficulty and AI thinking time are set
     * Message shows that the all is updated
     */
    @FXML
    void pressOKip(ActionEvent event) {
        model.setIPandPort(ipAddressField.getText(), portField.getText());
        model.setAIThinkingTime(thinkingTimeField.getText());
        model.setInfoMessage("Updated"); // Regression: not showing "henlo" anymore
        model.updateView();

        MinimaxAIPlayer.AIDifficulty difficulty = MinimaxAIPlayer.AIDifficulty.fromDisplayName(comboBoxDifficulty.getValue());
        ConfigData.getInstance().setAIDifficulty(difficulty);
    }
}