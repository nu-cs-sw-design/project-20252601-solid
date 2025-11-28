package domain.game;

public class ExplodingKittenCard extends Card {

	public ExplodingKittenCard() {
		super(CardType.EXPLODING_KITTEN);
	}

	@Override
	public void play(Game game, Player currentPlayer) {
		// In the actual game rules, Exploding Kitten is NOT played from hand.
		// It only takes effect when it is drawn from the deck.
		//
		// The explosion / defuse logic is still handled by Game when a card
		// is drawn and identified as EXPLODING_KITTEN.
		//
		// We keep this method as a no-op (or could throw) to make the design
		// explicit and avoid duplicating that logic here.
	}
}
