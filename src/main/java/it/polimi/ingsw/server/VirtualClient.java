package it.polimi.ingsw.server;

import it.polimi.ingsw.client.RequestTimedOutException;
import it.polimi.ingsw.communication.server.ServerKeepAlive;
import it.polimi.ingsw.communication.timeout_handler.ServerTimeoutHandler;
import it.polimi.ingsw.communication.client.ClientMessage;
import it.polimi.ingsw.communication.client.ClientResponse;
import it.polimi.ingsw.communication.server.ServerMessage;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Class to handler a client server-side
 */
public class VirtualClient implements Runnable {
    private final Integer clientID;
    private final Socket clientSocket;
    private final Server server;
    private final ServerTimeoutHandler timeoutHandler;
    private final VirtualClientCommandDispatcher clientCommandDispatcher;
    private Game game;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean connected;
    private final ExecutorService executors;
    private ScheduledFuture<?> heartBeatService;
    private GameState gameState;

    /**
     * Constructor of the class
     *
     * @param socket   of this Client
     * @param server   where the client is connected
     * @param clientID unique ID of this client
     */
    public VirtualClient(Socket socket, Server server, Integer clientID) {
        this.clientSocket = socket;
        this.server = server;
        this.clientID = clientID;
        connected = true;
        executors = Executors.newCachedThreadPool();
        timeoutHandler = new ServerTimeoutHandler(this);
        clientCommandDispatcher = new VirtualClientCommandDispatcher(this);
        gameState = GameState.Active;
    }

    /**
     * Send a message to the actual Client
     *
     * @param serverMessage message to be sent
     */
    public void send(ServerMessage serverMessage) {
        synchronized (this) {
            try {
                outputStream.reset();
                outputStream.writeObject(serverMessage);
                outputStream.flush();
            } catch (IOException e) {
                System.out.println("Couldn't send " + serverMessage + "to " + game.getNicknameByClient(this));
            }
        }
    }

    /**
     * Send Message and waits for answer
     *
     * @param serverMessage    message to be sent
     * @param timeoutInSeconds time before RequestTimedOutException is thrown, -1 to wait indefinitely
     * @throws RequestTimedOutException thrown if timeout is exceeded.
     */
    public void sendAndWait(ServerMessage serverMessage, int timeoutInSeconds) throws RequestTimedOutException {
        try {
            timeoutHandler.sendAndWait(serverMessage, timeoutInSeconds);
        } catch (TimeoutException e) {
            System.out.println("Timeout on message expired.");
            throw new RequestTimedOutException();
        }

    }

    /**
     * Handles the input stream from the client and the TimeoutHandler
     */
    public void run() {
        try {
            if(server.timeoutEnabled)
                clientSocket.setSoTimeout(5000);
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            heartBeatService = executor.scheduleAtFixedRate(this::startHeartBeat, 500, 1000, TimeUnit.MILLISECONDS);
            outputStream = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            inputStream = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            ClientMessage inputClass;
            while (connected) {
                //           System.out.println("Client" + this +" is waiting...");
                inputClass = (ClientMessage) inputStream.readObject();
                ClientMessage finalInputClass = inputClass;
                //           System.out.println("Reading" + inputClass);
                try {
                    if (finalInputClass instanceof ClientResponse) {
                        timeoutHandler.tryDisengage(finalInputClass.getTimeoutID());
                        executors.submit(() -> finalInputClass.read(this)).get();
                        timeoutHandler.defuse(finalInputClass.getTimeoutID());
                    } else {
                        executors.submit(() -> finalInputClass.read(this));
                    }
                } catch (RequestTimedOutException e) {
                    server.requestTimedOut(this);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("->Virtual Client is being shut down<- (message received) " + this);
                    e.printStackTrace();
                }
            }
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            connected = false;
            close();
            server.disconnectClient(this, game, game.getNicknameByClient(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startHeartBeat() {
        send(new ServerKeepAlive());
    }


    public Integer getID() {
        return clientID;
    }

    public Server getServer() {
        return server;
    }

    public Game getGame() {
        return game;
    }

    public VirtualClientCommandDispatcher getCommandDispatcher() {
        return clientCommandDispatcher;
    }

    /**
     * Set instance of Game where this player is playing
     *
     * @param game instance of Game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    public void close() {
        heartBeatService.cancel(true);
        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }
}
