### Use Case 1: Start New Game

**Primary Actor:** Player  
**Goal:** Start a new game session and reach the first player’s turn.

**Preconditions:**
- The application can be launched.

**Trigger:**
- The Player runs the program.

1. The Player runs the program.
2. The Game System displays a language menu and prompts the player to enter a number to choose the language.
3. The Player enters a valid language choice
4. The Game System confirms the choice
5. The Game System displays a game mode menu and prompts the player to enter the number corresponding to their choice.
6. The Player enters a valid game mode option.
7. The Game System prints a confirmation message for the chosen mode.
8. The Game System prompts: “How many players will be playing (2–4)?”.
9. The Player enters a valid number of players within the allowed range.
10. The Game System acknowledges and initializes the game:

    - Creates the correct number of Player objects.
    - Builds and shuffles the deck.
    - Deals starting hands to each player (e.g., DEFUSE, SHUFFLE, CAT_THREE, …).
    - Chooses which player goes first and sets their turn/turn count.

11. The Game System displays the start-of-turn state, e.g.:

    - “It is currently player 0’s turn.”
    - “They have a hand of: DEFUSE SHUFFLE CAT_THREE CAT_THREE ATTACK SEE_THE_FUTURE”
    - “Player currently has 1 more turns.”
    - Prompts: “Do you want to end your turn? 1. Yes 2. No”

**Postconditions:**
- A new game session exists.
- The number of players and game mode are set.
- Initial hands have been dealt.
- The current player and their hand are visible, and the system is ready for actions on that turn.

**Exception Flows:**

- 3a. Player enters an invalid language number
    - The Game System prints an error and re-prompts for language.

- 6a. Player enters an invalid game mode number
    - The Game System prints an error and re-prompts for game mode.

- 9a. Player enters an invalid number of players (outside 2–4 or non-numeric)
    - The Game System prints an error and re-prompts for a valid player count.

### Use Case 2 – Play EXPLODING_KITTEN

**Primary Actor:** Active Player (current player)  
**Goal:** Resolve drawing an EXPLODING_KITTEN: either defuse it and keep playing, or explode and be eliminated.

**Preconditions:**
- A game is in progress.
- It is the Active Player’s turn.
- The Active Player has just drawn the top card from the Deck.

**Trigger:**
- The drawn card is an EXPLODING_KITTEN.

1. The Active Player draws the top card from the Deck.
2. The Game System reveals that the card is EXPLODING_KITTEN.
3. The Game System checks whether the Active Player has a DEFUSE card.
4. If the player does not have a DEFUSE:
    - The Game System marks the player as dead (eliminated).
    - The Game System removes the player from the turn rotation.
    - The Game System checks if only one player remains; if so, that player wins and the game ends.
5. If the player does have a DEFUSE:
    - The Game System prompts the player to choose where to put the EXPLODING_KITTEN back into the Deck (for example, by choosing an index).
    - The Game System inserts the EXPLODING_KITTEN card into the Deck at the chosen position.
    - The Game System removes one DEFUSE card from the Active Player’s hand.
    - The game continues with the next player’s turn as normal.


**Postconditions:**
- If no DEFUSE: the Active Player is dead and out of the game.
- If DEFUSE was used: the EXPLODING_KITTEN is hidden back in the Deck, the DEFUSE has been discarded, and play continues.
- The game may end if only one player remains.

**Exception Flows:**
- E1: Invalid insertion index for EXPLODING_KITTEN
  If the Active Player chooses an invalid index (negative or beyond the deck size), the Game System prints an error and re-prompts for a valid index.

### Use Case 3 – Play NOPE

**Primary Actor:** Reacting Player (a player holding NOPE)
**Secondary Actor:** Original Player who played an action card
**Goal:** Cancel the effect of another player’s action card such as SHUFFLE or SEE_THE_FUTURE.

**Preconditions:**
- A game is in progress.
- A player (the Original Player) has just played an action card (e.g., SHUFFLE or SEE_THE_FUTURE).
- At least one other player has a NOPE card in their hand.

**Trigger:**
- The Game System asks if any player wants to play NOPE, or a player declares that they will play NOPE.


