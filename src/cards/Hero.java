package cards;

import fileio.CardInput;

public class Hero extends Card{

    private static final int MAX_HEALTH = 30;
    private int health;

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Hero(CardInput card) {
        super(card);
        this.health = MAX_HEALTH;
    }

    public Hero(Hero hero){
        super(hero);
        this.health = hero.getHealth();
    }

}

