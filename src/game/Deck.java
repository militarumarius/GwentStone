package game;

import java.util.ArrayList;

import cards.Minion;
import cards.Disciple;
import cards.Miraj;
import cards.Ripper;
import cards.CursedOne;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;


public class Deck {
    @JsonIgnore
    private int numberCards;
    private ArrayList<Minion> cards;

    /**
     */
    public ArrayList<Minion> getCards() {
        return cards;
    }

    public Deck(final ArrayList<CardInput> deck) {
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

    /**
     * copy constructor for displaying the output
     * @param deck the deck to be copied
     */
    public Deck(final Deck deck) {
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
