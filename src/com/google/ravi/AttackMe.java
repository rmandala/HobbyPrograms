package com.google.ravi;

public class AttackMe {
	private static AttackMeGame game;
	
	public static AttackMeGame startNewGame(int numberOfPawns, boolean hasXes) {
		game = new AttackMeGame(11, false);
		
		return game;
	}

	public static MoveOutcome movePawn(int src, int dest) {
		return game.movePawn(src, dest);
	}

	public static MoveOutcome removePawn(int loc) {
		return game.removePawn(loc);
	}
	
	public static void main(String[] args) throws Exception {	
		int[] moves = {13, 18, 10, 11, 6, 14, 0, 7, 8, 16, 20, 17, 19, 23, 
				20, 20, 21, 22, 1, 4, 12, 3, 9, 6, 5, 7, 14, 6, 13, 14, 12, 
				13, 4, 12, 1, 2, 12, 4, 6, 13, 12, 14, 13, 2, 1, 3, 2};
		int totalPawns = 11;
		startNewGame(totalPawns, false);
		int i = 0, src, dest, currentPlayer = 1, moveNo = 0;
		MoveOutcome outcome;
				
		while(i < moves.length) {
			if(moveNo < totalPawns * 2) {
				dest = i++;
				outcome = movePawn(24, moves[dest]); 

				if(outcome.eventType == EventType.ATTACK && i<moves.length) {
					src = i++;
					outcome = removePawn(moves[src]); 
				}
			}
			else {
				if(i < moves.length - 1) {
					src = i++; dest = i++;
					outcome = movePawn(moves[src], moves[dest]);
					
					if(outcome.eventType == EventType.ATTACK) {
						System.out.println("Following positions are in ATTACK: " 
								+ outcome.attack[0] + ", " 
								+ outcome.attack[1] + ", " 
								+ outcome.attack[2]);
						if(i<moves.length) {
							src = i++;
							outcome = removePawn(moves[src]);
						}
						else {
							throw new IllegalArgumentException("Not enough moves!!!");
						}
					}
				}
				else {
					throw new IllegalArgumentException("Not enough moves!!!");
				}
			}
			
			switch(outcome.eventType) {
			case SUCCESSFUL:
				currentPlayer = (currentPlayer % 2) + 1;
				break;
			case GAME_OVER:
				System.out.println("Player#" + outcome.winningPlayer + " won. Congratulations!");
				if(i<moves.length) {
					System.out.println("Excess moves specified, though!!!");
				}
				return;
			case ERROR:
				System.out.println("FATAL: " + outcome.errorMessage);
				return;
			default:
				throw new Exception("Unknown status!!!");
			}
			moveNo++;
		}
		
		System.out.println("Not enough moves given. Game is adandoned in the middle.");
	}
}
