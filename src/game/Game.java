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
    private final int one = 1;
    private final Player player1, player2;
    private int numberTurns;
    private static final int MAN_MANA = 10;
    private static final int SECOND_ROW_FIRST_PLAYER = 3;
    private int nextMana;
    private final Table table;

    public Game(final StartGameInput input, final DecksInput deckPlayer1,
                final DecksInput deckPlayer2) {
        int deckIndexPlayerOne = input.getPlayerOneDeckIdx();
        Deck deckPlayerOne = new Deck(deckPlayer1.getDecks().get(deckIndexPlayerOne));
        int deckIndexPlayerTwo = input.getPlayerTwoDeckIdx();
        Deck deckPlayerTwo = new Deck(deckPlayer2.getDecks().get(deckIndexPlayerTwo));
        this.player1 = new Player(deckPlayerOne, input.getPlayerOneHero(), one, 2,
                SECOND_ROW_FIRST_PLAYER);
        int two = 2;
        this.player2 = new Player(deckPlayerTwo, input.getPlayerTwoHero(), two, 1, 0);
        int seed = input.getShuffleSeed();
        Collections.shuffle(this.player1.getDeck().getCards(), new Random(seed));
        Collections.shuffle(this.player2.getDeck().getCards(), new Random(seed));
        this.nextMana = 0;
        this.table = new Table();
        if (input.getStartingPlayer() == one) {
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
     * @param output array node to display the objects
     */
    public void playingGame(final ArrayList<ActionsInput> actions,
                          final Leaderboard leaderboard, final ArrayNode output) {
        this.newRound();
        for (ActionsInput action : actions) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            switch (action.getCommand()) {
                case "getPlayerDeck" -> {
                    int player = action.getPlayerIdx();
                    Deck deck = new Deck(getPlayer(player).getDeck());
                    Commands getPlayerDeck = new Commands(player, "getPlayerDeck", deck.getCards());
                    getPlayerDeck.playerOutput(output);
                }
                case "getPlayerHero" -> {
                    int player = action.getPlayerIdx();
                    Hero copy = new Hero(getPlayer(player).getHero());
                    Commands getPlayerHero = new Commands(player, "getPlayerHero", copy);
                    getPlayerHero.playerOutput(output);
                }
                case "getPlayerTurn" -> {
                    objectNode.put("command", "getPlayerTurn");
                    objectNode.put("output", getCurrentPlayer().getNumber());
                    output.addPOJO(objectNode);
                }
                case "endPlayerTurn" -> this.endTurn();
                case "getCardsInHand" -> {
                    int player = action.getPlayerIdx();
                    ArrayList<Minion> copyHand = getPlayer(player).getCopyHand();
                    Commands getCardsInHand = new Commands(player, "getCardsInHand", copyHand);
                    getCardsInHand.playerOutput(output);

                }
                case "getPlayerMana" -> {
                    int player = action.getPlayerIdx();
                    Commands getPlayerMana = new Commands(player, "getPlayerMana",
                            getPlayer(player).getMana());
                    getPlayerMana.playerOutput(output);
                }
                case "placeCard" -> {
                    int handIdx = action.getHandIdx();
                    int firstRow = getCurrentPlayer().getFirstRow();
                    int secondRow = getCurrentPlayer().getSecondRow();
                    table.placeCardOnTable(getCurrentPlayer(), handIdx,
                            output, firstRow, secondRow);
                }
                case "getCardsOnTable" -> {
                    Table copyTable = new Table(table);
                    Commands getCardsOnTable = new Commands("getCardsOnTable",
                            copyTable.getCardsOnTable());
                    getCardsOnTable.easyCommand(output);
                }
                case "cardUsesAttack" -> {
                    Coordinates attacker = new Coordinates(action.getCardAttacker().getX(),
                            action.getCardAttacker().getY());
                    Coordinates attacked = new Coordinates(action.getCardAttacked().getX(),
                            action.getCardAttacked().getY());
                    if ((attacked.getY() + 1
                            <= table.getCardsOnTable().get(attacked.getX()).size())) {
                        table.attackCard(attacker, attacked, getCurrentPlayer(),
                                getOpponent(), output);
                    }
                }
                case "getCardAtPosition" -> {
                    Coordinates cardPosition = new Coordinates(action.getX(), action.getY());
                    table.getCardPosition(cardPosition, output);
                }
                case "cardUsesAbility" -> {
                    Coordinates attacker = new Coordinates(action.getCardAttacker().getX(),
                            action.getCardAttacker().getY());
                    Coordinates attacked = new Coordinates(action.getCardAttacked().getX(),
                            action.getCardAttacked().getY());
                    if ((attacked.getY() + 1
                            <= table.getCardsOnTable().get(attacked.getX()).size())) {
                        table.useAbilityCard(attacker, attacked, getOpponent(), output);
                    }
                }
                case "useAttackHero" -> {
                    Coordinates attacker = new Coordinates(action.getCardAttacker().getX(),
                            action.getCardAttacker().getY());
                    table.attackHero(attacker, getOpponent(), getCurrentPlayer(), output);
                    if (player1.getHero().isStatusKilled()) {
                        leaderboard.setNumberTwoWins(leaderboard.getNumberTwoWins() + 1);
                    }
                    if (player2.getHero().isStatusKilled()) {
                        leaderboard.setNumberOneWins(leaderboard.getNumberOneWins() + 1);
                    }

                }
                case "useHeroAbility" -> {
                    int row = action.getAffectedRow();
                    getCurrentPlayer().specialAttackHero(table.getCardsOnTable().get(row), row,
                            output);
                }
                case "getFrozenCardsOnTable" -> {
                    ArrayList<Minion> frozen = table.getFrozenCards();
                    Commands getFrozenCardsOnTable = new Commands("getFrozenCardsOnTable", frozen);
                    getFrozenCardsOnTable.easyCommand(output);
                }
                case "getPlayerOneWins" -> {
                    Commands getPlayerOneWins = new Commands("getPlayerOneWins",
                            leaderboard.getNumberOneWins());
                    getPlayerOneWins.easyCommand(output);
                }
                case "getPlayerTwoWins" -> {
                    Commands getPlayerTwoWins = new Commands("getPlayerTwoWins",
                            leaderboard.getNumberTwoWins());
                    getPlayerTwoWins.easyCommand(output);
                }
                case "getTotalGamesPlayed" -> {
                    Commands getTotalGamesPlayed = new Commands("getTotalGamesPlayed",
                            leaderboard.getNumberGames());
                    getTotalGamesPlayed.easyCommand(output);
                }
                default -> {
                    return;
                }
            }
        }
    }

    /**
     * the method that resets the variables for a new round
     */
    public void newRound() {
        player1.getCardInHand();
        player2.getCardInHand();
        if (nextMana < MAN_MANA) {
            nextMana++;
        }
        player1.setMana(player1.getMana() + nextMana);
        player2.setMana(player2.getMana() + nextMana);
        table.unattackCards();
        player1.getHero().setHasAttacked(false);
        player2.getHero().setHasAttacked(false);
    }

    /**
     * method that get the player by index
     */
    public Player getPlayer(final int idx) {
        if (idx == one) {
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
     * method that end the turn for the current player
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
