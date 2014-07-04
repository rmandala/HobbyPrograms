package com.google.ravi;

import java.io.IOException;

enum TURN_TYPE { 
	NOTHING, STRIKE, DOUBLE, SPARE;
}

class TurnState {
	public TURN_TYPE pending;
	public int score;

	protected TurnState(TURN_TYPE type, int postTurnScore) {
		pending = type;
		score = postTurnScore;
	}
	
	public TurnState() {
		pending = TURN_TYPE.NOTHING;
		score = 0;
	}
}

class OneTurn {
	public OneTurn(int throw1, int throw2, int throw3) {
		super();
		this.throw1 = throw1;
		this.throw2 = throw2;
		this.throw3 = throw3;
	}

	public int throw1, throw2, throw3;

	public void turnScore(TurnState currentState, boolean lastTurn)
			throws IllegalArgumentException, UnknownError {
		int turnScore;

		if (lastTurn) {
			if (throw1 < 0 || throw2 < 0 || throw3 < 0
					|| throw1 > 10 || throw2 > 10 || throw3 > 10
					|| (throw1 != 10 && throw1 + throw2 > 10)
					|| (throw1 < 10 && throw2 != (10 - throw1) && throw3 > 0)) {
				throw new IllegalArgumentException("Impossible last turn score found!!!");
			}
			turnScore = throw1 + throw2 + throw3;

			switch (currentState.pending) {
			case NOTHING:
				currentState.pending = TURN_TYPE.NOTHING;
				currentState.score += turnScore;
				break;
			case SPARE:
				currentState.pending = TURN_TYPE.NOTHING;
				currentState.score += (turnScore + throw1);
				break;
			case STRIKE:
				currentState.pending = TURN_TYPE.NOTHING;
				currentState.score += (turnScore + throw1 + throw2);
				break;
			case DOUBLE:
				currentState.pending = TURN_TYPE.NOTHING;
				currentState.score += (turnScore + throw1 + throw1 + throw2);
				break;
			default:
				throw new UnknownError("Impossible case encountered!!!");
			}
		} else {
			TURN_TYPE turnType;

			if (throw1 < 0 || throw1 > 10 || throw2 < 0
					|| throw2 > (10 - throw1) || throw3 != 0) {
				throw new IllegalArgumentException("Impossible turn score found!!!");
			}

			turnScore = throw1 + throw2;

			if (throw1 == 10) {
				turnType = TURN_TYPE.STRIKE;
			} 
			else if (throw1 + throw2 == 10) {
				turnType = TURN_TYPE.SPARE;
			}
			else {
				turnType = TURN_TYPE.NOTHING;
			}

			switch (currentState.pending) {
			case NOTHING:
				currentState.pending = turnType;
				currentState.score += turnScore;
				break;
			case SPARE:
				currentState.pending = turnType;
				currentState.score += (throw1 * 2 + throw2);
				break;
			case STRIKE:
				if (turnType == TURN_TYPE.STRIKE) {
					currentState.pending = TURN_TYPE.DOUBLE;
					currentState.score += (turnScore * 2);
				} else {
					currentState.pending = turnType;
					currentState.score += (turnScore * 2);
				}
				break;
			case DOUBLE:
				if (turnType == TURN_TYPE.STRIKE) {
					currentState.pending = TURN_TYPE.DOUBLE;
					currentState.score += (throw1 + turnScore * 2);
				} else {
					currentState.pending = turnType;
					currentState.score += (throw1 + turnScore * 2);
				}
				break;
			default:
				throw new UnknownError("Impossible case encountered!!!");
			}
		}
	}
}

public class BowlingScore {
	public static int returnFinalScore(int[][] myThrows, int rounds) {
		TurnState current = new TurnState();

		for(int x = 0; x < rounds; x++) {
			new OneTurn(myThrows[x][0], myThrows[x][1], myThrows[x][2]).turnScore(current, x==(rounds-1));
			System.out.println("Score after turn#" + (x+1) + " is " + current.score);
		}

		return current.score;
	}

