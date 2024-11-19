package cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;

import java.util.ArrayList;

public class Hero extends Card {

    private static final int MAX_HEALTH = 30;
    private int health;
    @JsonIgnore
    private boolean statusKilled;

    /**
     */
    @JsonIgnore
    public boolean isStatusKilled() {
        return statusKilled;
    }

    /**
     */
    @JsonIgnore
    public void setStatusKilled(final boolean statusKilled) {
        this.statusKilled = statusKilled;
    }

    /**
     */
    public int getHealth() {
        return health;
    }

    /**
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    public Hero(final CardInput card) {
        super(card);
        this.health = MAX_HEALTH;
        this.statusKilled = false;
    }

    /**
     * copy constructor
     */
    public Hero(final Hero hero) {
        super(hero);
        this.health = hero.getHealth();
        this.statusKilled = hero.isStatusKilled();
    }

    /**
     * method required for extended classes
     */
    public void specialHeroAbility(final ArrayList<Minion> enemy) {

    }
}

