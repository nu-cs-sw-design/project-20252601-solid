package domain.game;

import java.util.ArrayList;
import java.util.List;

public class SeeTheFutureEffect implements CardEffect {

	private static final int CARDS_SEEN_STREAKING = 5;
	private static final int CARDS_SEEN_EXPLODING = 2;

	private final Game game;
	private final IGamePrompts prompts;

	public SeeTheFutureEffect(Game game, IGamePrompts prompts) {
		this.game = game;
		this.prompts = prompts;
	}

	@Override
	public void apply(CardPlayContext context) {
		Deck deck = context.getDeck();
		int deckSize = deck.getDeckSize();

		int baseCount = (game.getGameType() == GameType.STREAKING_KITTENS)
				? CARDS_SEEN_STREAKING
				: CARDS_SEEN_EXPLODING;

		int cardsToReveal = Math.min(baseCount, deckSize);

		List<Card> peekCards = new ArrayList<>();
		for (int i = 0; i < cardsToReveal; i++) {
			// top of deck is highest index, like in the original GameUI code
			Card c = deck.getCardAtIndex(deckSize - 1 - i);
			peekCards.add(c);
		}

		// Delegate the actual display to the UI via prompts
		prompts.showTopCards(context.getActivePlayer(), peekCards);
	}
}
