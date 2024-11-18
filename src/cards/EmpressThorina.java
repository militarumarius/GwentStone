package cards;

import fileio.CardInput;

import java.util.ArrayList;

public class EmpressThorina extends Hero{
    public EmpressThorina (CardInput card) {
        super(card);
    }
    @Override
    public void specialHeroAbility(ArrayList<Minion> enemy){
        if(enemy.isEmpty())
            return;
        Minion max = enemy.get(0);
        for(Minion minion : enemy)
            if(max.getHealth() < minion.getHealth())
                max = minion;
        enemy.remove(max);
    }
}
