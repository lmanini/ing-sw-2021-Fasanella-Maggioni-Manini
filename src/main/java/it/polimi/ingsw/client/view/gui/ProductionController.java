package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.communication.client.requests.RequestActivateProduction;
import it.polimi.ingsw.model.cards.CardDevelopment;
import it.polimi.ingsw.model.CardLeader;
import it.polimi.ingsw.model.ProductionSelection;
import it.polimi.ingsw.model.enums.Resource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class ProductionController extends StandardStage {

    public ProductionSelection productionSelection;
    private ArrayList<CardDevelopment> cardsDevelopment;
    private ArrayList<CardLeader> cardsLeader;
    private final Resource[] resources=new Resource[3];
    private final Boolean[] cardDevelopArray =new Boolean[3];
    private final ColorAdjust monochrome = new ColorAdjust();
    private final ColorAdjust brightness = new ColorAdjust();


    @FXML
    ImageView shield_in;
    @FXML
    ImageView coin_in;
    @FXML
    ImageView stone_in;
    @FXML
    ImageView servant_in;
    @FXML
    ImageView shield_out;
    @FXML
    ImageView coin_out;
    @FXML
    ImageView stone_out;
    @FXML
    ImageView servant_out;


    /**
     * Sets card for Production selection
     * @param cardsDevelopment of player
     * @param cardsLeader of player
     */
    public void setProduction(ArrayList<CardDevelopment> cardsDevelopment, ArrayList<CardLeader> cardsLeader){
        productionSelection=new ProductionSelection();
        this.cardsDevelopment=cardsDevelopment;
        this.cardsLeader=cardsLeader;
        cardDevelopArray[0]=false;
        cardDevelopArray[1]=false;
        cardDevelopArray[2]=false;
        monochrome.setSaturation(-0.5);
        brightness.setBrightness(1);
    }


    public ProductionSelection getProductionSelection(){
        return productionSelection;
    }



    //PRODUCTION BUTTONS

    public void basicProduction(ActionEvent actionEvent) {
        productionSelection.setBasicProduction(true);
        productionSelection.setBasicProdInfo(resources);
    }

    public void cardDevelopProduction(ActionEvent actionEvent) {
        FXMLLoader loader = load("/fxml/CardDevelopmentSelection.fxml");
        Scene secondScene = setScene(loader);
        CardDevelopmentSelectionController cardDevelopmentSelection = loader.getController();
        cardDevelopmentSelection.setProdEnvironment(true);
        cardDevelopmentSelection.setCardDevelopmentSelection(cardsDevelopment);
        showStage(secondScene);

        for (int i = 0; i < cardDevelopArray.length; i++) {
            cardDevelopArray[i] = cardDevelopmentSelection.getPosArray()[i];
        }
    }

    public void cardLeaderProduction(ActionEvent actionEvent) {

        FXMLLoader loader = load("/fxml/CardLeader.fxml");
        Scene secondScene = setScene(loader);

        CardLeaderController cardLeaderController=loader.getController();

        cardLeaderController.setCardLeaderDeck(cardsLeader);
        cardLeaderController.setProduction(true);

        // New window (Selection)

        showStage(secondScene);

        productionSelection.setCardLeadersToActivate(cardLeaderController.getCardLeaders());
        productionSelection.setCardLeaderProdOutputs(cardLeaderController.getResources());


    }

    public void production(ActionEvent actionEvent) {
        productionSelection.setCardDevelopmentSlotActive(cardDevelopArray);
        GUI.sendMessage(new RequestActivateProduction(productionSelection));
        GUI.displayMessage("Production activated!");
        closeStage(actionEvent);
    }


    //BASIC PRODUCTION BUTTONS

    public void stone_input(MouseEvent mouseEvent) {
        if(resources[0]==null){
            resources[0]=Resource.Stones;
        }
        else resources[1]=Resource.Stones;
        stone_in.setEffect(brightness);
    }

    public void coin_input(MouseEvent mouseEvent) {
        if(resources[0]==null){
            resources[0]=Resource.Coins;
        }
        else resources[1]=Resource.Coins;
        coin_in.setEffect(monochrome);
    }

    public void servant_input(MouseEvent mouseEvent) {
        if(resources[0]==null){
            resources[0]=Resource.Servants;
        }
        else resources[1]=Resource.Servants;
        servant_in.setEffect(monochrome);
    }

    public void shield_input(MouseEvent mouseEvent) {
        if(resources[0]==null){
            resources[0]=Resource.Shields;
        }
        else resources[1]=Resource.Shields;
        shield_in.setEffect(monochrome);
    }

    public void stone_output(MouseEvent mouseEvent) {
        resources[2]=Resource.Stones;
        stone_out.setEffect(brightness);
    }

    public void coin_output(MouseEvent mouseEvent) {
        resources[2]=Resource.Coins;
        coin_out.setEffect(monochrome);
    }

    public void shield_output(MouseEvent mouseEvent) {
        resources[2]=Resource.Shields;
        shield_out.setEffect(monochrome);
    }

    public void servant_output(MouseEvent mouseEvent) {
        resources[2]=Resource.Servants;
        servant_out.setEffect(monochrome);
    }


}
