package edu.harvard.cs262.DistributedGame.BattleshipGame;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.Key;

/**
 * Extends lanterna's WindowAdapter, which itself is an abstract class
 * implementing the WindowListener interface. Handles keyboard interactions 
 * from the user.
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class BattleshipWindowAdapter extends WindowAdapter {
    /**
     * Handle keyboard interaction on the window. Accesses parent
     * window's focusedBox instance variable to send input to client.
     * Also accesses focused BattleshipSquare's row and column variables.
     */
    public void onUnhandledKeyboardInteraction(Window window, Key key) {
        if (key.getKind() == Key.Kind.NormalKey) {
            // If space bar pressed, send input position to client
            if (key.getCharacter() == ' ') {
                BattleshipSquare square = (BattleshipSquare) ((BattleshipWindow) window).focusedBox;
                if (square != null)
                    ((BattleshipWindow) window).sendInput(square.row, square.column);
            } 
            // If q pressed, exit the game.
            else if (key.getCharacter() == 'q') {
                window.getOwner().getScreen().stopScreen();
            }
        }
    }

    /**
     * Update parent window's focusedBox instance variable when focus is changed.
     * focusedBox is used in onUnhandledKeyboardInteraction above.
     */
    public void onFocusChanged(Window window, Interactable fromComponent, Interactable toComponent) {
        ((BattleshipWindow) window).focusedBox = toComponent;
    }
}
