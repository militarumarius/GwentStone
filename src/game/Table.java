package game;

import cards.Hero;
import cards.Minion;
import cards.Disciple;
import cards.Miraj;
import cards.Ripper;
import cards.CursedOne;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;

import java.util.ArrayList;

public class Table {
    private ArrayList<ArrayList<Minion>> cardsOnTable;
    @JsonIgnore
    private final int ONE = 1;
    @JsonIgnore
    private final int TWO = 2;
    @JsonIgnore
    private final int ROWS = 4;

    /**
     */
    public ArrayList<ArrayList<Minion>> getCardsOnTable() {
        return cardsOnTable;
    }

    public Table() {
        this.cardsOnTable = new ArrayList<ArrayList<Minion>>();
        for (int i = 0; i < ROWS; i++) {
            ArrayList<Minion> rand = new ArrayList<>();
            this.cardsOnTable.add(rand);
        }
    }

    /**
     * method that place a card on table
     * @param minion the card place on table
     * @param player the player that place the card
     */
    public void addCard(final Minion minion, final Player player) {
            if (minion.isTankSpecial()) {
                cardsOnTable.get(player.getFirstRow()).add(minion);
            } else {
                cardsOnTable.get(player.getSecondRow()).add(minion);
            }
    }

    /**
     * copy constructor for getCardsOnTable command
     * @param table the table to be copy
     */
    public Table(final Table table) {
        cardsOnTable = new ArrayList<ArrayList<Minion>>();
        for (int i = 0; i < ROWS; i++) {
            ArrayList<Minion> rand = getCopyTableRows(table.getCardsOnTable().get(i));
            cardsOnTable.add(rand);
        }
    }

    /**
     * method that copy a row for getCardOnTable
     * @param row the row to pe copy
     * @return the copy row
     */
    private ArrayList<Minion> getCopyTableRows(final ArrayList<Minion> row) {
        ArrayList<Minion> copyRow = new ArrayList<>(row.size());
        for (Minion card : row) {
            Minion minion;
            switch (card.getName()) {
                case "The Ripper" -> {
                    minion = new Ripper(card);
                }
                case "Miraj" -> {
                    minion = new Miraj(card);
                }
                case "The Cursed One" -> {
                    minion = new CursedOne(card);
                }
                case "Disciple" -> {
                    minion  = new Disciple(card);
                }
                default -> {
                    minion = new Minion(card);
                }
            }
            copyRow.add(minion);
        }
        return copyRow;
    }

    /**
     * method that checks if exist tank cards on the front row
     * @param row the row
     * @return the result
     */
    public boolean checkTank(final int row) {
        for (Minion minion : cardsOnTable.get(row)) {
            if (minion.getIsTank()) {
                return true;
            }
        }
        return false;
    }

    /**
     * method that set the hasAttacked variable of a card to fals
     * method needed for a new round
     */
    public void unattackCards() {
        for (int i = 0; i < ROWS; i++) {
            for (Minion minion : cardsOnTable.get(i)) {
                minion.setHasAttacked(false);
            }
        }
    }

    /**
     * method that unfreeze a card
     * @param x coordinate x
     * @param y coordinate y
     */
    public void unfreezeCards(final int x, final int y) {
        for (int i = x; i <= y; i++) {
            for (Minion minion : cardsOnTable.get(i)) {
                minion.setIsFrozen(false);
            }
        }
    }

    /**
     * method that place a card on table
     * @param player player that place the card
     * @param handIdx index of the card in hand
     * @param output array node for displaying the output
     * @param x coordinate x of the card
     * @param y coordinate y of the card
     */
    public void placeCardOnTable(final Player player, final int handIdx,
                                 final ArrayNode output, final int x, final int y) {
        int firstRow = this.getCardsOnTable().get(x).size();
        int secondRow = this.getCardsOnTable().get(y).size();
        String error =  player.errorPlaceCard(handIdx, firstRow, secondRow);
        if (error == null) {
            this.addCard(player.getHand().get(handIdx), player);
            player.setMana(player.getMana() - player.getHand().get(handIdx).getMana());
            player.getHand().remove(handIdx);
            return;
        }
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode objectNode = mapper.createObjectNode();
//        objectNode.put("command", "placeCard");
//        objectNode.put("handIdx", handIdx);
//        objectNode.put("error", error);
//        output.addPOJO(objectNode);
        Commands getPlayerHero = new Commands("placeCard", handIdx, error);
        getPlayerHero.errorPlaceCard(output);
    }

    /**
     * method that get the copy of frozen cards on the table
     * @return a list of frozen card
     */
    public ArrayList<Minion> getFrozenCards() {
        ArrayList<Minion> frozen = new ArrayList<Minion>();
        for (int i = 0; i < ROWS; i++) {
            ArrayList<Minion> rand = getCopyTableRows(getCardsOnTable().get(i));
            if (!rand.isEmpty() && rand.get(0).getIsFrozen()) {
                frozen.addAll(rand);
            }
        }
        return frozen;

    }

