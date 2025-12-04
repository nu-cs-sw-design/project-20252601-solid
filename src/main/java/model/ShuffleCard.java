package model;

public class ShuffleCard extends Card {

	public ShuffleCard() {
		super(CardType.SHUFFLE);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// Core effect of a SHUFFLE card is to randomize the draw pile via Game.
		game.playShuffle(1);
	}
}
