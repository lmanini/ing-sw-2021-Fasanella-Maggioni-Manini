package it.polimi.ingsw.model.marbles;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.enums.MarbleType;

/**
 * Concrete Marble
 */
public class MarbleWhite extends Marble {

    /**
     * gets White Marble conversion from PlayerBoard and adds one resource of the desired one
     * @param playerBoard player where to activate marble
     */
    @Override
    public void activate (PlayerBoard playerBoard) {
        if(playerBoard.getWhiteEffect() == null)
            return;
       playerBoard.addToTemporaryDeposit(playerBoard.getWhiteEffect());
    }

    /**
     * cloner of the class
     * @return a deep clone of Marble
     */
    @Override
    public Marble clone(){
        return new MarbleWhite();
    }

    @Override
    public MarbleType getType() {
        return MarbleType.MarbleWhite;
    }
}
