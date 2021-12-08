package project23.connection;

import project23.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {

    private static final long KEEP_ALIVE_INTERVAL = 30 * 1000;

    private final Socket clientSocket;
    private BufferedReader inputStream;
    private final CommunicationHandler communicationHandler;
    private PrintWriter outputStream;

    private boolean running = true;

    /**
     * @param clientSocket         The clientsocket
     * @param communicationHandler Communication handler which handles the communicationprotocol.
     */
    public Client(Socket clientSocket, CommunicationHandler communicationHandler) {
        this.clientSocket = clientSocket;
        this.communicationHandler = communicationHandler;

        communicationHandler.setClient(this);

        try {
            this.inputStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.outputStream = new PrintWriter(this.clientSocket.getOutputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (Logger.DEBUG) {
            startConsolePassthroughThread();
        }

        startKeepAliveThread();
    }

    /**
     * Debug method, starts a consolepassthrough thread to read console input while application is running.
     */
    public void startConsolePassthroughThread() {
        Thread thread = new Thread(this::consolePassthrough, "ConsoleServerPassthrough");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Debug method, reads input from console while application is running, and sends it to the server.
     */
    private void consolePassthrough() {
        if (!Logger.DEBUG) {
            Logger.warning(
                    "Warning! Starting console <> server command passthrough, but CommunicationHandler.DEBUG is set to false! You won't receive any feedback!");
        } else {
            Logger.info("Console <> server command passthrough started.");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (running) {
                String line = reader.readLine();
                sendCommandToServer(line + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.info("Console <> server command passthrough stopped.");
    }

    /**
     * Starts a new thread calling Client#keepAlive
     */
    public void startKeepAliveThread() {
        Thread thread = new Thread(this::keepAlive, "ServerKeepAlive");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Periodically sends a message to the server to prevent SocketTimeout
     */
    private void keepAlive() {
        Logger.info("Keep alive thread started.");

        while (running) {
            try {
                Thread.sleep(KEEP_ALIVE_INTERVAL);
            } catch (InterruptedException ignored) {
            }

            sendKeepAlive();
        }

        Logger.info("Keep alive thread stopped.");
    }

    /**
     * See Client#keepAlive
     */
    private void sendKeepAlive() {
        Logger.info("Sending keep-alive message..");
        sendGetPlayerlistMessage();
    }

    /**
     * Send a command to the server
     *
     * @param command The command to send to the server
     */
    public void sendCommandToServer(String command) {
        Logger.debug(" to server   = " + command.trim());

        outputStream.print(command);
        outputStream.flush();
    }

    /**
     * Reads server messages and sends them off to be handled
     */
    @Override
    public void run() {
        running = true;

        String input;
        try {
            while (running && (input = inputStream.readLine()) != null) {
                //There was input, handle it
                if (!input.equals("")) {
                    try {
                        communicationHandler.handleServerInput(input);
                    } catch (Exception e) {
                        Logger.error("Could not handle server input '" + input + "': " + e.toString());
                    }
                }
            }
        } catch (IOException ignored) {
        }

        Logger.info("Closed connection to the server.");
    }

    /**
     * Cleans up the client before closing down the thread.
     */
    public void close() {
        communicationHandler.sendLogoutMessage();
        running = false;

        try {
            clientSocket.close();
        } catch (Exception ignored) {
        }
    }

    public CommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    /**
     * Sends the server the forfeit command.
     */
    public void sendForfeitMessage() {
        communicationHandler.sendForfeitMessage();
    }

    /**
     * Passthrough method, calls communicationHandler#move
     *
     * @param move move chosen by the player or AI
     */
    public void sendMoveMessage(int move) {
        communicationHandler.sendMoveMessage(move);
    }

    /**
     * Subscribe to random matches for the given game type
     *
     * @param gameType the game to for which to subscribe
     */
    public void sendSubscribeMessage(String gameType) {
        communicationHandler.sendSubscribeMessage(gameType);
    }

    /**
     * Logs the player into the server
     *
     * @param playerName the player name
     */
    public void sendLoginMessage(String playerName) {
        communicationHandler.sendLoginMessage(playerName);
    }

    /**
     * Sends a challenge to a player in the lobby
     *
     * @param playerToChallenge the player to challenge
     * @param gameType          the game to play
     */
    public void sendChallengeMessage(String playerToChallenge, String gameType) {
        communicationHandler.sendChallengeMessage(playerToChallenge, gameType);
    }

    /**
     * Sends the get gamelist command to the server
     */
    public void sendGetPlayerlistMessage() {
        communicationHandler.sendGetPlayerlistMessage();
    }

    /**
     * Accepts a challenge
     *
     * @param challengeNr the challenge number
     */
    public void acceptChallenge(int challengeNr) {
        communicationHandler.sendAcceptChallengeMessage("" + challengeNr);
    }
}
