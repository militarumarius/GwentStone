package cards;

import fileio.CardInput;

import java.util.ArrayList;

public class KingMudface extends Hero{
    public KingMudface (CardInput card) {
        super(card);
    }
    @Override
    public void specialHeroAbility(ArrayList<Minion> enemy){
        for(Minion minion : enemy)
            minion.setHealth(minion.getHealth()+1);
    }

}
