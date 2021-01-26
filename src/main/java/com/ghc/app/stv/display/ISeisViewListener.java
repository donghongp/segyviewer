package com.ghc.app.stv.display;

import java.awt.Dimension;

/**
 * Seismic view listener
 */
public interface ISeisViewListener {
	public void settingsChanged(ViewSettings settings);

	public void vertScrollChanged(int scrollValue);

	public void horzScrollChanged(int scrollValue);

	public void sizeChanged(Dimension size);

}
