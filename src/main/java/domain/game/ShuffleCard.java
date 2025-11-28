package domain.game;

public class ShuffleCard extends Card {

	public ShuffleCard() {
		super(CardType.SHUFFLE);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// Shuffle the deck once.
		game.playShuffle(1);
	}
}
