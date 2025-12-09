package model;

public class CardPlayContext {
	private final Game game;
	private final Player activePlayer;
	private final Card card;
	private final IGamePrompts prompts;

	public CardPlayContext(Game game, Player activePlayer, Card card, IGamePrompts prompts) {
		this.game = game;
		this.activePlayer = activePlayer;
		this.card = card;
		this.prompts = prompts;
	}

	public Game getGame() {
		return game;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public Card getCard() {
		return card;
	}

	public IGamePrompts getPrompts() {
		return prompts;
	}

	public Deck getDeck() {
		return game.getDeck();
	}

	public DiscardPile getDiscardPile() {
		return game.getDiscardPile();
	}
}
