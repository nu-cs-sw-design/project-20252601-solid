package domain.game;

import java.util.List;
import java.util.Random;

public class Game {
	private int numberOfPlayers;
	private domain.game.GameType gameType;
	private domain.game.Deck deck;
	private domain.game.Player[] players;
	private Random rand;

	private TurnManager turnManager;


	private static final String PLAYER_HAND_EMPTY_EXCEPTION = "Player has no cards to steal";
	private static final String INVALID_PLAYER_INDEX_EXCEPTION = "Invalid player index.";
	private static final String INVALID_GAME_TYPE_EXCEPTION = "Must Provide a Valid Game Type";
	private static final String NO_PLAYERS_EXCEPTION = "No players to select from.";
	private static final String OUT_OF_BOUNDS_PLAYER_INDEX_EXCEPTION =
			"playerIndex out of Bounds";
	private static final String PLAYER_DEAD_EXCEPTION = "Player is dead";
	private static final String CARD_INDEX_OUT_OF_BOUNDS_EXCEPTION = "cardIndex out of Bounds";
	private static final String CARD_TYPE_NOT_FOUND_EXCEPTION =
			"Player does not have the card type to steal";
	private static final String INVALID_NUMBER_OF_PLAYERS_EXCEPTION =
			"Number of players must be between 2 and 5 inclusive";
	private static final String NUMBER_OF_TURNS_OUT_OF_BOUNDS_EXCEPTION =
			"Number of turns must be between 1 and 6.";

	public Game(int numberOfPlayers, GameType gameType,
				Deck deck, Player[] players, Random rand,
				List<Integer> attackQueue,
				int[] turnTracker) {

		this.numberOfPlayers = numberOfPlayers;
		this.gameType = gameType;
		this.deck = deck;
		this.players = players;
		this.rand = rand;
		this.turnManager = new TurnManager(numberOfPlayers, attackQueue, turnTracker);
	}


	public void swapTopAndBottom() {
		if (checkDeckHasOneCardOrLess()) {
			return;
		}
		Card bottomCard = deck.drawCardFromBottom();
		Card topCard = drawCard();
		deck.insertCard(bottomCard.getCardType(), 1, false);
		deck.insertCard(topCard.getCardType(), 1, true);
	}

	public Card stealRandomCard(int playerToStealFrom) {
		domain.game.Player player = players[playerToStealFrom];
		if (checkPlayerHandEmpty(player)) {
			throw new IllegalArgumentException(PLAYER_HAND_EMPTY_EXCEPTION);
		}

		int randomCardIndex = rand.nextInt(player.getHandSize());
		Card stealedCard = player.getCardAt(randomCardIndex);
		player.removeCardFromHand(randomCardIndex);
		addCardToHand(stealedCard);
		return stealedCard;
	}

	public void stealTypeCard(CardType cardType, int playerToStealFrom) {
		// Basic bounds check is still useful
		if (checkUserOutOfBounds(playerToStealFrom)) {
			throw new IllegalArgumentException(OUT_OF_BOUNDS_PLAYER_INDEX_EXCEPTION);
		}

		Player fromPlayer = getPlayerAtIndex(playerToStealFrom);
		Player currentPlayer = getCurrentPlayer();

		try {
			int cardIndex = fromPlayer.getIndexOfCard(cardType);
			Card stolenCard = fromPlayer.getCardAt(cardIndex);
			fromPlayer.removeCardFromHand(cardIndex);
			currentPlayer.addCardToHand(stolenCard);
		} catch (IllegalArgumentException e) {
			// Preserve existing behavior / message
			throw new IllegalArgumentException(CARD_TYPE_NOT_FOUND_EXCEPTION);
		}
	}



	public void startAttackPhase() {
		final int attackedAttackThreshold = 4;

		// Attack starts from whoever is currently active
		turnManager.setAttackCounter(getPlayerTurn());
		turnManager.resetNumberOfAttacks();

		while (!isAttackQueueEmpty()) {
			int attack = removeAttackQueue();
			if (attack <= attackedAttackThreshold) {
				playTargetedAttack(attack);
			} else {
				playAttack();
			}
		}

		int attackCounter = turnManager.getAttackCounter();
		int[] tracker = turnManager.getTurnTracker();

		// Mirror old logic: if they already had 1 turn, clear it first
		if (tracker[attackCounter] == 1) {
			tracker[attackCounter] = 0;
		}

		int numberOfAttacks = turnManager.getNumberOfAttacks();

		if (attackCounter == getPlayerTurn()) {
			// All attacks came back to current player
			int turns = turnManager.getCurrentTurns();
			turns += numberOfAttacks;
			turnManager.setCurrentTurns(turns);
			tracker[attackCounter] = 1;
			turnManager.setAttacked(false);
		} else {
			// Targeted attacks accumulate on another player
			tracker[attackCounter] += numberOfAttacks;
		}

		decrementNumberOfTurns();
		if (checkIfNumberOfTurnsIsZero()) {
			incrementPlayerTurn();
		}
	}

