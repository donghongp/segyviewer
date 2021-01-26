package com.ghc.app.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class GMenuBar extends JMenuBar {
	private Graphics2D g2d = null;
	private Color lineColor = GhcLookAndFeel.MENU_LINE_COLOR;

	public void setColor(Color color) {
		lineColor = color;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		this.g2d = g2d;
		g2d.setColor(lineColor);
		int w = getWidth() - 1;
		int h = getHeight();
		float lineWidth = 3;
		BasicStroke bs = new BasicStroke(lineWidth);
		g2d.setStroke(bs);

		g2d.drawLine(0, h - (int) lineWidth, w, h - (int) lineWidth);
		// g2d.fillRect(0, 0, getWidth() - 1, 5);
		// g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		// g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 250, 25);


	}

	public void refresh() {
		// paintComponent(g2d);
		repaint();
	}

	public JMenu genEmptySpacer(int width) {
		JMenu spacer = new JMenu();
		spacer.setEnabled(false);
		spacer.setMinimumSize(new Dimension(width, getSize().height));
		spacer.setPreferredSize(new Dimension(width, getSize().height));
		spacer.setMaximumSize(new Dimension(width, getSize().height));
		return spacer;
	}

}