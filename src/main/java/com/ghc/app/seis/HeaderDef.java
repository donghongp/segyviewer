package com.ghc.app.seis;

/**
 * Header definition
 */
public class HeaderDef implements Comparable<HeaderDef> {
	public static final int TYPE_INT = 0;
	public static final int TYPE_LONG = 1;
	public static final int TYPE_FLOAT = 2;
	public static final int TYPE_DOUBLE = 3;
	public static final int TYPE_STRING = 4;

	/// Header type. Currently supported are TYPE_INT, TYPE_FLOAT and TYPE_DOUBLE
	public int type;
	/// Header name
	public String name;
	/// Header description
	public String desc;
	/// Index, user specifiable. May for example be used if some kind of sorting is
	/// required.
	public int index;

	/**
	 * Default constructor
	 */
	public HeaderDef() {
		name = "";
		desc = "";
		type = TYPE_FLOAT;
		index = 0;
	}

	/**
	 * 'Copy' constructor
	 * 
	 * @param h
	 */
	public HeaderDef(HeaderDef h) {
		name = h.name;
		desc = h.desc;
		type = h.type;
		index = h.index;
	}

	/**
	 * Constructor
	 * 
	 * @param theName Header name
	 * @param theDesc Header description
	 * @param theType Header type
	 */
	public HeaderDef(String theName, String theDesc, int theType) {
		this(theName, theDesc, theType, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param theName Header name
	 * @param theDesc Header description
	 * @param theType Header type
	 * @param theIndex Index, user specifiable. May for example be used if some kind of
	 *            sorting is required.
	 * 
	 */
	public HeaderDef(String theName, String theDesc, int theType, int theIndex) {
		name = theName;
		type = theType;
		desc = theDesc;
		index = theIndex;
	}

	public String toString() {
		return name;
	}

	public int compareTo(HeaderDef otherHeader) {
		return (name.compareTo(otherHeader.name));
	}

	public boolean equals(Object obj) {
		if (obj instanceof HeaderDef) {
			return (((HeaderDef) obj).name.compareTo(name) == 0);
		} else {
			return false;
		}
	}
}
