package cards;

import fileio.CardInput;

import java.util.ArrayList;

public class LordRoyce extends Hero {

    public LordRoyce(final CardInput card) {
        super(card);
    }

    /**
     * method that make the special ability of the LordRoyce
     */
    @Override
    public void specialHeroAbility(final ArrayList<Minion> enemy) {
        for (Minion minion : enemy) {
            minion.setIsFrozen(true);
        }
    }
}
