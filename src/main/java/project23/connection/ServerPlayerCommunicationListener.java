package project23.connection;

public interface ServerPlayerCommunicationListener {

    /**
     * Handler for when we receive a MOVE message from the server
     *
     * @param whoPlayer the player that made the move
     * @param move      the move
     */
    void turnReceive(String whoPlayer, String move);

    /**
     * Handler to finalize the turn of the Serverplayer
     */
    void finalizeTurn();
}
