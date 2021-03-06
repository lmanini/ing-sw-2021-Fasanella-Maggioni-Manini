package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.communication.client.requests.*;
import it.polimi.ingsw.communication.server.requests.GamePhase;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.ActionCardEnum;
import it.polimi.ingsw.model.enums.Resource;
import it.polimi.ingsw.model.enums.MarbleType;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Command Line implementation of ViewInterface
 */
public class CLI implements ViewInterface {

    // Attributes
    private final Client client;
    private static final PrintWriter out = new PrintWriter(System.out, true);
    private static final Scanner in = new Scanner(System.in);
    private final LightFaithTrail lightFaithTrail;
    private final Utils utils;
    private final ParsingCommand parsingCommand;
    private boolean open = false;

    /**
     * Constructor of CLI.
     *
     * @param client Client instance that instantiated this CLI instance.
     * @param debug  boolean to set CLI in debug-mode.
     */
    public CLI(Client client, Boolean debug) {
        this.client = client;
        this.lightFaithTrail = new LightFaithTrail(client);
        this.utils = new Utils(out, in);
        this.parsingCommand = new ParsingCommand(utils, this, out, in, debug);
    }

    public LightModel getLightModel() {
        return client.getLightModel();
    }

    @Override
    public LightFaithTrail getLightFaithTrail() {
        return lightFaithTrail;
    }

    public ConnectionInfo displayWelcome() {
        utils.setColoredCLI();
        utils.printWelcomeMessage();
        ConnectionInfo connectionInfo = new ConnectionInfo();
        boolean invalid = true;
        while (invalid) {
            try {
                connectionInfo.setNickname(askNickName());
                invalid = false;
            } catch (IllegalNicknameException e) {
                out.println(e.getMessage());
            }
        }
        invalid = true;
        while (invalid) {
            try {
                connectionInfo.setAddress(askIP());
                invalid = false;
            } catch (IllegalAddressException e) {
                out.println(e.getMessage());
            }
        }
        invalid = true;
        while (invalid) {
            try {
                connectionInfo.setPort(askPortNumber());
                invalid = false;
            } catch (IllegalPortException e) {
                out.println(e.getMessage());
            }
        }
        return connectionInfo;
    }

    private int askPortNumber() {
        out.println("Port Number (default 51214):");
        return utils.readNumberWithBounds(1024, 65535);
    }

    private String askIP() {
        out.println("IP:");
        return utils.readString();
    }

    @Override
    public void displayStartingGame() {
        out.println("Game is starting!!!");
    }

    @Override
    public void displayPosition() {
        String nickname = getLightModel().getNickname();
        utils.printFaithTrail(nickname, lightFaithTrail.getFaithTrail());
    }

    @Override
    public void displayTimeOut() {
        out.println("Timeout error! Your connection to server may has been lost");
    }

    @Override
    public void displayResourceMarket() {
        ArrayList<ArrayList<MarbleType>> marketClone = getLightModel().getMarket();
        utils.printMarket(marketClone, getLightModel().getSpareMarble());
    }

    @Override
    public void displayStrongBox() {
        out.println("---StrongBox---");
        HashMap<Resource, Integer> strongboxClone = getLightModel().getStrongbox();
        utils.printListResource(strongboxClone);
    }

    @Override
    public void displayNotActivePlayerError() {
        out.println("Ops, you are no more active!");
    }

    @Override
    public void displayNotEnoughResource() {
        out.println("There are not enough resources to perform this action.");
    }

    @Override
    public void displayDisconnection() {
        out.println("Oops, connection to server was lost!");
    }

    @Override
    public void displayConnection() {}

    @Override
    public void displayWin() {
        utils.printWinnerMessage();
    }

    @Override
    public void displayLost() {
        utils.printLoserMessage();
    }

    @Override
    public void displaySuccess() {
        out.println("Action executed successfully! ");
    }

    @Override
    public void displayLeaderRequirementsNotMet() {
        out.println("Ops, requirements for this Card Leader are not met! ");
    }

