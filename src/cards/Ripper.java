package cards;

import fileio.CardInput;

public class Ripper extends Minion {

    public Ripper(final CardInput card) {
        super(card);
    }

    /**
     * copy constructor
     */
    public Ripper(final Minion minion) {
        super(minion);
    }

    /**
     * method that make the special ability of the Ripper
     */
    @Override
    public void specialAbility(final Minion enemy) {
        int attack = enemy.getAttackDamage();
        if (attack < 2) {
            enemy.setAttackDamage(0);
            return;
        }
        enemy.setAttackDamage(attack - 2);
    }
}
