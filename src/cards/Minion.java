package cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;

public class Minion extends Card{

    private int attackDamage;
    private int health;
    @JsonIgnore
    private boolean isFrozen;
    @JsonIgnore
    private boolean hasAttacked;
    @JsonIgnore
    private boolean isTank;


    public int getAttackDamage() {
        return attackDamage;
    }

    public int getHealth(){
        return health;
    }

    @JsonIgnore
    public boolean getIsTank() {
        return isTank;
    }

    public void setTank(boolean tank) {
        isTank = tank;
    }

    @JsonIgnore
    public boolean getIsFrozen() {
        return isFrozen;
    }
    @JsonIgnore
    public boolean getIsHasAttacked() {
        return hasAttacked;
    }


    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setIsFrozen(boolean frozen){
        this.isFrozen = frozen;
    }

    public void setHasAttacked(boolean hasAttacked){
        this.hasAttacked = hasAttacked;
    }

    public Minion(CardInput card) {
        super(card);
        this.health = card.getHealth();
        this.attackDamage = card.getAttackDamage();
        this.isFrozen = false;
        this.hasAttacked = false;
        this.isTank = card.getName().equals("Warden") || card.getName().equals("Goliath")
                || card.getName().equals("The Ripper") || card.getName().equals("Miraj");

    }

    public Minion(Minion minion){
        super(minion);
        this.health = minion.getHealth();
        this.attackDamage = minion.getAttackDamage();
        this.isFrozen = minion.getIsFrozen();
        this.hasAttacked = minion.getIsHasAttacked();
        this.isTank = minion.getIsTank();
    }

//    @Override
//    public ObjectNode makeOutput(ObjectNode outputNode) {
//        outputNode.put("mana", this.getMana());
//        outputNode.put("attackDamage ", this.getAttackDamage());
//        outputNode.put("health", this.getHealth());
//        outputNode.put("description", this.getDescription());
//        ArrayNode new_array_node_for_colors = outputNode.withArray("colors");
//        for (String color : this.getColors()) {
//            new_array_node_for_colors.add(color);
//        }
//        outputNode.put("name", this.getName());
//        return outputNode;
//    }
}
