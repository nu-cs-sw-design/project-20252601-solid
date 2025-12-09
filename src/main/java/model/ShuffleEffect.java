package model;

public class ShuffleEffect implements CardEffect {

	private final Game game;
	private final IGamePrompts prompts;

	public ShuffleEffect(Game game, IGamePrompts prompts) {
		this.game = game;
		this.prompts = prompts;
	}

	@Override
	public void apply(CardPlayContext context) {
		// Optional: let the UI show a friendly "shuffling..." message
		prompts.showDeckShuffled();

		// Core effect: randomize the deck
		// The original GameUI asked the user for # of shuffles; here we just do 1.
		game.playShuffle(1);
	}
}
