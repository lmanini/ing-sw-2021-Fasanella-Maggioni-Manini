package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.CardLeaderType;
import it.polimi.ingsw.model.enums.Resource;
import it.polimi.ingsw.model.exceptions.CardLeaderRequirementsNotMetException;
import it.polimi.ingsw.model.exceptions.CardLeaderWrongOwnerException;

public class CardLeaderWhiteMarble extends CardLeader {

    /**
     * Constructor of the class, this should only be instanced by CardLeaderFactory
     * @param resource Resource type of card
     * @param requirements to activate the card
     * @param victoryPoints worth
     */
    public CardLeaderWhiteMarble(Resource resource, CardLeaderRequirements requirements, Integer victoryPoints){
        this.resource = resource;
        this.requirements = requirements;
        this.victoryPoints = victoryPoints;
        active = false;
    }

    /**
     * set playerboard's white marble conversion. activate() should be called only if canActivate() == true
     */
    @Override
    public void activate(PlayerBoard playerBoard) {
        if(!playerBoard.getNickname().equals(playerName)) throw new CardLeaderWrongOwnerException();
        if(!canActivate(playerBoard) && !active) throw new CardLeaderRequirementsNotMetException();
        playerBoard.setWhiteEffect(resource);
        active = true;
    }

    @Override
    public CardLeaderType getDescription() {
        return CardLeaderType.WhiteMarble;
    }
}
