package it.polimi.ingsw.controller;

import com.sun.net.httpserver.Authenticator;
import it.polimi.ingsw.client.RequestTimeoutException;
import it.polimi.ingsw.communication.server.*;
import it.polimi.ingsw.controller.exceptions.NotActivePlayerException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.VirtualClient;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is responsible for instantiating the controller and model classes, receiving and dispatching requests
 * from the clients and sending requests and responses to them.
 *
 * This class maintains the correlations between VirtualClient(s) and nickname(s), in order to be able to expose methods
 * parametric in VirtualClient, find the corresponding nickname and call Controller's methods, which are parametric
 * in String.
 */

public class Game implements Runnable{

    private ArrayList<VirtualClient> players;
    private GameTable gameTable;
    private Controller controller;
    private HashMap<String, VirtualClient> nicknameClientMap;
    private HashMap<VirtualClient, String> clientNicknameMap;
    private HashMap<Integer, VirtualClient> idPlayerClientMap;

    public Game(){
        nicknameClientMap = new HashMap<>();
        clientNicknameMap = new HashMap<>();
        idPlayerClientMap = new HashMap<>();
    }

    public void distributeInitialSelection(VirtualClient _vClient, ArrayList<CardLeader> _cardLeader, Resource _resource1, Resource _resource2)  {

        String nickname = clientNicknameMap.get(_vClient);

        try {
            controller.assignInitialBenefits(nickname, _cardLeader, _resource1, _resource2);
            send(_vClient, new ResponseSuccess());
        } catch (NotActivePlayerException ex) {
            send(_vClient, new ResponseNotActivePlayerError());
        }

    }

    @Override
    public void run() {
        System.out.println("Game partito");
        start();
        solicitInitialSelections();

        //System.out.println("debug");
    }

    /**
     * get player's virtual client
     * @param index index of the player, starting by 1
     * @return player's virtual client
     */
    public VirtualClient getClientByIndex(Integer index){
        return players.get(index + 1);
    }

    public void addAllPlayers(ArrayList<VirtualClient> virtualClients, ArrayList<String> playersNicknames){
        this.players = new ArrayList<>(virtualClients);
        for (int i = 0; i < virtualClients.size(); i++) {
            VirtualClient virtualClient = virtualClients.get(i);
            virtualClient.setGame(this);
            idPlayerClientMap.put(virtualClient.getID(), virtualClient);
            nicknameClientMap.put(playersNicknames.get(i), virtualClient);
            clientNicknameMap.put(virtualClient, playersNicknames.get(i));
        }
    }

    private void start() {

        // Initialized model
        gameTable = new GameTable(players.size() == 1);

        // Populate match
        for (String nickname : nicknameClientMap.keySet()) {
            gameTable.addPlayer(nickname);
        }

        // Initialize controller
        controller = new Controller(gameTable);

        // Distribute initial cards
        gameTable.startGame();
    }

    private void solicitInitialSelections() {

        for (VirtualClient vClient : players) {

            send(vClient, new RequestInitialSelection(
                    controller.getPlayerBoardByNickname(clientNicknameMap.get(vClient)).getCardsLeaderBeforeSelecting(),
                    gameTable.getIndexFromPlayer(controller.getPlayerBoardByNickname(clientNicknameMap.get(vClient)))
            ));
        }
    }

    // Overloaded send method
    public void send(VirtualClient virtualClient, ServerMessage serverMessage){
        virtualClient.send(serverMessage);
    }

    public void send(String nickname, ServerMessage serverMessage){
        nicknameClientMap.get(nickname).send(serverMessage);
    }

    public void send(Integer playerID, ServerMessage serverMessage){
        idPlayerClientMap.get(playerID).send(serverMessage);
    }

    // Overloaded sendAndWait method
    public void sendAndWait(VirtualClient virtualClient, ServerMessage serverMessage, Integer timeoutInSeconds) throws RequestTimeoutException {
        virtualClient.sendAndWait(serverMessage, timeoutInSeconds);
    }

    public void sendAndWait(String nickname, ServerMessage serverMessage, Integer timeoutInSeconds) throws RequestTimeoutException{
        nicknameClientMap.get(nickname).sendAndWait(serverMessage, timeoutInSeconds);
    }

    public void sendAndWait(Integer playerID, ServerMessage serverMessage, Integer timeoutInSeconds) throws RequestTimeoutException{
        idPlayerClientMap.get(playerID).sendAndWait(serverMessage, timeoutInSeconds);
    }

    public void sendAll(ServerMessage serverMessage){
        for (VirtualClient player :
                players) {
            player.send(serverMessage);
        }
    }
}
