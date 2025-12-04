package model;

public class ExplodingKittenCard extends Card {

	public ExplodingKittenCard() {
		super(CardType.EXPLODING_KITTEN);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		int playerIndex = game.getPlayerTurn();
		boolean exploded = game.playExplodingKitten(playerIndex);

		// If exploded == false, that means the player has a DEFUSE.
		// The controller / UI will:
		//   1) asking where to reinsert the bomb
		//   2) calling game.playDefuse(indexToInsert, playerIndex)
	}

}
