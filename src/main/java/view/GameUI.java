package view;

import model.*;

import java.util.*;
import java.text.MessageFormat;
import java.nio.charset.StandardCharsets;





public class GameUI {
	//private Game game;
	private ResourceBundle messages;

	public GameUI() {

	}


	private String getLocalizedCardType(CardType cardType) {
		// Fallback if language wasn't chosen yet
		if (messages == null) {
			return cardType.name();
		}

		String cardTypeKey = "card." + cardType.name();

		try {
			return messages.getString(cardTypeKey);
		} catch (MissingResourceException e) {
			// Fallback if the key doesn't exist in the bundle
			return cardType.name();
		}
	}

	public void chooseLanguage() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String language = "1. English\n2. 한국어\n";
		final String askLanguage = "Enter the number to choose the language:";
		final String invalidChoice = "Invalid choice. Please enter 1 or 2.";
		System.out.println(language);
		System.out.println(askLanguage);

		while (true) {
			String userInput = scanner.nextLine();
			switch (userInput) {
				case "1":
					messages = ResourceBundle.getBundle
							("message", new Locale("en"));
					final String languageSetEnglish = messages.getString
							("setLanguage");
					System.out.println(languageSetEnglish);
					return;
				case "2":
					messages = ResourceBundle.getBundle
							("message", new Locale("ko"));
					final String languageSetKorean = messages.getString
							("setLanguage");
					System.out.println(languageSetKorean);
					return;
				default:
					System.out.println(invalidChoice);
			}
		}
	}

	public GameType chooseGameType() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String gameModePrompt = messages.getString("gameModePrompt");
		final String gameModeExplodingOption =
				messages.getString("gameModeExplodingOption");
		final String gameModeImplodingOption =
				messages.getString("gameModeImplodingOption");
		final String gameModeStreakingOption =
				messages.getString("gameModeStreakingOption");
		final String gameModeChoicePrompt = messages.getString("gameModeChoicePrompt");
		final String gameModeInvalid = messages.getString("gameModeInvalid");

		System.out.println(gameModePrompt);
		System.out.println(gameModeExplodingOption);
		System.out.println(gameModeImplodingOption);
		System.out.println(gameModeStreakingOption);

		while (true) {
			System.out.print(gameModeChoicePrompt);
			String userInput = scanner.nextLine();
			switch (userInput) {
				case "1":
					System.out.println(messages.getString("gameModeExploding"));
					return GameType.EXPLODING_KITTENS;
				case "2":
					System.out.println(messages.getString("gameModeImploding"));
					return GameType.IMPLODING_KITTENS;
				case "3":
					System.out.println(messages.getString("gameModeStreaking"));
					return GameType.STREAKING_KITTENS;
				default:
					System.out.println(gameModeInvalid);
			}
		}
	}


	public int chooseNumberOfPlayers() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		final String numOfPlayersPrompt = messages.getString("numOfPlayersPrompt");
		final String numOfPlayersTwo = messages.getString("numOfPlayersTwo");
		final String numOfPlayersThree = messages.getString("numOfPlayersThree");
		final String numOfPlayersFour = messages.getString("numOfPlayersFour");
		final String invalidPlayersNum = messages.getString("invalidPlayersNum");

		System.out.println(numOfPlayersPrompt);

		while (true) {
			String userInput = scanner.nextLine();
			switch (userInput) {
				case "2":
					System.out.println(numOfPlayersTwo);
					return 2;
				case "3":
					System.out.println(numOfPlayersThree);
					return 3;
				case "4":
					System.out.println(numOfPlayersFour);
					return 4;
				default:
					System.out.println(invalidPlayersNum);
			}
		}
	}

	// Show whose turn it is, their hand, and how many turns they have
	public void displayTurnStart(Player player, List<Card> hand, int turns) {
		// Use existing messages if available; otherwise safe defaults
		String divider = (messages != null && messages.containsKey("dividerLine"))
				? messages.getString("dividerLine")
				: "--------------------------------------------------";

		System.out.println(divider);

		// We assume playerID is meaningful to show
		int playerId = player.getPlayerID();
		if (messages != null && messages.containsKey("currentPlayerTurn")) {
			String msg = MessageFormat.format(
					messages.getString("currentPlayerTurn"),
					playerId
			);
			System.out.println(msg);
		} else {
			System.out.println("It is currently player " + playerId + "'s turn.");
		}

		// Print hand
		StringBuilder handMsg = new StringBuilder();
		if (messages != null && messages.containsKey("playerHand")) {
			handMsg.append(messages.getString("playerHand"));
		} else {
			handMsg.append("They have a hand of:");
		}

		for (Card c : hand) {
			handMsg.append(" ").append(getLocalizedCardType(c.getCardType()));
		}
		System.out.println(handMsg);

		// Print remaining turns
		if (messages != null && messages.containsKey("playerTurnsMessage")) {
			String turnMsg = MessageFormat.format(
					messages.getString("playerTurnsMessage"),
					turns
			);
			System.out.println(turnMsg);
		} else {
			System.out.println("Player currently has " + turns + " more turns.");
		}
	}

	// Ask the user if they want to end their turn (no game logic here)
	public boolean promptEndTurn() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		String endTurnPrompt = messages != null && messages.containsKey("endTurnPrompt")
				? messages.getString("endTurnPrompt")
				: "Do you want to end your turn?";
		String optionYes = messages != null && messages.containsKey("optionYes")
				? messages.getString("optionYes")
				: "1. Yes";
		String optionNo = messages != null && messages.containsKey("optionNo")
				? messages.getString("optionNo")
				: "2. No";
		String typeOptionPrompt = messages != null && messages.containsKey("typeOptionPrompt")
				? messages.getString("typeOptionPrompt")
				: "Please type 1 or 2.";
		String invalidInput = messages != null && messages.containsKey("invalidInput")
				? messages.getString("invalidInput")
				: "Invalid input. Please try again.";

		System.out.println(endTurnPrompt);
		System.out.println(optionYes);
		System.out.println(optionNo);

		while (true) {
			System.out.println(typeOptionPrompt);
			String input = scanner.nextLine().trim();
			if ("1".equals(input)) {
				return true;   // yes, end turn
			}
			if ("2".equals(input)) {
				return false;  // no, keep playing
			}
			System.out.println(invalidInput);
		}
	}



	// Ask whether to play a special combo (controller will decide what to do)
