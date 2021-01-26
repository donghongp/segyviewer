package com.ghc.app.utils;

import java.math.BigDecimal;

/**
 * Collected constants of very general utility.
 *
 * <P>
 * All constants are immutable.
 */
public final class Consts  {

	public static final String APP_NAME = "StocksMonitor";
	public static final String APP_VERSION = "1.5.0";
	public static final String AUTHOR = "www.javapractices.com";

	// Common Strings
	public static final String NEW_LINE = System.getProperty("line.separator");
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String PATH_SEPARATOR = System.getProperty("path.separator");

	public static final String EMPTY_STRING = "";
	public static final String SPACE = " ";
	public static final String PERIOD = ".";
	public static final String TAB = "\t";
	public static final String COMMA = ",";
	public static final String DOUBLE_QUOTE = "\"";
	public static final String ELLIPSIS = "...";

	// numbers and algebraic signs
	public static final int POSITIVE = 1;
	public static final int NEGATIVE = -1;
	public static final String PLUS_SIGN = "+";
	public static final String NEGATIVE_SIGN = "-";

	public static final Integer ZERO = new Integer(0);
	public static final Integer ONE = new Integer(1);

	public static final long ONE_KILOBYTE = 1024L;

	// For monetary calculations
	public static final int MONEY_DECIMAL_PLACES = 2;
	public static final int MONEY_ROUNDING_STYLE = BigDecimal.ROUND_HALF_UP;
	public static final BigDecimal ZERO_MONEY = new BigDecimal("0");
	public static final BigDecimal ZERO_MONEY_WITH_DECIMAL = new BigDecimal("0.00");

	// Time conversion factors
	public static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int SECONDS_PER_MINUTE = 60;

	// PRIVATE //

	/** Prevent object construction. */
	private Consts() {
		throw new AssertionError();
	}
}
