/*
 * =========================
 * MINI AKINATOR GUI OVERVIEW
 * =========================
 *
 * The interface is divided into three main screens:
 *
 * 1. START MENU
 *    - Displays the game title
 *    - Plays background menu music
 *    - Contains buttons:
 *        • Start Game
 *        • Add Character
 *        • Exit
 *
 * 2. IN-GAME SCREEN
 *    - Displays the current question from the engine
 *    - Plays in-game background music
 *    - Provides three answer buttons:
 *        • YES
 *        • NO
 *        • I DON'T KNOW
 *    - Sends user answers to the Akinator_Engine
 *    - Updates the next question dynamically
 *
 * 3. ADD CHARACTER MENU
 *    - Allows the user to add a new character to the database
 *    - Prompts the user to enter the character's name
 *    - Asks all available questions so attributes can be recorded
 *    - Stores answers (YES/NO/I DON'T KNOW) as true/false/null
 *    - Saves the new character to the JSON file for future games
 *
 * The GUI is responsible only for input/output.
 * All decision-making logic is handled by the Akinator_Engine.
 */

public class GUI {
    
}
