package model;

public class NopeCard extends Card {

	public NopeCard() {
		super(CardType.NOPE);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// NOPE cancels another action *before* it takes effect.
		// The actual cancellation logic lives in the controller:
		// when a NOPE is played, the controller simply does not
		// resolve the pending action card.
		//
		// So at the model level, there is no direct state change here.
	}
}
