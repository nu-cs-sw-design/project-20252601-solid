package domain.game;

import java.util.List;

public class SeeTheFutureCard extends Card {

	private static final int NUM_CARDS_TO_PEEK = 3;

	public SeeTheFutureCard() {
		super(CardType.SEE_THE_FUTURE);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// Domain behavior: peek at the top few cards in the deck.
		// The UI/controller layer is responsible for displaying these
		// cards to the player.
		List<Card> topCards = game.getDeck().peekTopCards(NUM_CARDS_TO_PEEK);


	}
}
