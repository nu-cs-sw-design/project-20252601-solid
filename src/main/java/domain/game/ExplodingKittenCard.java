package domain.game;

public class ExplodingKittenCard extends Card {

	public ExplodingKittenCard() {
		super(CardType.EXPLODING_KITTEN);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// Use existing Game logic as a helper:
		// playerID usually matches their index in the array for this codebase.
		int playerIndex = currentPlayer.getPlayerID();

		boolean exploded = game.playExplodingKitten(playerIndex);

		// If exploded == false, the game logic expects a DEFUSE to be used.
		// The controller can now:
		//  - ask where to put the bomb back
		//  - call game.playDefuse(indexToInsert, playerIndex)
		//
		// We don't handle UI prompts in the model.
	}
}
