package it.polimi.ingsw.model;


import java.util.ArrayList;
import java.util.HashMap;

import it.polimi.ingsw.model.enums.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class DepositTest {

    @Test
    public void tryAdd() {

        ArrayList<String> nicknames = new ArrayList<>();
        nicknames.add("test");
        GameTable table = new GameTable(nicknames);
        PlayerBoard player = table.getPlayerByIndex(0);

        // Map which can be added once, if it is added another time deposit's invariant is not valid
        HashMap<Resource, Integer> map = new HashMap<>();
        map.put(Resource.Coins, 0);
        map.put(Resource.Servants, 1);
        map.put(Resource.Shields, 2);
        map.put(Resource.Stones, 1);

        // Empty map for edge case
        HashMap<Resource, Integer> emptyMap = new HashMap<>();
        emptyMap.put(Resource.Coins, 0);
        emptyMap.put(Resource.Servants, 0);
        emptyMap.put(Resource.Shields, 0);
        emptyMap.put(Resource.Stones, 0);

        assertTrue(player.getDepositInstance().tryAdd(map));
        assertFalse(player.getDepositInstance().tryAdd(map));
        assertTrue(player.getDepositInstance().tryAdd(emptyMap));


    }
}