package it.polimi.ingsw.client.view.CLI;

public class ParsingCommand {
    private Utils utils;
    private CLI cli;
    public ParsingCommand(Utils utils,CLI cli){
        this.utils=utils;
        this.cli=cli;
    }

    public void readCommand(){
        String command =utils.readString();
       switch (command){
          case "help": utils.printHelp();
           case "resource market": cli.askMarketChoice();
           case "card leader": cli.askCardLeaderActivation();
           case "card development market": cli.askDevelopmentCardChoice();
           case "production": cli.askProductionActivation();
           case "end turn": cli.askEndTurn();
           default: utils.printCommandError();
       }

    }
}
