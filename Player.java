import java.util.Random;
import java.util.Scanner;


/**
 * The Player class represents a player in the Nim game, producing Moves as a response to a Board state. Each player 
 * is initialized with a type, either human or one of several computer strategies, which defines the move he 
 * produces when given a board in some state. The heuristic strategy of the player is already implemented. You are 
 * required to implement the rest of the player types according to the exercise description.
 */
public class Player {

	//Constants that represent the different players.
	/** The constant integer representing the Random player type. */
	public static final int RANDOM = 1;
	/** The constant integer representing the Heuristic player type. */
	public static final int HEURISTIC = 2;
	/** The constant integer representing the Smart player type. */
	public static final int SMART = 3;
	/** The constant integer representing the Human player type. */
	public static final int HUMAN = 4;
	
	private static final int BINARY_LENGTH = 4;	//Used by produceHeuristicMove() for binary representation of board rows.
	
	private final int playerType;
	private final int playerId;
	private Scanner scanner;
	
	/**
	 * Initializes a new player of the given type and the given id, and an initialized scanner.
	 * @param type The type of the player to create.
	 * @param id The id of the player (either 1 or 2).
	 * @param inputScanner The Scanner object through which to get user input
	 * for the Human player type. 
	 */
	public Player(int type, int id, Scanner inputScanner){		
		// Check for legal player type (we will see better ways to do this in the future).
		if (type != RANDOM && type != HEURISTIC 
				&& type != SMART && type != HUMAN){
			System.out.println("Received an unknown player type as a parameter"
					+ " in Player constructor. Terminating.");
			System.exit(-1);
		}		
		playerType = type;	
		playerId = id;
		scanner = inputScanner;
	}
	
	/**
	 * @return an integer matching the player type.
	 */	
	public int getPlayerType(){
		return playerType;
	}
	
	/**
	 * @return the players id number.
	 */	
	public int getPlayerId(){
		return playerId;
	}
	
	
	/**
	 * @return a String matching the player type.
	 */
	public String getTypeName(){
		switch(playerType){
			
			case RANDOM:
				return "Random";			    
	
			case SMART: 
				return "Smart";	
				
			case HEURISTIC:
				return "Heuristic";
				
			case HUMAN:			
				return "Human";
		}
		//Because we checked for legal player types in the
		//constructor, this line shouldn't be reachable.
		return "UnknownPlayerType";
	}
	
	/**
	 * This method encapsulates all the reasoning of the player about the game. The player is given the 
	 * board object, and is required to return his next move on the board. The choice of the move depends
	 * on the type of the player: a human player chooses his move manually; the random player should 
	 * return some random move; the Smart player can represent any reasonable strategy; the Heuristic 
	 * player uses a strong heuristic to choose a move. 
	 * @param board - a Board object representing the current state of the game.
	 * @return a Move object representing the move that the current player will play according to his strategy.
	 */
	public Move produceMove(Board board){
		
		switch(playerType){
		
			case RANDOM:
				return produceRandomMove(board);

			case SMART:
				return produceSmartMove(board);
				
			case HEURISTIC:
				return produceHeuristicMove(board);
				
			case HUMAN:
				return produceHumanMove(board);

			//Because we checked for legal player types in the
			//constructor, this line shouldn't be reachable.
			default: 
				return null;			
		}
	}
	
	/*
	 * Produces a random move.
	 */
	private Move produceRandomMove(Board board){
		int rowNum = getValidRandomRow(board);  // uses helper method
		int leftNum = getValidRandomLeft(board, rowNum);  // uses helper method
		int rightNum = getValidRandomRight(board, rowNum, leftNum);  // uses helper method

		return new Move(rowNum, leftNum, rightNum);  // returns a valid move
	}

	/*
 	* Helper method for produceRandomMove
 	* This method gets a random integers for the row until it returns a valid one (the row number must
 	* exist, and there must be at least one unmarked stick in this row).
 	*/
	private int getValidRandomRow(Board board){
		int row;
		Random rowRandom = new Random();
		while (true){
			boolean isValid = false;
			row = 1 + rowRandom.nextInt(board.getNumberOfRows());
			for (int i=1; i<=board.getRowLength(row); i++){
				if(board.isStickUnmarked(row, i)) {
					isValid = true;
					// at least one unmarked stick in this row was found
					break;
				}
			}
			if(isValid) {
				return row;  // return valid row
			}
		}
	}

