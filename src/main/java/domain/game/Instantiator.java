package domain.game;

import java.util.ArrayList;
import java.util.Random;

public class Instantiator {
	public Card createCard(CardType cardType) {
		switch (cardType) {
			case SHUFFLE:
				return new ShuffleCard();
			case EXPLODING_KITTEN:
				return new ExplodingKittenCard();
			case NOPE:
				return new NopeCard();
			case SEE_THE_FUTURE:
				return new SeeTheFutureCard();

			default:
				return new Card(cardType);
		}
	}


	public Random createRandom() {

		return new Random();
	}

	public ArrayList<Card> createCardList() {

		return new ArrayList<Card>();
	}

}
