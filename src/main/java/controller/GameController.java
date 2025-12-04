//package controller;
//
//import model.Card;
//import model.Game;
//import model.Player;
//import model.SeeTheFutureCard;
//import view.GameUI;
//
//import java.util.List;
//
///**
// * Controller: coordinates between Game (model) and GameUI (view).
// * Main should just wire things up and call controller.run().
// */
//public class GameController {
//
//	private final Game game;
//	private final GameUI ui;
//
//	public GameController(Game game, GameUI ui) {
//		this.game = game;
//		this.ui = ui;
//	}
//
//	/**
//	 * Entry point for the game loop.
//	 * Main should just do:
//	 *   GameController controller = new GameController(game, ui);
//	 *   controller.run();
//	 */
//	public void run() {
//		// If/when you move language / gameType / numPlayers prompts here,
//		// you can put that in handleStartGame().
//		handleStartGame();
//
//		while (!game.isGameOver()) {
//			handleTurn();
//		}
//
//		Player winner = findWinner();
//		ui.displayGameOver(winner);
//	}
//
//	/**
//	 * For now, this is a no-op because your setup (language, mode, players,
//	 * Game construction) already happens before GameController is created.
//	 *
//	 * If you later want a SUPER clean MVC, you can:
//	 *  - call ui.chooseLanguage(), ui.chooseGameType(), ui.chooseNumberOfPlayers()
//	 *  - build Deck / Players / Game here or in a factory
//	 */
//	public void handleStartGame() {
//		// currently nothing to do – game is already constructed in Main.
//	}
//
//	/**
//	 * Handles one player's whole turn:
//	 * - shows state
//	 * - asks if they want to end turn
//	 * - otherwise lets them play a card (or combo later)
//	 * - repeats while they still have turns.
//	 */
//	public void handleTurn() {
//		// While this player still has turns AND game is not over
//		while (!game.isGameOver() && game.getNumberOfTurns() > 0) {
//			Player current = game.getCurrentPlayer();
//			List<Card> hand = game.getCurrentPlayerHand();
//			int turns = game.getNumberOfTurns();
//
//			ui.displayTurnStart(current, hand, turns);
//
//			// Give the player a chance to end their turn
//			boolean wantsToEnd = ui.promptEndTurn();
//			if (wantsToEnd) {
//				handleEndTurn();
//				// After endTurn(), control goes back to run()
//				return;
//			}
//
//			// Otherwise, let them try to play a card (or a combo)
//			boolean wantsCombo = ui.promptPlaySpecialCombo();
//			if (wantsCombo) {
//				// For now, we don't implement combos yet.
//				ui.displayError("Special combos not implemented yet; playing a single card instead.");
//			}
//
//			handlePlayCard();
//
//			// Game logic (Attack, Skip, etc.) may have changed turn count or killed a player.
//			if (game.isGameOver()) {
//				return;
//			}
//
//			// Loop continues while this player still has turns.
//		}
//	}
//
//	/**
//	 * Handles a single "play card" action:
//	 * - asks UI for an index
//	 * - validates it
//	 * - calls Game.playCardAtIndex()
//	 * - does any extra UI behavior for special cards (e.g. See The Future)
//	 */
//	public void handlePlayCard() {
//		List<Card> hand = game.getCurrentPlayerHand();
//		if (hand.isEmpty()) {
//			ui.displayError("You have no cards to play.");
//			return;
//		}
//
//		int index = ui.promptPlayCardIndex();
//
//		if (index < 0 || index >= hand.size()) {
//			ui.displayError("Invalid card index.");
//			return;
//		}
//
//		// Capture the card before playing (playCardAtIndex removes it from hand)
//		Card cardToPlay = hand.get(index);
//
//		try {
//			game.playCardAtIndex(index);
//
//			// Hook up special UI for SeeTheFuture
//			if (cardToPlay instanceof SeeTheFutureCard) {
//				// peekTopCards is in Game -> delegates to Deck
//				List<Card> topCards = game.peekTopCards(3); // adjust count if needed
//				ui.displaySeeTheFuture(topCards);
//			}
//
//		} catch (IllegalArgumentException ex) {
//			ui.displayError(ex.getMessage());
//		}
//	}
//
//	/**
//	 * Ends the current player's turn (1 "turn unit" as defined by the model).
//	 */
//	public void handleEndTurn() {
//		try {
//			game.endTurn();
//		} catch (IllegalArgumentException | IllegalStateException ex) {
//			// Just in case you ever throw on invalid endTurn calls
//			ui.displayError(ex.getMessage());
//		}
//	}
//
//	/**
//	 * Finds the winner (the last non-dead player), if any.
//	 */
//	private Player findWinner() {
//		for (int i = 0; i < game.getNumberOfPlayers(); i++) {
//			Player p = game.getPlayerAtIndex(i);
//			if (!p.getIsDead()) {
//				return p;
//			}
//		}
//		return null; // should not happen if isGameOver() is true
//	}
//}
