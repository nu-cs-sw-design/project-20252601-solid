package controller;

import model.Instantiator;
import model.Card;
import model.CardType;
import model.Deck;
import model.Game;
import model.GameType;
import model.Player;
import view.GameUI;

import java.util.ArrayList;
import java.security.SecureRandom;

public class Main {
	public static void main(String[] args) {

		final int maxDeckSize = 42;
		Instantiator instantiator = new Instantiator();

		// UI: choose language, game type, and player count
		GameUI gameUI = new GameUI();
		gameUI.chooseLanguage();
		GameType chosenType = gameUI.chooseGameType();
		int numPlayers = gameUI.chooseNumberOfPlayers();

		//  Model setup
		Deck deck = new Deck(new ArrayList<>(), new SecureRandom(),
				chosenType, 0, maxDeckSize, instantiator);

		Player[] players = new Player[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			players[i] = new Player(i, instantiator);
		}


		int[] turnTracker = new int[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			turnTracker[i] = 1;
		}

		Game game = new Game(
				0,
				chosenType,
				deck,
				players,
				new SecureRandom(),
				new ArrayList<Integer>(),
				turnTracker
		);

		//  Initial cards / deck setup
		// Give each player a DEFUSE
		for (int playerCounter = 0; playerCounter < game.getNumberOfPlayers(); playerCounter++) {
			game.getPlayerAtIndex(playerCounter).addDefuse(new Card(CardType.DEFUSE));
		}

		game.getDeck().initializeDeck();
		game.getDeck().shuffleDeck();

		final int cardDrawnPerPlayer = 5;
		for (int cardDrawnCounter = 0;
			 cardDrawnCounter < cardDrawnPerPlayer; cardDrawnCounter++) {
			for (int playerCtr = 0; playerCtr < game.getNumberOfPlayers(); playerCtr++) {
				Player current = game.getPlayerAtIndex(playerCtr);
				current.addCardToHand(game.getDeck().drawCard());
			}
		}

		// Insert Exploding / Imploding kittens based on game type
		if (game.getGameType() == GameType.STREAKING_KITTENS) {
			game.getDeck().insertCard(CardType.EXPLODING_KITTEN,
					game.getNumberOfPlayers(), false);
		} else {
			game.getDeck().insertCard(CardType.EXPLODING_KITTEN,
					game.getNumberOfPlayers() - 1, false);
		}
		if (game.getGameType() == GameType.IMPLODING_KITTENS) {
			game.getDeck().insertCard(CardType.IMPLODING_KITTEN,
					1, false);
		}

		game.playShuffle(1);
	}
}