	/*
 	* Helper method for produceRandomMove
 	* This method gets the row number and random integers that represent the left stick of the row until it
 	* returns a valid number (it must be unmarked).
 	*/
	private int getValidRandomLeft(Board board, int rowNumber){
		int left;
		Random leftRandom = new Random();
		while (true){
			left = 1 + leftRandom.nextInt(board.getRowLength(rowNumber));
			if(board.isStickUnmarked(rowNumber, left)){
				return left;
			}
		}
	}

	/*
 	* Helper method for produceRandomMove
 	* This method gets the row number, left number that represent the left stick and random integers that
 	* represent the right stick of the row until it returns a valid number (left must be equal or smaller
 	* than right. and between the right and left there are no marked sticks.
 	*/
	private int getValidRandomRight(Board board, int rowNumber, int leftNumber){
		int right;
		Random rightRandom = new Random();
		while (true){
			boolean isValid = true;
			right = 1 + rightRandom.nextInt(board.getRowLength(rowNumber));
			if(right >= leftNumber){
				for (int i=leftNumber; i<=right;i++){
					if(!board.isStickUnmarked(rowNumber, i)){
						isValid = false;
						break;  // when not valid stick found exit this loop and return to while  to try another
								  // number.
					}
				}
			}
			else {
				isValid = false;
			}
			if(isValid){  // the random number that represents the right stick remained valid through the tests,
				return right;  // so return it as valid stick.
			}
		}
	}



	/*
	 * Produce some intelligent strategy to produce a move
	 * The strategy is to try to find two continuous unmarked sticks when the number of unmarked sticks is
	 * not even. otherwise it will create a random move.
	 */
	private Move produceSmartMove(Board board){
		if (board.getNumberOfUnmarkedSticks() % 2 == 0){
			return produceRandomMove(board);  // returns random move
		}
		else {
			int [] continuousArray = getContinuousSticks(board);  // using the helper method
			if (continuousArray[0] >= 1){  //row>0 (means there are two continuous sticks found)
				return new Move(continuousArray[0],continuousArray[1],continuousArray[2]);  // returns new move
			}
			else {
				return produceRandomMove(board);  // returns random move
			}
		}
	}

	/*
 	* Helper method for produceSmartMove
 	* This method try to find any two continuous sticks that is unmarked. if found return an array includes
 	* the row and the left and right sticks numbers. if not return an empty array
 	*/
	private int [] getContinuousSticks(Board board){
		int [] array = new int [3];
		for (int row = 1; row<=board.getNumberOfRows();row++){
			for(int stick=1; stick<=board.getRowLength(row);stick++){
				if(board.isStickUnmarked(row,stick) && board.isStickUnmarked(row,stick+1)){
					array[0] = row;
					array[1] = stick;  // left stick
					array[2] = stick + 1;  // right stick
					return array;
				}
			}
		}
		return array;  // empty array (row is < 1)
	}



	/*
	 * Interact with the user to produce his move.
	 */
	private Move produceHumanMove(Board board){
		while (true) {
			System.out.println("Press 1 to display the board. Press 2 to make a move:");
			int inputHuman = scanner.nextInt(); // gets input from user
			if (inputHuman == 1) {  // if input = 1. print the board
				System.out.println(board);
			} else if (inputHuman == 2) {  // if input = 2. ask and get the row, left and right numbers
				System.out.println("Enter the row number:");
				int rowNum = scanner.nextInt();
				System.out.println("Enter the index of the leftmost stick:");
				int leftNum = scanner.nextInt();
				System.out.println("Enter the index of the rightmost stick:");
				int rightNum = scanner.nextInt();

				return new Move(rowNum, leftNum, rightNum);  // return the move that the user chose
			}
			else {  // if input was not 1 neither 2. print error message
				System.out.println("Unsupported command");
			}
		}

	}



