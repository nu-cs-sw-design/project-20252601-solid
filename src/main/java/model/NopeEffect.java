package model;

public class NopeEffect implements CardEffect {

	private final Game game;
	private final IGamePrompts prompts;

	public NopeEffect(Game game, IGamePrompts prompts) {
		this.game = game;
		this.prompts = prompts;
	}

	/**
	 * We don't use NOPE through the normal "playCard" flow like ATTACK/SHUFFLE.
	 * Instead, NOPE is played reactively when another card is played.
	 * So apply(..) is not used in this design.
	 */
	@Override
	public void apply(CardPlayContext ctx) {
		// Intentionally left blank or:
		// throw new UnsupportedOperationException("NOPE is handled reactively");
	}

	/**
	 * Ask other players if they want to NOPE the originalCard.
	 * Returns true if the original card's effect should be canceled.
	 */
	public boolean isCanceledByNope(Player originalPlayer, Card originalCard) {

		boolean canceled = false;

		// Loop over all players except the original one
		for (int i = 0; i < game.getNumberOfPlayers(); i++) {
			Player reactingPlayer = game.getPlayerAtIndex(i);
			if (reactingPlayer == originalPlayer) {
				continue;
			}
			// Skip dead players or players without NOPE
			if (reactingPlayer.getIsDead()
					|| !reactingPlayer.hasCard(CardType.NOPE)) {
				continue;
			}

			// Ask via prompts (GameUI implements IGamePrompts)
			boolean wantsToNope =
					prompts.askPlayerWantsToPlayNope(reactingPlayer, originalCard);

			if (wantsToNope) {
				// Remove the NOPE card from their hand
				game.removeCardFromHand(reactingPlayer.getPlayerID(), CardType.NOPE);
				// For now: one NOPE cancels the action. (No NOPE-on-NOPE chain)
				canceled = true;
				break;
			}
		}

		return canceled;
	}
}
