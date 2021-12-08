package project23.gui.controller;

import project23.gui.MainWindow;
import project23.gui.model.Model;

public abstract class Controller<T extends Model> {

    protected T model;
    protected MainWindow mainWindow;

    public void setModel(T model) {
        this.model = model;
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }
}