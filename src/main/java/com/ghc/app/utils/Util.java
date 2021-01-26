package com.ghc.app.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Static convenience methods for common tasks, which eliminate code duplication.
 */
public final class Util {

	/**
	 * Return <tt>true</tt> only if <tt>aText</tt> is not null, and is not empty after
	 * trimming. (Trimming removes both leading/trailing whitespace and ASCII control
	 * characters.)
	 *
	 * <P>
	 * For checking argument validity, {@link Args#checkForContent} should be used instead
	 * of this method.
	 *
	 * @param aText possibly-null.
	 */
	public static boolean textHasContent(String aText) {
		return (aText != null) && (aText.trim().length() > 0);
	}

	/**
	 * Return <tt>true</tt> only if <tt>aNumber</tt> is in the range <tt>aLow..aHigh</tt>
	 * (inclusive).
	 *
	 * <P>
	 * For checking argument validity, {@link Args#checkForRange} should be used instead
	 * of this method.
	 *
	 * @param aLow less than or equal to <tt>aHigh</tt>.
	 */
	static public boolean isInRange(int aNumber, int aLow, int aHigh) {
		if (aLow > aHigh) {
			throw new IllegalArgumentException("Low is greater than High.");
		}
		return (aLow <= aNumber && aNumber <= aHigh);
	}

	/**
	 * Return <tt>true</tt> if <tt>aBoolean</tt> equals "true" (ignore case), or
	 * <tt>false</tt> if <tt>aBoolean</tt> equals "false" (ignore case).
	 *
	 * <P>
	 * Note that this behavior is different from that of <tt>Boolean.getValue</tt>.
	 *
	 * @param aBoolean equals "true" or "false" (not case-sensitive).
	 */
	public static Boolean parseBoolean(String aBoolean) {
		if (aBoolean.equalsIgnoreCase("true")) {
			return Boolean.TRUE;
		} else if (aBoolean.equalsIgnoreCase("false")) {
			return Boolean.FALSE;
		} else {
			throw new IllegalArgumentException("Cannot parse into Boolean: " + aBoolean);
		}
	}

	/**
	 * Convert a <tt>Collection</tt> represented in the form of
	 * <tt>AbstractCollection.toString</tt> into a <tt>List</tt> of <tt>String</tt>
	 * objects.
	 *
	 * <P>
	 * Intended for use as an aid in parsing collections of objects which were stored in
	 * their <tt>toString</tt> form. This method will not parse such <tt>String</tt>s into
	 * the original objects, but will aid in doing so by tokenizing it into its parts.
	 *
	 * @param aText has format of <tt>AbstractCollection.toString</tt>
	 * @return <tt>String</tt> objects.
	 */
	public static final List<String> getListFromString(String aText) {
		if (aText == null)
			throw new IllegalArgumentException("Text must not be null.");
		List<String> result = new ArrayList<String>();
		StringTokenizer parser = new StringTokenizer(aText, "[,] ");
		while (parser.hasMoreTokens()) {
			result.add(parser.nextToken());
		}
		return result;
	}

	/**
	 * Return <tt>true</tt> only if <tt>aMoney</tt> equals {@link Consts#ZERO_MONEY} or
	 * {@link Consts#ZERO_MONEY_WITH_DECIMAL}.
	 */
	public static boolean isZeroMoney(BigDecimal aMoney) {
		return aMoney.equals(Consts.ZERO_MONEY) ||
				aMoney.equals(Consts.ZERO_MONEY_WITH_DECIMAL);
	}

	/**
	 * Return a {@link Logger} whose name follows a specific naming convention.
	 *
	 * <P>
	 * The conventional logger names are taken as <tt>aClass.getPackage().getName()</tt>.
	 *
	 * <P>
	 * Logger names appearing in the <tt>logging.properties</tt> config file must match
	 * the names returned by this method.
	 */
	public static Logger getLogger(Class<?> aClass) {
		return Logger.getLogger(aClass.getPackage().getName());
	}
}
