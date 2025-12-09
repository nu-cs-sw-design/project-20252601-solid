package controller;

import model.Card;
import model.CardType;
import model.Game;
import model.GameType;
import model.Player;
import view.GameUI;

public class GameController {

	private final Game game;
	private final GameUI ui;

	public GameController(Game game, GameUI ui) {
		this.game = game;
		this.ui = ui;
	}

	public void runGame() {
		// 1. Ask the user for language, game mode, and number of players
		ui.chooseLanguage();
		ui.chooseGame();
		ui.chooseNumberOfPlayers();

		// 2. Give each player a DEFUSE card
		for (int playerCounter = 0;
			 playerCounter < game.getNumberOfPlayers();
			 playerCounter++) {

			game.getPlayerAtIndex(playerCounter)
					.addDefuse(new Card(CardType.DEFUSE));
		}

		// 3. Initialize and shuffle the deck
		game.getDeck().initializeDeck();
		game.getDeck().shuffleDeck();

		// 4. Deal starting hands (5 cards each)
		final int cardDrawnPerPlayer = 5;
		for (int cardDrawnCounter = 0;
			 cardDrawnCounter < cardDrawnPerPlayer;
			 cardDrawnCounter++) {

			for (int playerCtr = 0;
				 playerCtr < game.getNumberOfPlayers();
				 playerCtr++) {

				Player current = game.getPlayerAtIndex(playerCtr);
				current.addCardToHand(game.getDeck().drawCard());
			}
		}

		// 5. Insert EXPLODING_KITTEN(s) depending on game mode
		if (game.getGameType() == GameType.STREAKING_KITTENS) {
			game.getDeck().insertCard(
					CardType.EXPLODING_KITTEN,
					game.getNumberOfPlayers(),
					false
			);
		} else {
			game.getDeck().insertCard(
					CardType.EXPLODING_KITTEN,
					game.getNumberOfPlayers() - 1,
					false
			);
		}

		// 6. Insert IMPLODING_KITTEN if needed
		if (game.getGameType() == GameType.IMPLODING_KITTENS) {
			game.getDeck().insertCard(
					CardType.IMPLODING_KITTEN,
					1,
					false
			);
		}

		// 7. Initial shuffle (same as your original main)
		game.playShuffle(1);

		// 8. Main game loop
		while (!ui.checkIfGameOver()) {
			ui.startTurn();
		}

		// 9. End game
		ui.endGame();
	}
}
