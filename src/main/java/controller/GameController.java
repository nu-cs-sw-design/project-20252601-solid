package controller;

import model.Game;
import model.Player;
import model.Card;
import model.CardType;
import model.SeeTheFutureCard;
import view.GameUI;

import java.util.List;

public class GameController {
	private final Game game;
	private final GameUI ui;

	public GameController(Game game, GameUI ui) {
		this.game = game;
		this.ui = ui;
	}

	// Main loop: run until only one player is alive
	public void runGame() {
		while (!game.isGameOver()) {
			playSingleTurn();
		}

		// Find the winner (last alive player)
		Player winner = null;
		for (int i = 0; i < game.getNumberOfPlayers(); i++) {
			if (!game.checkIfPlayerDead(i)) {
				winner = game.getPlayerAtIndex(i);
				break;
			}
		}

		ui.displayGameOver(winner);
	}

	private void playSingleTurn() {
		// If no turns are currently set, initialize them
		if (game.getNumberOfTurns() == 0) {
			// If there was an Attack, jump to the targeted player
			game.setTurnToTargetedIndexIfAttackOccurred();
			// Load that player's turn count from the tracker
			game.setPlayerNumberOfTurns();
		}

		boolean turnFinished = false;

		while (!turnFinished && !game.isGameOver()) {
			Player currentPlayer = game.getCurrentPlayer();
			List<Card> hand = game.getCurrentPlayerHand();
			int turns = game.getNumberOfTurns();

			// Show state at start of this (sub)turn
			ui.displayTurnStart(currentPlayer, hand, turns);

			// Ask if they want to end their turn
			boolean wantsToEnd = ui.promptEndTurn();
			if (wantsToEnd) {
				// Exploding Kittens rule: at end of turn, you draw 1 card
				handleEndTurnDraw(currentPlayer);
				// Let Game advance turns / next player
				game.endTurn();
				turnFinished = true;
			} else {
				// They want to play a card
				int cardIndex = ui.promptPlayCardIndex();

				try {
					Card cardToPlay = currentPlayer.getCardAt(cardIndex);
					CardType type = cardToPlay.getCardType();

					// Give other players a chance to NOPE certain actions
					boolean canceledByNope = handleNopeWindow(type, currentPlayer);
					if (!canceledByNope) {
						// Actually play the card: removes from hand + calls card.play(...)
						game.playCardAtIndex(cardIndex);

						// Any extra UI behavior that depends on the card type
						handlePostPlayEffects(type, cardToPlay);
					}
				} catch (IllegalArgumentException | UnsupportedOperationException e) {
					ui.displayError(e.getMessage());
				}
			}
		}
	}

	/**
	 * End-of-turn rule: draw one card.
	 * If it's EXPLODING_KITTEN, resolve explode/defuse.
	 * Otherwise, add it to the player's hand.
	 */
	private void handleEndTurnDraw(Player currentPlayer) {
		Card drawn = game.drawCard();
		ui.displayCardDrawn(currentPlayer, drawn);

		if (drawn.getCardType() == CardType.EXPLODING_KITTEN) {
			resolveExplodingKitten(currentPlayer);
		} else {
			// Normal draw: card joins the player's hand
			game.addCardToHand(drawn);
		}
	}

	/**
	 * Uses Game logic to resolve an EXPLODING_KITTEN draw:
	 * - if player has no DEFUSE, they explode and die
	 * - if they have a DEFUSE, controller asks where to put the bomb back.
	 */
	private void resolveExplodingKitten(Player currentPlayer) {
		int playerIndex = currentPlayer.getPlayerID();
		boolean exploded = game.playExplodingKitten(playerIndex);

		if (exploded) {
			// No DEFUSE -> player eliminated
			ui.displayPlayerExploded(currentPlayer);
		} else {
			// They had a DEFUSE; ask where to re-insert the bomb
			ui.displayExplodingKittenDefused(currentPlayer);

			int deckSize = game.getDeckSize();
			int insertIndex = ui.promptExplodingKittenInsertIndex(deckSize);

			try {
				game.playDefuse(insertIndex, playerIndex);
			} catch (IllegalArgumentException e) {
				ui.displayError(e.getMessage());
				// You could optionally re-prompt here if you want to be fancy.
			}
		}
	}

	/**
	 * Simple NOPE window:
	 * If the pending card is SHUFFLE or SEE_THE_FUTURE, give each other
	 * alive player who has a NOPE the option to play it.
	 * If someone plays NOPE, we remove their NOPE from hand and cancel
	 * the pending action (we do NOT call playCardAtIndex).
	 */
	private boolean handleNopeWindow(CardType pendingType, Player originalPlayer) {
		// Only certain actions can be NOPE’d in our simplified version
		if (pendingType != CardType.SHUFFLE && pendingType != CardType.SEE_THE_FUTURE) {
			return false;
		}

		int numPlayers = game.getNumberOfPlayers();

		for (int i = 0; i < numPlayers; i++) {
			Player p = game.getPlayerAtIndex(i);

			// Skip original player and dead players
			if (p == originalPlayer || p.getIsDead()) {
				continue;
			}

			// Only prompt players who actually have a NOPE
			if (!p.hasCard(CardType.NOPE)) {
				continue;
			}

			boolean wantsNope = ui.promptPlayNope(p, pendingType);
			if (wantsNope) {
				int nopeIndex = p.getIndexOfCard(CardType.NOPE);
				p.removeCardFromHand(nopeIndex);   // discard NOPE
				ui.displayNopePlayed(p);
				return true;   // cancel the pending action
			}
		}

		return false;
	}

	/**
	 * Extra, card-specific *UI* behavior.
	 * Rules (shuffle deck, peek cards) are in Game / Card classes;
	 * here we only decide what to show.
	 */
	private void handlePostPlayEffects(CardType type, Card card) {
		switch (type) {
			case SEE_THE_FUTURE: {
				int peekCount = (card instanceof SeeTheFutureCard)
						? ((SeeTheFutureCard) card).getPeekCount()
						: 3;

				List<Card> topCards = game.peekTopCards(peekCount);
				ui.displaySeeTheFuture(topCards);  // you already have this
				break;
			}
			case SHUFFLE: {
				// ShuffleCard.play already shuffled the deck in the model.
				ui.displayDeckShuffled();
				break;
			}
			case NOPE:
				// Effect was handled in handleNopeWindow; nothing else to do here.
				break;
			default:
				// Other cards: no controller-specific UI
				break;
		}
	}
}
