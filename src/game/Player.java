package game;

import cards.*;
import fileio.CardInput;

import java.util.ArrayList;

public class Player {
    private Deck deck;
    private Hero hero;
    private int number;
    private ArrayList<Minion> hand;
    private boolean isPlayerTurn;
    private int mana;

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

    public Player(Deck deckPlayer, CardInput hero, int number) {
        this.deck = deckPlayer;
        this.number = number;
        this.hand = new ArrayList<>();
        this.isPlayerTurn = false;
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

}
