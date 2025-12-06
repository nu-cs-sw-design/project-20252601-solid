package domain.game;

import java.util.List;

public interface IGamePrompts {
	// Exploding Kitten
	int askExplodingKittenInsertIndex(int maxIndex);

	// See The Future
	void showTopCards(Player active, List<Card> cards);

	// Shuffle
	void showDeckShuffled();

	// Nope
	boolean askPlayerWantsToPlayNope(Player reactingPlayer, Card originalCard);
}