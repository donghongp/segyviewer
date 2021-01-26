package com.ghc.app.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;

/** Static convenience methods for GUIs which eliminate code duplication.*/
public final class UiUtil {

	/**
	 * <tt>pack</tt>, center, and <tt>show</tt> a window on the screen.
	 *
	 * <P>
	 * If the size of <tt>aWindow</tt> exceeds that of the screen, then the size of
	 * <tt>aWindow</tt> is reset to the size of the screen.
	 */
	public static void centerAndShow(Window aWindow) {
		// note that the order here is important

		aWindow.pack();
		/*
		 * If called from outside the event dispatch thread (as is the case upon startup,
		 * in the launch thread), then in principle this code is not thread-safe: once
		 * pack has been called, the component is realized, and (most) further work on the
		 * component should take place in the event-dispatch thread.
		 *
		 * In practice, it is exceedingly unlikely that this will lead to an error, since
		 * invisible components cannot receive events.
		 */
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window = aWindow.getSize();
		// ensure that no parts of aWindow will be off-screen
		if (window.height > screen.height) {
			window.height = screen.height;
		}
		if (window.width > screen.width) {
			window.width = screen.width;
		}
		int xCoord = (screen.width / 2 - window.width / 2);
		int yCoord = (screen.height / 2 - window.height / 2);
		aWindow.setLocation(xCoord, yCoord);

		aWindow.show();
	}

	/**
	 * A window is packed, centered with respect to a parent, and then shown.
	 *
	 * <P>
	 * This method is intended for dialogs only.
	 *
	 * <P>
	 * If centering with respect to a parent causes any part of the dialog to be off
	 * screen, then the centering is overidden, such that all of the dialog will always
	 * appear fully on screen, but it will still appear near the parent.
	 *
	 * @param aWindow must have non-null result for <tt>aWindow.getParent</tt>.
	 */
	public static void centerOnParentAndShow(Window aWindow) {
		// aWindow.pack();
		Dimension parent = aWindow.getParent().getSize();
		Dimension window = aWindow.getSize();
		int xCoord = aWindow.getParent().getLocationOnScreen().x + (parent.width / 2 - window.width / 2);
		int yCoord = aWindow.getParent().getLocationOnScreen().y + (parent.height / 2 - window.height / 2);
		// int xCoord = (parent.width/2 - window.width/2);
		// int yCoord = (parent.height/2 - window.height/2);
		// System.out.println("px="+aWindow.getParent().getLocationOnScreen().x+
		// " py="+aWindow.getParent().getLocationOnScreen().y+" pw="+parent.width+"
		// ph="+parent.height +
		// " ww="+window.width+" wh="+window.height);
		// System.out.println("px1="+xCoord+" py1="+yCoord);
		// Ensure that no part of aWindow will be off-screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int xOffScreenExcess = xCoord + window.width - screen.width;
		if (xOffScreenExcess > 0) {
			xCoord = xCoord - xOffScreenExcess;
		}
		if (xCoord < 0) {
			xCoord = 0;
		}
		int yOffScreenExcess = yCoord + window.height - screen.height;
		if (yOffScreenExcess > 0) {
			yCoord = yCoord - yOffScreenExcess;
		}
		if (yCoord < 0) {
			yCoord = 0;
		}
		// System.out.println("px2="+xCoord+" py2="+yCoord);
		aWindow.setLocation(xCoord, yCoord);
		// aWindow.setLocationRelativeTo(aWindow.getParent());
	}

	public static void centerOnParentAndShow(Window aWindow, int w, int h) {
		centerOnParentAndShow(aWindow);
		aWindow.setSize(w, h);
	}

	public static void centerOnParentAndShow(Window aWindow, double wPercentage, double hPercentage) {
		aWindow.pack();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int) (wPercentage * screen.width);
		int h = (int) (hPercentage * screen.height);

