package cards;

import fileio.CardInput;

import java.util.ArrayList;

public class KingMudface extends Hero {
    public KingMudface(final CardInput card) {
        super(card);
    }

    /**
     * method that make the special ability of the KingMudFace
     * @param enemy the enemy of the hero
     */
    @Override
    public void specialHeroAbility(final ArrayList<Minion> enemy) {
        for (Minion minion : enemy) {
            minion.setHealth(minion.getHealth() + 1);
        }
    }

}
