package game;

import cards.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public class Player {
    private Deck deck;
    private Hero hero;
    private int number;
    private ArrayList<Minion> hand;
    private boolean isPlayerTurn;
    private int mana;
    private int firstRow;
    private int secondRow;
    private int numberWins;

    public int getNumberWins() {
        return numberWins;
    }

    public void setNumberWins(int numberWins) {
        this.numberWins = numberWins;
    }

    public int getSecondRow() {
        return secondRow;
    }


    public int getFirstRow() {
        return firstRow;
    }



    public Hero getHero() {
        return hero;
    }

    public ArrayList<Minion> getHand() {
        return hand;
    }


    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public Deck getDeck() {
        return deck;
    }

    public int getNumber() {
        return number;
    }

    public boolean getIsPlayerTurn() {
        return isPlayerTurn;
    }

    public void setIsPlayerTurn(boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
    }

    public Player(Deck deckPlayer, CardInput hero, int number, int x, int y) {
        this.deck = deckPlayer;
        this.number = number;
        this.hand = new ArrayList<>();
        this.isPlayerTurn = false;
        this.firstRow = x;
        this.secondRow = y;
        this.numberWins = 0;
        switch (hero.getName()) {
            case "Lord Royce" -> this.hero = new LordRoyce(hero);
            case "King Mudface" -> this.hero = new KingMudface(hero);
            case "General Kocioraw" -> this.hero = new GeneralKocioraw(hero);
            case "Empress Thorina" -> this.hero = new EmpressThorina(hero);
        }
    }

    public ArrayList<Minion> getCopyHand() {
        ArrayList<Minion> copyHand = new ArrayList<>(this.getHand().size());
        for(Minion card : this.getHand()){
            switch (card.getName()) {
                case "The Ripper" -> {
                    Ripper ripper = new Ripper(card);
                    copyHand.add(ripper);
                }
                case "Miraj" -> {
                    Miraj miraj = new Miraj(card);
                    copyHand.add(miraj);
                }
                case "The Cursed One" -> {
                    CursedOne cursedOne = new CursedOne(card);
                    copyHand.add(cursedOne);
                }
                case "Disciple" -> {
                    Disciple disciple = new Disciple(card);
                    copyHand.add(disciple);
                }
                default -> {
                    Minion minion = new Minion(card);
                    copyHand.add(minion);
                }
            }
        }
        return copyHand;
    }
    public void specialAttackHero(ArrayList<Minion> rowOnTable, int row, ArrayNode output){
        String error = "";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        if(this.mana < this.hero.getMana()){
            error = "Not enough mana to use hero's ability.";
        } else if (this.hero.getIsHasAttacked()) {
            error = "Hero has already attacked this turn.";
        } else if(this.hero.getName().equals("Lord Royce") || this.hero.getName().equals("Empress Thorina")) {
            if (row == this.firstRow || row == this.secondRow) {
                error = "Selected row does not belong to the enemy.";
            } else {
                this.mana -= this.hero.getMana();
                this.hero.setHasAttacked(true);
                this.hero.specialHeroAbility(rowOnTable);
                return;
            }
        }else{
            if (row != this.firstRow && row != this.secondRow) {
                error = "Selected row does not belong to the current player.";
            } else {
                this.mana -= this.hero.getMana();
                this.hero.setHasAttacked(true);
                this.hero.specialHeroAbility(rowOnTable);
                return;
            }
        }
        objectNode.put("command", "useHeroAbility");
        objectNode.put("affectedRow", row);
        objectNode.put("error", error);
        output.addPOJO(objectNode);
    }
    public void getCardInHand(){
        if (!(deck.getCards().isEmpty())) {
            hand.add(deck.getCards().get(0));
            deck.getCards().remove(0);
        }
    }

    public String errorPlaceCard(int handIdx, int firstRowSize, int SecondRowSize) {
        if (hand.size() < handIdx + 1)
            return null;
        if (mana < hand.get(handIdx).getMana()) {
            return "Not enough mana to place card on table.";
        }
        if(hand.get(handIdx).isTankSpecial() &&  firstRowSize == 5) {
            return "Cannot place card on table since row is full.";
        }
        if (!hand.get(handIdx).isTankSpecial() &&  SecondRowSize == 5) {
            return  "Cannot place card on table since row is full.";
        }
        return null;
    }
    public String errorAttackCard(Minion cardAttacker, Minion cardAttacked, Coordinates attacked, boolean checkOpponent) {
        if (attacked.x == secondRow|| attacked.x == firstRow) {
            return "Attacked card does not belong to the enemy.";
        }
        if (cardAttacker.getIsHasAttacked()) {
            return "Attacker card has already attacked this turn.";
        }
        if (cardAttacker.getIsFrozen()) {
            return "Attacker card is frozen.";
        }
        if (!cardAttacked.isTankSpecial() && checkOpponent) {
            return "Attacked card is not of type 'Tank'.";
        }
        return null;
    }

}
