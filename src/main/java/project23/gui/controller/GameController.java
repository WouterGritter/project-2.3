package project23.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import project23.framework.ConfigData;
import project23.framework.GameManager;
import project23.gui.MainWindow;
import project23.gui.model.GameModel;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController extends Controller<GameModel> implements Initializable {

    @FXML
    private Pane board;
    @FXML
    private Label messageField;
    @FXML
    private Label clock;

    /**
     * Sets the labelNode to be used for showInfoMessage() in the model
     * Sets the boardsize in the model
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        model.setLabelNode(messageField);
        model.setBoardSize(board.getPrefWidth());
        model.setClockLabel(clock);
    }

    /**
     * Switches back to the main menu view
     * Destroys current view
     */
    @FXML
    public void pressBackToMainMenu(ActionEvent event) {
        mainWindow.switchView(MainWindow.ViewEnum.MAINMENU);
        GameManager gm = ConfigData.getInstance().getGameManager();
        gm.destroy();
    }

    /**
     * When mouse is released get X and Y coordinates
     */
    @FXML
    public void onMouseReleased(MouseEvent event) {
        model.clickTile(event.getX(), event.getY());
    }

    /**
     * Restarts the game when pressed
     */
    public void pressRetry(ActionEvent event) {
        model.restartGame();
    }
}
