package cards;

import fileio.CardInput;

public class Disciple extends Minion {

    public Disciple(final CardInput card) {
        super(card);
    }

    /**
     * copy constructor
     */
    public Disciple(final Minion minion) {
        super(minion);
    }

    /**
     * method that make the special ability of the Disciple
     */
    @Override
    public void specialAbility(final Minion ally) {
        ally.setHealth(ally.getHealth() + 2);
    }
}
