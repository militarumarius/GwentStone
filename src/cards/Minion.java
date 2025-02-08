package cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;
import game.Coordinates;

public class Minion extends Card {

    private int attackDamage;
    private int health;
    @JsonIgnore
    private boolean isFrozen;
    @JsonIgnore
    private boolean isTank;
    @JsonIgnore
    private boolean isTankSpecial;

    /**
     */
    @JsonIgnore
    public boolean isTankSpecial() {
        return isTankSpecial;
    }

    /**
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     */
    public int getHealth() {
        return health;
    }

    /**
     */
    @JsonIgnore
    public boolean getIsTank() {
        return isTank;
    }

    /**
     */
    public void setTank(final boolean tank) {
        isTank = tank;
    }

    /**
     */
    @JsonIgnore
    public boolean getIsFrozen() {
        return isFrozen;
    }

    /**
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     */
    public void setIsFrozen(final boolean frozen) {
        this.isFrozen = frozen;
    }


    public Minion(final CardInput card) {
        super(card);
        this.health = card.getHealth();
        this.attackDamage = card.getAttackDamage();
        this.isFrozen = false;
        this.isTank = card.getName().equals("Warden") || card.getName().equals("Goliath");
        this.isTankSpecial = card.getName().equals("Warden") || card.getName().equals("Goliath")
                || card.getName().equals("The Ripper") || card.getName().equals("Miraj");

    }

    /**
     * copy constructor
     */
    public Minion(final Minion minion) {
        super(minion);
        this.health = minion.getHealth();
        this.attackDamage = minion.getAttackDamage();
        this.isFrozen = minion.getIsFrozen();
        this.isTank = minion.getIsTank();
        this.isTankSpecial = minion.isTankSpecial();
    }

    /**
     * methot that check if the command use ability is not valid
     * @param attacker coordinates of the attacker
     * @param attacked coordinates of the attacked
     * @param cardAttacked the attacked card
     * @param checkTank check if the opponent has a tank card
     */
    public String errorUseAbility(final Coordinates attacker, final Coordinates attacked,
                                  final Minion cardAttacked, final boolean checkTank) {
        if (isFrozen) {
            return "Attacker card is frozen.";
        }
        if (this.getIsHasAttacked()) {
            return "Attacker card has already attacked this turn.";
        }
        if (this.getName().equals("Disciple")) {
            if ((attacked.getX() / 2 != attacker.getX() / 2)) {
                return "Attacked card does not belong to the current player.";
            }
            return null;
        }
        if (attacked.getX() / 2 == attacker.getX() / 2) {
            return "Attacked card does not belong to the enemy.";
        }
        if (!cardAttacked.getIsTank() && checkTank) {
            return "Attacked card is not of type 'Tank'.";
        }
        return null;
    }

    /**
     * methot that check if the command attack hero is not valid
     * @param checkTank check if the opponent has a tank card
     */
    public String errorAttackHero(final boolean checkTank) {
        if (isFrozen) {
            return  "Attacker card is frozen.";
        }
        if (this.getIsHasAttacked()) {
            return  "Attacker card has already attacked this turn.";
        }
        if (checkTank) {
            return  "Attacked card is not of type 'Tank'.";
        }
        return null;
    }
}
