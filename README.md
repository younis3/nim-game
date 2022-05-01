# nim-game
Ex1 of the Java OOP course


## File description
- Board.java - The Board class represents a board of the Nim game
- Player.java - The Player class represents a player in the Nim game
- Competition.java - The Competition class represents a Nim competition between two players
- Move.java - The Move class represents a move in the Nim game by a player
____________________


## Implementation notes
My implementation of the smart player explanation;
The strategy is to try to find two continuous unmarked sticks when the number of unmarked
sticks is not even. otherwise it will create a random move.