    @Override
    public void displayTurn(String currentPlayer, GamePhase gamePhase) {
        //utils.clearScreen();
        if (currentPlayer.equals(getLightModel().getNickname())) {
            if (open) {
                System.out.println("There is a console opened already! Exploding...");
                throw new RuntimeException("There is a console opened already!");
            }
            open = true;
            parsingCommand.PlayerMenu(gamePhase);
            open = false;
        } else {
            displayWaitingOpponent(currentPlayer);
            out.println();
            out.println();
            displayPosition();
            out.println();
            out.println();
            displayResourceMarket();
        }
    }

    @Override
    public void displayWaitingOpponent(String currentPlayer) {
        out.println("Wait for your move, " + currentPlayer + " is now playing...");
    }

    @Override
    public void displayDeposit() {
        out.println("---Deposit---");
        HashMap<Resource, Integer> cloneDeposit = getLightModel().getDeposit();
        utils.printListResource(cloneDeposit);

        ArrayList<Resource> depositLeaderResources = getLightModel().getDepositLeaderResources();

        if (depositLeaderResources.size() > 0) {

            out.println("---Leader Deposit---");
            utils.printLeaderDeposit(depositLeaderResources, getLightModel().getDepositLeaderContent());
        }
    }

    @Override
    public void displayCardLeader() {
        out.println("---Card Leader---");
        utils.printCardLeaderDeck(getLightModel().getCardsLeader());
    }

    @Override
    public void displayCardDevelopment() {
        out.println("---Card Development---");
        utils.printCardDevelopmentDeck(getLightModel().getCardsDevelopment());
    }

    public String askNickName() {
        //utils.setColoredCLI();
        //Welcome Message
        //displayWelcome();
        //Reads the nickname
        String input;
        out.println("NickName:");
        input = utils.readString();
        client.setNickname(input);
        return input;
    }

    @Override
    public int askPlayerNumber() {
        out.println("How many players?");
        return utils.readNumberWithBounds(1, 4);
    }

    @Override
    public HashMap<Resource, Integer> askForResourceToDiscard(HashMap<Resource, Integer> choice) {
        HashMap<Resource, Integer> temp = new HashMap<>(choice);
        HashMap<Resource, Integer> selection = new HashMap<>();
        boolean loop = true;

        //Read resources
        out.println("Ops, there's not enough space:");
        out.println("(Remember that you have to discard resources all in one move starting from now, and if selection is incorrect you will have to discard again)");
        out.println();
        displayDeposit();
        out.println();
        out.println("Here's a list of available resources to discard:");
        utils.printListResource(choice);
        do {
            Resource resource = utils.readResource(false);
            if (temp.get(resource) > 0) {
                int i = temp.get(resource);
                if (!selection.containsKey(resource)) {
                    selection.put(resource, 1);
                } else {
                    int r = selection.get(resource);
                    selection.replace(resource, r + 1);
                }
                temp.replace(resource, i - 1);
                if (utils.checkEmptyResourceMap(temp)) {
                    break;
                }
                out.println("Discard another resource?");
                loop = utils.readYesOrNo(false);
            } else {
                out.println("You can't discard this resource, try with another.");
                out.println("Here's a list of available resources to discard:");
                utils.printListResource(temp);
            }
        } while (loop);

        return selection;
    }

    @Override
    public void displayWaiting(int timeoutInSeconds) {
        try {
            utils.printWaitingMessage(timeoutInSeconds);
        } catch (InterruptedException | IOException e) {
            utils.printErrorMessage();
            e.printStackTrace();
        }
    }

