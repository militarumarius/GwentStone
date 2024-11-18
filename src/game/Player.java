package game;

import cards.*;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import fileio.CardInput;

import java.util.ArrayList;

public class Player {
    private Deck deck;
    private Hero hero;
    private int number;
    private ArrayList<Minion> hand;
    private boolean isPlayerTurn;
    private int mana;
    private int x;
    private int y;

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Hero getHero() {
        return hero;
    }

    public ArrayList<Minion> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Minion> hand) {
        this.hand = hand;
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

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void addCard(Minion card){
        Minion minion = new Minion(card);

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
        this.x = x;
        this.y = y;
        if(hero.getName().equals("Lord Royce")){
            this.hero = new LordRoyce(hero);
        } else if(hero.getName().equals("King Mudface") ){
            this.hero = new KingMudface(hero);
        } else if(hero.getName().equals("GeneralKociraw")){
            this.hero = new GeneralKocioraw(hero);
        } else if(hero.getName().equals("Empress Thorina")){
            this.hero = new EmpressThorina(hero);
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


}
