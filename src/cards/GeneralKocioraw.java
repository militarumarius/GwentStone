package cards;

import fileio.CardInput;

import java.util.ArrayList;

public class GeneralKocioraw extends Hero {

    public GeneralKocioraw(final CardInput card) {
        super(card);
    }


    /**
     * method that make the special ability of the GeneralKocioraw
     */
    @Override
    public void specialHeroAbility(final ArrayList<Minion> enemy) {
        for (Minion minion : enemy) {
            minion.setAttackDamage(minion.getAttackDamage() + 1);
        }
    }

}
