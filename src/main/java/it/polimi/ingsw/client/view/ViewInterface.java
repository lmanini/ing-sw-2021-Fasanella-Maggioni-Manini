package it.polimi.ingsw.client.view;

import it.polimi.ingsw.model.CardLeader;
import it.polimi.ingsw.model.Marble;
import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;
import java.util.HashMap;

public interface ViewInterface {
    //DISPLAY

    /*
    Display Welcome Message
     */
    void displayWelcome();

    /*
    Display Match Starting
     */
    void displayStartingGame();

    /*
    Display Message
     */
    void displayMessage(String message);


    /*
    Display Position in FaithTrail
     */
    void displayPosition();

    void displayTimeOut();

    /*
    Display Market
     */
    void displayMarket();

    /*
    Display StrongBox
     */
    void displayStrongBox();

    /*
    Display that the Player is not active
     */
    void displayNotActivePlayerError();

    /*
    Display that there are not enough resources to continue action
     */
    void displayNotEnoughResource();

    /*
    Display Disconnection
     */
    void displayDisconnection();

    /*
    Display Win Message
     */
    void displayWin();

    /*
    Display Lost Message
     */
    void displayLost();

    /*
    Display successful action
     */
    void displaySuccess();

    /*
    Display requirements not met
     */
    void displayLeaderRequirementsNotMet();

    //ASK

       //REQUEST

    /*
    Ask for NickName
     */
    String askNickName();

    int askPlayerNumber();

    void askMarketChoice();

    void askDevelopmentCardChoice();

    void askProductionActivation();

     void askCardLeaderActivation();

     void askEndTurn();

       //RESPONSE

     ArrayList<Marble> askForResourceSelection(ArrayList<Marble> marbles);

     ArrayList<Resource> askForInitialResourcesSelection(int playerNumber);

     ArrayList<CardLeader> askForLeaderCardSelection(ArrayList<CardLeader> cardLeaders);

    HashMap<Resource,Integer> askForResourceToDiscard(HashMap<Resource,Integer> choice);
}
