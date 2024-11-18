package game;

import cards.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
    private int whoStartTheGame;
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
        this.nextMana = 0;
        this.seed = input.getShuffleSeed();
        this.table = new Table();
        this.whoStartTheGame = input.getStartingPlayer();
        if (whoStartTheGame == ONE)
            player1.setIsPlayerTurn(true);
        else
            player2.setIsPlayerTurn(false);
        Collections.shuffle(this.player1.getDeck().getCards(), new Random(this.seed));
        Collections.shuffle(this.player2.getDeck().getCards(), new Random(this.seed));
    }


    public void startGame(ArrayList<ActionsInput> actions, ArrayNode output) {
        this.newRound();
        for (ActionsInput action : actions) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            if (action.getCommand().equals("getPlayerDeck")) {
                int player = action.getPlayerIdx();
                objectNode.put("command", "getPlayerDeck");
                objectNode.put("playerIdx", player);
                Deck deck = new Deck(getPlayer(player).getDeck());
                objectNode.putPOJO("output", deck.getCards());
                output.addPOJO(objectNode);
            }
            if (action.getCommand().equals("getPlayerHero")) {
                int player = action.getPlayerIdx();
                objectNode.put("command", "getPlayerHero");
                objectNode.put("playerIdx", player);
                Hero copy = new Hero(getPlayer(player).getHero());
                objectNode.putPOJO("output", copy);
                output.addPOJO(objectNode);
            }
            if (action.getCommand().equals("getPlayerTurn")) {
                objectNode.put("command", "getPlayerTurn");
                if (player1.getIsPlayerTurn())
                    objectNode.put("output", ONE);
                else
                    objectNode.put("output", TWO);
                output.addPOJO(objectNode);
            }
            if (action.getCommand().equals("endPlayerTurn")) {
                if (player1.getIsPlayerTurn()) {
                    player1.setIsPlayerTurn(false);
                    player2.setIsPlayerTurn(true);
                    table.unfreezeCards(player1.getX(), player1.getY());
                } else {
                    player1.setIsPlayerTurn(true);
                    player2.setIsPlayerTurn(false);
                    table.unfreezeCards(player2.getX(), player2.getY());

                }
                this.numberTurns++;
                if (this.numberTurns == 2) {
                    this.newRound();
                    numberTurns = 0;
                }
            }
            if (action.getCommand().equals("getCardsInHand")) {
                int player = action.getPlayerIdx();
                objectNode.put("command", "getCardsInHand");
                objectNode.put("playerIdx", player);
                ArrayList<Minion> copyHand = getPlayer(player).getCopyHand();
                objectNode.putPOJO("output", copyHand);
                output.addPOJO(objectNode);
            }
            if (action.getCommand().equals("getPlayerMana")) {
                int player = action.getPlayerIdx();
                objectNode.put("command", "getPlayerMana");
                objectNode.put("playerIdx", player);
                objectNode.putPOJO("output", getPlayer(player).getMana());
                output.addPOJO(objectNode);
            }
            if (action.getCommand().equals("placeCard")) {
                int handIdx = action.getHandIdx();
                if (player1.getIsPlayerTurn())
                    table.placeCard(player1, handIdx, output, 2, 3);
                else
                    table.placeCard(player2, handIdx, output, 1, 0);
            }
            if (action.getCommand().equals("getCardsOnTable")) {
                Table copyTable = new Table(table);
                objectNode.put("command", "getCardsOnTable");
                objectNode.putPOJO("output", copyTable.getCardsOnTable());
                output.addPOJO(objectNode);
            }
            if (action.getCommand().equals("cardUsesAttack")) {
                Coordinates attacker = new Coordinates(action.getCardAttacker().getX(), action.getCardAttacker().getY());
                Coordinates attacked = new Coordinates(action.getCardAttacked().getX(), action.getCardAttacked().getY());
                if ((attacked.y + 1 <= table.getCardsOnTable().get(attacked.x).size())) {
                    int x1 = 2;
                    if (player2.getIsPlayerTurn())
                        x1 = 0;
                    if (attacked.x == x1 || attacked.x == x1 + 1) {
                        objectNode.put("command", "cardUsesAttack");
                        objectNode.putPOJO("cardAttacker", attacker);
                        objectNode.putPOJO("cardAttacked", attacked);
                        objectNode.put("error", "Attacked card does not belong to the enemy.");
                        output.addPOJO(objectNode);
                    } else
                        this.attackCard(attacker, attacked, output);
                }
            }
            if (action.getCommand().equals("getCardAtPosition")) {
                this.getCardPosition(action, output);
            }
            if (action.getCommand().equals("cardUsesAbility")) {
                Coordinates attacker = new Coordinates(action.getCardAttacker().getX(), action.getCardAttacker().getY());
                Coordinates attacked = new Coordinates(action.getCardAttacked().getX(), action.getCardAttacked().getY());
                if ((attacked.y + 1 <= table.getCardsOnTable().get(attacked.x).size())) {
                        this.useAbilityCard(attacker, attacked, output);
                }
            }
            if (action.getCommand().equals("useAttackHero")){
                Coordinates attacker = new Coordinates(action.getCardAttacker().getX(), action.getCardAttacker().getY());
                    this.attackHero(attacker, output);
            }
            if (action.getCommand().equals("useHeroAbility")){
                int row = action.getAffectedRow();
                getCurrentPlayer().specialAttackHero(table.getCardsOnTable().get(row),row,output);
            }
            if (action.getCommand().equals("getFrozenCardsOnTable")) {
                ArrayList<Minion> frozen = table.getFrozenCards();
                objectNode.put("command", "getFrozenCardsOnTable");
                objectNode.putPOJO("output", frozen);
                output.addPOJO(objectNode);
            }
        }
    }

    public void getCardPosition(ActionsInput action, ArrayNode output) {
        Coordinates cardPosition = new Coordinates(action.getX(), action.getY());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", "getCardAtPosition");
        objectNode.put("x", cardPosition.x);
        objectNode.put("y", cardPosition.y);

        if (cardPosition.y + 1 > table.getCardsOnTable().get(cardPosition.x).size()) {
            objectNode.put("output", "No card available at that position.");
        } else {
            Minion copy = new Minion(table.getCardsOnTable().get(cardPosition.x).get(cardPosition.y));
            objectNode.putPOJO("output", copy);
        }
        output.addPOJO(objectNode);

    }

