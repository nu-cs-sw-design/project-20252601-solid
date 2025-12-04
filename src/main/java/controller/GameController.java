package controller;

import model.Game;
import model.Player;
import model.Card;
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
				// Let Game handle drawing, exploding, next player, etc.
				game.endTurn();
				turnFinished = true;
			} else {
				// They want to play a card
				int cardIndex = ui.promptPlayCardIndex();

				try {
					game.playCardAtIndex(cardIndex);
				} catch (IllegalArgumentException | UnsupportedOperationException e) {
					ui.displayError(e.getMessage());
				}
				// After playing a card, loop again:
				// - Game may have changed current player, turns, etc.
				// - If the game ended (everyone else exploded), the outer while will stop.
			}
		}
	}
}

