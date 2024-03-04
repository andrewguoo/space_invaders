This involved implementing various design patterns to create a functioning Space Invaders game.

The GOF design patterns implemented include :
- Difficulty level was chosen via Singleton design pattern
- Time and Score was kept via Observer design pattern
- Undo was implemented using Memento design pattern

Difficulty level :
- My implementation involves a level select screen, which users can interact with before they launch the game.
- There will be 3 different buttons and depending on what option they pick a different json file will be loaded.
- Buttons are pressed using the mouse
- If no option is chosen, there is no default and the game will exit

Time and Score :
- My implementation clocks the duration of the game as well as the score until the game is over
- Time is shown on the top left of the screen, whereas Score on the top right
- Time is clocked automatically, and is always there until the game ends
- Score is calculated based on what the player has shot, for instance :
    - Slow Projectile = 1
    - Fast Projectile = 2
    - Slow Alien = 3
    - Fast Alien = 4

Undo :
- My implementation of undo involves pressing a button to save a state, and then pressing another to undo
- The 'Z' key should be pressed when the user wants to save the state
- The 'X' key should be pressed when the user wants to return to the saved state
- A user may undo, only if there is a state to return to and only one undo is allowed per save
    - This is to discourage undo spamming

Cheat :
- My implementation of cheat involves pressing a button to activate a cheat of the player's choice
- The cheats :
    - The 'A' key should be pressed to remove all Slow Projectiles
    - The 'S' key should be pressed to remove all Fast Projectiles
    - The 'D' key should be pressed to remove all Slow Aliens
    - The 'F' key should be pressed to remove all Fast Aliens
- Additionally, removing all these different entities will also add points to the score

To run the program, use the following command :

         gradle clean build run

Controls :

       left arrow key (<) : move player left
       right arrow key (>) : move player right
       spacebar : shoot a projectile
       Z key : save current game state
       X key : load saved game state
       A key : remove all slow projectiles
       S key : remove all fast projectiles
       D key : remove all slow aliens
       F key : remove all fast aliens
