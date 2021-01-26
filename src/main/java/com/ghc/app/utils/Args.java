package com.ghc.app.utils;

import java.util.Collection;

/**
 * Utility methods for common argument validations.
 *
 * <P>
 * Replace <tt>if</tt> statements at the start of a method with more compact method calls.
 */
public final class Args {

	/**
	 * If <tt>aObject</tt> is null, throw a <tt>NullPointerException</tt>.
	 *
	 * <P>
	 * Use cases :
	 * 
	 * <pre>
	 * doSomething(SoccerBall aBall) {
	 * 	// call some method on the argument :
	 * 	// if aBall is null, then exception is automatically thrown, so
	 * 	// there is no need for an explicit check for null.
	 * 	aBall.inflate();
	 * 
	 * 	// assign to a corresponding field (common in constructors):
	 * 	// if aBall is null, no exception is immediately thrown, so
	 * 	// an explicit check for null may be useful here
	 * 	Args.checkForNull(aBall);
	 * 	fBall = aBall;
	 * 
	 * 	// passed on to some other method as param :
	 * 	// it may or may not be appropriate to have an explicit check
	 * 	// for null here, according the needs of the problem
	 * 	Args.checkForNull(aBall); // ??
	 * 	fReferee.verify(aBall);
	 * }
	 * </pre>
	 */
	public static void checkForNull(Object aObject) {
		if (aObject == null) {
			throw new NullPointerException();
		}
	}

	/**
	 * Throw an <tt>IllegalArgumentException</tt> if <tt>aText</tt> does not satisfy
	 * {@link Util#textHasContent}.
	 *
	 * <P>
	 * Most text used in an application is meaningful only if it has visible content.
	 */
	public static void checkForContent(String aText) {
		if (!Util.textHasContent(aText)) {
			throw new IllegalArgumentException("Text has no visible content");
		}
	}

	/**
	 * Throw an <tt>IllegalArgumentException</tt> if {@link Util#isInRange} returns
	 * <tt>false</tt>.
	 *
	 * @param aLow is less than or equal to <tt>aHigh</tt>.
	 */
	public static void checkForRange(int aNumber, int aLow, int aHigh) {
		if (!Util.isInRange(aNumber, aLow, aHigh)) {
			throw new IllegalArgumentException(aNumber + " not in range " + aLow + ".." + aHigh);
		}
	}

	/**
	 * Throw an <tt>IllegalArgumentException</tt> only if <tt>aCollection.isEmpty</tt>
	 * returns <tt>true</tt>.
	 */
	public static void checkForEmpty(Collection aCollection) {
		if (aCollection.isEmpty()) {
			throw new IllegalArgumentException("Collection is empty.");
		}
	}
}