public void attackHero(Coordinates attacker, ArrayNode output) {
    String error = "";
    Minion cardAttacker = table.getCardsOnTable().get(attacker.x).get(attacker.y);
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode objectNode = mapper.createObjectNode();
    if (cardAttacker.getIsFrozen()) {
        error = "Attacker card is frozen.";
    } else if (cardAttacker.getIsHasAttacked()) {
        error = "Attacker card has already attacked this turn.";
    } else if (table.checkTank(getOpponent().getX())) {
        error = "Attacked card is not of type 'Tank'.";
    } else {
        Hero hero = getOpponent().getHero();
        cardAttacker.setHasAttacked(true);
        hero.setHealth(hero.getHealth() - cardAttacker.getAttackDamage());
        if(getOpponent().getHero().getHealth() <= 0 && getCurrentPlayer().getNumber() == ONE){
            objectNode.put("gameEnded", "Player one killed the enemy hero.");
            output.addPOJO(objectNode);
        } else if(getOpponent().getHero().getHealth() <= 0 && getCurrentPlayer().getNumber() == TWO){
            objectNode.put("gameEnded", "Player two killed the enemy hero.");
            output.addPOJO(objectNode);
        }
        return;
    }
    objectNode.put("command", "useAttackHero");
    objectNode.putPOJO("cardAttacker", attacker);
    objectNode.put("error", error);
    output.addPOJO(objectNode);

}

public void attackCard(Coordinates attacker, Coordinates attacked, ArrayNode output) {
    String error = "";
    Minion cardAttacked = table.getCardsOnTable().get(attacked.x).get(attacked.y);
    Minion cardAttacker = table.getCardsOnTable().get(attacker.x).get(attacker.y);
    if (cardAttacker.getIsHasAttacked()) {
        error = "Attacker card has already attacked this turn.";
    } else if (cardAttacker.getIsFrozen()) {
        error = "Attacker card is frozen.";
    } else if (!cardAttacked.isTankSpecial() && table.checkTank(getOpponent().getX())) {
        error = "Attacked card is not of type 'Tank'.";
    } else {
        cardAttacker.setHasAttacked(true);
        if (cardAttacked.getHealth() <= cardAttacker.getAttackDamage()) {
            table.getCardsOnTable().get(attacked.x).remove(attacked.y);
        } else
            cardAttacked.setHealth(cardAttacked.getHealth() - cardAttacker.getAttackDamage());
        return;
    }
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode objectNode = mapper.createObjectNode();
    objectNode.put("command", "cardUsesAttack");
    objectNode.putPOJO("cardAttacker", attacker);
    objectNode.putPOJO("cardAttacked", attacked);
    objectNode.put("error", error);
    output.addPOJO(objectNode);

}
public void useAbilityCard(Coordinates attacker, Coordinates attacked, ArrayNode output){
    String error = "";
    Minion cardAttacked = table.getCardsOnTable().get(attacked.x).get(attacked.y);
    Minion cardAttacker = table.getCardsOnTable().get(attacker.x).get(attacker.y);
    if (cardAttacker.getIsFrozen()) {
        error = "Attacker card is frozen.";
    } else if (cardAttacker.getIsHasAttacked()) {
        error = "Attacker card has already attacked this turn.";
    } else if (cardAttacker.getName().equals("Disciple")) {
        if ((attacked.x / 2 != attacker.x /2)) {
            error = "Attacked card does not belong to the current player.";
        } else {
            cardAttacker.setHasAttacked(true);
            cardAttacker.specialAbility(cardAttacked);
            if (cardAttacked.getHealth() <= 0)
                table.getCardsOnTable().get(attacked.x).remove(attacked.y);
            return;
        }
    } else {
        if (attacked.x / 2 == attacker.x / 2) {
            error = "Attacked card does not belong to the enemy.";
        } else if (!cardAttacked.isTankSpecial() && table.checkTank(getOpponent().getX())) {
            error = "Attacked card is not of type 'Tank'.";
        } else {
            cardAttacker.setHasAttacked(true);
            cardAttacker.specialAbility(cardAttacked);
            if (cardAttacked.getHealth() <= 0)
                table.getCardsOnTable().get(attacked.x).remove(attacked.y);
            return;
        }
    }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", "cardUsesAbility");
        objectNode.putPOJO("cardAttacker", attacker);
        objectNode.putPOJO("cardAttacked", attacked);
        objectNode.put("error", error);
        output.addPOJO(objectNode);

}

public void newRound() {
    if (!(player1.getDeck().getCards().isEmpty())) {
        player1.getHand().add(player1.getDeck().getCards().get(0));
        player1.getDeck().getCards().remove(0);
    }
    if (!(player2.getDeck().getCards().isEmpty())) {
        player2.getHand().add(player2.getDeck().getCards().get(0));
        player2.getDeck().getCards().remove(0);
    }
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
}
