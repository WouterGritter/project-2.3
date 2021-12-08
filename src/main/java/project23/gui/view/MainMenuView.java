package project23.gui.view;

import javafx.scene.Parent;
import project23.gui.controller.Controller;
import project23.gui.model.MainMenuModel;

public class MainMenuView extends View<MainMenuModel> {

    public MainMenuView(Parent parent, Controller controller, int windowWidth, int windowHeight) {
        super(parent, controller, windowWidth, windowHeight);
    }

    @Override
    public void update(MainMenuModel model) {
    }
}