	public void playAttack() {
		incrementAttackCounter();
		addAttacks();
		turnManager.setAttacked(true);
	}

	public void playTargetedAttack(int attackedPlayerIndex) {
		setAttackCounter(attackedPlayerIndex);
		addAttacks();
		turnManager.setAttacked(true);
	}


	public boolean playExplodingKitten(int playerIndex) {
		if (checkUserOutOfBounds(playerIndex)) {
			throw new UnsupportedOperationException(INVALID_PLAYER_INDEX_EXCEPTION);
		}
		if (checkIfPlayerHasCard(playerIndex, CardType.DEFUSE)) {
			return false;
		}
		getPlayerAtIndex(playerIndex).setIsDead();
		if (playerIndex == getPlayerTurn()) {
			setCurrentPlayerNumberOfTurns(0);
		}
		return true;
	}

	public void playImplodingKitten() {
		setCurrentPlayerNumberOfTurns(0);
		getCurrentPlayer().setIsDead();
	}


	public void playDefuse(int idxToInsertExplodingKitten, int playerIndex) {
		if (checkUserOutOfBounds(playerIndex)) {
			throw new UnsupportedOperationException(INVALID_PLAYER_INDEX_EXCEPTION);
		}
		Player currentPlayer = getPlayerAtIndex(playerIndex);

		deck.insertExplodingKittenAtIndex(idxToInsertExplodingKitten);

		int defuseIdx = currentPlayer.getIndexOfCard(CardType.DEFUSE);
		currentPlayer.removeCardFromHand(defuseIdx);

		if (playerIndex == getPlayerTurn()) {
			setCurrentPlayerNumberOfTurns(0);
		}
	}

	public Card drawFromBottom() {
		return deck.drawCardFromBottom();
	}

	public void playCatomicBomb() {
		int numberOfBombs = deck.removeBombs();
		deck.insertCard(CardType.EXPLODING_KITTEN, numberOfBombs, false);
		decrementNumberOfTurns();
		if (checkIfNumberOfTurnsIsZero()) {
			incrementPlayerTurn();
		}
	}

	public void setNumberOfPlayers (int numberOfPlayers) {
		if (checkInvalidNumberOfPlayers(numberOfPlayers)) {
			throw new IllegalArgumentException
					(INVALID_NUMBER_OF_PLAYERS_EXCEPTION);
		}
		this.numberOfPlayers = numberOfPlayers;
		getDeck().setNumberOfPlayers(numberOfPlayers);
	}

	public void playReverse() {
		int startPointer = 0;
		int endPointer = numberOfPlayers - 1;

		turnManager.toggleReversed();   // instead of isReversed = !isReversed;

		while (startPointer < endPointer) {
			Player temporaryPlayerOne = players[startPointer];
			Player temporaryPlayerTwo = players[endPointer];
			players[startPointer] = temporaryPlayerTwo;
			players[endPointer] = temporaryPlayerOne;
			startPointer++;
			endPointer--;
		}

		// use turnManager instead of currentPlayerTurn directly
		int current = getPlayerTurn();
		current = numberOfPlayers - current - 1;
		setCurrentPlayerTurn(current);

		decrementNumberOfTurns();
		if (checkIfNumberOfTurnsIsZero()) {
			incrementPlayerTurn();
		}
	}

	public void retrieveGameMode(GameType gameType) {
		if (matchingGameType(gameType)) {
			throw new IllegalArgumentException(INVALID_GAME_TYPE_EXCEPTION);
		}
		this.gameType = gameType;
		deck.chooseGameType(gameType);
	}

	public domain.game.Player selectRandomPlayer() {
		int randomPlayerIndex = rand.nextInt(numberOfPlayers);
		if (hasZeroPlayers()) {
			throw new UnsupportedOperationException(NO_PLAYERS_EXCEPTION);
		}
		return players[randomPlayerIndex];
	}

