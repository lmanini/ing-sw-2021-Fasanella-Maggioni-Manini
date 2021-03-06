package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.communication.client.requests.RequestMarketUse;
import it.polimi.ingsw.model.enums.MarbleType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;


public class ResourceMarketController extends StandardStage {


    private String key = "row";
    private Integer message = 2;
    private boolean viewOnly=false;

    @FXML
    GridPane ResourceMarket_grid;

    public void setCol1(ActionEvent actionEvent) {
        key = "column";
        message = 2;
    }

    public void setCol0(ActionEvent actionEvent) {
        key = "column";
        message = 1;

    }

    public void setRow2(ActionEvent actionEvent) {
        key = "row";
        message = 3;
    }

    public void setCol2(ActionEvent actionEvent) {
        key = "column";
        message = 3;
    }

    public void ResourceMarketPurchase(ActionEvent actionEvent) {
        if (!viewOnly) {
            String s="Resources purchased at "+key+" "+message.toString();
            GUI.sendMessage(new RequestMarketUse(message, key));
            GUI.displayMessage(s);
        }
        else {
            GUI.displayMessage("You cant buy resources, you are in view only mode");
        }
        closeStage(actionEvent);
    }

    public void setCol3(ActionEvent actionEvent) {
        key = "column";
        message = 4;
    }

    public void setRow1(ActionEvent actionEvent) {
        key = "row";
        message = 2;
    }

    public void setRow0(ActionEvent actionEvent) {
        key = "row";
        message = 1;
    }




    /**
     * Sets the Resource Market to GridPane
     * @param resourceMarket Matrix of MarbleTypes that define the Market at a given time.
     */
    public void setResourceMarket(ArrayList<ArrayList<MarbleType>> resourceMarket) {
        ImageView[][] resourceMatrix = new ImageView[3][4];
        int nRow = 3;
        for (int i = 0; i < nRow; i++) {
            int nCol = 4;
            for (int j = 0; j < nCol; j++) {
                String color = switch (resourceMarket.get(i).get(j)) {
                    case MarbleGrey -> "grey";
                    case MarbleBlue -> "blue";
                    case MarblePurple -> "purple";
                    case MarbleRed -> "red";
                    case MarbleYellow -> "yellow";
                    case MarbleWhite -> "white";
                };


                String path = "/images/Marbles/Marble_" + color + ".png";

                setImageToMatrix(i,j, resourceMatrix,path,50,50);

                //Adding to GridPane
                ResourceMarket_grid.add(resourceMatrix[i][j], j, i);

            }

        }
    }

    public void setViewOnly() {
        viewOnly=true;
    }
}
