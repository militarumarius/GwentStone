package cards;

import fileio.CardInput;

public class Ripper extends Minion{
    public Ripper (CardInput card){
        super(card);
    }
    public Ripper (Minion minion) {
        super(minion);
    }
    public void weakKnees(Minion enemy){
        int attack = enemy.getAttackDamage();
        if(attack < 2)
            enemy.setAttackDamage(0);
        else
            enemy.setAttackDamage(attack - 2);
    }
}
