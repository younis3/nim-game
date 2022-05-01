import java.util.Scanner;

/**
 * The Competition class represents a Nim competition between two players, consisting of a given number of rounds.
 * It also keeps track of the number of victories of each player.
 */
public class Competition {

	Player player1;
	Player player2;
	boolean displayMessage;

	private int[] gameScores = new int[2];

    /**
     * Receives two Player objects, representing the two competing opponents, and a flag determining whether
     * messages should be displayed.
     * @param player1 The Player objects representing the first player.
     * @param player2 The Player objects representing the second player.
     * @param displayMessage a flag indicating whether game play messages should be printed to the console.
     */
	public Competition(Player player1, Player player2, boolean displayMessage){
		this.player1 = player1;
		this.player2 = player2;
		this.displayMessage = displayMessage;
	}


    /**
     * If playerPosition = 1, the results of the first player is returned. If playerPosition = 2, the result
     * of the second player is returned. If playerPosition equals neiter, -1 is returned.
     * messages should be displayed.
     * @return the number of victories of a player
     */
	public int getPlayerScore(int playerPosition){
		int playerScore = -1;
		if (playerPosition == 1 || playerPosition == 2){
			playerScore = gameScores[playerPosition - 1];
		}
		return playerScore;
	}


	/*
	 * Returns the integer representing the type of player 1; returns -1 on bad
	 * input.
	 */
	private static int parsePlayer1Type(String[] args){
		try{
			return Integer.parseInt(args[0]);
		} catch (Exception E){
			return -1;
		}
	}

	/*
	 * Returns the integer representing the type of player 2; returns -1 on bad
	 * input.
	 */
	private static int parsePlayer2Type(String[] args){
		try{
			return Integer.parseInt(args[1]);
		} catch (Exception E){
			return -1;
		}
	}

	/*
	 * Returns the integer representing the type of player 2; returns -1 on bad
	 * input.
	 */
	private static int parseNumberOfGames(String[] args){
		try{
			return Integer.parseInt(args[2]);
		} catch (Exception E){
			return -1;
		}
	}



    /**
     * Run the game for the given number of rounds.
     * @param numberOfRounds number of rounds to play.
     */
	public void playMultipleRounds(int numberOfRounds) {
       for (int i = 1; i <= numberOfRounds; i++) {
           Board board = new Board();
           if (displayMessage) {
               System.out.println("Welcome to the sticks game!");
           }
           Player curPlayer = player1;
           boolean showYourTurnMsg = true;  // to display the message "its now your turn" only ONCE.
           while (board.getNumberOfUnmarkedSticks() > 0) {
               if (displayMessage && showYourTurnMsg)
                   System.out.println("Player "+curPlayer.getPlayerId()+", it is now your turn!");

               Move move = curPlayer.produceMove(board);

               int boardMove = board.markStickSequence(move);
               if (boardMove == 0) {
                   if (displayMessage) {
                       System.out.println("Player " + curPlayer.getPlayerId() + " made the move: " + move);
                   }
               }
               else {
                   if (displayMessage) {
                       System.out.println("Invalid move. Enter another:");
                       showYourTurnMsg = false;
                       continue; // return to while loop to enter new move numbers
                   }
               }
               // change player turns
               if (curPlayer == player1) {
                   curPlayer = player2;
               } else {
                   curPlayer = player1;
               }
               showYourTurnMsg = true;
           }

           gameScores[curPlayer.getPlayerId() - 1] += 1;  // add one score to the winner in this round
           if (displayMessage) {
               System.out.println("Player " + curPlayer.getPlayerId() + " won!");
           }
       }
       System.out.println("The results are " + gameScores[0] + ":" + gameScores[1]);  // show the final result
   }


	/**
	 * The method runs a Nim competition between two players according to the three user-specified arguments.
	 * (1) The type of the first player, which is a positive integer between 1 and 4: 1 for a Random computer
	 *     player, 2 for a Heuristic computer player, 3 for a Smart computer player and 4 for a human player.
	 * (2) The type of the second player, which is a positive integer between 1 and 4.
	 * (3) The number of rounds to be played in the competition.
	 * @param args an array of string representations of the three input arguments, as detailed above.
	 */
	public static void main(String[] args) {
       int p1Type = parsePlayer1Type(args);
       int p2Type = parsePlayer2Type(args);
       int numGames = parseNumberOfGames(args);

       Scanner scanner = new Scanner(System.in);
       Player player1 = new Player(p1Type, 1, scanner);
       Player player2 = new Player(p2Type, 2, scanner);

       boolean dsplyMessage = false;

       if (p1Type == Player.HUMAN || p2Type == Player.HUMAN) { // show messages if at least there is one human
           dsplyMessage = true;
       }

       Competition competition = new Competition(player1, player2, dsplyMessage); // starts new competition
       System.out.println("Starting a Nim competition of " + numGames + " rounds between a "
               + player1.getTypeName() + " player and a " + player2.getTypeName() + " player.");

       competition.playMultipleRounds(numGames);  // run competition numGames of rounds

       scanner.close();
	}

}
