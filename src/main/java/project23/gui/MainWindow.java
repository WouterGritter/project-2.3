package project23.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import project23.framework.ConfigData;
import project23.gui.controller.*;
import project23.gui.model.GameConfigurationModel;
import project23.gui.model.GameLobbyModel;
import project23.gui.model.GameMenuModel;
import project23.gui.model.GameModel;
import project23.gui.view.*;

import java.io.IOException;
import java.util.Objects;

public class MainWindow {

    private Stage stage;
    private GameConfigurationView gcView;
    private GameView gView;
    private GameMenuView gmView;
    private MainMenuView mmView;
    private GameLobbyView glView;

    public static final int WINDOW_WIDTH = 650;
    public static final int WINDOW_HEIGHT = 750;

    /**
     * Different kind of scenes/views
     */
    public enum ViewEnum {
        GAME_LOBBY,
        MAINMENU,
        GAME_MENU,
        GAME_CONFIGURATION,
        GAME
    }

    /**
     * Configures title, icon, view etc. when the program is loaded
     *
     * @param primaryStage, displays scene/view
     */
    public MainWindow(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        setupMVC();

        stage.setOnCloseRequest(windowEvent -> this.quit());

        //stage = primaryStage;
        stage.getIcons()
                .add(new Image(Objects.requireNonNull(getClass().getResource("/images/icon.png")).toExternalForm()));
        stage.setTitle("C4Games");
        switchView(ViewEnum.MAINMENU);
    }

    /**
     * closes any connections and closes the program
     */
    public void quit() {
        if (ConfigData.getInstance().getGameManager() != null) {
            ConfigData.getInstance().getGameManager().destroy();
        }
    }

    /**
     * Creates all models, controllers, views and registers them
     */
    private void setupMVC() throws IOException {
        // Game settings
        GameConfigurationModel gcModel = new GameConfigurationModel();
        GameConfigurationController gcController = new GameConfigurationController();
        gcController.setMainWindow(this);
        gcController.setModel(gcModel);
        gcView = new GameConfigurationView(
                getFXMLParent("fxml/gameConfiguration.fxml", gcController), gcController, WINDOW_WIDTH, WINDOW_HEIGHT);
        gcModel.registerView(gcView);

        // Gameboard
        GameModel gModel = new GameModel();
        GameController gController = new GameController();
        gController.setMainWindow(this);
        gController.setModel(gModel);
        gView = new GameView(
                getFXMLParent("fxml/game.fxml", gController),
                gController,
                WINDOW_WIDTH,
                WINDOW_HEIGHT
        );
        gModel.registerView(gView);

        // Game lobby
        GameLobbyModel glModel = new GameLobbyModel(gModel, this);
        GameLobbyController glController = new GameLobbyController();
        glController.setMainWindow(this);
        glController.setModel(glModel);
        glView = new GameLobbyView(
                getFXMLParent("fxml/gameLobby.fxml", glController), glController, WINDOW_WIDTH, WINDOW_HEIGHT);
        glModel.registerView(glView);

        // Game Menu
        GameMenuModel gmModel = new GameMenuModel(gModel, glModel);
        GameMenuController gmController = new GameMenuController();
        gmController.setMainWindow(this);
        gmController.setModel(gmModel);
        gmView = new GameMenuView(
                getFXMLParent("fxml/gameMenu.fxml", gmController), gmController, WINDOW_WIDTH, WINDOW_HEIGHT);
        gmModel.registerView(gmView);

        // Main Menu
        //MainMenuModel mmModel = new MainMenuModel();
        MainMenuController mmController = new MainMenuController();
        mmController.setMainWindow(this);
        mmView = new MainMenuView(
                getFXMLParent("fxml/mainMenu.fxml", mmController), mmController, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    /**
     * Returns a Parent object with specified controller set
     *
     * @param fxmlFile   the fxml file
     * @param controller the controller
     * @return Parent object of the fxml file
     * @throws IOException when the fxml file isn't found
     */
    private Parent getFXMLParent(String fxmlFile, Controller controller) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        fxmlLoader.setController(controller);
        return fxmlLoader.load();
    }

    /**
     * Sets the view to one of the specified views
     *
     * @param view which view to show
     */
    public void switchView(ViewEnum view) {
        switch (view) {
            case MAINMENU:
                stage.setScene(mmView);
                break;
            case GAME_MENU:
                stage.setScene(gmView);
                break;
            case GAME_CONFIGURATION:
                stage.setScene(gcView);
                break;
            case GAME:
                stage.setScene(gView);
                break;
            case GAME_LOBBY:
                stage.setScene(glView);
                break;
            default:
                throw new IllegalArgumentException();
        }
        stage.show();
    }
}