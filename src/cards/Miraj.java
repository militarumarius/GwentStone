package cards;

import fileio.CardInput;

public class Miraj extends Minion {
    public Miraj(CardInput card) {
        super(card);
    }
    public Miraj(Minion minion) {
        super(minion);
    }
    @Override
    public void specialAbility(Minion enemy) {
        int auxHealth = enemy.getHealth();;
        this.setHealth(enemy.getHealth());
        enemy.setHealth(auxHealth);
    }

}
