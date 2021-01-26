package com.ghc.app.general;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import com.ghc.app.resources.GhcImageLoader;

public class MouseModes {
	public static final int NO_MODE = 1;
	public static final int ZOOM_MODE = 2;
	public static final int PAN_MODE = 3;
	public static final int KILL_MODE = 4;
	public static final int SPECTRUM_MODE = 5;
	public static final int PICK_MODE = 6;

	public static final Cursor ZOOM_CURSOR;
	public static final Cursor PAN_CURSOR;
	public static final Cursor KILL_CURSOR;
	public static final Cursor SPECTRUM_CURSOR;
	public static final Cursor PICK_CURSOR;

	static {
		ZOOM_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
				GhcImageLoader.getImage("icons/csZoomCursor.png"), new Point(15, 15), "ZOOM_CURSOR");
		PICK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
				GhcImageLoader.getImage("icons/csPickCursor.png"), new Point(15, 15), "PICK_CURSOR");
		SPECTRUM_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
				GhcImageLoader.getImage("icons/csSpectrumCursor.png"), new Point(15, 15), "SPECTRUM_CURSOR");
		PAN_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		KILL_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	}
}