		centerOnParentAndShow(aWindow, w, h);
	}

	/**
	 * Return a border of dimensions recommended by the Java Look and Feel Design
	 * Guidelines, suitable for many common cases.
	 *
	 * <P>
	 * Each side of the border has size {@link UiConsts#STANDARD_BORDER}.
	 */
	public static Border getStandardBorder() {
		return BorderFactory.createEmptyBorder(
				UiConsts.STANDARD_BORDER,
				UiConsts.STANDARD_BORDER,
				UiConsts.STANDARD_BORDER,
				UiConsts.STANDARD_BORDER);
	}

	/**
	 * Return text which conforms to the Look and Feel Design Guidelines for the title of
	 * a dialog : the application name, a colon, then the name of the specific dialog.
	 *
	 * <P>
	 * Example return value: <tt>StocksMonitor: Preferences</tt>
	 *
	 * @param aSpecificDialogName must have visible content
	 */
	public static String getDialogTitle(String aSpecificDialogName) {
		Args.checkForContent(aSpecificDialogName);
		StringBuilder result = new StringBuilder(Consts.APP_NAME);
		result.append(": ");
		result.append(aSpecificDialogName);
		return result.toString();
	}

	/**
	 * Make a horizontal row of buttons of equal size, which are equally spaced, and
	 * aligned on the right.
	 *
	 * <P>
	 * The returned component has border spacing only on the top (of the size recommended
	 * by the Look and Feel Design Guidelines). All other spacing must be applied
	 * elsewhere ; usually, this will only mean that the dialog's top-level panel should
	 * use {@link #getStandardBorder}.
	 *
	 * @param aButtons contains the buttons to be placed in a row.
	 */
	public static JComponent getCommandRow(java.util.List<JComponent> aButtons) {
		equalizeSizes(aButtons);
		JPanel panel = new JPanel();
		LayoutManager layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.setBorder(BorderFactory.createEmptyBorder(UiConsts.THREE_SPACES, 0, 0, 0));
		panel.add(Box.createHorizontalGlue());
		Iterator<JComponent> buttonsIter = aButtons.iterator();
		while (buttonsIter.hasNext()) {
			panel.add(buttonsIter.next());
			if (buttonsIter.hasNext()) {
				panel.add(Box.createHorizontalStrut(UiConsts.ONE_SPACE));
			}
		}
		return panel;
	}

	/**
	 * Make a vertical row of buttons of equal size, whch are equally spaced, and aligned
	 * on the right.
	 *
	 * <P>
	 * The returned component has border spacing only on the left (of the size recommended
	 * by the Look and Feel Design Guidelines). All other spacing must be applied
	 * elsewhere ; usually, this will only mean that the dialog's top-level panel should
	 * use {@link #getStandardBorder}.
	 *
	 * @param aButtons contains the buttons to be placed in a column
	 */
	public static JComponent getCommandColumn(java.util.List<JComponent> aButtons) {
		equalizeSizes(aButtons);
		JPanel panel = new JPanel();
		LayoutManager layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		panel.setBorder(
				BorderFactory.createEmptyBorder(0, UiConsts.THREE_SPACES, 0, 0));
		// (no for-each is used here, because of the 'not-yet-last' check)
		Iterator<JComponent> buttonsIter = aButtons.iterator();
		while (buttonsIter.hasNext()) {
			panel.add(buttonsIter.next());
			if (buttonsIter.hasNext()) {
				panel.add(Box.createVerticalStrut(UiConsts.ONE_SPACE));
			}
		}
		panel.add(Box.createVerticalGlue());
		return panel;
	}

	/**
	 * Return an <tt>ImageIcon</tt> using its <tt>String</tt> identifier.
	 *
	 * @param aImageId starts with '/', and refers to an image resource which is
	 *            accessible through {@link Class#getResource}.
	 */
	public static ImageIcon getImageIcon(String aImageId) {
		if (!aImageId.startsWith(BACK_SLASH)) {
			throw new IllegalArgumentException(
					"Image identifier does not start with backslash: " + aImageId);
		}
		return fetchImageIcon(aImageId, UiUtil.class);
	}

	/**
	 * Return an <tt>ImageIcon</tt> using its <tt>String</tt> identifier, relative to a
	 * given class.
	 *
	 * @param aImageId does NOT start with '/', and must refer to an image resource which
	 *            is accessible through {@link Class#getResource}.
	 * @param aClass the class relative to which the image is located.
	 */
	public static ImageIcon getImageIcon(String aImageId, Class<?> aClass) {
		if (aImageId.startsWith(BACK_SLASH)) {
			throw new IllegalArgumentException(
					"Image identifier starts with a backslash: " + aImageId);
		}
		return fetchImageIcon(aImageId, aClass);
	}

	/**
	 * Return a square icon which paints nothing, and whose dimensions correspond to the
	 * user preference for icon size.
	 *
	 * <P>
	 * A common problem occurs with text alignment in menus, where there is a mixture of
	 * menu items with and without an icon. Adding an empty icon to menu items which do
	 * not have one will adjust its alignment to match that of the others which do have an
	 * icon.
	 */
	public static Icon getEmptyIcon() {
		GeneralLookPreferencesEditor prefs = new GeneralLookPreferencesEditor();
		return prefs.hasLargeIcons() ? EmptyIcon.SIZE_24 : EmptyIcon.SIZE_16;
	}

	/**
	 * Return a <tt>Dimension</tt> whose size is defined not in terms of pixels, but in
	 * terms of a given percent of the screen's width and height.
	 *
	 * <P>
	 * Use to set the preferred size of a component to a certain percentage of the screen.
	 *
	 * @param aPercentWidth percentage width of the screen, in range <tt>1..100</tt>.
	 * @param aPercentHeight percentage height of the screen, in range <tt>1..100</tt>.
	 */
	public static final Dimension getDimensionFromPercent(
			int aPercentWidth, int aPercentHeight) {
		int low = 1;
		int high = 100;
		Args.checkForRange(aPercentWidth, low, high);
		Args.checkForRange(aPercentHeight, low, high);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return calcDimensionFromPercent(screenSize, aPercentWidth, aPercentHeight);
	}

	/**
	 * Sets the items in <tt>aComponents</tt> to the same size.
	 *
	 * <P>
	 * Sets each component's preferred and maximum sizes. The actual size is determined by
	 * the layout manager, whcih adjusts for locale-specific strings and customized fonts.
	 * (See this <a href="http://java.sun.com/products/jlf/ed2/samcode/prefere.html">Sun
	 * doc</a> for more information.)
	 *
	 * @param aComponents items whose sizes are to be equalized
	 */
	public static void equalizeSizes(java.util.List<JComponent> aComponents) {
		Dimension targetSize = new Dimension(0, 0);
		for (JComponent comp : aComponents) {
			Dimension compSize = comp.getPreferredSize();
			double width = Math.max(targetSize.getWidth(), compSize.getWidth());
			double height = Math.max(targetSize.getHeight(), compSize.getHeight());
			targetSize.setSize(width, height);
		}
		setSizes(aComponents, targetSize);
	}

	/**
	 * Create a pair of components, a <tt>JLabel</tt> and an associated
	 * <tt>JTextField</tt>, as is typically used for user input.
	 *
	 * <P>
	 * The <tt>JLabel</tt> appears on the left, and the <tt>JTextField</tt> appears on the
	 * same row, just to the right of the <tt>JLabel</tt>. The <tt>JLabel</tt> has a
	 * mnemonic which forwards focus to the <tt>JTextField</tt> when activated.
	 *
	 * @param aContainer holds the pair of components.
	 * @param aName text of the <tt>JLabel</tt> component.
	 * @param aInitialValue possibly-null initial value to appear in the
	 *            <tt>JTextField</tt>; if <tt>null</tt>, then <tt>JTextField</tt> will be
	 *            blank.
	 * @param aMnemonic <tt>KeyEvent</tt> field, used as the mnemonic for the
	 *            <tt>JLabel</tt>.
	 * @param aConstraints applied to the <tt>JLabel</tt>; the corresponding constraints
	 *            for the <tt>JTextField</tt> are the same as <tt>aConstraints</tt>,
	 *            except for <tt>gridx</tt> being incremented by one; in addition, if
	 *            <tt>aConstraints</tt> has <tt>weightx=0</tt> (the default), then the
	 *            entry field will receive <tt>weightx=1.0</tt> (entry field gets more
	 *            horizontal space upon resize).
	 * @param aTooltip possibly-null text displayed as tool tip for the
	 *            <tt>JTextField</tt> ; if <tt>null</tt>, the tool tip is turned off.
	 * @return the user input <tt>JTextField</tt>.
	 */
	public static JTextField addSimpleEntryField(
			Container aContainer,
			String aName,
			String aInitialValue,
			int aMnemonic,
			GridBagConstraints aConstraints,
			String aTooltip) {
		Args.checkForNull(aName);

		JLabel label = new JLabel(aName);
		label.setDisplayedMnemonic(aMnemonic);
		aContainer.add(label, aConstraints);

		JTextField result = new JTextField(UiConsts.SIMPLE_FIELD_WIDTH);
		label.setLabelFor(result);
		result.setToolTipText(aTooltip);
		if (aInitialValue != null) {
			result.setText(aInitialValue);
		}
		aConstraints.gridx = ++aConstraints.gridx;
		if (aConstraints.weightx == 0.0) {
			aConstraints.weightx = 1.0;
		}
		aContainer.add(result, aConstraints);
		return result;
	}

	/**
	 * Return a set of constraints with convenient default values.
	 *
	 * <P>
	 * Return constraints with these values :
	 * <ul>
	 * <li><tt>gridx, gridy</tt> - set to <tt>aX, aY</tt>
	 * <li><tt>anchor - GridBagConstraints.WEST</tt>
	 * <li><tt>insets - Insets(0,0,0, UiConsts.ONE_SPACE)</tt>
	 * </ul>
	 *
	 * <P>
	 * All other items simply take their default values :
	 * <ul>
	 * <li><tt>fill - GridBagConstraints.NONE</tt>
	 * <li><tt>gridwidth, gridheight - 0, 0</tt>
	 * <li><tt>weightx , weighty - 0, 0</tt>
	 * <li><tt>ipadx, ipady - 0, 0</tt>
	 * </ul>
	 *
	 * <P>
	 * The caller is free to change the returned constraints, to customize for their
	 * particular needs.
	 *
	 * @param aY in range <tt>0..10</tt>.
	 * @param aX in range <tt>0..10</tt>.
	 */
	public static GridBagConstraints getConstraints(int aY, int aX) {
		int low = 0;
		int high = 10;
		Args.checkForRange(aY, low, high);
		Args.checkForRange(aX, low, high);
		GridBagConstraints result = new GridBagConstraints();
		result.gridy = aY;
		result.gridx = aX;
		result.anchor = GridBagConstraints.WEST;
		result.insets = new Insets(0, 0, 0, UiConsts.ONE_SPACE);
		return result;
	}

	/**
	 * Return {@link #getConstraints(int, int)}, with the addition of setting
	 * <tt>gridwidth</tt> to <tt>aWidth</tt>, and setting <tt>gridheight</tt> to
	 * <tt>aHeight</tt>.
	 *
	 * <P>
	 * The caller is free to change the returned constraints, to customize for their
	 * particular needs.
	 *
	 * @param aY in range <tt>0..10</tt>.
	 * @param aX in range <tt>0..10</tt>.
	 * @param aWidth in range <tt>1..10</tt>.
	 * @param aHeight in range <tt>1..10</tt>.
	 */
	public static GridBagConstraints getConstraints(int aY, int aX, int aWidth, int aHeight) {
		int low = 0;
		int high = 10;
		Args.checkForRange(aHeight, low, high);
		Args.checkForRange(aWidth, low, high);
		GridBagConstraints result = getConstraints(aY, aX);
		result.gridheight = aHeight;
		result.gridwidth = aWidth;
		return result;
	}

	/**
	 * Create a pair of <tt>JLabel</tt> components, as is typically needed for display of
	 * a name-value pair.
	 *
	 * <P>
	 * The name appears on the left, and the value appears on the right, all on the same
	 * row. A colon and an empty space are appended to the name.
	 *
	 * <P>
	 * If the the length of "value" label is greater than
	 * {@link UiConsts#MAX_LABEL_LENGTH}, then the text is truncated, an ellipsis is
	 * placed at its end, and the full text is placed in a tooltip.
	 *
	 * @param aContainer holds the pair of components.
	 * @param aName text of the name <tt>JLabel</tt>.
	 * @param aValue possibly-null ; if null, then an empty <tt>String</tt> is used for
	 *            the value; otherwise <tt>Object.toString</tt> is used.
	 * @param aConstraints for the name <tt>JLabel</tt>; the corresponding constraints for
	 *            the value <tt>JLabel</tt> are mostly taken from <tt>aConstraints</tt>,
	 *            except for <tt>gridx</tt> being incremented by one (<tt>weightx</tt> may
	 *            differ as well - see <tt>aWeightOnDisplay</tt>.)
	 * @param aWeightOnDisplay if true, then set <tt>weightx</tt> for the value field to
	 *            1.0 (to give it more horizontal space upon resize).
	 * @return the <tt>JLabel</tt> for the value (which is usually variable).
	 */
	public static JLabel addSimpleDisplayField(
			Container aContainer,
			String aName,
			Object aValue,
			GridBagConstraints aConstraints,
			boolean aWeightOnDisplay) {
		StringBuilder formattedName = new StringBuilder(aName);
		formattedName.append(": ");
		JLabel name = new JLabel(formattedName.toString());
		aContainer.add(name, aConstraints);

		String valueText = (aValue != null ? aValue.toString() : Consts.EMPTY_STRING);
		JLabel value = new JLabel(valueText);
		truncateLabelIfLong(value);
		aConstraints.gridx = ++aConstraints.gridx;
		if (aWeightOnDisplay) {
			aConstraints.weightx = 1.0;
		}
		aContainer.add(value, aConstraints);

		return value;
	}

	/**
	 * Present a number of read-only items to the user as a vertical listing of
	 * <tt>JLabel</tt> name-value pairs.
	 *
	 * <P>
	 * Each pair is added in the style of {@link #addSimpleDisplayField} (its
	 * <tt>aConstraints</tt> param are those returned by
	 * {@link #getConstraints(int, int)}, and its <tt>aWeightOnDisplay</tt> param is set
	 * to <tt>true</tt>).
	 *
	 * <P>
	 * The order of presentation is determined by the iteration order of
	 * <tt>aNameValuePairs</tt>.
	 *
	 * <P>
	 * The number of items which should be presented using this method is limited, since
	 * no scrolling mechanism is given to the user.
	 *
	 * @param aContainer holds the display fields.
	 * @param aNameValuePairs has <tt>String</tt> keys for the names, and values are
	 *            possibly null <tt>Object</tt>s; if null, then an empty <tt>String</tt>
	 *            is displayed, otherwise <tt>Object.toString</tt> is called on the value
	 *            and displayed.
	 */
	public static void addSimpleDisplayFields(
			Container aContainer, Map<String, String> aNameValuePairs) {
		Set<String> keys = aNameValuePairs.keySet();
		int rowIdx = 0;
		for (String name : keys) {
			String value = aNameValuePairs.get(name);
			if (value == null) {
				value = Consts.EMPTY_STRING;
			}
			UiUtil.addSimpleDisplayField(
					aContainer,
					name,
					value,
					UiUtil.getConstraints(rowIdx, 0),
					true);
			++rowIdx;
		}
	}

	/**
	 * Adds "glue" (an empty component with desired resizing behavior) to the bottom row
	 * of a <tt>GridBagLayout</tt> of components. When resized, this glue will take up
	 * extra vertical space.
	 *
	 * <P>
	 * This method is especially useful for text data presented in a listing or tabular
	 * form. Such components naturally resize horizontally, while their vertical resizing
	 * should often be absent. If such a listing is resized vertically, then this glue can
	 * take up the remaining vertical space, keeping the text at the top.
	 *
	 * @param aPanel uses <tt>GridBagLayout</tt>, and contains components whose
	 *            <tt>weighty</tt> values are all 0.0 (the default).
	 * @param aLastRowIdx index of the last row of components, in which the glue will be
	 *            placed.
	 */
	public static void addVerticalGridGlue(JPanel aPanel, int aLastRowIdx) {
		GridBagConstraints glueConstraints = UiUtil.getConstraints(aLastRowIdx, 0);
		glueConstraints.weighty = 1.0;
		glueConstraints.fill = GridBagConstraints.VERTICAL;
		aPanel.add(new JLabel(), glueConstraints);
	}

	/**
	 * Return a <tt>String</tt>, suitable for presentation to the end user, representing a
	 * percentage having two decimal places, using the default locale.
	 *
	 * <P>
	 * An example return value is "5.15%". The intent of this method is to provide a
	 * standard representation and number of decimals for the entire application. If a
	 * different number of decimal places is required, then the caller should use
	 * <tt>NumberFormat</tt> instead.
	 */
	public static String getLocalizedPercent(Number aNumber) {
		NumberFormat localFormatter = NumberFormat.getPercentInstance();
		localFormatter.setMinimumFractionDigits(2);
		return localFormatter.format(aNumber.doubleValue());
	}

	/**
	 * Return a <tt>String</tt>, suitable for presentation to the end user, representing
	 * an integral number with no decimal places, using the default locale.
	 *
	 * <P>
	 * An example return value is "8,000". The intent of this method is to provide a
	 * standard representation of integers for the entire application.
	 */
	public static String getLocalizedInteger(Number aNumber) {
		NumberFormat localFormatter = NumberFormat.getNumberInstance();
		return localFormatter.format(aNumber.intValue());
	}

	/**
	 * Return a <tt>String</tt>, suitable for presentation to the end user, representing a
	 * date in <tt>DateFormat.SHORT</tt> and the default locale.
	 */
	public static String getLocalizedTime(Date aDate) {
		DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		return dateFormat.format(aDate);
	}

	/**
	 * Make the sytem emit a beep.
	 *
	 * <P>
	 * May not beep unless the speakers are turned on, so this cannot be guaranteed to
	 * work.
	 */
	public static void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	/**
	 * An alternative to multi-line labels, for the presentation of several lines of text,
	 * and for which the line breaks are determined solely by the widget.
	 *
	 * @param aText must have visible content, doesn't contain newline characters or html.
	 * @return <tt>JTextArea</tt> which is not editable, has improved spacing over the
	 *         supplied default (placing {@link UiConsts#ONE_SPACE} on the left and
	 *         right), which wraps lines on word boundaries, and whose background color is
	 *         the same as
	 *         {@link javax.swing.plaf.metal.MetalLookAndFeel#getMenuBackground}.
	 */
	public static JTextArea getStandardTextArea(String aText) {
		Args.checkForContent(aText);
		if (aText.indexOf(Consts.NEW_LINE) != -1) {
			throw new IllegalArgumentException("Must not contain new line characters: " + aText);
		}
		JTextArea result = new JTextArea(aText);
		result.setEditable(false);
		result.setWrapStyleWord(true);
		result.setLineWrap(true);
		result.setMargin(new Insets(0, UiConsts.ONE_SPACE, 0, UiConsts.ONE_SPACE));
		// this is a bit hacky: the desired color is "secondary3", but cannot see how
		// to reference it directly; hence, an element which uses secondary3 is used
		// instead.
		result.setBackground(MetalLookAndFeel.getMenuBackground());

		return result;
	}

	/**
	 * An alternative to multi-line labels, for the presentation of several lines of text,
	 * and for which line breaks are determined solely by <tt>aText</tt>, and not by the
	 * widget.
	 *
	 * @param aText has visible content
	 * @return <tt>JTextArea</tt> which is not editable, has improved spacing over the
	 *         supplied default (placing {@link UiConsts#ONE_SPACE} on the left and
	 *         right), and whose background color is the same as
	 *         {@link javax.swing.plaf.metal.MetalLookAndFeel#getMenuBackground}.
	 */
	public static JTextArea getStandardTextAreaHardNewLines(String aText) {
		Args.checkForContent(aText);
		JTextArea result = new JTextArea(aText);
		result.setEditable(false);
		result.setMargin(new Insets(0, UiConsts.ONE_SPACE, 0, UiConsts.ONE_SPACE));
		result.setBackground(MetalLookAndFeel.getMenuBackground());
		return result;
	}

	/**
	 * Imposes a uniform horizontal alignment on all items in a container.
	 *
	 * <P>
	 * Intended especially for <tt>BoxLayout</tt>, where all components need to share the
	 * same alignment in order for display to be reasonable. (Indeed, this method may only
	 * work for <tt>BoxLayout</tt>, since apparently it is the only layout to use
	 * <tt>setAlignmentX, setAlignmentY</tt>.)
	 *
	 * @param aContainer contains only <tt>JComponent</tt> objects.
	 */
	public static void alignAllX(Container aContainer, UiUtil.AlignX aAlignment) {
		java.util.List<Component> components = Arrays.asList(aContainer.getComponents());
		for (Component comp : components) {
			JComponent jcomp = (JComponent) comp;
			jcomp.setAlignmentX(aAlignment.getValue());
		}
	}

	/** Enumeration for horizontal alignment. */
	public enum AlignX {
		LEFT(Component.LEFT_ALIGNMENT), CENTER(Component.CENTER_ALIGNMENT), RIGHT(Component.RIGHT_ALIGNMENT);
		public float getValue() {
			return fValue;
		}

		private final float fValue;

		private AlignX(float aValue) {
			fValue = aValue;
		}
	}

	/**
	 * Imposes a uniform vertical alignment on all items in a container.
	 *
	 * <P>
	 * Intended especially for <tt>BoxLayout</tt>, where all components need to share the
	 * same alignment in order for display to be reasonable. (Indeed, this method may only
	 * work for <tt>BoxLayout</tt>, since apparently it is the only layout to use
	 * <tt>setAlignmentX, setAlignmentY</tt>.)
	 *
	 * @param aContainer contains only <tt>JComponent</tt> objects.
	 */
	public static void alignAllY(Container aContainer, UiUtil.AlignY aAlignment) {
		java.util.List components = Arrays.asList(aContainer.getComponents());
		Iterator compsIter = components.iterator();
		while (compsIter.hasNext()) {
			JComponent comp = (JComponent) compsIter.next();
			comp.setAlignmentY(aAlignment.getValue());
		}
	}

	/** Type-safe enumeration vertical alignment. */
	public enum AlignY {
		TOP(Component.TOP_ALIGNMENT), CENTER(Component.CENTER_ALIGNMENT), BOTTOM(Component.BOTTOM_ALIGNMENT);
		float getValue() {
			return fValue;
		}

		private final float fValue;

		private AlignY(float aValue) {
			fValue = aValue;
		}
	}

	/**
	 * Ensure that <tt>aRootPane</tt> has no default button associated with it.
	 *
	 * <P>
	 * Intended mainly for dialogs where the user is confirming a delete action. In this
	 * case, an explicit Yes or No is preferred, with no default action being taken when
	 * the user hits the Enter key.
	 */
	public static void noDefaultButton(JRootPane aRootPane) {
		aRootPane.setDefaultButton(null);
	}

	// PRIVATE //

	private static final String BACK_SLASH = "/";

	/**
	 * If <tt>aIconName</tt> indicates that the icon is part of the standard graphic
	 * repository (by starting with "/toolbar"), then append either "16.gif" or "24.gif"
	 * to the name, according to the user's current preference for icon size.
	 */
	private static String addSizeToStandardIcon(String aIconName) {
		assert (Util.textHasContent(aIconName));
		StringBuilder result = new StringBuilder(aIconName);
		if (aIconName.startsWith("/toolbar")) {
			GeneralLookPreferencesEditor prefs = new GeneralLookPreferencesEditor();
			if (prefs.hasLargeIcons()) {
				result.append("24.gif");
			} else {
				result.append("16.gif");
			}
		}
		return result.toString();
	}

	private static void setSizes(java.util.List aComponents, Dimension aDimension) {
		Iterator compsIter = aComponents.iterator();
		while (compsIter.hasNext()) {
			JComponent comp = (JComponent) compsIter.next();
			comp.setPreferredSize((Dimension) aDimension.clone());
			comp.setMaximumSize((Dimension) aDimension.clone());
		}
	}

	private static Dimension calcDimensionFromPercent(
			Dimension aSourceDimension, int aPercentWidth, int aPercentHeight) {
		int width = aSourceDimension.width * aPercentWidth / 100;
		int height = aSourceDimension.height * aPercentHeight / 100;
		return new Dimension(width, height);
	}

	/**
	 * If aLabel has text which is longer than MAX_LABEL_LENGTH, then truncate the label
	 * text and place an ellipsis at the end; the original text is placed in a tooltip.
	 *
	 * This is particularly useful for displaying file names, whose length can vary widely
	 * between deployments.
	 */
	private static void truncateLabelIfLong(JLabel aLabel) {
		String originalText = aLabel.getText();
		if (originalText.length() > UiConsts.MAX_LABEL_LENGTH) {
			aLabel.setToolTipText(originalText);
			String truncatedText = originalText.substring(0, UiConsts.MAX_LABEL_LENGTH) + Consts.ELLIPSIS;
			aLabel.setText(truncatedText);
		}
	}

	private static ImageIcon fetchImageIcon(String aImageId, Class<?> aClass) {
		String imgLocation = addSizeToStandardIcon(aImageId);
		URL imageURL = aClass.getResource(imgLocation);
		if (imageURL != null) {
			return new ImageIcon(imageURL);
		} else {
			throw new IllegalArgumentException("Cannot retrieve image using id: " + aImageId);
		}
	}
}
