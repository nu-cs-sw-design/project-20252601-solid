### Use Case 1: Start New Game

**Primary Actor:** Player  
**Goal:** Start a new game session and reach the first player’s turn.

**Preconditions:**
- The application can be launched.

**Trigger:**
- The Player runs the program.

**Main Success Scenario:**
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

