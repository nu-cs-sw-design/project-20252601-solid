package model;

import java.util.List;

public class TurnManager {
	private int currentPlayerTurn;
	private int currentPlayerNumberOfTurns;
	private boolean isReversed;
	private List<Integer> attackQueue;
	private int attackCounter;
	private int numberOfAttacks;
	private int[] turnTracker;
	private boolean attacked;

	public TurnManager(int numberOfPlayers,
					   List<Integer> attackQueue,
					   int[] turnTracker) {
		this.currentPlayerTurn = 0;
		this.currentPlayerNumberOfTurns = 0;
		this.isReversed = false;
		this.attackQueue = attackQueue;
		this.turnTracker = turnTracker;
		this.attackCounter = 0;
		this.numberOfAttacks = 0;
		this.attacked = false;
	}


	// --- basic getters/setters  ---

	public int getCurrentPlayerIndex() {
		return currentPlayerTurn;
	}

	public void setCurrentPlayerIndex(int index) {
		this.currentPlayerTurn = index;
	}

	public int getCurrentTurns() {
		return currentPlayerNumberOfTurns;
	}

	public void setCurrentTurns(int turns) {
		this.currentPlayerNumberOfTurns = turns;
	}

	public void decrementTurns() {
		currentPlayerNumberOfTurns--;
	}

	public boolean hasNoTurnsLeft() {
		return currentPlayerNumberOfTurns == 0;
	}

	public boolean isReversed() {
		return isReversed;
	}

	public void toggleReversed() {
		isReversed = !isReversed;
	}

	// --- attack queue helpers ---

	public void addAttack(int attack) {
		attackQueue.add(attack);
	}

	public int removeAttack() {
		return attackQueue.remove(0);
	}

	public boolean isAttackQueueEmpty() {
		return attackQueue.isEmpty();
	}

	// --- attack tracking ---

	public int getAttackCounter() {
		return attackCounter;
	}

	public void setAttackCounter(int attackCounter) {
		this.attackCounter = attackCounter;
	}

	public int getNumberOfAttacks() {
		return numberOfAttacks;
	}

	public void incrementNumberOfAttacks() {
		numberOfAttacks++;
	}

	public void resetNumberOfAttacks() {
		numberOfAttacks = 0;
	}

	// --- turn tracker ---

	public int[] getTurnTracker() {
		return turnTracker;
	}

	public void resetTurnsFromTrackerForCurrentPlayer() {
		currentPlayerNumberOfTurns = turnTracker[currentPlayerTurn];
		turnTracker[currentPlayerTurn] = 1;
	}

	public int getTurnCountOfPlayer(int playerIndex) {
		return turnTracker[playerIndex];
	}

	// --- attack flag ---

	public boolean getAttacked() {
		return attacked;
	}

	public void setAttacked(boolean attacked) {
		this.attacked = attacked;
	}
}
