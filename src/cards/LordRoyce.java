package cards;

import fileio.CardInput;

import java.util.ArrayList;

public class LordRoyce extends Hero{
    public LordRoyce (CardInput card){
        super(card);
    }

    @Override
    public void specialHeroAbility(ArrayList<Minion> enemy){
        for(Minion minion : enemy)
            minion.setIsFrozen(true);
    }
}
