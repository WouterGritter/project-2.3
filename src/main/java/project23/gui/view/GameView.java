package project23.gui.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import project23.framework.BoardState;
import project23.framework.ConfigData;
import project23.framework.board.Board;
import project23.framework.board.BoardPiece;
import project23.framework.player.Player;
import project23.gui.controller.Controller;
import project23.gui.model.GameModel;
import project23.util.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameView extends View<GameModel> {

    private final Pane gameBoardPane;
    private List<URL> playerIconFileURLs;
    private final Text waitingText;
    private Label clock;
    private int seconds;

    // Cell margin value between 0 (no margin) and 1 (no space for the piece at all)
    public static final double MARGIN = 0.2;
    //private Timeline animation = new Timeline(new KeyFrame(Duration.seconds(1), e -> countDown()));
    private final Timeline animation = new Timeline(new KeyFrame(Duration.ZERO, actionEvent -> countDown()),
                                                    new KeyFrame(Duration.seconds(1)));

    /**
     * Sets the game from the view/scene
     * Sets waitingtext
     *
     * @param parent,       screen nodes (fxml)
     * @param controller,   controller of the nodes
     * @param windowWidth,  width of the window
     * @param windowHeight, height of the window
     */
    public GameView(Parent parent, Controller controller, int windowWidth, int windowHeight) {
        super(parent, controller, windowWidth, windowHeight);
        gameBoardPane = (Pane) lookup("#board");
        this.waitingText = new Text("Please wait for the game to start.");
        animation.setCycleCount(10);
    }

    public void setBoardPieceIcons(List<URL> playerIconFileURLs) {
        this.playerIconFileURLs = playerIconFileURLs;
    }

    /**
     * Updates the clock
     * If online match is waiting, show "waiting for game"
     * Otherwise draws the board
     *
     * @param model
     */
    @Override
    public void update(GameModel model) {
        if (clock == null) {
            clock = model.getClockLabel();
        }
        if (model.restartClock()) {
            resetClock();
        }
        if (model.stopClock()) {
            stopClock();
        }

        showDialog(model.getDialogMessage(), model.getDialogTitle());
        showInfoText(model.getInfoMessage(), model.getLabelNode());

        if (model.getBoard().getBoardState() == BoardState.WAITING && !ConfigData.getInstance()
                .getCurrentGame()
                .isOnline()) {
            setBackgroundColorBoard(null);
            clearBoard();
            gameBoardPane.getChildren().add(waitingText);
        } else {
            drawBoard(model);
        }
    }

    /**
     * Resets the clock to 10 seconds and starts the countdown
     */
    private void resetClock() {
        seconds = 10;
        animation.playFromStart();
    }

    /**
     * Stops the clock and removes the time
     */
    public void stopClock() {
        animation.stop();
        Platform.runLater(() -> clock.setText(""));
    }

    private void countDown() {
        if (seconds == 1) {
            Platform.runLater(() -> clock.setText("Time's up!"));
        } else {
            Platform.runLater(() -> clock.setText(--seconds + " seconds remaining"));
        }
    }

    /**
     * Draws the game board, sets the grid, draws the gamepieces
     *
     * @param model
     */
    private void drawBoard(GameModel model) {
        Board board = model.getBoard();
        if (board == null) {
            clearBoard();
            return;
        }

        setBackgroundColorBoard(ConfigData.getInstance().getCurrentGame().getBoardBackgroundColor());
        setBoardPieceIcons(ConfigData.getInstance().getCurrentGame().getBoardPieceIcons());

        // Player stats
        showPlayerInformation(model.getPlayerInfo(board.piecesCount()));

        // Draw board, pieces and hints
        int gridSize = board.getWidth();
        drawLines(gridSize);

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                drawPiece(board.getBoardPiece(x, y), gridSize);
            }
        }

        Player player = board.getCurrentPlayer();
        if (player != null && board.isShowValidMoves() && player.isShowValidMoves()) {
            drawValidMoves(board.getValidMoves(), gridSize);
        }
    }

    /**
     * Draws the possible moves for the player
     *
     * @param validMoves, list of possible moves saved in boardpieces
     * @param gridSize,   size of the grid
     */
    private void drawValidMoves(List<BoardPiece> validMoves, int gridSize) {
        double cellSize = gameBoardPane.getPrefWidth() / gridSize;

        for (BoardPiece piece : validMoves) {
            double boardX = piece.getX() * cellSize + cellSize / 2;
            double boardY = piece.getY() * cellSize + cellSize / 2;

            Circle boardHighlight = new Circle(boardX, boardY, cellSize / 2 * (1 - MARGIN));

            boardHighlight.setFill(null);
            boardHighlight.setStroke(Color.rgb(200, 200, 200, 0.5));
            boardHighlight.setStrokeWidth(1.0);

            gameBoardPane.getChildren().add(boardHighlight);
        }
    }

    /**
     * Draws the lines of the grid
     *
     * @param gridSize, size of the grid
     */
    public void drawLines(int gridSize) {
        // Clear board
        clearBoard();

        double boardSize = gameBoardPane.getPrefWidth();

        // 3 gridboxes => 2 lines 1|2|3
        for (int i = 0; i < gridSize - 1; i++) {
            double pos = (double) (i + 1) / gridSize * boardSize;

            Line horizontalDivider = new Line();
            horizontalDivider.setStartX(pos);
            horizontalDivider.setStartY(0.0f);
            horizontalDivider.setEndX(pos);
            horizontalDivider.setEndY(boardSize);

            Line verticalDivider = new Line();
            verticalDivider.setStartX(0.0f);
            verticalDivider.setStartY(pos);
            verticalDivider.setEndX(boardSize);
            verticalDivider.setEndY(pos);

            gameBoardPane.getChildren().add(horizontalDivider);
            gameBoardPane.getChildren().add(verticalDivider);
        }
    }

    /**
     * Clears the board
     */
    public void clearBoard() {
        gameBoardPane.getChildren().clear();
    }

    /**
     * Draws the gamepiece
     * Gets the coordinates and adds a boardpiece to the coordinate when clicked
     *
     * @param piece,    boardpiece
     * @param gridSize, size of the grid
     */
    public void drawPiece(BoardPiece piece, int gridSize) {
        if (!piece.hasOwner()) {
            return;
        }

        int x = piece.getX();
        int y = piece.getY();
        double cellSize = gameBoardPane.getPrefWidth() / gridSize;

        // Hoop dat er geen outofbounds komt! lol
        URL pngURL = playerIconFileURLs.get(piece.getOwner().getID());

        // Create image
        Image pieceImage;
        try {
            pieceImage = new Image(pngURL.openStream(), cellSize * (1 - MARGIN), cellSize * (1 - MARGIN), true, true);
        } catch (IOException e) {
            Logger.error("Image does not exist?");
            e.printStackTrace();
            return; // stop, image not found
        }

        // Draw
        ImageView imageView = new ImageView(pieceImage);
        imageView.setX(cellSize * (x + MARGIN / 2));
        imageView.setY(cellSize * (y + MARGIN / 2));
        gameBoardPane.getChildren().add(imageView);
    }

    /**
     * shows the playerinformation in the top information box in a label
     *
     * @param playerInformationList, list of playerinformation like name etc.
     */
    public void showPlayerInformation(ArrayList<String> playerInformationList) {
        HBox playerInformationHBox = (HBox) lookup("#playerInformationHBox");

        playerInformationHBox.getChildren().clear();

        for (String playerInformation : playerInformationList) {
            Label playerInformationLabel = new Label(playerInformation);
            playerInformationLabel.setId("playerInformationLabel");
            playerInformationHBox.getChildren().add(playerInformationLabel);
        }
    }

    /**
     * sets the background color for the game boards
     *
     * @param color, the color of the background
     */
    public void setBackgroundColorBoard(Color color) {
        gameBoardPane.setBackground(new Background(new BackgroundFill(color, null, null)));
    }
}