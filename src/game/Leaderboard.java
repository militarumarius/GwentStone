package game;


public class Leaderboard {
    private int numberOneWins = 0;
    private int numberTwoWins = 0;
    private int numberGames = 0;

    /**
     */
    public int getNumberOneWins() {
        return numberOneWins;
    }

    /**
     */
    public void setNumberOneWins(final int numberOneWins) {
        this.numberOneWins = numberOneWins;
    }

    /**
     */
    public int getNumberTwoWins() {
        return numberTwoWins;
    }

    /**
     */
    public void setNumberTwoWins(final int numberTwoWins) {
        this.numberTwoWins = numberTwoWins;
    }

    /**
     */
    public int getNumberGames() {
        return numberGames;
    }

    /**
     */
    public void setNumberGames(final int numberGames) {
        this.numberGames = numberGames;
    }

}
