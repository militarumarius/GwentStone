package cards;

import fileio.CardInput;

public class Disciple extends Minion{
    public Disciple (CardInput card) {
        super(card);
    }

    public Disciple (Minion minion) {
        super(minion);
    }
    public void godsPlan(Minion ally) {
        ally.setHealth(ally.getHealth() + 2);
    }
}
