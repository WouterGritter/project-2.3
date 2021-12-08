package project23.connection;

import java.util.List;

public interface GameManagerCommunicationListener {

    /**
     * Handler for when a match is started
     *
     * @param opponent      the opponent's name
     * @param playerToBegin the player to make the first move
     */
    void startServerMatch(String opponent, String playerToBegin);

    /**
     * Handler for when a challenge is received
     *
     * @param opponent           the challenger's name
     * @param gameTypeServerName the name of the game (according to the server)
     * @param challengeNr        challengeID
     */
    void onChallengeRequestReceive(String opponent, String gameTypeServerName, int challengeNr);

    /**
     * Handler for when a challenge gets cancelled
     *
     * @param challengeNr challengeID
     */
    void challengeRequestCancelled(int challengeNr);

    /**
     * Handler for when an updated playerlist is sent by the server
     *
     * @param lobbyPlayers list of playernames
     */
    void updateLobbyPlayers(List<String> lobbyPlayers);

    /**
     * Handler for when we receive the result of a match
     *
     * @param result WIN, LOSS or DRAW
     */
    void endMatch(String result);

    /**
     * Handler for when the server sends us an error message
     *
     * @param errorMessage the error message
     */
    void onServerError(String errorMessage);
}
