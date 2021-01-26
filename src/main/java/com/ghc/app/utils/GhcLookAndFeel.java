package com.ghc.app.utils;

/**
 * This class establishes the Look and Feel of TRex-One Enterprise. All colors and
 * UIDelegates whould be specified here. Global changes to LookAndFeel is implemented
 * here.
 *
 * NOTE: TComponents often specify colors, insets, etc. and margainlize the usefullness of
 * this class. Over time, all display characteristics should be removed from the
 * TComponents where they are not needed for specific appearence. JComponents will offer a
 * more "pure" look and feel and smaller memory footprint.
 *
 * cobrien 3/10/2003
 */

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicLookAndFeel;

public class GhcLookAndFeel extends BasicLookAndFeel {

	private static final long serialVersionUID = -1;

	public static Color MENU_LINE_COLOR = new Color(50, 200, 50);

	public static Color MEDIUM_BACKGROUND_COLOR = new Color(214, 207, 189);
	public static Color DARK_BACKGROUND_COLOR = new Color(189, 170, 140);
	public static Color LIGHT_BACKGROUND_COLOR = new Color(236, 233, 223);
	public static Color CONTENT_BACKGROUND_COLOR = new Color(214, 207, 189);
	public static Color CONTENT_LIGHT_BACKGROUND_COLOR = new Color(236, 233, 223);
	public static Color BLANK_BACKGROUND_COLOR = new Color(189, 170, 140);
	public static Color BORDER_HIGHLIGHT_COLOR = new Color(236, 233, 223);
	public static Color BORDER_INNER_HIGHLIGHT_COLOR = new Color(214, 207, 189);
	public static Color BORDER_INNER_SHADOW_COLOR = new Color(189, 170, 140);
	public static Color TABLE_BACKGROUND_COLOR = new Color(236, 233, 223);

	// COLORS
	public static Color BORDER_SHADOW_COLOR = new Color(129, 112, 75);
	public static Color FOCUS_FOREGROUND_COLOR = new Color(51, 102, 204);
	public static Color LOGIN_LABEL_HIGHLIGHT_COLOR = new Color(0, 51, 102);
	public static Color TITLE_BACKGROUND_COLOR = new Color(102, 153, 204);
	public static Color REQUIRED_FIELD_INDICATOR_COLOR = new Color(255, 0, 0);
	public static Color SELECTION_BACKGROUND_COLOR = new Color(189, 170, 140);
	public static Color TABLE_SELECTED_DKBLUE_COLOR = new Color(0, 51, 102);
	public static Color DISABLED_TEXT_COLOR_COLOR = new Color(80, 80, 80);
	public static Color TEXT_COLOR_COLOR = Color.black;
	public static Color BUTTON_BORDER_HIGHLIGHT_COLOR = Color.white;
	public static Color PRACTICE_SYSTEM_YELLOW_COLOR = new Color(255, 255, 0);
	public static Color TITLE_BACKGROUND_GREEN_COLOR = new Color(129, 112, 75);;
	public static Color WHITE_BACKGOUND_COLOR = Color.WHITE;
	public static Color YELLOW_COLOR_COLOR = Color.YELLOW;
	public static Color IMAGE_BACKGROUND_COLOR = new Color(153, 153, 153);

	public GhcLookAndFeel() {
		super();
	}

	public String getID() {
		return "Ghc-One";
	}

	public String getName() {
		return "Ghc-One Look And Feel";
	}

	public String getDescription() {
		return "The Cross Platform Look and Feel of Ghc Enterprise.";
	}

	public boolean isNativeLookAndFeel() {
		return false;
	}

	public boolean isSupportedLookAndFeel() {
		return true;
	}

	public static void setDefaultLookAndFeel() {

		UIManager.put("Panel.background", Color.WHITE);
		UIManager.put("Dialog.background", Color.RED);
		UIManager.put("CheckBox.background", Color.WHITE);
		UIManager.put("ComboBox.background", Color.WHITE);
		UIManager.put("ComboBox.buttonBackground", Color.WHITE);
		// UIManager.put("ComboBox.selectionBackground", Color.GREEN);
		// UIManager.put("ComboBox.border", BLACK_MATTE_BORDER);
		UIManager.put("TextArea.background", Color.WHITE);
		UIManager.put("RadioButton.background", Color.WHITE);
		UIManager.put("Box.background", Color.WHITE);
		UIManager.put("ToggleButton.background", Color.WHITE);
		UIManager.put("Separator.background", Color.WHITE);
		UIManager.put("ScrollPane.background", Color.WHITE);
		UIManager.put("SplitPane.background", Color.WHITE);
		UIManager.put("Slider.background", Color.WHITE);
		UIManager.put("TabbedPane.background", Color.WHITE);
		UIManager.put("Label.background", Color.WHITE);
		UIManager.put("OptionPane.background", Color.WHITE);
		UIManager.put("TextField.background", Color.WHITE);
		UIManager.put("TextPane.background", Color.WHITE);
		UIManager.put("EditorPane.background", Color.WHITE);
		UIManager.put("Table.background", Color.WHITE);
		// UIManager.put("Viewport.background", Color.WHITE);
	}

	public void initialize() {
		super.initialize();
	}

}

