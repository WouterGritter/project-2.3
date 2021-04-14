package gui.model;

import framework.ConfigData;

public class GenericGameConfigurationModel extends Model{

    public void setIPandPort(String ip, String port) {
        ConfigData.getInstance().setServerIP(ip);
        try {
            ConfigData.getInstance().setServerPort(port);
        } catch(NumberFormatException e) { //TODO: Eigen exception maken, InvalidConfigDataException? Niet heel hard nodig
            setDialogMessageAndTitle("Invalid port", "Error");
            updateView();
        }
    }
    public void setAIThinkingTime(String aiThinkingTime){
        try {
            int thinkingTime = Integer.parseInt(aiThinkingTime);
            ConfigData.getInstance().setMinimaxThinkingTime(thinkingTime);
        }
        catch(NumberFormatException e) {
            setDialogMessageAndTitle("AI thinking time must be a number!", "Error");
            updateView();
        }
    }
}
