package main;

import load.*;
import javax.swing.*;

/*
 * Main class file.
 * This is where the game gets the start.
 */

public class Main {

	public static void main(String[] args) {
	    GameLogger.initialize();
	    SwingUtilities.invokeLater(() -> new StartScreen());
	}
}