package cards;

import fileio.CardInput;

public class Disciple extends Minion{
    public Disciple (CardInput card) {
        super(card);
    }

    public Disciple (Minion minion) {
        super(minion);
    }
    @Override
    public void specialAbility(Minion ally) {
        ally.setHealth(ally.getHealth() + 2);
    }
}