	public static int[][] decodeThrows(int[] myThrowsRaw) {
		int pos = 0;
		int maxRounds = myThrowsRaw[pos++];
		
		if(maxRounds < 0 || maxRounds > 10) {
			maxRounds = 10;
		}
		
		int[][] decodedThrows = new int[maxRounds][3];  

		for(int round = 1; round <= maxRounds; round++) {
			int throw1 = 0, throw2 = 0, throw3 = 0;
			
			throw1 = myThrowsRaw[pos++];
			if(throw1 == 10) {
				if(round == maxRounds) {
					throw2 = myThrowsRaw[pos++];
					if(throw2 < 0 || throw2 > 10) {
						throw new UnknownError("Impossible case encountered!!!");
					}
					throw3 = myThrowsRaw[pos++];
					if(throw3 < 0 || throw2+throw3 > 10) {
						throw new UnknownError("Impossible case encountered!!!");
					}
				}
			}
			else if(throw1 >= 0 && throw1 <= 9) {
				throw2 = myThrowsRaw[pos++];
				if(throw2 < 0 || throw2 > 10-throw1) {
					throw new UnknownError("Impossible case encountered!!!");
				}
				else if(throw2 + throw1 == 10) {
					if(round == maxRounds) {
						throw3 = myThrowsRaw[pos++];
						if(throw3 < 0 || throw3 > 10) {
							throw new UnknownError("Impossible case encountered!!!");
						}
					}
				}
			}
			else {
				throw new UnknownError("Impossible case encountered!!!");
			}
			
			decodedThrows[round-1][0] = throw1;
			decodedThrows[round-1][1] = throw2;
			decodedThrows[round-1][2] = throw3;
		}
		
		if(pos != myThrowsRaw.length) {
			throw new UnknownError("Impossible case encountered!!!");
		}

		return decodedThrows;
	}
	
	public static void main(String[] args) throws IOException {
		//{{6,4,0}, {10,0,0}, {0,10,3}, {4,5,0}, {10,0,0}, {0,0,0}, {10,0,0}, {0,0,0}, {0,0,0}, {0,0,0}};
		//{10, 6, 4, 10, 0, 10, 4, 5, 10, 0, 0, 10, 0, 0, 0, 0, 10, 0, 10};
		//{3, 10, 3, 6, 6, 4, 10};
		
		int myThrowsRaw[] = {3, 10, 3, 6, 6, 4, 10};
		int myThrows[][] = decodeThrows(myThrowsRaw); 

		int maxRounds = myThrowsRaw[0];
		if(maxRounds < 0 || maxRounds > 10) {
			maxRounds = 10;
		}
		
		System.out.println("Final score is... " + returnFinalScore(myThrows, maxRounds));
		
		simulateLiveScoring(myThrowsRaw);
	}

	/**
	 * @param myThrowsRaw
	 * @param maxRounds
	 * @throws UnknownError
	 * @throws IllegalArgumentException
	 */
	private static void simulateLiveScoring(int[] myThrowsRaw)
			throws UnknownError, IllegalArgumentException {
		int pos = 0;
		int maxRounds = myThrowsRaw[pos++];

		if(maxRounds < 0 || maxRounds > 10) {
			maxRounds = 10;
		}
		System.out.println("Now let's try live score. We'll have a total of " + maxRounds + " round(s). " +
				"Don't you bail on me. Now, let's start!!!");
		
		TurnState currentState = new TurnState();
		for(int round = 1; round <= maxRounds; round++) {
			int throw1 = 0, throw2 = 0, throw3 = 0;
			
			System.out.println("Tell me what happened in first shot of round#" + round);
			throw1 = myThrowsRaw[pos++];
			if(throw1 == 10) {
				if(round == maxRounds) {
					System.out.println("Awesome... that's a STRIKE in first shot of last round!");
					System.out.println("You get two more turns. Have fun!!!");
					
					throw2 = myThrowsRaw[pos++];
					if(throw2 < 0 || throw2 > 10) {
						throw new UnknownError("Impossible case encountered!!!");
					}
					throw3 = myThrowsRaw[pos++];
					if(throw3 < 0 || throw2+throw3 > 10) {
						throw new UnknownError("Impossible case encountered!!!");
					}
				}
				else {
					System.out.println("Awesome... that's a STRIKE!");
				}

				new OneTurn(throw1, throw2, throw3).turnScore(currentState, round == maxRounds);
			}
			else if(throw1 >= 0 && throw1 <= 9) {
				System.out.println("Go for one more shot.");
				
				throw2 = myThrowsRaw[pos++];
				if(throw2 < 0 || throw2 > 10-throw1) {
					throw new UnknownError("Impossible case encountered!!!");
				}
				else if(throw2 + throw1 == 10) {
					if(round == maxRounds) {
						System.out.println("Excellent... that's a SPARE in last round!");
						System.out.println("You get one more shot. Go for it.");

						throw3 = myThrowsRaw[pos++];
						if(throw3 < 0 || throw3 > 10) {
							throw new UnknownError("Impossible case encountered!!!");
						}
					}
					else {
						System.out.println("Nice... that's a SPARE.");
					}
				}
				else {
					System.out.println("Keep it up.");
				}

				new OneTurn(throw1, throw2, throw3).turnScore(currentState, round == maxRounds);
			}
			else {
				throw new UnknownError("Impossible case encountered!!!");
			}
			
			System.out.println("Score after round#" + round + " is " + currentState.score 
					+ " with " + currentState.pending.name() + " pending.");
		}
		System.out.println("Good game!!!");
	}
}
