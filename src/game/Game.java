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

    public Game (StartGameInput input, DecksInput deckPlayer1, DecksInput deckPlayer2) {
        int deckIndexPlayerOne = input.getPlayerOneDeckIdx();
        Deck deckPlayerOne = new Deck(deckPlayer1.getDecks().get(deckIndexPlayerOne));
        int deckIndexPlayerTwo = input.getPlayerTwoDeckIdx();
        Deck deckPlayerTwo = new Deck(deckPlayer2.getDecks().get(deckIndexPlayerTwo));
        this.player1 = new Player(deckPlayerOne,input.getPlayerOneHero(), ONE);
        this.player2 = new Player(deckPlayerTwo,input.getPlayerTwoHero(), TWO);
        this.nextMana = 0;
        this.seed = input.getShuffleSeed();
        this.table = new Table();
        this.whoStartTheGame = input.getStartingPlayer();
        if(whoStartTheGame == ONE)
            player1.setIsPlayerTurn(true);
        else
            player2.setIsPlayerTurn(false);
        Collections.shuffle(this.player1.getDeck().getCards(),new Random(this.seed));
        Collections.shuffle(this.player2.getDeck().getCards(), new Random(this.seed));
    }


    public void startGame(ArrayList <ActionsInput> actions, ArrayNode output){
        this.newRound();
        for (ActionsInput action : actions){
            if(action.getCommand().equals("getPlayerDeck")){
                int player = action.getPlayerIdx();
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "getPlayerDeck");
                objectNode.put("playerIdx", player);
                if(player == ONE) {
                    Deck deck1 = new Deck(player1.getDeck());
                    objectNode.putPOJO("output", deck1.getCards());
                    output.addPOJO(objectNode);
                }
                else {
                    Deck deck2 = new Deck(player2.getDeck());
                    objectNode.putPOJO("output", deck2.getCards());
                    output.addPOJO(objectNode);
                }

            }
            if(action.getCommand().equals("getPlayerHero")){
                int player = action.getPlayerIdx();
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "getPlayerHero");
                objectNode.put("playerIdx", player);
                if(player == ONE)
                    objectNode.putPOJO("output", player1.getHero());
                else
                    objectNode.putPOJO("output", player2.getHero());
                output.addPOJO(objectNode);
            }
            if(action.getCommand().equals("getPlayerTurn")){
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "getPlayerTurn");
                if(player1.getIsPlayerTurn())
                    objectNode.put("output", ONE);
                else
                    objectNode.put("output", TWO);
                output.addPOJO(objectNode);
            }
            if(action.getCommand().equals("endPlayerTurn")){
                if(player1.getIsPlayerTurn()) {
                    player1.setIsPlayerTurn(false);
                    player2.setIsPlayerTurn(true);
                } else {
                    player1.setIsPlayerTurn(true);
                    player2.setIsPlayerTurn(false);
                }
                this.numberTurns ++;
                if(this.numberTurns == 2) {
                    this.newRound();
                    numberTurns = 0;
                }
            }
            if(action.getCommand().equals("getCardsInHand")){
                int player = action.getPlayerIdx();
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "getCardsInHand");
                objectNode.put("playerIdx", player);
                if (player == ONE) {
                    ArrayList<Minion> copyHand = getCopyHand(player1);
                    objectNode.putPOJO("output", copyHand);
                } else {
                    ArrayList<Minion> copyHand = getCopyHand(player2);
                    objectNode.putPOJO("output", copyHand);
                }
                output.addPOJO(objectNode);
            }
            if(action.getCommand().equals("getPlayerMana")){
                int player = action.getPlayerIdx();
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "getPlayerMana");
                objectNode.put("playerIdx", player);
                if (player == ONE) {
                    objectNode.putPOJO("output", player1.getMana());
                } else {
                    objectNode.putPOJO("output", player2.getMana());
                }
                output.addPOJO(objectNode);
            }
            if(action.getCommand().equals("placeCard")){
                int handIdx = action.getHandIdx();
                if(player1.getIsPlayerTurn())
                    this.placeCard(player1, handIdx, output,2,3);
                else
                    this.placeCard(player2, handIdx, output,1,0);
            }
            if(action.getCommand().equals("getCardsOnTable")){
                Table copyTable = new Table(table);
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "getCardsOnTable");
                objectNode.putPOJO("output", copyTable.getCardsOnTable());
                output.addPOJO(objectNode);
            }
        }
    }


    private ArrayList<Minion> getCopyHand(Player player) {
        ArrayList<Minion> copyHand = new ArrayList<>(player.getHand().size());
        for(Minion card : player.getHand()){
            switch (card.getName()) {
                case "The Ripper" -> {
                    Ripper ripper = new Ripper(card);
                    copyHand.add(ripper);
                }
                case "Miraj" -> {
                    Miraj miraj = new Miraj(card);
                    copyHand.add(miraj);
                }
                case "The Cursed One" -> {
                    CursedOne cursedOne = new CursedOne(card);
                    copyHand.add(cursedOne);
                }
                case "Disciple" -> {
                    Disciple disciple = new Disciple(card);
                    copyHand.add(disciple);
                }
                default -> {
                    Minion minion = new Minion(card);
                    copyHand.add(minion);
                }
            }
        }
        return copyHand;
    }

    public void placeCard(Player player, int handIdx, ArrayNode output, int x,int y) {
        if (player.getHand().size() >= handIdx + 1) {
            if (player.getMana() < player.getHand().get(handIdx).getMana()) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "placeCard");
                objectNode.put("handIdx", handIdx);
                objectNode.put("error", "Not enough mana to place card on table.");
                output.addPOJO(objectNode);
            } else if (player.getHand().get(handIdx).getIsTank() && table.getCardsOnTable().get(x).size() == 5) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "placeCard");
                objectNode.put("handIdx", handIdx);
                objectNode.put("error", "Cannot place card on table since row is full.");
                output.addPOJO(objectNode);
            } else if (!player.getHand().get(handIdx).getIsTank() && table.getCardsOnTable().get(y).size() == 5) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("command", "placeCard");
                objectNode.put("handIdx", handIdx);
                objectNode.put("error", "Cannot place card on table since row is full.");
                output.addPOJO(objectNode);
            }  else if(player.getNumber() == ONE) {
                table.addCard(player.getHand().get(handIdx), ONE);
                player.setMana(player.getMana() - player.getHand().get(handIdx).getMana());
                player.getHand().remove(handIdx);
            } else {
                table.addCard(player.getHand().get(handIdx), TWO);
                player.setMana(player.getMana() - player.getHand().get(handIdx).getMana());
                player.getHand().remove(handIdx);
            }
        }
    }

    public void newRound() {
        if(!(player1.getDeck().getCards().isEmpty())) {
            player1.getHand().add(player1.getDeck().getCards().get(0));
            player1.getDeck().getCards().remove(0);
        }
        if(!(player2.getDeck().getCards().isEmpty())){
            player2.getHand().add(player2.getDeck().getCards().get(0));
            player2.getDeck().getCards().remove(0);
        }
        if(getNextMana() <= MAX_MANA)
            setNextMana(getNextMana() + 1);
        player1.setMana(player1.getMana() + getNextMana());
        player2.setMana(player2.getMana() + getNextMana());
    }

}
