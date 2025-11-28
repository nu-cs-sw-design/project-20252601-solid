package domain.game;

public class ExplodingKittenCard extends Card {

	public ExplodingKittenCard() {
		super(CardType.EXPLODING_KITTEN);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// For now, delegate to existing game logic.
		int playerIndex = currentPlayer.getPlayerID();
		boolean exploded = game.playExplodingKitten(playerIndex);

		// If exploded == false, the controller/UI will later handle
		
	}
}
