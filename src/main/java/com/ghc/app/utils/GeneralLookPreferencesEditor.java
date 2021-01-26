package com.ghc.app.utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalTheme;

/**
 * Allows editing of user preferences related to the general appearance of the
 * application, such as font size, toolbar icon size, theme, and the like.
 *
 * <P>
 * Also allows programmatic read-only access to the current stored preferences for these
 * items.
 */
public final class GeneralLookPreferencesEditor extends Observable {

	public JComponent getUI() {
		JPanel content = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		content.setLayout(gridbag);

		addShowToolbarAndLargeIcons(content);
		addTheme(content);
		addRestoreDefaults(content);
		UiUtil.addVerticalGridGlue(content, 4);

		matchGuiToStoredPrefs();
		return content;
	}

	public String getTitle() {
		return TITLE;
	}

	public int getMnemonic() {
		return MNEMONIC;
	}

	public void savePreferences() {
		fLogger.fine("Updating general preferences.");
		fPrefs.putBoolean(SHOW_TOOL_BAR_KEY, fShowToolBar.isSelected());
		fPrefs.putBoolean(USE_LARGE_ICONS, fLargeIcons.isSelected());
		fPrefs.put(THEME_NAME_KEY, fThemes.getSelectedItem().toString());
		setChanged();
		notifyObservers();
	}

	public void matchGuiToDefaultPreferences() {
		fShowToolBar.setSelected(SHOW_TOOLBAR_DEFAULT);
		fLargeIcons.setSelected(USE_LARGE_ICONS_DEFAULT);
		fThemes.setSelectedItem(Theme.valueOf(THEME_NAME_DEFAULT));
	}

	/**
	 * Return the stored user preference for hiding or showing the toolbar.
	 */
	public boolean hasShowToolBar() {
		return fPrefs.getBoolean(SHOW_TOOL_BAR_KEY, SHOW_TOOLBAR_DEFAULT);
	}

	/**
	 * Return the stored user preference for using large icons.
	 */
	public boolean hasLargeIcons() {
		return fPrefs.getBoolean(USE_LARGE_ICONS, USE_LARGE_ICONS_DEFAULT);
	}

	/**
	 * Return the stored user preference for the theme to be applied to the Java
	 * look-and-feel.
	 */
	public MetalTheme getTheme() {
		String themeName = fPrefs.get(THEME_NAME_KEY, THEME_NAME_DEFAULT);
		return Theme.valueOf(themeName);
	}

	// PRIVATE //

	private static final String GENERAL_LOOK_NODE = "stocksmonitor/ui/prefs/GeneralLook";
	private Preferences fPrefs = Preferences.userRoot().node(GENERAL_LOOK_NODE);

	private static final String TITLE = "General Look";
	private static final int MNEMONIC = KeyEvent.VK_G;

	private static final boolean SHOW_TOOLBAR_DEFAULT = true;
	private static final String SHOW_TOOL_BAR_KEY = "ShowToolbar";

	private static final boolean USE_LARGE_ICONS_DEFAULT = false;
	private static final String USE_LARGE_ICONS = "UseLargeIcons";

	// Theme name is mapped to Theme using Theme.valueOf
	private static final String THEME_NAME_DEFAULT = "Default";
	private static final String THEME_NAME_KEY = "ThemeName";

	private JCheckBox fShowToolBar;
	private JCheckBox fLargeIcons;
	private JComboBox fThemes;

	private static final Logger fLogger = Util.getLogger(GeneralLookPreferencesEditor.class);

	private void matchGuiToStoredPrefs() {
		fShowToolBar.setSelected(hasShowToolBar());
		fLargeIcons.setSelected(hasLargeIcons());
		fThemes.setSelectedItem(getTheme());
	}

	private void addShowToolbarAndLargeIcons(JPanel aContent) {
		JLabel toolbar = new JLabel("Show:");
		aContent.add(toolbar, getConstraints(0, 0));

		fShowToolBar = new JCheckBox("Toolbar");
		fShowToolBar.setMnemonic(KeyEvent.VK_O);
		aContent.add(fShowToolBar, getConstraints(0, 1));

		JLabel iconSize = new JLabel("Icon Size:");
		aContent.add(iconSize, getConstraints(1, 0));

		fLargeIcons = new JCheckBox("Use Large Icons");
		fLargeIcons.setMnemonic(KeyEvent.VK_U);
		iconSize.setLabelFor(fLargeIcons);
		aContent.add(fLargeIcons, getConstraints(1, 1));
	}

	private void addTheme(JPanel aContent) {
		JLabel theme = new JLabel("Theme:");
		theme.setDisplayedMnemonic(KeyEvent.VK_T);
		aContent.add(theme, getConstraints(2, 0));

		DefaultComboBoxModel themesModel = new DefaultComboBoxModel(Theme.VALUES.toArray());
		fThemes = new JComboBox(themesModel);
		theme.setLabelFor(fThemes);
		aContent.add(fThemes, getConstraints(2, 1));
	}

	private void addRestoreDefaults(JPanel aContent) {
		JButton restore = new JButton("Restore Defaults");
		restore.setMnemonic(KeyEvent.VK_D);
		restore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				matchGuiToDefaultPreferences();
			}
		});
		GridBagConstraints constraints = UiUtil.getConstraints(3, 1);
		constraints.insets = new Insets(UiConsts.ONE_SPACE, 0, 0, 0);
		aContent.add(restore, constraints);
	}

	private GridBagConstraints getConstraints(int aY, int aX) {
		GridBagConstraints result = UiUtil.getConstraints(aY, aX);
		result.insets = new Insets(0, 0, UiConsts.ONE_SPACE, UiConsts.ONE_SPACE);
		return result;
	}
}
