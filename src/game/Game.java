package game;

import cards.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.ActionsInput;
import fileio.DecksInput;
import fileio.StartGameInput;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {
    private final int ONE = 1;
    private final int TWO = 2;
    private Player player1, player2;
    private int seed;
    private int numberTurns;
    private int MAX_MANA = 10;
    private int nextMana;
    private Table table;

    public int getNextMana() {
        return nextMana;
    }

    public void setNextMana(int nextMana) {
        this.nextMana = nextMana;
    }

    public Game(StartGameInput input, DecksInput deckPlayer1, DecksInput deckPlayer2) {
        int deckIndexPlayerOne = input.getPlayerOneDeckIdx();
        Deck deckPlayerOne = new Deck(deckPlayer1.getDecks().get(deckIndexPlayerOne));
        int deckIndexPlayerTwo = input.getPlayerTwoDeckIdx();
        Deck deckPlayerTwo = new Deck(deckPlayer2.getDecks().get(deckIndexPlayerTwo));
        this.player1 = new Player(deckPlayerOne, input.getPlayerOneHero(), ONE, 2, 3);
        this.player2 = new Player(deckPlayerTwo, input.getPlayerTwoHero(), TWO,1,0);
        this.seed = input.getShuffleSeed();
        Collections.shuffle(this.player1.getDeck().getCards(), new Random( this.seed));
        Collections.shuffle(this.player2.getDeck().getCards(), new Random( this.seed));
        this.nextMana = 0;
        this.table = new Table();
        if (input.getStartingPlayer() == ONE)
            player1.setIsPlayerTurn(true);
        player2.setIsPlayerTurn(false);
        table.unattackCards();
        player1.getHero().setHasAttacked(false);
        player2.getHero().setHasAttacked(false);

    }


    public void startGame(ArrayList<ActionsInput> actions, int gamesPlayed, ArrayNode output) {
        this.newRound();
        for (ActionsInput action : actions) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            if (action.getCommand().equals("getPlayerDeck")) {
                int player = action.getPlayerIdx();
//                objectNode.put("command", "getPlayerDeck");
//                objectNode.put("playerIdx", player);
//                Deck deck = new Deck(getPlayer(player).getDeck());
//                objectNode.putPOJO("output", deck.getCards());
//                output.addPOJO(objectNode);
                Deck deck = new Deck(getPlayer(player).getDeck());
                Comands getPlayerDeck = new Comands(player, "getPlayerDeck", deck.getCards());
                getPlayerDeck.playerOutput(output);
            }
            if (action.getCommand().equals("getPlayerHero")) {
                int player = action.getPlayerIdx();
//                objectNode.put("command", "getPlayerHero");
//                objectNode.put("playerIdx", player);
//
//                objectNode.putPOJO("output", copy);
//                output.addPOJO(objectNode);
                Hero copy = new Hero(getPlayer(player).getHero());
                Comands getPlayerHero = new Comands(player, "getPlayerHero", copy);
                getPlayerHero.playerOutput(output);
            }
            if (action.getCommand().equals("getPlayerTurn")) {
                objectNode.put("command", "getPlayerTurn");
                objectNode.put("output", getCurrentPlayer().getNumber());
                output.addPOJO(objectNode);
            }
            if (action.getCommand().equals("endPlayerTurn")) {
                this.endTurn();
            }
            if (action.getCommand().equals("getCardsInHand")) {
                int player = action.getPlayerIdx();
//                objectNode.put("command", "getCardsInHand");
//                objectNode.put("playerIdx", player);
//                ArrayList<Minion> copyHand = getPlayer(player).getCopyHand();
//                objectNode.putPOJO("output", copyHand);
//                output.addPOJO(objectNode);
                ArrayList<Minion> copyHand = getPlayer(player).getCopyHand();
                Comands getCardsInHand = new Comands(player, "getCardsInHand", copyHand);
                getCardsInHand.playerOutput(output);

            }
            if (action.getCommand().equals("getPlayerMana")) {
                int player = action.getPlayerIdx();
//                objectNode.put("command", "getPlayerMana");
//                objectNode.put("playerIdx", player);
//                objectNode.putPOJO("output", getPlayer(player).getMana());
//                output.addPOJO(objectNode);
                Comands getPlayerMana = new Comands(player, "getPlayerMana", getPlayer(player).getMana());
                getPlayerMana.playerOutput(output);
            }
            if (action.getCommand().equals("placeCard")) {
                int handIdx = action.getHandIdx();
                int firstRow = getCurrentPlayer().getFirstRow();
                int secondRow = getCurrentPlayer().getSecondRow();
                table.placeCardOnTable(getCurrentPlayer(), handIdx, output, firstRow , secondRow);
            }
            if (action.getCommand().equals("getCardsOnTable")) {
                Table copyTable = new Table(table);
//                objectNode.put("command", "getCardsOnTable");
//                objectNode.putPOJO("output", copyTable.getCardsOnTable());
//                output.addPOJO(objectNode);
                Comands getCardsOnTable = new Comands("getCardsOnTable", copyTable.getCardsOnTable());
                getCardsOnTable.easyCommand(output);
            }
            if (action.getCommand().equals("cardUsesAttack")) {
                Coordinates attacker = new Coordinates(action.getCardAttacker().getX(), action.getCardAttacker().getY());
                Coordinates attacked = new Coordinates(action.getCardAttacked().getX(), action.getCardAttacked().getY());
                if ((attacked.y + 1 <= table.getCardsOnTable().get(attacked.x).size())) {
                    table.attackCard(attacker, attacked, getCurrentPlayer(), getOpponent(), output);
                }
            }
            if (action.getCommand().equals("getCardAtPosition")) {
                table.getCardPosition(action, output);
            }
            if (action.getCommand().equals("cardUsesAbility")) {
                Coordinates attacker = new Coordinates(action.getCardAttacker().getX(), action.getCardAttacker().getY());
                Coordinates attacked = new Coordinates(action.getCardAttacked().getX(), action.getCardAttacked().getY());
                if ((attacked.y + 1 <= table.getCardsOnTable().get(attacked.x).size())) {
                        table.useAbilityCard(attacker, attacked, getOpponent(), output);
                }
            }
            if (action.getCommand().equals("useAttackHero")){
                Coordinates attacker = new Coordinates(action.getCardAttacker().getX(), action.getCardAttacker().getY());
                    table.attackHero(attacker,getOpponent(), getCurrentPlayer(), output);
            }
            if (action.getCommand().equals("useHeroAbility")){
                int row = action.getAffectedRow();
                getCurrentPlayer().specialAttackHero(table.getCardsOnTable().get(row),row,output);
            }
            if (action.getCommand().equals("getFrozenCardsOnTable")) {
                ArrayList<Minion> frozen = table.getFrozenCards();
//                objectNode.put("command", "getFrozenCardsOnTable");
//                objectNode.putPOJO("output", frozen);
//                output.addPOJO(objectNode);
                Comands getFrozenCardsOnTable = new Comands("getFrozenCardsOnTable", frozen);
                getFrozenCardsOnTable.easyCommand(output);
            }
            if (action.getCommand().equals("getPlayerOneWins")){
//                objectNode.put("command", "getPlayerOneWins");
//                objectNode.putPOJO("output", player1.getNumberWins());
//                output.addPOJO(objectNode);
                Comands getPlayerOneWins = new Comands("getPlayerOneWins", player1.getNumberWins());
                getPlayerOneWins.easyCommand(output);
            }
            if (action.getCommand().equals("getPlayerTwoWins")){
//                objectNode.put("command", "getPlayerTwoWins");
//                objectNode.putPOJO("output", player2.getNumberWins());
//                output.addPOJO(objectNode);
                Comands getPlayerTwoWins = new Comands("getPlayerTwoWins", player2.getNumberWins());
                getPlayerTwoWins.easyCommand(output);

            }
            if (action.getCommand().equals("getTotalGamesPlayed")){
//                objectNode.put("command", "getTotalGamesPlayed");
//                objectNode.putPOJO("output", gamesPlayed);
//                output.addPOJO(objectNode);
                Comands getTotalGamesPlayed = new Comands("getTotalGamesPlayed", gamesPlayed);
                getTotalGamesPlayed.easyCommand(output);
            }

        }
    }

    public void newRound() {
        player1.getCardInHand();
        player2.getCardInHand();
        if (getNextMana() <= MAX_MANA)
            setNextMana(getNextMana() + 1);
        player1.setMana(player1.getMana() + getNextMana());
        player2.setMana(player2.getMana() + getNextMana());
        table.unattackCards();
        player1.getHero().setHasAttacked(false);
        player2.getHero().setHasAttacked(false);
    }

    public Player getPlayer(final int idx) {
        if (idx == ONE)
            return player1;
        return player2;
    }

    public Player getCurrentPlayer(){
        if(player1.getIsPlayerTurn())
            return player1;
        return player2;
    }

    public Player getOpponent(){
        if(player1.getIsPlayerTurn())
            return player2;
        return player1;
    }

    public void endTurn(){
        table.unfreezeCards(getCurrentPlayer().getFirstRow(), getCurrentPlayer().getSecondRow());
        Player opponent = getOpponent();
        getCurrentPlayer().setIsPlayerTurn(false);
        opponent.setIsPlayerTurn(true);
        this.numberTurns++;
        if (this.numberTurns == 2) {
            this.newRound();
            numberTurns = 0;
        }
    }
}
