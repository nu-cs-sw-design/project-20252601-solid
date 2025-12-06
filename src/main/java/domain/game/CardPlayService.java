package domain.game;

public class CardPlayService {
	private final Game game;
	private final IGamePrompts prompts;
	private final CardEffectRegistry registry;

	public CardPlayService(Game game, IGamePrompts prompts) {
		this.game = game;
		this.prompts = prompts;
		this.registry = new CardEffectRegistry(game, prompts);
	}

	/**
	 * Play the card at handIndex from activePlayer's hand.
	 * Handles NOPE reaction and then executes the effect.
	 */
	public void playCard(Player activePlayer, int handIndex) {
		Card card = activePlayer.getCardAt(handIndex);
		activePlayer.removeCardFromHand(handIndex);

		CardType type = card.getCardType();

		if (type == CardType.NOPE) {
			game.getDiscardPile().discard(card);
			return;
		}

		// Only some cards can be NOPE’d (we’ll forbid NOPE on Exploding Kitten)
		boolean canBeNoped = (type != CardType.EXPLODING_KITTEN);

		if (canBeNoped) {
			boolean canceled = isCanceledByNope(card, activePlayer);
			if (canceled) {
				game.getDiscardPile().discard(card);
				return;
			}
		}

		CardEffect effect = registry.getEffect(type);
		if (effect != null) {
			CardPlayContext ctx = new CardPlayContext(game, activePlayer, card, prompts);
			effect.apply(ctx);
		}

		if (type != CardType.EXPLODING_KITTEN) {
			game.getDiscardPile().discard(card);
		}
	}


	/**
	 * Simple NOPE resolution:
	 * - Iterate over other players, ask if they want to play NOPE
	 * - If someone plays NOPE, discard their NOPE and cancel the card
	 * - (No chaining for simplicity; can be extended to multi-NOPE chains).
	 */
	private boolean isCanceledByNope(Card originalCard, Player originalPlayer) {
		int numPlayers = game.getNumberOfPlayers();
		for (int i = 0; i < numPlayers; i++) {
			Player p = game.getPlayerAtIndex(i);
			if (p == originalPlayer || p.getIsDead()) {
				continue;
			}

			if (p.hasCard(CardType.NOPE)) {
				boolean wantsNope = prompts.askPlayerWantsToPlayNope(p, originalCard);
				if (wantsNope) {
					// Remove the NOPE from that player's hand and discard it
					int nopeIndex = p.getIndexOfCard(CardType.NOPE);
					Card nopeCard = p.getCardAt(nopeIndex);
					p.removeCardFromHand(nopeIndex);
					game.getDiscardPile().discard(nopeCard);

					// Single NOPE cancels the original card
					return true;
				}
			}
		}
		return false;
	}
}
