package com.ghc.app.general;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

/**
 * Square color icon, defined by a square size and a color. This icon can for example be
 * used as the icon in a JLabel.
 */
public class RectIcon implements Icon {
	private int _width;
	private int _height;
	private Color _color;
	private ColorMap _colorMap;
	private boolean _isSingleColor;

	public RectIcon(int width, int height, Color color) {
		_isSingleColor = true;
		_color = color;
		_width = width;
		_height = height;
	}

	public RectIcon(int width, int height, ColorMap colorMap) {
		_isSingleColor = false;
		_colorMap = colorMap;
		_width = width;
		_height = height;
	}

	public Color getColor() {
		return _color;
	}

	public void setColor(Color color) {
		_color = color;
	}

	public ColorMap getColorMap() {
		return _colorMap;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setColor(Color.black);
		g2d.drawRect(x, y, _width - 1, _height - 1);

		if (_isSingleColor) {
			g2d.setColor(_color);
			g2d.fillRect(x + 1, y + 1, _width - 2, _height - 2);
		} else {
			for (int i = 0; i < _height - 2; i++) {
				float value = 1.0f - (float) i / (float) (_height - 3);
				g2d.setColor(new Color(_colorMap.getColorRGB(value)));
				g2d.drawLine(x + 1, y + i + 1, x + _width - 2, y + i + 1);
			}
		}

		g2d.dispose();
	}

	public int getIconWidth() {
		return _width;
	}

	public int getIconHeight() {
		return _height;
	}
}