	/*
	 * Uses a winning heuristic for the Nim game to produce a move.
	 */
	private Move produceHeuristicMove(Board board){

		int numRows = board.getNumberOfRows();
		int[][] bins = new int[numRows][BINARY_LENGTH];
		int[] binarySum = new int[BINARY_LENGTH];
		int bitIndex,higherThenOne=0,totalOnes=0,lastRow=0,lastLeft=0,lastSize=0,lastOneRow=0,lastOneLeft=0;
		
		for(bitIndex = 0;bitIndex<BINARY_LENGTH;bitIndex++){
			binarySum[bitIndex] = 0;
		}
		
		for(int k=0;k<numRows;k++){
			
			int curRowLength = board.getRowLength(k+1);
			int i = 0;
			int numOnes = 0;
			
			for(bitIndex = 0;bitIndex<BINARY_LENGTH;bitIndex++){
				bins[k][bitIndex] = 0;
			}
			
			do {
				if(i<curRowLength && board.isStickUnmarked(k+1,i+1) ){
					numOnes++;
				} else {
					
					if(numOnes>0){
						
						String curNum = Integer.toBinaryString(numOnes);
						while(curNum.length()<BINARY_LENGTH){
							curNum = "0" + curNum;
						}
						for(bitIndex = 0;bitIndex<BINARY_LENGTH;bitIndex++){
							bins[k][bitIndex] += curNum.charAt(bitIndex)-'0'; //Convert from char to int
						}
						
						if(numOnes>1){
							higherThenOne++;
							lastRow = k +1;
							lastLeft = i - numOnes + 1;
							lastSize = numOnes;
						} else {
							totalOnes++;
						}
						lastOneRow = k+1;
						lastOneLeft = i;
						
						numOnes = 0;
					}
				}
				i++;
			}while(i<=curRowLength);
			
			for(bitIndex = 0;bitIndex<BINARY_LENGTH;bitIndex++){
				binarySum[bitIndex] = (binarySum[bitIndex]+bins[k][bitIndex])%2;
			}
		}
		
		
		//We only have single sticks
		if(higherThenOne==0){
			return new Move(lastOneRow,lastOneLeft,lastOneLeft);
		}
		
		//We are at a finishing state				
		if(higherThenOne<=1){
			
			if(totalOnes == 0){
				return new Move(lastRow,lastLeft,lastLeft+(lastSize-1) - 1);
			} else {
				return new Move(lastRow,lastLeft,lastLeft+(lastSize-1)-(1-totalOnes%2));
			}
			
		}
		
		for(bitIndex = 0;bitIndex<BINARY_LENGTH-1;bitIndex++){
			
			if(binarySum[bitIndex]>0){
				
				int finalSum = 0,eraseRow = 0,eraseSize = 0,numRemove = 0;
				for(int k=0;k<numRows;k++){
					
					if(bins[k][bitIndex]>0){
						eraseRow = k+1;
						eraseSize = (int)Math.pow(2,BINARY_LENGTH-bitIndex-1);
						
						for(int b2 = bitIndex+1;b2<BINARY_LENGTH;b2++){
							
							if(binarySum[b2]>0){
								
								if(bins[k][b2]==0){
									finalSum = finalSum + (int)Math.pow(2,BINARY_LENGTH-b2-1);
								} else {
									finalSum = finalSum - (int)Math.pow(2,BINARY_LENGTH-b2-1);
								}
								
							}
							
						}
						break;
					}
				}
				
				numRemove = eraseSize - finalSum;
				
				//Now we find that part and remove from it the required piece
				int numOnes=0,i=0;
				while(numOnes<eraseSize){

					if(board.isStickUnmarked(eraseRow,i+1)){
						numOnes++;
					} else {
						numOnes=0;
					}
					i++;
					
				}
				return new Move(eraseRow,i-numOnes+1,i-numOnes+numRemove);
			}
		}
		
		//If we reached here, and the board is not symmetric, then we only need to erase a single stick
		if(binarySum[BINARY_LENGTH-1]>0){
			return new Move(lastOneRow,lastOneLeft,lastOneLeft);
		}
		
		//If we reached here, it means that the board is already symmetric, and then we simply mark one stick from the last sequence we saw:
		return new Move(lastRow,lastLeft,lastLeft);		
	}
	
	
}
