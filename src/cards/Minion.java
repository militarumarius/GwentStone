package cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;

public class Minion extends Card{

    private int attackDamage;
    private int health;
    @JsonIgnore
    private boolean isFrozen;
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


    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setIsFrozen(boolean frozen){
        this.isFrozen = frozen;
    }

    public Minion(CardInput card) {
        super(card);
        this.health = card.getHealth();
        this.attackDamage = card.getAttackDamage();
        this.isFrozen = false;
        this.isTank = card.getName().equals("Warden") || card.getName().equals("Goliath")
                || card.getName().equals("The Ripper") || card.getName().equals("Miraj");

    }

    public Minion(Minion minion){
        super(minion);
        this.health = minion.getHealth();
        this.attackDamage = minion.getAttackDamage();
        this.isFrozen = minion.getIsFrozen();
        this.isTank = minion.getIsTank();
    }

}
