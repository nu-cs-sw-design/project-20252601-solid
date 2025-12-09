package model;

import java.util.ArrayList;
import java.util.List;

public class DiscardPile {
	private final List<Card> cards = new ArrayList<>();

	public void discard(Card card) {
		cards.add(card);
	}

	public Card top() {
		if (cards.isEmpty()) {
			return null;
		}
		return cards.get(cards.size() - 1);
	}

	public int size() {
		return cards.size();
	}

	public List<Card> getAllCards() {
		return new ArrayList<>(cards);
	}
}
