package domain.game;

public class ShuffleCard extends Card {

	public ShuffleCard() {
		super(CardType.SHUFFLE);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// Core effect of a SHUFFLE card: randomize the draw pile.
		game.getDeck().shuffleDeck();

	}
}
