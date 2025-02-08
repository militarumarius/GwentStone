package cards;

import fileio.CardInput;

public class Miraj extends Minion {

    public Miraj(final CardInput card) {
        super(card);
    }

    /**
     * copy constructor
     */
    public Miraj(final Minion minion) {
        super(minion);
    }

    /**
     * method that make the special ability of the Miraj
     */
    @Override
    public void specialAbility(final Minion enemy) {
        int auxHealth = this.getHealth();
        this.setHealth(enemy.getHealth());
        enemy.setHealth(auxHealth);
    }

}
