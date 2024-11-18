package game;

import cards.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class Table {
    private ArrayList<ArrayList<Minion>> cardsOnTable;
    @JsonIgnore
    private final int ONE = 1;
    @JsonIgnore
    private final int TWO = 2;
    @JsonIgnore
    private final int ROWS = 4;
    @JsonIgnore
    private final int COLUMNS = 5;

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
            if (minion.getIsTank())
                cardsOnTable.get(2).add(minion);
            else
                cardsOnTable.get(3).add(minion);
        } else {
            if (minion.getIsTank())
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
            switch (card.getName()) {
                case "The Ripper" -> {
                    Ripper ripper = new Ripper(card);
                    copyRow.add(ripper);
                }
                case "Miraj" -> {
                    Miraj miraj = new Miraj(card);
                    copyRow.add(miraj);
                }
                case "The Cursed One" -> {
                    CursedOne cursedOne = new CursedOne(card);
                    copyRow.add(cursedOne);
                }
                case "Disciple" -> {
                    Disciple disciple = new Disciple(card);
                    copyRow.add(disciple);
                }
                default -> {
                    Minion minion = new Minion(card);
                    copyRow.add(minion);
                }
            }
        }
        return copyRow;
    }
    public boolean checkTank(int row) {
        for(Minion minion : cardsOnTable.get(row)){
            if(minion.isTankSpecial())
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
    public void unfreezeCards(){
        for (int i = 0; i < ROWS; i++) {
            for(Minion minion : cardsOnTable.get(i)){
                minion.setIsFrozen(false);
            }
        }
    }
    public void placeCard(Player player, int handIdx, ArrayNode output, int x, int y) {
        if (player.getHand().size() < handIdx + 1)
            return;

        String error = "";
        if (player.getMana() < player.getHand().get(handIdx).getMana()) {
            error = "Not enough mana to place card on table.";
        } else if (player.getHand().get(handIdx).getIsTank() && this.getCardsOnTable().get(x).size() == 5) {
            error = "Cannot place card on table since row is full.";
        } else if (!player.getHand().get(handIdx).getIsTank() && this.getCardsOnTable().get(y).size() == 5) {
            error = "Cannot place card on table since row is full.";
        } else {
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
    }
}
