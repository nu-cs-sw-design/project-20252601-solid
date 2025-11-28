package domain.game;

import java.util.List;

public class SeeTheFutureCard extends Card {

	private static final int DEFAULT_PEEK_COUNT = 3;

	public SeeTheFutureCard() {
		super(CardType.SEE_THE_FUTURE);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// Peek at the top N cards without changing deck order.
		List<Card> topCards = game.getDeck().peekTopCards(DEFAULT_PEEK_COUNT);

		// This method enforces the *rules* of the card:
		//   - look at the top cards
		//   - don't remove / reorder them
		//

	}
}