//	public boolean promptPlaySpecialCombo() {
//		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
//
//		String specialComboPrompt = messages != null && messages.containsKey("specialComboPrompt")
//				? messages.getString("specialComboPrompt")
//				: "Would you like to play a special combo?";
//		String optionYes = messages != null && messages.containsKey("optionYes")
//				? messages.getString("optionYes")
//				: "1. Yes";
//		String optionNo = messages != null && messages.containsKey("optionNo")
//				? messages.getString("optionNo")
//				: "2. No";
//		String optionsPrompt = messages != null && messages.containsKey("optionsPrompt")
//				? messages.getString("optionsPrompt")
//				: "Enter 1 or 2: ";
//		String invalidInput = messages != null && messages.containsKey("invalidInput")
//				? messages.getString("invalidInput")
//				: "Invalid input. Please try again.";
//
//		System.out.println(specialComboPrompt);
//		System.out.println(optionYes);
//		System.out.println(optionNo);
//
//		while (true) {
//			System.out.print(optionsPrompt);
//			String input = scanner.nextLine().trim();
//			if ("1".equals(input)) {
//				return true;
//			}
//			if ("2".equals(input)) {
//				return false;
//			}
//			System.out.println(invalidInput);
//		}
//	}

	// Ask which card index to play
	public int promptPlayCardIndex() {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		String prompt = messages != null && messages.containsKey("playedCardPrompt")
				? messages.getString("playedCardPrompt")
				: "Which index card do you want to play?";
		String invalidNumber = messages != null && messages.containsKey("invalidNumber")
				? messages.getString("invalidNumber")
				: "Invalid number. Please enter an integer.";

		System.out.println(prompt);

		while (true) {
			String input = scanner.nextLine().trim();
			try {
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				System.out.println(invalidNumber);
			}
		}
	}

	// Show the cards for "See the Future"
	public void displaySeeTheFuture(List<Card> cards) {
		if (cards == null || cards.isEmpty()) {
			System.out.println("You see nothing in the future… (no cards to show)");
			return;
		}

		String header = messages != null && messages.containsKey("futureCards")
				? messages.getString("futureCards")
				: "You peek at the top {0} card(s) of the deck.";
		String singleCardFmt = messages != null && messages.containsKey("futureCard")
				? messages.getString("futureCard")
				: "Card: {0}";

		String formattedHeader = MessageFormat.format(header, cards.size());
		System.out.println(formattedHeader);

		for (Card c : cards) {
			String name = getLocalizedCardType(c.getCardType());
			String line = MessageFormat.format(singleCardFmt, name);
			System.out.println(line);
		}
	}

	// Show game over and the winner
	public void displayGameOver(Player winner) {
		String gameOver = messages != null && messages.containsKey("gameOverMessage")
				? messages.getString("gameOverMessage")
				: "Game over!";
		System.out.println(gameOver);

		if (winner != null) {
			// If you have a localized winner key, use it; otherwise simple fallback
			if (messages != null && messages.containsKey("winnerMessage")) {
				String winnerMsg = MessageFormat.format(
						messages.getString("winnerMessage"),
						winner.getPlayerID()
				);
				System.out.println(winnerMsg);
			} else {
				System.out.println("Winner is player " + winner.getPlayerID());
			}
		}
	}

	// Called when a player draws a card at end of turn
	public void displayCardDrawn(Player player, Card card) {
		String tmpl = (messages != null && messages.containsKey("cardDrawn"))
				? messages.getString("cardDrawn")
				: "Player {0} draws a {1}.";
		String line = MessageFormat.format(
				tmpl,
				player.getPlayerID(),
				getLocalizedCardType(card.getCardType())
		);
		System.out.println(line);
	}

	// Called when a player explodes (no DEFUSE)
	public void displayPlayerExploded(Player player) {
		String tmpl = (messages != null && messages.containsKey("playerExploded"))
				? messages.getString("playerExploded")
				: "💥 Player {0} has exploded and is out of the game!";
		String line = MessageFormat.format(tmpl, player.getPlayerID());
		System.out.println(line);
	}

	// Called when a player successfully uses DEFUSE
	public void displayExplodingKittenDefused(Player player) {
		String tmpl = (messages != null && messages.containsKey("playerDefused"))
				? messages.getString("playerDefused")
				: "Player {0} used a DEFUSE!";
		String line = MessageFormat.format(tmpl, player.getPlayerID());
		System.out.println(line);
	}

	// Ask where to re-insert the Exploding Kitten in the deck
	public int promptExplodingKittenInsertIndex(int deckSize) {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		String prompt = (messages != null && messages.containsKey("ekInsertPrompt"))
				? messages.getString("ekInsertPrompt")
				: "Where do you want to put the Exploding Kitten back? (0 to {0})";
		String invalid = (messages != null && messages.containsKey("invalidNumber"))
				? messages.getString("invalidNumber")
				: "Invalid number. Please enter an integer in range.";

		System.out.println(MessageFormat.format(prompt, deckSize));

		while (true) {
			String input = scanner.nextLine().trim();
			try {
				int idx = Integer.parseInt(input);
				if (idx >= 0 && idx <= deckSize) {
					return idx;
				}
			} catch (NumberFormatException ignored) {}
			System.out.println(invalid);
		}
	}

	// SEE_THE_FUTURE: reuse your existing display, just with player name if you want
	public void displaySeeTheFuturePeek(Player player, List<Card> cards) {
		// optional: mention who is peeking
		String header = (messages != null && messages.containsKey("futurePeekPlayer"))
				? messages.getString("futurePeekPlayer")
				: "Player {0} is peeking at the future...";
		System.out.println(MessageFormat.format(header, player.getPlayerID()));

		// reuse your existing method
		displaySeeTheFuture(cards);
	}

	// SHUFFLE feedback
	public void displayDeckShuffled() {
		String msg = (messages != null && messages.containsKey("deckShuffled"))
				? messages.getString("deckShuffled")
				: "The deck has been shuffled.";
		System.out.println(msg);
	}

	// NOPE feedback
	public void displayNopePlayed(Player player) {
		String tmpl = (messages != null && messages.containsKey("nopePlayed"))
				? messages.getString("nopePlayed")
				: "Player {0} played NOPE!";
		String line = MessageFormat.format(tmpl, player.getPlayerID());
		System.out.println(line);
	}

	public boolean promptPlayNope(Player reactingPlayer, CardType pendingType) {
		Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

		String prompt = (messages != null && messages.containsKey("nopePrompt"))
				? messages.getString("nopePrompt")
				: "Player {0}, do you want to play NOPE against {1}? (1 = Yes, 2 = No)";

		System.out.println(MessageFormat.format(
				prompt,
				reactingPlayer.getPlayerID(),
				pendingType.name()
		));

		String invalid = (messages != null && messages.containsKey("invalidInput"))
				? messages.getString("invalidInput")
				: "Invalid input. Please try again.";

		while (true) {
			String input = scanner.nextLine().trim();
			if ("1".equals(input)) {
				return true;
			}
			if ("2".equals(input)) {
				return false;
			}
			System.out.println(invalid);
		}
	}



	// Generic error printing for the controller
	public void displayError(String message) {
		System.out.println("Error: " + message);
	}



}