	public void playShuffle(int numberOfShuffles) {
		for (int i = 0; i < numberOfShuffles; i++) {
			deck.shuffleDeck();
		}
	}


	public int playSkip(boolean superSkip) {
		if (checkIfNumberOfTurnsOutOfBounds()) {
			throw new UnsupportedOperationException(
					NUMBER_OF_TURNS_OUT_OF_BOUNDS_EXCEPTION);
		}
		if (superSkip) {
			setCurrentPlayerNumberOfTurns(0);
		} else {
			setCurrentPlayerNumberOfTurns(getNumberOfTurns() - 1);
		}
		if (checkIfNumberOfTurnsIsZero()) {
			incrementPlayerTurn();
		}
		return getNumberOfTurns();
	}


	public void playGarbageCollection(CardType cardToDiscard) {
		deck.insertCard(cardToDiscard, 1, false);
		deck.shuffleDeck();
	}

	public Deck getDeck() {
		return deck;
	}

	public void incrementPlayerTurn() {
		int idx = getPlayerTurn();
		do {
			idx = (idx + 1) % numberOfPlayers;
		} while (checkIfPlayerDead(idx));
		setCurrentPlayerTurn(idx);
	}


	public void incrementAttackCounter() {
		int idx = turnManager.getAttackCounter();
		do {
			idx = (idx + 1) % numberOfPlayers;
		} while (checkIfPlayerDead(idx));
		turnManager.setAttackCounter(idx);
	}


	public void setAttackCounter(int playerIndex) {
		turnManager.setAttackCounter(playerIndex);
	}

	GameType getGameTypeForTesting() {
		return gameType;
	}

	public GameType getGameType() {
		return gameType;
	}

	public domain.game.Player getPlayerAtIndex(int playerIndex) {
		return players[playerIndex];
	}

	public void addAttacks() {
		turnManager.incrementNumberOfAttacks();
		turnManager.incrementNumberOfAttacks();
	}


