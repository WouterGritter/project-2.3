package project23.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import project23.framework.ConfigData;
import project23.framework.GameType;
import project23.gui.MainWindow;
import project23.gui.model.MainMenuModel;

public class MainMenuController extends Controller<MainMenuModel> {

    @FXML
    private Text actiontarget;

    /**
     * Switches to the TTT view
     */
    @FXML
    public void pressTTTMenuButton(ActionEvent event) {
        mainWindow.switchView(MainWindow.ViewEnum.GAME_MENU);
        ConfigData.getInstance().setCurrentGameType(GameType.TIC_TAC_TOE);
    }

    /**
     * Switches to the Othello menu view
     */
    @FXML
    public void pressOthelloMenuButton(ActionEvent event) {
        mainWindow.switchView(MainWindow.ViewEnum.GAME_MENU);
        ConfigData.getInstance().setCurrentGameType(GameType.OTHELLO);
    }

    /**
     * Close the program
     */
    @FXML
    public void pressExitButton(ActionEvent event) {
        mainWindow.quit();
        Platform.exit();
    }

    /**
     * Switches to the configuration view
     */
    @FXML
    public void pressConfigurationButton(ActionEvent event) {
        mainWindow.switchView((MainWindow.ViewEnum.GAME_CONFIGURATION));
    }
}
