package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ConnectionInfo;
import it.polimi.ingsw.client.LightFaithTrail;
import it.polimi.ingsw.client.LightModel;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.communication.client.ClientMessage;
import it.polimi.ingsw.communication.client.requests.RequestActivateProduction;
import it.polimi.ingsw.communication.server.requests.GamePhase;
import it.polimi.ingsw.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

public class GUI extends Application implements ViewInterface {

    public static Semaphore semaphoreRequest = new Semaphore(0);
    private static Client client;
    private static LightFaithTrail lightFaithTrail;
    private final Utils utils = new Utils();
    public static Stage primaryStage;
    public static FXMLLoader fxmlLoader;
    public static Scene scene;
    private static ConnectionInfo connectionInfo;
    private static int playerNumber;
    public static ArrayList<CardLeader> cardLeaderList;
    public static ArrayList<Resource> resourceList;
    public static HashMap<Resource, Integer> discardList;

    public static void setPlayerNumber(int i) {
        playerNumber=i;
    }

    public static void sendMessage(ClientMessage clientMessage) {
        client.send(clientMessage);
    }

    public void setClient(Client client) {
        GUI.client = client;
        GUI.lightFaithTrail = new LightFaithTrail(client);
    }

    public static void setConnectionInfo(ConnectionInfo connectionInfo) {
        GUI.connectionInfo = connectionInfo;
    }

    private Stage Scene(String fxmlPath) {
        setupStage(fxmlPath);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        primaryStage=stage;
        return stage;
    }

    private void mainScene(String fxmlPath) {
        Platform.runLater(() -> {
            setupStage(fxmlPath);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        });
    }

