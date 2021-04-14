package gui.model;

import gui.view.View;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.ArrayList;

public abstract class Model {
    private ArrayList<View> observers;
    private String dialogMessage = ""; // pop-up dialog
    private String dialogTitle = "";
    private String infoMessage = ""; // small text, no pop-up
    private Label textNode;

    public Model() {
        this.observers = new ArrayList<>();
    }

    public void registerView(View view) {
        observers.add(view);
    }

    public void updateView() {
        for(View view : observers) {
            view.update(this);
        }
    }

    
    
    public void setDialogMessageAndTitle(String message, String title) {
        this.dialogMessage = message;
        this.dialogTitle = title;
    }

    /**
     * Gets the pop-up message, and then clears it
     * @return the message
     */
    public String getDialogMessage() {
        String dialogMessageTmp = dialogMessage;
        dialogMessage = "";
        return dialogMessageTmp;
    }

    public String getDialogTitle() {
        String dialogTitleTmp = dialogTitle;
        dialogTitle = "";
        return dialogTitleTmp;
    }

    public void setInfoMessage(String message) {
        this.infoMessage = message;
    }

    /**
     * Gets the info message, and then clears it
     */
    public String getInfoMessage() {
        String infoMessageTmp = infoMessage;
        infoMessage = "";
        return infoMessageTmp;
    }

    public void setLabelNode(Label textNode) {
        this.textNode = textNode;
    }

    public Label getLabelNode() {
        return textNode;
    }
}