# GwentStone 
## Nume : Militaru Ionut-Marius 323CAb

### Project Description 
* The main goal of this project was to implement 
a two-player game, incorporating various concepts from Hearthstone and Gwent.


## Implemented Classes
* The functionalities for card types were implemented using the Cards class, 
which was extended by the Minion and Hero classes. For special cards, I created 
separate classes for each card type to allow adding new special cards at any time 
without modifying the base Hero and Minion classes.
* The Deck class holds a player’s deck, represented as an array of cards.
* The Player class contains the necessary information for a player and various 
methods that a player can perform (e.g., placing a card on the table).
* The Table class contains an array of arrays of cards representing 
the game table and various methods necessary for gameplay.
* The Game class is specific to a game, and the gameplay takes place in the 
playingGame method.
* The Commands class is used to output results in JSON format.
* The Leaderboard class keeps track of each player’s score.

## Gameplay description
* Each player is assigned a set of decks from which they choose one.
Cards come in different types, and each player has a hero representing them.
* The game is played in rounds, during which players can take multiple actions.
* The game ends when the opponent’s hero dies.

## Game Implementation
* For commands where cards need to attack or use different abilities, 
the action is first checked to see if it is valid and then the command is executed. 
* For displaying output, I create new objects specifically for the output 
representation. This is necessary because existing objects are modified during the 
game, and the output always includes a reference to the object state at that specific moment.
* To maintain the leaderboard, I created a single instance of the Leaderboard class 
in the main method, ensuring a centralized way to track each player's performance.
