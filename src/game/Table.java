package game;

import cards.*;
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

    public void addCard(Minion minion, int playerIdx) {

        if (playerIdx == ONE) {
            if (minion.isTankSpecial())
                cardsOnTable.get(2).add(minion);
            else
                cardsOnTable.get(3).add(minion);
        } else {
            if (minion.isTankSpecial())
                cardsOnTable.get(1).add(minion);
                else
                cardsOnTable.get(0).add(minion);
        }
    }

    public Table (Table table){
        cardsOnTable = new ArrayList<ArrayList<Minion>>();
        for (int i = 0; i < ROWS; i++) {
            ArrayList<Minion> rand = getCopyTableRows(table.getCardsOnTable().get(i));
            cardsOnTable.add(rand);
        }
    }
    private ArrayList<Minion> getCopyTableRows(ArrayList<Minion> row) {
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
    public boolean checkTank(int row) {
        for(Minion minion : cardsOnTable.get(row)){
            if(minion.getIsTank())
                return true;
        }
        return false;
    }
    public void unattackCards(){
        for (int i = 0; i < ROWS; i++) {
            for(Minion minion : cardsOnTable.get(i)){
                minion.setHasAttacked(false);
            }
        }
    }
    public void unfreezeCards(int x, int y){
        for (int i = x; i <= y; i++) {
            for(Minion minion : cardsOnTable.get(i)){
                minion.setIsFrozen(false);
            }
        }
    }
    public void placeCardOnTable(Player player, int handIdx, ArrayNode output, int x, int y) {
        int firstRow = this.getCardsOnTable().get(x).size();
        int secondRow = this.getCardsOnTable().get(y).size();
        String error =  player.errorPlaceCard(handIdx, firstRow, secondRow);
        if(error == null){
            this.addCard(player.getHand().get(handIdx), player.getNumber());
            player.setMana(player.getMana() - player.getHand().get(handIdx).getMana());
            player.getHand().remove(handIdx);
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", "placeCard");
        objectNode.put("handIdx", handIdx);
        objectNode.put("error", error);
        output.addPOJO(objectNode);
//        Comands getPlayerHero = new Comands(player, "getPlayerHero", copy);
//        getPlayerHero.playerOutput(output);
    }

    public ArrayList<Minion> getFrozenCards() {
        ArrayList<Minion> frozen = new ArrayList<Minion>();
        for (int i = 0; i < ROWS; i++) {
            ArrayList<Minion> rand = this.getCardsOnTable().get(i);
            if(!rand.isEmpty() && rand.get(0).getIsFrozen())
                for(Minion minion : rand)
                    if(minion.getIsFrozen())
                        frozen.add(minion);
        }
        return frozen;

    }

    public void useAbilityCard(Coordinates attacker, Coordinates attacked, Player opponent, ArrayNode output){
        String error = "";
        Minion cardAttacked = this.getCardsOnTable().get(attacked.x).get(attacked.y);
        Minion cardAttacker = this.getCardsOnTable().get(attacker.x).get(attacker.y);
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
                    this.getCardsOnTable().get(attacked.x).remove(attacked.y);
                return;
            }
        } else {
            if (attacked.x / 2 == attacker.x / 2) {
                error = "Attacked card does not belong to the enemy.";
            } else if (!cardAttacked.getIsTank() && this.checkTank(opponent.getFirstRow())) {
                error = "Attacked card is not of type 'Tank'.";
            } else {
                cardAttacker.setHasAttacked(true);
                cardAttacker.specialAbility(cardAttacked);
                if (cardAttacked.getHealth() <= 0)
                    this.getCardsOnTable().get(attacked.x).remove(attacked.y);
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
    public void getCardPosition(ActionsInput action, ArrayNode output) {
        Coordinates cardPosition = new Coordinates(action.getX(), action.getY());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", "getCardAtPosition");
        objectNode.put("x", cardPosition.x);
        objectNode.put("y", cardPosition.y);
        if (cardPosition.y + 1 > cardsOnTable.get(cardPosition.x).size()) {
            objectNode.put("output", "No card available at that position.");
        } else {
            Minion copy = new Minion(cardsOnTable.get(cardPosition.x).get(cardPosition.y));
            objectNode.putPOJO("output", copy);
        }
        output.addPOJO(objectNode);

    }

    public void attackCard(Coordinates attacker, Coordinates attacked, Player current, Player opponent, ArrayNode output) {
        Minion cardAttacked = this.getCardsOnTable().get(attacked.x).get(attacked.y);
        Minion cardAttacker = this.getCardsOnTable().get(attacker.x).get(attacker.y);
        String error = current.errorAttackCard(cardAttacker,cardAttacked,attacked,checkTank(opponent.getFirstRow()));
        if(error == null){
            cardAttacker.setHasAttacked(true);
            if (cardAttacked.getHealth() <= cardAttacker.getAttackDamage()) {
                this.getCardsOnTable().get(attacked.x).remove(attacked.y);
            }
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

    public void attackHero(Coordinates attacker, Player opponent, Player current,  ArrayNode output) {
        String error = "";
        Minion cardAttacker = cardsOnTable.get(attacker.x).get(attacker.y);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        if (cardAttacker.getIsFrozen()) {
            error = "Attacker card is frozen.";
        } else if (cardAttacker.getIsHasAttacked()) {
            error = "Attacker card has already attacked this turn.";
        } else if (this.checkTank(opponent.getFirstRow())) {
            error = "Attacked card is not of type 'Tank'.";
        } else {
            Hero hero = opponent.getHero();
            cardAttacker.setHasAttacked(true);
            hero.setHealth(hero.getHealth() - cardAttacker.getAttackDamage());
            if(opponent.getHero().getHealth() <= 0 && current.getNumber() == ONE && !hero.isStatusKilled()){
                objectNode.put("gameEnded", "Player one killed the enemy hero.");
                current.setNumberWins(current.getNumberWins() + 1);
                hero.setStatusKilled(true);
                output.addPOJO(objectNode);
            } else if(opponent.getHero().getHealth() <= 0 && current.getNumber() == TWO && !hero.isStatusKilled()){
                objectNode.put("gameEnded", "Player two killed the enemy hero.");
                hero.setStatusKilled(true);
                output.addPOJO(objectNode);
                current.setNumberWins(current.getNumberWins() + 1);
            }
            return;
        }
        objectNode.put("command", "useAttackHero");
        objectNode.putPOJO("cardAttacker", attacker);
        objectNode.put("error", error);
        output.addPOJO(objectNode);
    }
}
