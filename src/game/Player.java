package game;


import cards.Hero;
import cards.KingMudface;
import cards.LordRoyce;
import cards.Minion;
import cards.GeneralKocioraw;
import cards.EmpressThorina;
import cards.Disciple;
import cards.Miraj;
import cards.Ripper;
import cards.CursedOne;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public class Player {
    private final Deck deck;
    private Hero hero;
    private final int number;
    private final ArrayList<Minion> hand;
    private boolean isPlayerTurn;
    private int mana;
    private final int firstRow;
    private final int secondRow;
    private static final int MAX_CARDS_ROW = 5;

    /**
     */
    public int getSecondRow() {
        return secondRow;
    }

    /**
     */
    public int getFirstRow() {
        return firstRow;
    }

    /**
     */
    public Hero getHero() {
        return hero;
    }

    /**
     */
    public ArrayList<Minion> getHand() {
        return hand;
    }

    /**
     */
    public int getMana() {
        return mana;
    }

    /**
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     */
    public void setHero(final Hero hero) {
        this.hero = hero;
    }

    /**
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     */
    public int getNumber() {
        return number;
    }

    /**
     */
    public boolean getIsPlayerTurn() {
        return isPlayerTurn;
    }

    /**
     */
    public void setIsPlayerTurn(final boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
    }

    public Player(final Deck deckPlayer, final CardInput hero,
                   final int number, final int x, final int y) {
        this.deck = deckPlayer;
        this.number = number;
        this.hand = new ArrayList<>();
        this.isPlayerTurn = false;
        this.firstRow = x;
        this.secondRow = y;
        switch (hero.getName()) {
            case "Lord Royce" -> {
                this.hero = new LordRoyce(hero);
            }
            case "King Mudface" -> {
                this.hero = new KingMudface(hero);
            }
            case "General Kocioraw" -> {
                this.hero = new GeneralKocioraw(hero);
            }
            case "Empress Thorina" -> {
                this.hero = new EmpressThorina(hero);
            }
            default -> {
                this.hero = new Hero(hero);
            }
        }
    }

    /**
     * copy player's hand to print object
     */
    public ArrayList<Minion> getCopyHand() {
        ArrayList<Minion> copyHand = new ArrayList<>(this.getHand().size());
        for (Minion card : this.getHand()) {
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

    /**
     * method for hero attack command
     * @param rowOnTable the row that need to attack
     * @param row the idx of the row
     * @param output array node to display the objects
     */
    public void specialAttackHero(final ArrayList<Minion> rowOnTable, final int row,
                                  final ArrayNode output) {
        String error = errorHeroAbility(row);
        if (error == null) {
            mana -= hero.getMana();
            hero.setHasAttacked(true);
            hero.specialHeroAbility(rowOnTable);
            return;
        }
        Commands useHeroAbility = new Commands("useHeroAbility", row, error);
        useHeroAbility.errorCommandHero(output);
    }

    /**
     * method for getting  a card in hand from deck
     */
    public void getCardInHand() {
        if (!(deck.getCards().isEmpty())) {
            hand.add(deck.getCards().get(0));
            deck.getCards().remove(0);
        }
    }

    /**
     * method that checks if the game is over
     * @param opponent player's oopponent
     * @param output array node to display the object
     */
    public void checkEndGame(final Player opponent, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        if (opponent.getHero().getHealth() <= 0) {
            opponent.getHero().setStatusKilled(true);
            if (number == 1) {
                objectNode.put("gameEnded", "Player one killed the enemy hero.");
            } else {
                objectNode.put("gameEnded", "Player two killed the enemy hero.");
            }
            output.addPOJO(objectNode);
        }
    }

    /**
     * method that checks if we have an error at place card command
     * @param handIdx index of the card in hand
     * @param firstRowSize first player row
     * @param secondRowSize second player's row
     * @return error
     */
    public String errorPlaceCard(final int handIdx, final int firstRowSize,
                                 final int secondRowSize) {
        if (hand.size() < handIdx + 1) {
            return null;
        }
        if (mana < hand.get(handIdx).getMana()) {
            return "Not enough mana to place card on table.";
        }
        if (hand.get(handIdx).isTankSpecial() &&  firstRowSize == MAX_CARDS_ROW) {
            return "Cannot place card on table since row is full.";
        }
        if (!hand.get(handIdx).isTankSpecial() &&  secondRowSize == MAX_CARDS_ROW) {
            return  "Cannot place card on table since row is full.";
        }
        return null;
    }

    /**
     * method that checks if we have an error at Attack card command
     * @param cardAttacker the card that attack
     * @param cardAttacked the card that is attacked
     * @param attacked the coordinates of attacked card
     * @param checkOpponent the opponent tank check
     * @return error obtained
     */
    public String errorAttackCard(final Minion cardAttacker, final Minion cardAttacked,
                                  final Coordinates attacked, final boolean checkOpponent) {
        if (attacked.getX() == secondRow || attacked.getX() == firstRow) {
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

    /**
     * method that checks if we have an error at use Hero Ability command
     * @param row the row where the hero needs to do is special ability
     * @return  error obtained
     */
    public String errorHeroAbility(final int row) {
        if (mana < hero.getMana()) {
            return "Not enough mana to use hero's ability.";
        }
        if (hero.getIsHasAttacked()) {
            return "Hero has already attacked this turn.";
        }
        if (hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina")) {
            if (row == firstRow || row == secondRow) {
                return "Selected row does not belong to the enemy.";
            }
            return null;
        }
        if (row != firstRow && row != secondRow) {
                return  "Selected row does not belong to the current player.";
        }
        return null;
    }
}
