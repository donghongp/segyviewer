package com.ghc.app.seis;

/**
 * Header information
 */
public class Header {
	private Number _number = null;
	private String _text = null;
	private int _type;

	public Header() {
		_type = HeaderDef.TYPE_DOUBLE;
		_number = new Double(0);
		_text = "";
	}

	public Header(int value) {
		_type = HeaderDef.TYPE_INT;
		_number = new Integer(value);
	}

	public Header(long value) {
		_type = HeaderDef.TYPE_LONG;
		_number = new Long(value);
	}

	public Header(String value) {
		_type = HeaderDef.TYPE_STRING;
		_text = new String(value);
	}

	public void setValue(int value) {
		_number = new Integer(value);
		_type = HeaderDef.TYPE_INT;
		_text = "";
	}

	public void setValue(long value) {
		_number = new Long(value);
		_type = HeaderDef.TYPE_LONG;
		_text = "";
	}

	public void setValue(float value) {
		_number = new Float(value);
		_type = HeaderDef.TYPE_FLOAT;
		_text = "";
	}

	public void setValue(double value) {
		_number = new Double(value);
		_type = HeaderDef.TYPE_DOUBLE;
		_text = "";
	}

	public void setValue(String value) {
		_text = new String(value);
		_type = HeaderDef.TYPE_STRING;
		_number = new Double(0);
	}

	public Object value() {
		if (_type == HeaderDef.TYPE_STRING) {
			return _text;
		} else {
			return _number;
		}
	}

	public int intValue() {
		return _number.intValue();
	}

	public long longValue() {
		return _number.longValue();
	}

	public float floatValue() {
		return _number.floatValue();
	}

	public double doubleValue() {
		return _number.doubleValue();
	}

	public String stringValue() {
		return _text;
	}

	public int type() {
		return _type;
	}

	public String toString() {
		if (_type == HeaderDef.TYPE_STRING) {
			return _text;
		} else {
			return _number.toString();
		}
	}
}