	public void playMark(int playerIndex, int cardIndex) {
		if (checkUserOutOfBounds(playerIndex)) {
			throw new IllegalArgumentException(OUT_OF_BOUNDS_PLAYER_INDEX_EXCEPTION);
		}
		if (checkIfPlayerDead(playerIndex)) {
			throw new IllegalArgumentException(PLAYER_DEAD_EXCEPTION);
		}
		if (checkCardOutOfBoundsIndexed(cardIndex, playerIndex)) {
			throw new IllegalArgumentException(CARD_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		}
		Card card = getPlayerAtIndex(playerIndex).getCardAt(cardIndex);
		card.markCard();

	}

	public void addAttackQueue(int attack) {
		turnManager.addAttack(attack);
	}

	public int removeAttackQueue() {
		return turnManager.removeAttack();
	}

	public boolean isAttackQueueEmpty() {
		return turnManager.isAttackQueueEmpty();
	}


	public void setPlayerNumberOfTurns() {
		turnManager.resetTurnsFromTrackerForCurrentPlayer();
	}


	public int getPlayerTurn() {
		return turnManager.getCurrentPlayerIndex();
	}


	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public int checkNumberOfAlivePlayers() {
		int counter = 0;
		for (int playerIndex = 0; playerIndex < numberOfPlayers; playerIndex++) {
			domain.game.Player player = getPlayerAtIndex(playerIndex);
			if (!player.getIsDead()) {
				counter++;
			}
		}
		return counter;
	}

	public void setCurrentPlayerNumberOfTurns(int numberOfTurns) {
		turnManager.setCurrentTurns(numberOfTurns);
	}

	public void decrementNumberOfTurns() {
		turnManager.decrementTurns();
	}

	public int getNumberOfTurns() {
		return turnManager.getCurrentTurns();
	}

	public int getDeckSize() {
		return deck.getDeckSize();
	}

	public List<Card> peekTopCards(int count) {
		return deck.peekTopCards(count);
	}


	public Card drawCard() {
		return deck.drawCard();
	}

	public Card getCardAtIndex(int cardIndex) {
		return deck.getCardAtIndex(cardIndex);
	}

	public void removeCardFromHand(int playerIndex, CardType cardType) {
		getPlayerAtIndex(playerIndex).removeCardFromHand(
				getIndexOfCardFromHand(playerIndex, cardType));
	}

	public int getIndexOfCardFromHand(int playerIndex, CardType cardType) {
		return getPlayerAtIndex(playerIndex)
				.getIndexOfCard(cardType);
	}

	public void addCardToHand(Card card) {
		getCurrentPlayer().addCardToHand(card);
	}


	public boolean checkIfPlayerDead(int playerIndex) {
		return getPlayerAtIndex(playerIndex).getIsDead();
	}

	public boolean checkIfPlayerHasCard(int playerIndex, CardType cardType) {
		return getPlayerAtIndex(playerIndex).hasCard(cardType);
	}

	public CardType getCardType(int playerIndex, int cardIndex) {
		return getPlayerAtIndex(playerIndex).getCardAt(cardIndex).getCardType();
	}

	public int getHandSize(int playerIndex) {
		return getPlayerAtIndex(playerIndex).getHandSize();
	}

	public CardType getDeckCardType(int deckIndex) {
		return getCardAtIndex(deckIndex).getCardType();
	}

	public boolean getIsReversed() {
		return turnManager.isReversed();
	}


	protected void setCurrentPlayerTurn(int turn) {
		turnManager.setCurrentPlayerIndex(turn);
	}


	private boolean matchingGameType (GameType gameType) {
		return gameType == GameType.NONE;
	}

	private boolean checkCardOutOfBoundsIndexed(int cardIndex, int playerIndex) {
		return cardIndex > getHandSize(playerIndex) - 1
				|| cardIndex < 0;
	}

	private boolean checkUserOutOfBounds(int userIndex) {
		return userIndex < 0 || userIndex >= getNumberOfPlayers();
	}

	private boolean checkDeckHasOneCardOrLess() {
		return deck.getDeckSize() <= 1;
	}

	private boolean checkPlayerHandEmpty(domain.game.Player player) {
		return player.getHandSize() == 0;
	}


	private boolean checkInvalidNumberOfPlayers(int numPlayers) {
		final int minPlayerThreshold = 1;
		final int maxPlayerThreshold = 6;
		return numPlayers <= minPlayerThreshold
				|| numPlayers >= maxPlayerThreshold;
	}

	private boolean hasZeroPlayers() {
		return numberOfPlayers == 0;
	}


	private boolean checkIfNumberOfTurnsOutOfBounds() {
		final int minNumberOfTurnsThreshold = 1;
		final int maxNumberOfTurnsThreshold = 6;
		int turns = turnManager.getCurrentTurns();
		return turns < minNumberOfTurnsThreshold
				|| turns > maxNumberOfTurnsThreshold;
	}


	private boolean checkIfNumberOfTurnsIsZero() {
		return turnManager.hasNoTurnsLeft();
	}

	public void setTurnToTargetedIndexIfAttackOccurred() {
		if (turnManager.getAttacked()) {
			turnManager.setAttacked(false);

			int newIndex = turnManager.getAttackCounter();
			setCurrentPlayerTurn(newIndex);

			if (checkIfPlayerDead(getPlayerTurn())) {
				incrementPlayerTurn();
			}
		}
	}


	public int getTurnCountOfPlayer(int playerIndex) {
		return turnManager.getTurnCountOfPlayer(playerIndex);
	}

	public boolean getAttacked() {
		return turnManager.getAttacked();
	}

	public int getAttackCounter() {
		return turnManager.getAttackCounter();
	}


	public int getNumberOfAttacks() {
		return turnManager.getNumberOfAttacks();
	}

	void setNumberOfAttacks(int numberOfAttacks) {
		// if you really need a setter:
		for (int i = 0; i < numberOfAttacks; i++) {
			turnManager.incrementNumberOfAttacks();
		}
	}


	void setAttacked(boolean attacked) {
		turnManager.setAttacked(attacked);
	}

	public Player getCurrentPlayer() {
		return players[getPlayerTurn()];
	}

	public List<Card> getCurrentPlayerHand() {
		return getCurrentPlayer().getHand();
	}

	public void playCardAtIndex(int index) {
		Player currentPlayer = getCurrentPlayer();

		if (index < 0 || index >= currentPlayer.getHandSize()) {
			throw new IllegalArgumentException("cardIndex out of Bounds");
		}

		Card cardToPlay = currentPlayer.getCardAt(index);
		currentPlayer.removeCardFromHand(index);

		cardToPlay.play(this, currentPlayer);
	}

	public void endTurn() {
		// One turn for the current player has finished.
		decrementNumberOfTurns();
		if (checkIfNumberOfTurnsIsZero()) {
			incrementPlayerTurn();

		}
	}

	public boolean isGameOver() {
		// Reuse your existing helper
		return checkNumberOfAlivePlayers() <= 1;
	}



}