    @Override
    public void askCardLeaderDiscard() {
        out.println("Choose a card leader to discard:");
        try {

            int leaderIndex = utils.printAndGetCardLeaderIndex(getLightModel().getCardsLeader());

            if (leaderIndex >= 0) {
                client.sendAndWait(new RequestDiscardCardLeader(leaderIndex), -1);
            }

        } catch (RequestTimedOutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayCardDevelopmentMarket() {
        utils.printDevelopmentCardMarket(getLightModel().getCardDevelopmentMarket());
    }

    @Override
    public void displayStartingEndGame(String NickName) {
        out.println(NickName + " has activated endgame!");
    }

    @Override
    public void displayScoreBoard(HashMap<String, Integer> showScoreBoard) {
        int maxPoints = utils.checkWinner(showScoreBoard);
        String nickName = getLightModel().getNickname();
        if (showScoreBoard.get(nickName) == maxPoints) {
            displayWin();
        } else {
            displayLost();
        }
        utils.printScoreBoard(showScoreBoard, nickName);
    }

    @Override
    public void displayMainMoveAlreadyMade() {
        out.println("You can't make more than one main move in a single turn!\n" +
                "Main moves are : \"buy resource\", \"buy card development\" and \"production\"");
    }

    @Override
    public void displayConnectionError() {
        System.out.println("Server is unreachable!");
    }

    @Override
    public void displayTimeoutError() {

    }

    @Override
    public void displayInvalidPlacementSelection() {

        System.out.println("The placement index you have selected would not allow for a legal card placement!" +
                "\nEither you selected to place the card in a full slot or the placement would not follow the game rules.");

    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return displayWelcome();
    }

    @Override
    public void displayNickNameUnavailable() {
        System.out.println("NickName unavailable, please try again...");
    }

    @Override
    public void displayServerUnreachable() {
    }

    @Override
    public void gameHasStarted() {

    }

    @Override
    public void displayClientAccepted() {
        System.out.println("Connected to server");
    }

    @Override
    public void notifyDisconnectionOf(String nickname) {
        out.println("Oops, connection to server was lost!");
    }

    @Override
    public void notifyReconnection(String nickname) {

    }

    @Override
    public void displayMainMoveNotMade() {
        out.println("You have not made a main move in this turn! You can't pass the turn yet.");
        displayTurn(client.getNickname(), GamePhase.Final);
    }

    @Override
    public void unexpectedMove() {
        System.out.println("A game logic error was encountered, the move has been reverted.");
    }

    @Override
    public void displayLorenzoActivation(ActionCardEnum actionCardType) {
        out.println("A " + actionCardType + " Action Card has been used by Lorenzo!");
    }

    @Override
    public void askMarketChoice() {
        displayResourceMarket();
        int rowcolumn;
        String key;
        int message;
        out.println("Choose row or column (type r/c):");
        rowcolumn = utils.chooseRowOrColumn();
        /*
        rowcolumn=1 -> row
        rowcolumn=0 -> column
         */
        if (rowcolumn == 1) key = "row";
        else key = "column";
        out.println("Now type the " + key + " number:");
        if (rowcolumn == 1) message = utils.readNumberWithBounds(1, 3);
        else message = utils.readNumberWithBounds(1, 4);
        /*
        1 arg: row=1 or col=0
        2 arg: number of row/column
         */
        client.send(new RequestMarketUse(message, key));
    }

    @Override
    public void askDevelopmentCardChoice() {
        String s;
        utils.printDevelopmentCardMarket(getLightModel().getCardDevelopmentMarket());

        //Reads the card to buy
        out.println("Type the number in the round brackets of the corresponding card that you want to buy");
        s = utils.readString();
        char[] array = s.toCharArray();
        while (array.length != 2 || (!Character.isDigit(s.charAt(0)) || !Character.isDigit(s.charAt(1))) || (0 > Integer.parseInt(String.valueOf(array[0])) || Integer.parseInt(String.valueOf(array[0])) > 2) || (0 > Integer.parseInt(String.valueOf(array[1])) || Integer.parseInt(String.valueOf(array[1])) > 3)) {
            out.println("Invalid input. Type only the the two digits in the round brackets!");
            s = utils.readString();
            array = s.toCharArray();
        }
        Integer rowIndex = Integer.parseInt(String.valueOf(array[0]));
        Integer columnIndex = Integer.parseInt(String.valueOf(array[1]));

        out.println("In which slot would you like to place your new card? Possible selection are {0 ,1 ,2}");
        s = utils.readNumberWithBoundsToString(0, 2);

        Integer placementIndex = Integer.parseInt(s);

        client.send(new RequestBuyDevelopmentCard(rowIndex, columnIndex, placementIndex));
    }

    @Override
    public void askProductionActivation() {
        ProductionSelection productionSelection = new ProductionSelection();

        //Ask for basic production
        out.println("Do you want to activate basic production?");
        productionSelection.setBasicProduction(utils.readYesOrNo(false));
        if (productionSelection.getBasicProduction()) {
            productionSelection.setBasicProdInfo(utils.getBasicProduction());
        }

        //Ask for Card Development production
        out.println("Do you want to activate Card Development production?");
        if (utils.readYesOrNo(false)) {
            productionSelection.setCardDevelopmentSlotActive(utils.getCardDevelopmentActivation(getLightModel().getCardsDevelopment()));
        } else {
            Boolean[] falseArray = new Boolean[3];
            falseArray[0] = false;
            falseArray[1] = false;
            falseArray[2] = false;
            productionSelection.setCardDevelopmentSlotActive(falseArray);
        }


        //Ask for Card Leader production and output
        out.println("Do you want to activate Card Leader production?");
        if (utils.readYesOrNo(false)) {
            CardLeader[] cardLeaders;
            cardLeaders = utils.activationCardLeaderForProduction(getLightModel().getCardsLeader());
            productionSelection.setCardLeadersToActivate(cardLeaders);
            Resource[] resourcesOutput;
            resourcesOutput = utils.getCardLeaderOutputs(productionSelection.getCardLeadersToActivate());
            productionSelection.setCardLeaderProdOutputs(resourcesOutput);
        } else {
            CardLeader[] cardLeaders = new CardLeader[2];
            cardLeaders[0] = null;
            cardLeaders[1] = null;
            productionSelection.setCardLeadersToActivate(cardLeaders);
            Resource[] cardLeaderProdOutputs = new Resource[2];
            cardLeaderProdOutputs[0] = null;
            cardLeaderProdOutputs[1] = null;
            productionSelection.setCardLeaderProdOutputs(cardLeaderProdOutputs);
        }

        //Sending request to Server
        client.send(new RequestActivateProduction(productionSelection));
    }

    @Override
    public void askCardLeaderActivation() {
        out.println("Choose which card leader to activate:");
        try {
            CardLeader selection = utils.printAndGetCardLeader(getLightModel().getCardsLeader());

            if (selection != null) {
                client.sendAndWait(new RequestActivateCardLeader(selection), -1);
            } else {
                out.println("You have no leader cards!");
            }

        } catch (RequestTimedOutException e) {
            displayTimeOut();
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Resource> askForInitialResourcesSelection(int playerNumber) {
        ArrayList<Resource> resources = new ArrayList<>();
        switch (playerNumber) {
            case 0 -> {
                out.println("You have no resources to choose because you're the first player!");
                out.println();
                resources.add(null);
                resources.add(null);
            }
            case 1, 2 -> {
                out.println("Choose one resource : ");
                resources.add(utils.readResource(true));
                resources.add(null);
            }
            case 3 -> {
                out.println("Choose two resources : ");
                resources.add(utils.readResource(true));
                resources.add(utils.readResource(false));
            }
        }

        return resources;
    }

    /**
     * Ask to end turn
     */
    @Override
    public void askEndTurn() {
        out.println("Turn is finished, wait for other players...");
        client.send(new RequestEndTurn());
    }

    @Override
    public ArrayList<CardLeader> askForLeaderCardSelection(ArrayList<CardLeader> cardLeaders) {
        return utils.printAndGetCardLeaderFirstSelection(cardLeaders);
    }

    public void colorize() {
        utils.colorize();
    }

    public void checkoutPlayer() {
        String nickname = null;

        if (client.getLightModel().getNumberOfPlayers() == 1) {
            out.println("This command is disabled in single player mode");
            return;
        }

        while (!client.getPlayersNickname().contains(nickname) || client.getNickname().equals(nickname)) {
            out.println("Insert the player's nickname");
            nickname = utils.readString();
            if (client.getNickname().equals(nickname))
                out.println("You can't checkout yourself!");
        }
        BriefModel briefModel = client.getModelByNickname(nickname);
        if (briefModel.isEmpty()) {
            out.println("This player have not played yet, his player board is empty");
            return;
        }
        out.println("Cards development:");
        utils.printCardDevelopmentDeck(briefModel.getCardsDevelopment());
        out.println("Deposit:");
        utils.printListResource(briefModel.getDeposit());
        out.println("Strong box:");
        utils.printListResource(briefModel.getStrongBox());
        out.println("Cards Leader uncovered");
        utils.printCardLeaderDeck(briefModel.getVisibleCardsLeaders());
    }

}
