package view;

import controller.GameController;
import model.Deck;
import model.Game;
import model.GameType;
import model.Instantiator;
import model.Player;

import java.security.SecureRandom;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		final int playerIDZero = 0;
		final int playerIDOne = 1;
		final int playerIDTwo = 2;
		final int playerIDThree = 3;
		final int playerIDFour = 4;
		final int maxDeckSize = 42;

		Instantiator instantiator = new Instantiator();

		// Create an empty deck with GameType.NONE and 0 players for now.
		Deck deck = new Deck(
				new ArrayList<>(),
				new SecureRandom(),
				GameType.NONE,
				0,
				maxDeckSize,
				instantiator
		);

		// Pre-create 5 players; GameUI + controller will decide how many are actually used.
		Player[] players = {
				new Player(playerIDZero, instantiator),
				new Player(playerIDOne, instantiator),
				new Player(playerIDTwo, instantiator),
				new Player(playerIDThree, instantiator),
				new Player(playerIDFour, instantiator)
		};

		int[] turnTracker = {1, 1, 1, 1, 1};

		// Initial GameType.NONE and 0 players – these get updated by the UI/controller.
		Game game = new Game(
				0,
				GameType.NONE,
				deck,
				players,
				new SecureRandom(),
				new ArrayList<Integer>(),
				turnTracker
		);

		// UI and Controller
		GameUI gameUI = new GameUI(game);
		GameController controller = new GameController(game, gameUI);

		// Hand over control
		controller.runGame();
	}
}