    /**
     * Sets up the Stage
     * @param fxmlPath of FXML
     */
    private void setupStage(String fxmlPath) {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(fxmlPath));
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            scene = new Scene(new Label("Error during FXML Loading"));
        }
        ((StandardStage) fxmlLoader.getController()).init();
    }


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = Scene("/fxml/Logo.fxml");
        primaryStage.showAndWait();

    }

    @Override
    public LightModel getLightModel() {
        return client.getLightModel();
    }

    @Override
    public LightFaithTrail getLightFaithTrail() {
        return lightFaithTrail;
    }




    @Override
    public void displayStartingGame() {

    }

    @Override
    public void displayMessage(String message) {
        Platform.runLater(()->{
            StandardStage stage  = fxmlLoader.getController();
            stage.setDialogPane(message, PlayerBoardController.dialog);
        });
    }

    @Override
    public void displayPosition() {
        Platform.runLater(() -> mainScene("/fxml/FaithTrail.fxml"));
    }

    @Override
    public void displayTimeOut() {
    }

    @Override
    public void displayResourceMarket() {

        //Loading Scene
        mainScene("/fxml/ResourceMarket.fxml");

Platform.runLater(()->{
    ResourceMarketController resourceMarketController = fxmlLoader.getController();
    resourceMarketController.setResourceMarket(getLightModel().getMarket());
});


    }

    @Override
    public void displayStrongBox() {

    }

    @Override
    public void displayNotActivePlayerError() {
        displayMessage("Action not executed");
        displayMessage("Wait your turn to play!");
    }

    @Override
    public void displayNotEnoughResource() {

    }

    @Override
    public void displayDisconnection() {
        mainScene("/fxml/CardDevelopmentSelection.fxml");
    }

    @Override
    public void displayConnection() {
        primaryStage = Scene("/fxml/Logo.fxml");
    }

    @Override
    public void displayWin() {
        displayMessage("You win the game!");
    }

    @Override
    public void displayLost() {
        displayMessage("You lost the game :(");
    }

    @Override
    public void displaySuccess() {
        System.out.println("Action executed successfully");
        displayMessage("Action executed successfully");
    }

    @Override
    public void displayLeaderRequirementsNotMet() {
        displayMessage("You can't activate this card, you don't meet the requirements");
    }

    @Override
    public void displayTurn(String currentPlayer, GamePhase gamePhase) {
        StandardStage stage = fxmlLoader.getController();
        String messages = stage.getDialogPane();
        mainScene("/fxml/PlayerBoard.fxml");
        Platform.runLater(() -> {
            PlayerBoardController playerBoardController = fxmlLoader.getController();
            playerBoardController.setModels(getLightModel(), getLightFaithTrail(), gamePhase);
            displayMessage(messages);
            if(currentPlayer.equals(getLightModel().getNickname())){
                if(gamePhase == GamePhase.Final){
                    playerBoardController.setEndPhase();
                    displayMessage("Make your final action or press End Turn");
                } else {
                    displayMessage("It's your turn to play!");
                }
            } else{
                displayMessage("It's " + currentPlayer + "'s turn!");
            }
        });
    }

    @Override
    public void displayWaitingOpponent(String currentPlayer) {

    }

    @Override
    public void displayDeposit() {

    }

    @Override
    public void displayCardLeader() {

    }

    @Override
    public void displayCardDevelopment() {

    }

    @Override
    public String askNickName() {
        return null;
    }

    @Override
    public int askPlayerNumber() {

        Platform.runLater(()->{
            LogInController logInController= fxmlLoader.getController();
            logInController.askPlayerNumber();

        });
        try {
            GUI.semaphoreRequest.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

      return   playerNumber;
    }

    @Override
    public void askMarketChoice() {

    }

    @Override
    public void askDevelopmentCardChoice() {

    }

    @Override
    public void askProductionActivation() {
        Stage stage = Scene("/fxml/Production.fxml");
        ProductionController productionController = fxmlLoader.getController();
        productionController.setProduction(getLightModel().getCardsDevelopment(), getLightModel().getCardsLeader());
        stage.showAndWait();

        //Sends Request to Client
        client.send(new RequestActivateProduction(productionController.getProductionSelection()));
    }

    @Override
    public void askCardLeaderActivation() {

    }

    @Override
    public void askEndTurn() {

    }

    @Override
    public ArrayList<Resource> askForInitialResourcesSelection(int playerNumber) {
        Platform.runLater(()->{
            InitialSelectionController initialSelectionController = fxmlLoader.getController();
            initialSelectionController.setPlayerNumber(playerNumber);
        });

        try {
            semaphoreRequest.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        utils.fixResourceSelection(resourceList);
        return resourceList;
    }

    @Override
    public ArrayList<CardLeader> askForLeaderCardSelection(ArrayList<CardLeader> cardLeaders) {
        mainScene("/fxml/InitialSelection.fxml");
        Platform.runLater(()->{
            InitialSelectionController initialSelectionController = fxmlLoader.getController();
            initialSelectionController.setCardLeaderDeck(cardLeaders);
        });

        try {
            semaphoreRequest.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return cardLeaderList ;

    }

    @Override
    public HashMap<Resource, Integer> askForResourceToDiscard(HashMap<Resource, Integer> choice) {
        Platform.runLater(()->{
            PlayerBoardController playerBoardController=fxmlLoader.getController();
            playerBoardController.setDiscardRequest(choice);
        });

        if (discardList==null){
            discardList=new HashMap<>();
        }
        else {
            discardList.clear();
        }

        try {
            semaphoreRequest.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return discardList;
    }

    @Override
    public void displayWaiting(int timeoutInSeconds) {

    }

    @Override
    public void askCardLeaderDiscard() {

    }

    @Override
    public void displayCardDevelopmentMarket() {
    }

    @Override
    public void displayStartingEndGame(String payload) {

    }

    @Override
    public void displayScoreBoard(HashMap<String, Integer> showScoreBoard) {

    }

    @Override
    public void displayMainMoveAlreadyMade() {

    }

    @Override
    public void displayConnectionError() {

    }

    @Override
    public void displayTimeoutError() {

    }

    @Override
    public void displayLorenzoActivation(ActionCardEnum actionCardType) {

    }

    @Override
    public void displayInvalidPlacementSelection() {

    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    @Override
    public void displayNickNameUnavailable() {
        LogInController logInController=fxmlLoader.getController();
        Platform.runLater(()-> logInController.status_label.setText("STATUS: NickName Unavailable"));
    }

    @Override
    public void displayServerUnreachable() {
        LogInController logInController=fxmlLoader.getController();
        Platform.runLater(()-> logInController.status_label.setText("STATUS: Server Unreachable"));
    }

    @Override
    public void gameHasStarted() {
        LogInController logInController=fxmlLoader.getController();
        Platform.runLater(()-> logInController.status_label.setText("STATUS: Game Started, hold on..."));
    }

    @Override
    public void displayClientAccepted() {
        System.out.println("Connected to server");
        LogInController logInController=fxmlLoader.getController();
        Platform.runLater(()-> logInController.status_label.setText("STATUS: Server connected, waiting players"));
    }

    @Override
    public void notifyDisconnectionOf(String nickname) {

    }

    @Override
    public void notifyReconnection(String nickname) {

    }

    @Override
    public void displayMainMoveNotMade() {

    }

}
