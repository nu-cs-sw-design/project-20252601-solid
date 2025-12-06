package domain.game;

public class ExplodingKittenEffect implements CardEffect {

	private final Game game;
	private final IGamePrompts prompts;

	public ExplodingKittenEffect(Game game, IGamePrompts prompts) {
		this.game = game;
		this.prompts = prompts;
	}

	@Override
	public void apply(CardPlayContext context) {
		// We assume the active player is the one whose turn it is.
		int playerIndex = game.getPlayerTurn();

		// Game decides whether the player explodes or successfully defuses.
		// true => player exploded (no DEFUSE)
		// false => player survived (used a DEFUSE)
		boolean exploded = game.playExplodingKitten(playerIndex);

		if (exploded) {
			// Player is dead; nothing else to do.
			return;
		}

		// Player used a DEFUSE: we must put the kitten back in the deck.
		int maxIndex = game.getDeckSize(); // 0..maxIndex inclusive allowed
		int insertIndex = prompts.askExplodingKittenInsertIndex(maxIndex);

		// Put the Exploding Kitten back according to the rules
		game.playDefuse(insertIndex, playerIndex);

		// After a successful defuse, the player is no longer cursed.
		game.getPlayerAtIndex(playerIndex).setCursed(false);
	}
}
