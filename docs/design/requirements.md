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
5. The Game System displays a game mode menu and prompts teh player to enter the number corresponding to their choice.
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

### Use Case 2 – Play ATTACK

**Primary Actor:** Active Player (current player)  
**Secondary Actor:** Next Player in turn order;
**Goal:** End the current player’s turn without drawing and force the next player to take multiple turns.

**Preconditions:**
- A game is in progress.
- It is the Active Player’s turn.
- The Active Player has at least one ATTACK card in their hand.
- The Active Player has not yet drawn a card for this turn.

**Trigger:**
- During their turn, the Active Player chooses to play an ATTACK card from their hand.

1. The Game System shows it is the Active Player’s turn, along with their current hand and remaining turns.
2. The Active Player chooses not to end their turn and opts to play a card instead  and then chooses the index of an ATTACK card.
3. The Game System verifies that ATTACK can be played at this time (correct phase, card exists in hand).
4. The Game System removes the ATTACK card from the Active Player’s hand and places it into the discard pile.
5. The Game System immediately ends the Active Player’s turn without requiring them to draw a card.
6. The Game System updates the game state so that the next player in turn order must take 2 full turns in a row.
7. The Game System displays a message indicating:
    - That the Active Player played ATTACK.
8. Play proceeds to the next player, who must now take the required number of turns.

**Postconditions:**
- ATTACK is no longer in the Active Player’s hand and is in the discard pile.
- The Active Player’s turn has ended without drawing a card.
- The next player must take multiple turns according to the attack rules.

**Exception Flows:**
- **A1: ATTACK is canceled by NOPE**
    1. After the ATTACK is played, another player who has a NOPE card may be prompted: “Player X has a Nope Card. Would you like to play it? 1. Yes 2. No.”
    2. If that player chooses to play NOPE:
        - The Game System removes NOPE from their hand, places it in the discard pile, and cancels the ATTACK.
        - The game state is updated so the extra turns are not applied.
    3. The Active Player’s turn continues as if ATTACK had not been played (they may play another card or end their turn and draw).
- **A2: Invalid selection**
    - If the Player enters an invalid menu option or an index that doesn’t correspond to a card, the Game System prints an error (e.g., “Invalid input. Please try again.”) and re-prompts for a valid choice.

### Use Case 3 – Play SKIP

**Primary Actor:** Active Player  
**Goal:** End the current player’s turn without drawing a card, potentially reducing or clearing required turns due to an attack.

**Preconditions:**
- A game is in progress.
- It is the Active Player’s turn.
- The Active Player has at least one SKIP card in their hand.
- The Active Player has not yet drawn a card for this turn.

**Trigger:**
- During their turn, the Active Player chooses to play a SKIP card from their hand.


1. The Game System shows it is the Active Player’s turn and prompts: “Do you want to end your turn? 1. Yes 2. No.”
2. The Active Player chooses No, then chooses to play a single card and selects the index corresponding to SKIP.
3. The Game System verifies SKIP can be played.
4. The Game System removes SKIP from the Active Player’s hand and places it in the discard pile.
5. The Game System immediately ends the Active Player’s current turn without requiring them to draw a card.
6. If the Active Player was required to take multiple turns because of an ATTACK:
    - SKIP cancels one of those required turns (so 2 Skips would cancel both turns).
7. The Game System either:
    - Moves to the next required turn for the same player (if they still have remaining attack turns), or
    - Passes the turn to the next player in the turn order (if no turns remain).

**Postconditions:**
- SKIP is in the discard pile.
- The Active Player has completed this turn without drawing.
- Any attack-related required turns are reduced appropriately.

**Exception Flows:**
- **S1: Invalid play**
    - If the Player tries to play SKIP in an invalid state (e.g., after already drawing), the Game System rejects the action, shows an error, and leaves the card in the Player’s hand.

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
