package project23.framework;

/**
 * this class stores a challengerequest.
 */
public class ChallengeRequest {

    private final String opponentName;
    private final GameType gameType;
    private final int challengeNr;

    /**
     * @param opponentName string
     * @param gameType     framework.GameType
     * @param challengeNr  the challengenumber.
     */
    public ChallengeRequest(String opponentName, GameType gameType, int challengeNr) {
        this.opponentName = opponentName;
        this.gameType = gameType;
        this.challengeNr = challengeNr;
    }

    /**
     * @return The opponent who made this challenge request.
     */
    public String getOpponentName() {
        return opponentName;
    }

    /**
     * @return The game-type this challenge request refers to.
     */
    public GameType getGameType() {
        return gameType;
    }

    /**
     * @return The challenge number this request is known by, on the server.
     */
    public int getChallengeNr() {
        return challengeNr;
    }
}
