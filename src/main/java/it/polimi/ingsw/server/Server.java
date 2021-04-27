package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.*;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private SocketServer socketServer;
    private HashMap<VirtualClient, Integer> virtualClientIDMap;
    private HashMap<VirtualClient, Game> gameMap;
    private final WaitingLobby lobby;
    private Integer currentLobbySize;
    private Game currentGame;

    public Server(){
        currentGame = new Game();
        currentLobbySize = null;
        lobby = new WaitingLobby();
    }

    public Server(boolean SinglePlayer){
        //si instanzia sempre lato client
    }

    synchronized void registerClient(Socket clientSocket, VirtualClient virtualClient, Integer clientID){
        virtualClientIDMap.put(virtualClient, clientID);
        if(lobby.isEmpty())
            lobby.addFirstPlayer(virtualClient);
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Server server=new Server();
        Integer port = 6666;
        server.socketServer = new SocketServer(port, server);
        executor.submit(server.socketServer);
    }

    void setCurrentLobbySize(Integer currentLobbySize) {
        this.currentLobbySize = currentLobbySize;
    }
}