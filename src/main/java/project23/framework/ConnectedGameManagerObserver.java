package project23.framework;

public interface ConnectedGameManagerObserver {

    /**
     * Handler for server error messages
     *
     * @param errorMessage the error message from the server
     */
    void onServerError(String errorMessage);

    /**
     * Handler for when the server sends a playerlist
     */
    void onPlayerListReceive();

    /**
     * Handler for when we receive a challenge request
     *
     * @param challengeRequest the challenge request
     */
    void onChallengeRequestReceive(ChallengeRequest challengeRequest);

    /**
     * Handler for before the game starts
     */
    void onPreGameStart();

    /**
     * Handler for after the game starts
     */
    void onPostGameStart();
}
