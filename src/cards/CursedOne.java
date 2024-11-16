package cards;

import fileio.CardInput;

public class CursedOne extends Minion{
    public CursedOne(CardInput card) {
        super(card);
    }
    public CursedOne(Minion minion) {
        super(minion);
    }
    public void shapeshift(Minion enemy){
        if(enemy.getAttackDamage() == 0)
            enemy.setHealth(0);
        else{
            int aux = enemy.getHealth();
            enemy.setHealth(enemy.getAttackDamage());
            enemy.setAttackDamage(aux);
        }
    }
}
