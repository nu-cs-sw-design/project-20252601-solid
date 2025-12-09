package model;

import java.util.HashMap;
import java.util.Map;

public class CardEffectRegistry {
	private final Map<CardType, CardEffect> effects = new HashMap<>();

	public CardEffectRegistry(Game game, IGamePrompts prompts) {
		effects.put(CardType.EXPLODING_KITTEN, new ExplodingKittenEffect(game, prompts));
		effects.put(CardType.SHUFFLE,          new ShuffleEffect(game, prompts));
		effects.put(CardType.SEE_THE_FUTURE,   new SeeTheFutureEffect(game, prompts));
		effects.put(CardType.NOPE,             new NopeEffect(game, prompts));
	}

	public CardEffect getEffect(CardType type) {
		return effects.get(type);
	}
}
