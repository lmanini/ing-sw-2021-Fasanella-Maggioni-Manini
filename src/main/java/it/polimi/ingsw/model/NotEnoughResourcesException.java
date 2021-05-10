package it.polimi.ingsw.model;

public class NotEnoughResourcesException extends Throwable {

    public NotEnoughResourcesException(String playerNickname) {
        super("Player " + playerNickname + " does not have enough resources to execute the selected" +
                " operation");
    }

}
