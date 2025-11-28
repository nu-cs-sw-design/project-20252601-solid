package domain.game;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.security.SecureRandom;

public class Instantiator {
	public Card createCard(CardType cardType) {
		if (Objects.requireNonNull(cardType) == CardType.SHUFFLE) {
			return new ShuffleCard();
		}
		return new Card(cardType);
	}


	public Random createRandom() {

		return new SecureRandom();
	}

	public ArrayList<Card> createCardList() {

		return new ArrayList<Card>();
	}

}
