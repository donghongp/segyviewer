package com.ghc.app.stv.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.ghc.app.general.StvConst;
import com.ghc.app.seis.HeaderDef;
import com.ghc.app.stv.StvFrame;

public class SideLabelCornerPanel extends JPanel {
	protected StvFrame stvFrame = null;
	protected DisplayPanel displayPanel = null;
	protected ViewPanel viewPanel = null;
	protected ViewSettings viewSettings = null;

	protected BufferedImage bitmap = null;
	protected int width;
	protected int height;

	private String labelTrace = "Trace";
	private ArrayList<String> traceHeaderNames;

	public SideLabelCornerPanel(DisplayPanel displayPanel) {
		super();
		stvFrame = displayPanel.getStvFrame();
		viewPanel = stvFrame.getViewPanel();
		viewSettings = stvFrame.getTraceContext().getViewSettings();
		traceHeaderNames = new ArrayList<String>();
		if (viewSettings.showSeqTraceNum) {
			traceHeaderNames.add(labelTrace);
		}
		setPreferredSize(new Dimension(StvConst.DEFAULT_WIDTH_SIDELABEL, StvConst.DEFAULT_HEIGHT_SIDELABEL));
		setBorder(BorderFactory.createLoweredBevelBorder());
	}

	public void resetPreferredSize(int newHeight) {
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_VERTICAL) {
			setPreferredSize(new Dimension(StvConst.DEFAULT_WIDTH_SIDELABEL, newHeight));
		} else {
			setPreferredSize(new Dimension(newHeight, StvConst.DEFAULT_WIDTH_SIDELABEL));
		}
	}

	public void resetPlotDirection() {
		resetPreferredSize(StvConst.DEFAULT_HEIGHT_SIDELABEL);
		revalidate();
	}

	public void updateTraceHeaders(HeaderDef[] headers) {
		traceHeaderNames.clear();
		if (viewSettings.showSeqTraceNum) {
			traceHeaderNames.add(labelTrace);
		}

		for (int i = 0; i < headers.length; i++) {
			traceHeaderNames.add(headers[i].name);
		}
		revalidate();
		repaint();
	}

	public void refresh() {
		revalidate();
		repaint();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle rect = getVisibleRect();
		if (bitmap == null || width != rect.width || height != rect.height) {
			width = rect.width;
			height = rect.height;
			if (viewSettings.plotDirection == StvConst.PLOT_DIR_HORIZONTAL) {
				width = rect.height;
				height = rect.width;
			}
			bitmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			if (bitmap == null) {
				return;
			}
		}
		paintStep2((Graphics2D) bitmap.getGraphics());
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_VERTICAL) {
			g2.drawImage(bitmap, 0, 0, null);
		} else {
			java.awt.geom.AffineTransform affineTransform = new java.awt.geom.AffineTransform();
			affineTransform.rotate(-Math.PI / 2);
			affineTransform.translate(-width, 0);
			g2.drawImage(bitmap, affineTransform, null);
		}
	}

	// protected abstract void paintStep2(Graphics2D g);
	protected void paintStep2(Graphics2D g) {
		g.setFont(new Font("SansSerif", Font.PLAIN, StvConst.DEFAULT_LABEL_FONTSIZE));
		g.setColor(StvConst.DEFAULT_COLOR);
		g.fillRect(0, 0, width, height);
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.black);
		// Dimension size = getPreferredSize();
		Insets insets = getInsets();
		// int height = insets.top-insets.bottom;

		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int labelHeight = metrics.getHeight();
		for (int i = 0; i < traceHeaderNames.size(); i++) {
			String text = traceHeaderNames.get(i);
			int labelWidth = metrics.stringWidth(text);
			int xpos = Math.max(insets.left, width - insets.right - labelWidth - 2);
			int ypos = height - insets.bottom - 6 - i * (labelHeight);
			g.drawString(text, xpos, ypos);
		}
	}

	// @Override
	// public void settingsChanged(ViewSettings viewSettings) {
	//
	// }
}
