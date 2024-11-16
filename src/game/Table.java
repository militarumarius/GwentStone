package game;

import cards.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}
