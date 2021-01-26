package com.ghc.app.utils;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Represents a square icon having no graphical content.
 *
 * <P>
 * Intended for use with <tt>Action</tt> and <tt>JMenuItem</tt>. Alignment of text is poor
 * when the same menu mixes menu items without an icon with menu items having an icon. In
 * such cases, items without an icon can use an <tt>EmptyIcon</tt> to take up the proper
 * amount of space, and allow for alignment of all text in the menu.
 */
public final class EmptyIcon implements Icon {

	/**
	 * Convenience object for small icons, whose size matches the size of small icons in
	 * Sun's graphics repository.
	 */
	static final EmptyIcon SIZE_16 = new EmptyIcon(16);

	/**
	 * Convenience object for large icons, whose size matches the size of large icons in
	 * Sun's graphics repository.
	 */
	static final EmptyIcon SIZE_24 = new EmptyIcon(24);

	/**
	 * EmptyIcon objects are always square, having identical height and width.
	 *
	 * @param aSize length of any side of the icon in pixels, must be in the range 1..100
	 *            (inclusive).
	 */
	public EmptyIcon(int aSize) {
		Args.checkForRange(aSize, 1, 100);
		fSize = aSize;
	}

	/**
	 * Return the icon size (width is same as height).
	 */
	public int getIconWidth() {
		return fSize;
	}

	/**
	 * Return the icon size (width is same as height).
	 */
	public int getIconHeight() {
		return fSize;
	}

	/**
	 * This implementation is empty, and paints nothing.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		// empty
	}

	// PRIVATE //
	private int fSize;
}
