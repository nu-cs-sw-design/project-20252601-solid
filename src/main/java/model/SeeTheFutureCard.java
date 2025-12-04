package model;

import java.util.List;

public class SeeTheFutureCard extends Card {

	private static final int DEFAULT_PEEK_COUNT = 3;

	public SeeTheFutureCard() {

		super(CardType.SEE_THE_FUTURE);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// This card only allows a peek via controller/UI,
		// and must not change deck order.
	}

	public int getPeekCount() {
		return DEFAULT_PEEK_COUNT;
	}
}
