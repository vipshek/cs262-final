package edu.harvard.cs262.DistributedGame.BattleshipGame;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.Key;

public class BattleshipWindowAdapter extends WindowAdapter {

	public void onUnhandledKeyboardInteraction(Window window, Key key) {
		if (key.getKind() == Key.Kind.NormalKey) {
			if (key.getCharacter() == ' ') {
				BattleshipSquare square = (BattleshipSquare) ((BattleshipWindow) window).focusedBox;
				((BattleshipWindow) window).sendInput(square.x, square.y);
			} else if (key.getCharacter() == 'q') {
				window.close();
			}
		}
	}

	public void onFocusChanged(Window window, Interactable fromComponent, Interactable toComponent) {
		((BattleshipWindow) window).focusedBox = toComponent;
	}
}