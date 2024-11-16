package game;

import java.util.ArrayList;

import cards.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;


public class Deck {
    @JsonIgnore
    private int numberCards;

    private ArrayList<Minion> cards;

    public int getNumberCards() {
        return numberCards;
    }

    public ArrayList<Minion> getCards() {
        return cards;
    }

    public Deck(ArrayList<CardInput> deck) {
        this.cards = new ArrayList<>(deck.size());
        this.numberCards = deck.size();
        for (CardInput card : deck) {
            if (card.getName().equals("The Ripper")) {
                Ripper ripper = new Ripper(card);
                this.cards.add(ripper);
            } else if (card.getName().equals("Miraj")) {
                Miraj miraj = new Miraj(card);
                this.cards.add(miraj);
            } else if (card.getName().equals("The Cursed One")) {
                CursedOne cursedOne = new CursedOne(card);
                this.cards.add(cursedOne);
            } else if (card.getName().equals("Disciple")) {
                Disciple disciple = new Disciple(card);
                this.cards.add(disciple);
            } else {
                Minion minion = new Minion(card);
                this.cards.add(minion);
            }
        }
    }

    public Deck(Deck deck){
        this.cards = new ArrayList<>(deck.numberCards);
        this.numberCards = deck.numberCards;
        for (Minion card : deck.cards) {
            if (card.getName().equals("The Ripper")) {
                Ripper ripper = new Ripper(card);
                this.cards.add(ripper);
            } else if (card.getName().equals("Miraj")) {
                Miraj miraj = new Miraj(card);
                this.cards.add(miraj);
            } else if (card.getName().equals("The Cursed One")) {
                CursedOne cursedOne = new CursedOne(card);
                this.cards.add(cursedOne);
            } else if (card.getName().equals("Disciple")) {
                Disciple disciple = new Disciple(card);
                this.cards.add(disciple);
            } else {
                Minion minion = new Minion(card);
                this.cards.add(minion);
            }
        }
    }

}
