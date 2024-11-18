package cards;

import fileio.CardInput;

import java.util.ArrayList;

public class GeneralKocioraw extends Hero {
    public GeneralKocioraw (CardInput card){
        super(card);
    }
    @Override
    public void specialHeroAbility(ArrayList<Minion> enemy){
        for(Minion minion : enemy)
            minion.setAttackDamage(minion.getAttackDamage() + 1);
    }

}
