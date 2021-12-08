package project23.gui.model;

import project23.framework.ConfigData;

public class GameConfigurationModel extends Model {

    /**
     * Sets IP and port in ConfigData, used to connect to server for multiplayer matches
     *
     * @param ip   the IP, like server.domain.com or 12.34.567.890
     * @param port port, must be an integer
     */
    public void setIPandPort(String ip, String port) {
        ConfigData.getInstance().setServerIP(ip);
        try {
            ConfigData.getInstance().setServerPort(port);
        } catch (NumberFormatException e) {
            setDialogMessageAndTitle("Invalid port", "Error");
            updateView();
        }
    }

    /**
     * Sets timelimit the AI can tink for before stoppng
     *
     * @param aiThinkingTime time in milliseconds
     */
    public void setAIThinkingTime(String aiThinkingTime) {
        try {
            int thinkingTime = Integer.parseInt(aiThinkingTime);
            ConfigData.getInstance().setMinimaxThinkingTime(thinkingTime);
        } catch (NumberFormatException e) {
            setDialogMessageAndTitle("AI thinking time must be a number!", "Error");
            updateView();
        }
    }
}
