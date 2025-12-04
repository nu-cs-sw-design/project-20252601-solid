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

	// Main game loop. Repeats turns until only one player is left alive
	public void runGame() {
		while (!game.isGameOver()) {
			playSingleTurn();
		}

		// Find the winner (the last alive player)
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
			// If an attack happened, jump to the targeted player
			game.setTurnToTargetedIndexIfAttackOccurred();
			// Load that player's turn count from the tracker
			game.setPlayerNumberOfTurns();
		}

		boolean turnFinished = false;

		while (!turnFinished && !game.isGameOver()) {
			Player currentPlayer = game.getCurrentPlayer();
			List<Card> hand = game.getCurrentPlayerHand();
			int turns = game.getNumberOfTurns();

			// Show current state
			ui.displayTurnStart(currentPlayer, hand, turns);

			// Ask if they want to end their turn
			boolean wantsToEnd = ui.promptEndTurn();
			if (wantsToEnd) {
				// End turn in Exploding Kittens = draw 1 card, then resolve
				handleEndTurnDraw(currentPlayer);
				// Let Game handle turns/next player
				game.endTurn();
				turnFinished = true;
			} else {
				// They want to play a card from hand
				int cardIndex = ui.promptPlayCardIndex();

				try {
					// Look at the card BEFORE we remove it from the hand
					Card cardToPlay = currentPlayer.getCardAt(cardIndex);
					CardType type = cardToPlay.getCardType();

					// Delegate rules to Game + Card polymorphism
					game.playCardAtIndex(cardIndex);

					// Handle UI-level effects that depend on the card type
					handlePostPlayEffects(type, cardToPlay, currentPlayer);
				} catch (IllegalArgumentException | UnsupportedOperationException e) {
					ui.displayError(e.getMessage());
				}
			}
		}
	}

	/**
	 * Handles the rule "when you end your turn, draw a card".
	 * If it's an EXPLODING_KITTEN, resolve explode/defuse.
	 * Otherwise, add it to the current player's hand.
	 */
	private void handleEndTurnDraw(Player currentPlayer) {
		Card drawn = game.drawCard();
		ui.displayCardDrawn(currentPlayer, drawn);   // <-- add this to GameUI

		if (drawn.getCardType() == CardType.EXPLODING_KITTEN) {
			resolveExplodingKitten(currentPlayer);
		} else {
			// Normal draw: card joins the player's hand
			game.addCardToHand(drawn);
		}
	}

	/**
	 * Uses Game’s domain logic to resolve an Exploding Kitten draw:
	 * - if player has no DEFUSE, they explode and die
	 * - if they have a DEFUSE, we ask where to put the bomb back,
	 *   and call playDefuse.
	 */
	private void resolveExplodingKitten(Player currentPlayer) {
		int playerIndex = currentPlayer.getPlayerID();
		boolean exploded = game.playExplodingKitten(playerIndex);

		if (exploded) {
			// No DEFUSE -> player eliminated
			ui.displayPlayerExploded(currentPlayer);  // <-- add to GameUI
		} else {
			// They had a DEFUSE; controller handles where to re-insert bomb
			ui.displayExplodingKittenDefused(currentPlayer); // <-- add to GameUI

			int deckSize = game.getDeckSize();
			int insertIndex = ui.promptExplodingKittenInsertIndex(deckSize); // <-- add to GameUI

			try {
				game.playDefuse(insertIndex, playerIndex);
			} catch (IllegalArgumentException e) {
				// If they give a bad index, you can either:
				//  - show error and fallback to top/bottom, or
				//  - re-prompt (more complex).
				ui.displayError(e.getMessage());
			}
		}
	}

	/**
	 * Extra, card-specific behaviour that is about *presentation*,
	 * not core rules (rules are in Game / Card classes).
	 */
	private void handlePostPlayEffects(CardType type, Card card, Player currentPlayer) {
		switch (type) {
			case SEE_THE_FUTURE: {
				int peekCount = (card instanceof SeeTheFutureCard)
						? ((SeeTheFutureCard) card).getPeekCount()
						: 3;

				List<Card> topCards = game.peekTopCards(peekCount);
				ui.displaySeeTheFuturePeek(currentPlayer, topCards); // <-- add to GameUI
				break;
			}
			case SHUFFLE: {
				// ShuffleCard.play already shuffled the deck in the model.
				ui.displayDeckShuffled(); // <-- add to GameUI
				break;
			}
			case NOPE: {
				// For now, just show that someone played NOPE.
				// Proper "cancel last action" orchestration can build on this.
				ui.displayNopePlayed(currentPlayer); // <-- add to GameUI
				break;
			}
			default:
				// Other cards: nothing extra at controller level.
				break;
		}
	}
}
