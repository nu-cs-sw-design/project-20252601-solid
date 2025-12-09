package model;

public class CardPlayService {
	private final Game game;
	private final IGamePrompts prompts;
	private final CardEffectRegistry registry;
	private final NopeEffect nopeEffect;

	public CardPlayService(Game game, IGamePrompts prompts) {
		this.game = game;
		this.prompts = prompts;
		this.registry = new CardEffectRegistry(game, prompts);
		this.nopeEffect = (NopeEffect) registry.getEffect(CardType.NOPE);
	}

	/**
	 * Play the card at handIndex from activePlayer's hand.
	 * Handles NOPE reaction and then executes the effect.
	 */
	public void playCard(Player activePlayer, int handIndex) {
		Card card = activePlayer.getCardAt(handIndex);
		activePlayer.removeCardFromHand(handIndex);

		CardType type = card.getCardType();

		// Playing NOPE "directly" is a no-op in this design; it only matters reactively
		if (type == CardType.NOPE) {
			game.getDiscardPile().discard(card);
			return;
		}

		// Only some cards can be NOPE’d (we’ll forbid NOPE on Exploding Kitten)
		boolean canBeNoped = (type != CardType.EXPLODING_KITTEN);

		if (canBeNoped && nopeEffect != null) {
			boolean canceled = nopeEffect.isCanceledByNope(activePlayer, card);
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

		// Exploding Kitten's discard is handled in its effect logic
		if (type != CardType.EXPLODING_KITTEN) {
			game.getDiscardPile().discard(card);
		}
	}
}
