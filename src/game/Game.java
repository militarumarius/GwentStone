package game;

import cards.Hero;
import cards.Minion;
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
    private int maxMana = 10;
    private int nextMana;
    private Table table;

    /**
     */
    public int getNextMana() {
        return nextMana;
    }

    /**
     */
    public void setNextMana(final int nextMana) {
        this.nextMana = nextMana;
    }

    public Game(final StartGameInput input, final DecksInput deckPlayer1,
                final DecksInput deckPlayer2) {
        int deckIndexPlayerOne = input.getPlayerOneDeckIdx();
        Deck deckPlayerOne = new Deck(deckPlayer1.getDecks().get(deckIndexPlayerOne));
        int deckIndexPlayerTwo = input.getPlayerTwoDeckIdx();
        Deck deckPlayerTwo = new Deck(deckPlayer2.getDecks().get(deckIndexPlayerTwo));
        this.player1 = new Player(deckPlayerOne, input.getPlayerOneHero(), ONE, 2, 3);
        this.player2 = new Player(deckPlayerTwo, input.getPlayerTwoHero(), TWO, 1, 0);
        this.seed = input.getShuffleSeed();
        Collections.shuffle(this.player1.getDeck().getCards(), new Random(this.seed));
        Collections.shuffle(this.player2.getDeck().getCards(), new Random(this.seed));
        this.nextMana = 0;
        this.table = new Table();
        if (input.getStartingPlayer() == ONE) {
            player1.setIsPlayerTurn(true);
        }
        player2.setIsPlayerTurn(false);
        table.unattackCards();
        player1.getHero().setHasAttacked(false);
        player2.getHero().setHasAttacked(false);

    }

    /**
     * method that start the game, and executes the commands received as input
     * @param actions the actions
     * @param gamesPlayed number of games played
     * @param output array node to display the objects
     */
    public void startGame(final ArrayList<ActionsInput> actions,
                          final int gamesPlayed, final ArrayNode output) {
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
                Commands getPlayerDeck = new Commands(player, "getPlayerDeck", deck.getCards());
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
                Commands getPlayerHero = new Commands(player, "getPlayerHero", copy);
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
                Commands getCardsInHand = new Commands(player, "getCardsInHand", copyHand);
                getCardsInHand.playerOutput(output);

            }
            if (action.getCommand().equals("getPlayerMana")) {
                int player = action.getPlayerIdx();
//                objectNode.put("command", "getPlayerMana");
//                objectNode.put("playerIdx", player);
//                objectNode.putPOJO("output", getPlayer(player).getMana());
//                output.addPOJO(objectNode);
                Commands getPlayerMana = new Commands(player, "getPlayerMana",
                        getPlayer(player).getMana());
                getPlayerMana.playerOutput(output);
            }
            if (action.getCommand().equals("placeCard")) {
                int handIdx = action.getHandIdx();
                int firstRow = getCurrentPlayer().getFirstRow();
                int secondRow = getCurrentPlayer().getSecondRow();
                table.placeCardOnTable(getCurrentPlayer(), handIdx, output, firstRow, secondRow);
            }
            if (action.getCommand().equals("getCardsOnTable")) {
                Table copyTable = new Table(table);
//                objectNode.put("command", "getCardsOnTable");
//                objectNode.putPOJO("output", copyTable.getCardsOnTable());
//                output.addPOJO(objectNode);
                Commands getCardsOnTable = new Commands("getCardsOnTable",
                        copyTable.getCardsOnTable());
                getCardsOnTable.easyCommand(output);
            }
            if (action.getCommand().equals("cardUsesAttack")) {
                Coordinates attacker = new Coordinates(action.getCardAttacker().getX(),
                        action.getCardAttacker().getY());
                Coordinates attacked = new Coordinates(action.getCardAttacked().getX(),
                        action.getCardAttacked().getY());
                if ((attacked.getY() + 1 <= table.getCardsOnTable().get(attacked.getX()).size())) {
                    table.attackCard(attacker, attacked, getCurrentPlayer(),
                            getOpponent(), output);
                }
            }
            if (action.getCommand().equals("getCardAtPosition")) {
                table.getCardPosition(action, output);
            }
            if (action.getCommand().equals("cardUsesAbility")) {
                Coordinates attacker = new Coordinates(action.getCardAttacker().getX(),
                        action.getCardAttacker().getY());
                Coordinates attacked = new Coordinates(action.getCardAttacked().getX(),
                        action.getCardAttacked().getY());
                if ((attacked.getY() + 1 <= table.getCardsOnTable().get(attacked.getX()).size())) {
                        table.useAbilityCard(attacker, attacked, getOpponent(), output);
                }
            }
            if (action.getCommand().equals("useAttackHero")) {
                Coordinates attacker = new Coordinates(action.getCardAttacker().getX(),
                        action.getCardAttacker().getY());
                    table.attackHero(attacker, getOpponent(), getCurrentPlayer(), output);
            }
            if (action.getCommand().equals("useHeroAbility")) {
                int row = action.getAffectedRow();
                getCurrentPlayer().specialAttackHero(table.getCardsOnTable().get(row), row,
                        output);
            }
            if (action.getCommand().equals("getFrozenCardsOnTable")) {
                ArrayList<Minion> frozen = table.getFrozenCards();
//                objectNode.put("command", "getFrozenCardsOnTable");
//                objectNode.putPOJO("output", frozen);
//                output.addPOJO(objectNode);
                Commands getFrozenCardsOnTable = new Commands("getFrozenCardsOnTable", frozen);
                getFrozenCardsOnTable.easyCommand(output);
            }
            if (action.getCommand().equals("getPlayerOneWins")) {
//                objectNode.put("command", "getPlayerOneWins");
//                objectNode.putPOJO("output", player1.getNumberWins());
//                output.addPOJO(objectNode);
                Commands getPlayerOneWins = new Commands("getPlayerOneWins",
                        player1.getNumberWins());
                getPlayerOneWins.easyCommand(output);
            }
            if (action.getCommand().equals("getPlayerTwoWins")) {
//                objectNode.put("command", "getPlayerTwoWins");
//                objectNode.putPOJO("output", player2.getNumberWins());
//                output.addPOJO(objectNode);
                Commands getPlayerTwoWins = new Commands("getPlayerTwoWins",
                        player2.getNumberWins());
                getPlayerTwoWins.easyCommand(output);

            }
            if (action.getCommand().equals("getTotalGamesPlayed")) {
//                objectNode.put("command", "getTotalGamesPlayed");
//                objectNode.putPOJO("output", gamesPlayed);
//                output.addPOJO(objectNode);
                Commands getTotalGamesPlayed = new Commands("getTotalGamesPlayed",
                        gamesPlayed);
                getTotalGamesPlayed.easyCommand(output);
            }
        }
    }

    /**
     * the method that resets the variables for a new round
     */
    public void newRound() {
        player1.getCardInHand();
        player2.getCardInHand();
        if (getNextMana() <= maxMana) {
            setNextMana(getNextMana() + 1);
        }
        player1.setMana(player1.getMana() + getNextMana());
        player2.setMana(player2.getMana() + getNextMana());
        table.unattackCards();
        player1.getHero().setHasAttacked(false);
        player2.getHero().setHasAttacked(false);
    }

    /**
     * method that get the player by index
     */
    public Player getPlayer(final int idx) {
        if (idx == ONE) {
            return player1;
        }
        return player2;
    }

    /**
     * method that return the current player
     * @return the current player
     */
    public Player getCurrentPlayer() {
        if (player1.getIsPlayerTurn()) {
            return player1;
        }
        return player2;
    }

    /**
     * method that return the opponent
     * @return opponent
     */
    public Player getOpponent() {
        if (player1.getIsPlayerTurn()) {
            return player2;
        }
        return player1;
    }

    /**
     * methodd that end the turn for the current player
     */
    public void endTurn() {
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