    /**
     * method that use the ability of a card
     * @param attacker the coordinates of the attacker
     * @param attacked the coordinates of the attacked
     * @param opponent the opponent
     * @param output array node that display the JSON objects
     */
    public void useAbilityCard(final Coordinates attacker, final Coordinates attacked,
                               final Player opponent, final ArrayNode output) {
        Minion cardAttacked = this.getCardsOnTable().get(attacked.getX()).get(attacked.getY());
        Minion cardAttacker = this.getCardsOnTable().get(attacker.getX()).get(attacker.getY());
        String error = cardAttacker.errorUseAbility(attacker, attacked, cardAttacked,
                checkTank(opponent.getFirstRow()));
        if (error == null) {
            cardAttacker.setHasAttacked(true);
            cardAttacker.specialAbility(cardAttacked);
            if (cardAttacked.getHealth() <= 0) {
                cardsOnTable.get(attacked.getX()).remove(attacked.getY());
            }
            return;
        }
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode objectNode = mapper.createObjectNode();
//        objectNode.put("command", "cardUsesAbility");
//        objectNode.putPOJO("cardAttacker", attacker);
//        objectNode.putPOJO("cardAttacked", attacked);
//        objectNode.put("error", error);
//        output.addPOJO(objectNode);
        Commands cardUsesAbility = new Commands("cardUsesAbility", "cardAttacker",
                "cardAttacked", attacker, attacked, error);
        cardUsesAbility.errorUseAbility(output);
    }

    /**
     * method that get a card at a given position
     * @param action the actions input
     */
    public void getCardPosition(final ActionsInput action, final ArrayNode output) {
        Coordinates cardPosition = new Coordinates(action.getX(), action.getY());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", "getCardAtPosition");
        objectNode.put("x", cardPosition.getX());
        objectNode.put("y", cardPosition.getY());
        if (cardPosition.getY() + 1 > cardsOnTable.get(cardPosition.getX()).size()) {
            objectNode.put("output", "No card available at that position.");
        } else {
            Minion copy = new Minion(cardsOnTable.get(cardPosition.getX()).
                    get(cardPosition.getY()));
            objectNode.putPOJO("output", copy);
        }
        output.addPOJO(objectNode);
    }

    /**
     * methot that attack a card
     * @param attacker the coordinates of the attacker
     * @param attacked the coordinates of the attacked
     * @param current the current player
     * @param opponent the opponent
     */
    public void attackCard(final Coordinates attacker, final Coordinates attacked,
                           final Player current, final Player opponent, final ArrayNode output) {
        Minion cardAttacked = this.getCardsOnTable().get(attacked.getX()).
                get(attacked.getY());
        Minion cardAttacker = this.getCardsOnTable().get(attacker.getX()).
                get(attacker.getY());
        String error = current.errorAttackCard(cardAttacker, cardAttacked, attacked,
                checkTank(opponent.getFirstRow()));
        if (error == null) {
            cardAttacker.setHasAttacked(true);
            if (cardAttacked.getHealth() <= cardAttacker.getAttackDamage()) {
                this.getCardsOnTable().get(attacked.getX()).remove(attacked.getY());
            }
            cardAttacked.setHealth(cardAttacked.getHealth() - cardAttacker.
                    getAttackDamage());
            return;
        }
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode objectNode = mapper.createObjectNode();
//        objectNode.put("command", "cardUsesAttack");
//        objectNode.putPOJO("cardAttacker", attacker);
//        objectNode.putPOJO("cardAttacked", attacked);
//        objectNode.put("error", error);
//        output.addPOJO(objectNode);
        Commands cardUsesAttack = new Commands("cardUsesAttack", "cardAttacker",
                "cardAttacked", attacker, attacked, error);
        cardUsesAttack.errorUseAbility(output);
    }

    /**
     * methot that attack a hero
     * @param attacker the coordinates of the attacker
     * @param opponent the opponent
     * @param current the current player
     */
    public void attackHero(final Coordinates attacker, final Player opponent,
                           final Player current, final ArrayNode output) {
        Minion cardAttacker = cardsOnTable.get(attacker.getX()).get(attacker.getY());
        String error = cardAttacker.errorAttackHero(checkTank(opponent.getFirstRow()));
        if (error == null) {
            Hero hero = opponent.getHero();
            cardAttacker.setHasAttacked(true);
            hero.setHealth(hero.getHealth() - cardAttacker.getAttackDamage());
            current.checkEndGame(opponent, output);
            return;
        }
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode objectNode = mapper.createObjectNode();
//        objectNode.put("command", "useAttackHero");
//        objectNode.putPOJO("cardAttacker", attacker);
//        objectNode.put("error", error);
//        output.addPOJO(objectNode);'
        Commands useAttackHero = new Commands("useAttackHero", "cardAttacker",
                attacker, error);
        useAttackHero.errorAttackHero(output);
    }
}
