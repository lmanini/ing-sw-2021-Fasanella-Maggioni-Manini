package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.Resource;
import it.polimi.ingsw.model.exceptions.InvalidCardDevelopmentPlacementException;
import it.polimi.ingsw.model.exceptions.InvalidSlotIndexException;
import it.polimi.ingsw.model.exceptions.NotEnoughResourcesException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ActionControllerTest {

    @Test
    void buyAndPlaceDevCard() {


        GameTable gameTable = new GameTable(true);
        String nickname = "test";

        ActionController actionController = new ActionController(gameTable);

        gameTable.addPlayer(nickname);
        gameTable.startGame();

        PlayerBoard player = gameTable.getActivePlayer();

        HashMap<Resource, Integer> temp = new HashMap<>();

        temp.put(Resource.Coins, 0);
        temp.put(Resource.Stones, 0);
        temp.put(Resource.Shields, 10);
        temp.put(Resource.Servants, 2);

        player.getStrongboxInstance().tryAdd(temp);

        assertThrows(InvalidCardDevelopmentPlacementException.class, () -> actionController.buyAndPlaceDevCard(player, 1, 0, 1));

        assertThrows(InvalidSlotIndexException.class, () -> actionController.buyAndPlaceDevCard(player, 0, 0, 5));

        assertThrows(InvalidSlotIndexException.class, () -> actionController.buyAndPlaceDevCard(player, 0, 0, -1));

        assertDoesNotThrow(() -> actionController.buyAndPlaceDevCard(player, 0, 0, 0));

        assertThrows(NotEnoughResourcesException.class, () -> actionController.buyAndPlaceDevCard(player, 1, 1, 0));
    }

    @Test
    void useMarket() {

        GameTable gameTable = new GameTable(true);
        String nickname = "test1";

        ActionController actionController = new ActionController(gameTable);

        gameTable.addPlayer(nickname);
        gameTable.startGame();

        PlayerBoard player = gameTable.getActivePlayer();

        assertThrows(Exception.class, () -> actionController.useMarket(player, 5, "row"));

        assertThrows(Exception.class, () -> actionController.useMarket(player, 5, "column"));

        assertThrows(Exception.class, () -> actionController.useMarket(player, 1, "invalidArg"));

        //assertTrue(actionController.useMarket(player, 2, "row"));

        HashMap<Resource, Integer> temp;
        HashMap<Resource, Integer> discardSelection = new HashMap<>();

        for (int i = 0; i < 5; i++) {

            actionController.useMarket(player, 1, "row");
            temp = actionController.useMarket(player, 1, "column");

            System.out.println(temp);

            if (temp != null) {

                do {
                    for (Resource res : Resource.values()) {

                        discardSelection.put(res, new Random().nextInt(4));
                    }
                    System.out.println("Trying to discard : " + discardSelection);

                } while (actionController.discardResources(player, discardSelection) != null);
            }
        }

        System.out.println("debug");
    }
}