package cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;

import java.util.ArrayList;

public class Card {

    private int mana;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    @JsonIgnore
    private boolean hasAttacked;
    @JsonIgnore
    public boolean getIsHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public int getMana() {

        return mana;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getColors() {

        return colors;
    }

    public String getName() {

        return name;
    }

    public void setMana(int mana) {

        this.mana = mana;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setColors(ArrayList<String> colors) {

        this.colors = colors;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Card (CardInput card) {
        this.mana = card.getMana();
        this.health = card.getHealth();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.name = card.getName();
    }

    public Card (Card card) {
        this.mana = card.getMana();
        this.description = card.getDescription();
        this.colors = new ArrayList<String>(card.getColors());
        this.name = card.getName();
        this.hasAttacked = card.hasAttacked;
    }

    public void specialAbility(Minion enemy){}

}
