package it.polimi.ingsw.communication.client.requests;

import it.polimi.ingsw.communication.client.ClientRequest;
import it.polimi.ingsw.server.VirtualClient;

public class RequestEndTurn extends ClientRequest {

    public RequestEndTurn() {
        super(null, null);
    }

    @Override
    public void read(VirtualClient virtualClient) {
        virtualClient.getCommandDispatcher().requestEndTurn();
    }
}
