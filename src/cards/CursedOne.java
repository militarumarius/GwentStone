package cards;

import fileio.CardInput;

public class CursedOne extends Minion {

    public CursedOne(final CardInput card) {
        super(card);
    }

    /**
     * copy constructor
     */
    public CursedOne(final Minion minion) {
        super(minion);
    }

    /**
     * method that make the special ability of the CursedOne
     */
    @Override
    public void specialAbility(final Minion enemy) {
            int aux = enemy.getHealth();
            enemy.setHealth(enemy.getAttackDamage());
            enemy.setAttackDamage(aux);
    }
}