1. The Original Player plays an action card (e.g., SHUFFLE).
2. Before the action takes effect, the Game System checks if any other player has a NOPE card.
3. The Game System prompts eligible players, for example:
   “Player X has played SHUFFLE. Do you want to play NOPE? 1. Yes 2. No”
4. A Reacting Player chooses “Yes” and selects the NOPE card from their hand.
5. The Game System removes the NOPE card from the Reacting Player’s hand and places it in the discard pile.
6. The Game System cancels the effect of the Original Player’s action card (as if it were never played).
7. The game continues with turn flow as if the original action card’s effect did not happen.

**Postconditions:**
- The NOPE card is in the discard pile.
- The canceled action card has no effect on the game state.
- Turn flow continues according to the normal rules.

**Exception Flows:**
- N1: No player chooses to play NOPE
  If no one responds with “Yes”, the Game System resolves the original action card normally.
- 
### Use Case 4 – Play SEE_THE_FUTURE

**Name:** Play SEE_THE_FUTURE  
**Primary Actor:** Active Player  
**Goal:** Allow the player to privately view the top few cards of the draw pile in order, without changing their order.

**Preconditions:**
- A game is in progress.
- It is the Active Player’s turn.
- The Active Player has at least one SEE_THE_FUTURE card in their hand.
- The draw pile contains at least one card.

**Trigger:**
- During their turn, the Active Player chooses to play a SEE_THE_FUTURE card from their hand.

1. The Game System shows it is the Active Player’s turn and their hand.
2. The Active Player chooses not to end the turn and opts to play a single card.
3. The Active Player selects the index corresponding to SEE_THE_FUTURE.
4. The Game System verifies the card can be played, removes SEE_THE_FUTURE from the Player’s hand, and places it in the discard pile.
5. The Game System looks at the top N cards of the draw pile.
6. The Game System displays these top N cards only to the Active Player, in order from top-most to next.
7. The Game System returns the cards to the top of the draw pile in the same order.
8. The Active Player’s turn continues; they may now use that knowledge to decide whether to play another card or end their turn and draw.

**Postconditions:**
- SEE_THE_FUTURE is in the discard pile.
- The Active Player knows the identities and order of the next N top cards.
- The draw pile order is unchanged relative to before the peek.

**Exception Flows:**
- **F1: Fewer than N cards remain in the deck**
    - The Game System reveals as many cards as exist (e.g., 1 or 2) and clearly explains that fewer than N were available.
- **F2: Invalid index / input**
    - If the Player enters an invalid card index or menu option, the Game System prints “Invalid input. Please try again.” and re-prompts.

### Use Case 5 – Play SHUFFLE

**Primary Actor:** Active Player  
**Goal:** Randomize the order of the draw pile so that no player knows which card will be drawn next.

**Preconditions:**
- A game is in progress.
- It is the Active Player’s turn.
- The Active Player has at least one SHUFFLE card in their hand.
- The draw pile contains at least one card.

**Trigger:**
- During their turn, the Active Player chooses to play a SHUFFLE card.

1. The Game System shows it is the Active Player’s turn and their current hand.
2. The Active Player chooses not to end the turn and opts to play a single card.
3. The Active Player selects the index of a SHUFFLE card.
4. The Game System verifies that SHUFFLE can be played, removes it from the Player’s hand, and places it in the discard pile.
5. The Game System randomly shuffles the draw pile, so that the order of cards is unknown to all players.
6. The Game System displays a message indicating that the deck has been shuffled.
7. The Active Player’s turn continues: they may choose to play additional cards or to end their turn and draw a card from the newly shuffled deck.

**Postconditions:**
- SHUFFLE is in the discard pile.
- The draw pile has been randomized.
- The Active Player can continue their turn or end it and draw from the shuffled deck.

**Alternate / Exception Flows:**
- **H1: Draw pile effectively empty**
    - If there are no meaningful cards to shuffle (e.g., deck has 0 or 1 card), the Game System may still consume the SHUFFLE card, display a message that shuffling had no real effect, and allow the Player to continue their turn.
