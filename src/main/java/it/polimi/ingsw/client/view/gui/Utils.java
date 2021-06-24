package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;
import java.util.HashMap;

public class Utils {


    /**
     * Fixes the Resource selection for being elaborated from controller
     */
    public void fixResourceSelection(ArrayList<Resource> resourceList){
       if(resourceList.size()==0){
           resourceList.add(null);
           resourceList.add(null);
       }
       else if(resourceList.size()==1){
           resourceList.add(null);
       }
    }


    public Boolean[] initializeDepositLevel(Boolean[] depositLevel) {
        depositLevel =new Boolean[3];
        depositLevel[0]=false;
        depositLevel[1]=false;
        depositLevel[2]=false;
        return depositLevel;
    }

    public String getResourcePath(Resource resource) {
        String stone = "/images/Resources/stone.png";
        String shield = "/images/Resources/shield.png";
        String servant = "/images/Resources/servant.png";
        String coin = "/images/Resources/coin.png";
        return switch (resource) {
            case Servants -> servant;
            case Coins -> coin;
            case Shields -> shield;
            case Stones -> stone;
        };
    }

    public int checkWinner(HashMap<String, Integer> showScoreBoard) {
        Integer maxValue = 0;
        for (String name : showScoreBoard.keySet()) {
            if (showScoreBoard.get(name) > maxValue) maxValue = showScoreBoard.get(name);
        }
        return maxValue;
    }
}
