package it.polimi.ingsw.model;

import jdk.jshell.spi.ExecutionControl;

public class Lorenzo {
    private final ActionCardDeck actionCardDeck;
    private final GameTable gameTable;

    /**
     * Initializes Lorenzo and the ActionCardDeck
     * @param gameTable caller of the constructor
     */
    Lorenzo(GameTable gameTable){
        this.gameTable = gameTable;
        actionCardDeck = new ActionCardDeck(gameTable);
    }

    /**
     * move Lorenzo forward in the faith trail
     * @param i number of moves
     *          TODO
     */
    public void advanceFaith(int i) {
        try {
            throw new ExecutionControl.NotImplementedException("Lorenzo advance faith is not implemented yet");
        } catch (ExecutionControl.NotImplementedException e) {
            e.printStackTrace();
        }
    }

    public ActionCardDeck getActionCardDeck() {
        return actionCardDeck;
    }
}
