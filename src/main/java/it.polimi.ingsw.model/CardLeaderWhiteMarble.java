package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.Map;

public class CardLeaderWhiteMarble extends CardLeader {

    public CardLeaderWhiteMarble(Resource resource, CardLeaderRequirements requirements, Integer victoryPoints){
        this.resource = resource;
        this.requirements = requirements;
        this.victoryPoints = victoryPoints;
    }

    @Override
    public void activate() {

    }
}