package project23.gui.view;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import project23.gui.controller.Controller;
import project23.gui.model.Model;

import static java.lang.System.currentTimeMillis;

public abstract class View<T extends Model> extends Scene {

    protected Controller controller;
    public static final int MESSAGE_CLEARING_DELAY_MS = 2000;
    private long expireTimeMessage = 0;

    /**
     * Sets the controller to be used
     *
     * @param parent,       screen nodes (fxml)
     * @param controller,   controller of the nodes
     * @param windowWidth,  width of the window
     * @param windowHeight, height of the window
     */
    public View(Parent parent, Controller controller, int windowWidth, int windowHeight) {
        super(parent, windowWidth, windowHeight);
        this.controller = controller;
    }

    /**
     * Only method that can be called from model, used to update view (redraw scene)
     *
     * @param model, the model to query data from
     */
    public abstract void update(T model);

    /**
     * Dialog is a pop-up message
     * Makes new dialog, adds message and icons, shows the dialog
     *
     * @param message,     the message that needs to be shown
     * @param dialogTitle, title of the pop-up
     */
    public void showDialog(String message, String dialogTitle) {
        if (message != null && !message.isBlank()) {
            //creating dialog
            Dialog<String> dialog = new Dialog<>();
            //add button
            ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            //setting dialog content
            dialog.setContentText(message);
            //add button to dialogpane
            dialog.getDialogPane().getButtonTypes().add(type);
            dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            dialog.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
            dialog.getDialogPane()
                    .getStylesheets()
                    .add(getClass().getResource("/CSS/dialogStyle.css").toExternalForm());
            dialog.setTitle(dialogTitle);
            Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(new Image(getClass().getResource("/images/icon.png").toExternalForm()));

            //show dialog
            dialog.show();
        }
    }

    /**
     * if not displaying a message show a message when this method gets called.
     * Messages disappears after {@link View#MESSAGE_CLEARING_DELAY_MS} seconds
     *
     * @param message, the message to display
     */
    public void showInfoText(String message, Label node) {
        if (!message.isBlank()) {
            this.expireTimeMessage = currentTimeMillis() + MESSAGE_CLEARING_DELAY_MS;

            new Thread(() -> {
                Platform.runLater(() -> node.setText(message));
                try {
                    Thread.sleep(MESSAGE_CLEARING_DELAY_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (currentTimeMillis() >= expireTimeMessage) {
                    Platform.runLater(() -> node.setText(""));
                }
            }).start();
        }
    }
}