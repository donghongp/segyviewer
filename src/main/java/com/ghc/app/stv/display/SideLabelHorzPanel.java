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

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.ghc.app.general.StvConst;
import com.ghc.app.seis.ISeismicTraceBuffer;
import com.ghc.app.stv.StvFrame;

public class SideLabelHorzPanel extends JPanel {

	protected StvFrame stvFrame = null;
	protected DisplayPanel displayPanel = null;
	protected ViewPanel viewPanel = null;
	protected ViewSettings viewSettings = null;

	protected BufferedImage bitmap = null;
	protected int width;
	protected int height;

	private int numHeaders = 0;
	private int[] headerIndex = null;
	private String[] displayedHeaderNames = null;

	private int numTraces;
	private int traceStep = 1;

	private int mouseX = -1;
	private int mouseY = -1;

	public SideLabelHorzPanel(DisplayPanel displayPanel) {
		super();
		this.displayPanel = displayPanel;
		stvFrame = displayPanel.getStvFrame();
		viewSettings = stvFrame.getTraceContext().getViewSettings();

		resetPlotDirection();
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
	}

	protected double getSampleInt() {
		return stvFrame.getSampleInt();
	}

	protected ISeismicTraceBuffer getTraceBuffer() {
		return stvFrame.getTraceBuffer();
	}

	public void resetPreferredSize(int newHeight) {
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_VERTICAL) {
			setPreferredSize(new Dimension(0, newHeight));
		} else {
			setPreferredSize(new Dimension(newHeight, 0));
		}
	}

	protected int setNewSize() {
		int labelHeight = StvConst.DEFAULT_LABEL_FONTSIZE + 3;
		int newSize = StvConst.DEFAULT_HEIGHT_SIDELABEL + numHeaders * (labelHeight);
		if (!viewSettings.showSeqTraceNum) {
			newSize -= (labelHeight);
		}
		resetPreferredSize(newSize);
		return newSize;
	}

	public int resetPlotDirection() {
		return setNewSize();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
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

	protected void paintStep2(Graphics2D g) {
		g.setFont(new Font("SansSerif", Font.PLAIN, StvConst.DEFAULT_LABEL_FONTSIZE));
		g.setColor(StvConst.DEFAULT_COLOR);
		g.fillRect(0, 0, width, height);
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.black);
		Insets insets = getInsets();

		FontMetrics metrics = g.getFontMetrics(g.getFont());
		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		if (traceBuffer == null) {
			String text = "Trace headers are unavailable";
			int labelWidth = metrics.stringWidth(text);
			int xpos = Math.max(insets.left, width / 2 - insets.right - labelWidth - 2);
			int ypos = height - insets.bottom - 6;
			g.drawString(text, xpos, ypos);
			return;
		}

		int numTraces = traceBuffer.numTraces();
		//// Increase trace step automatically when size is too small
		int labelWidth = 2 * metrics.stringWidth(numTraces + "");

		int xpix1 = (int) displayPanel.getViewPanel().xModel2View(0, 0);
		int xpix2 = (int) displayPanel.getViewPanel().xModel2View(0, numTraces - 1);
		double x_int = (double) (xpix2 - xpix1) / (double) (numTraces - 1);
		int traceStep = 1;
		if (x_int > labelWidth) {
			traceStep = 1;
		} else if (x_int > labelWidth / 2) {
			traceStep = 2;
		} else if (x_int > labelWidth / 5) {
			traceStep = 5;
		} else if (x_int > labelWidth / 10) {
			traceStep = 10;
		} else if (x_int > labelWidth / 20) {
			traceStep = 20;
		} else if (x_int > labelWidth / 50) {
			traceStep = 50;
		}

		int minTrace = (int) displayPanel.getViewPanel().xView2Trace(0);
		int maxTrace = (int) displayPanel.getViewPanel().xView2Trace(width + 1) + 1;
		minTrace = Math.max(0, minTrace);
		maxTrace = Math.min(numTraces - 1, maxTrace);

		int firstTrace = ((int) (minTrace / traceStep)) * traceStep;
		int ymin, ymax;
		// System.out.println("firstTrace=" + firstTrace + " maxTrace=" + maxTrace);
		ymin = height - 4 - insets.bottom;
		ymax = height - 1 - insets.bottom;
		int ypix = height - (ymax - ymin) - insets.bottom - 2;
		if (viewSettings.showSeqTraceNum) {
			for (int itrc = firstTrace; itrc <= maxTrace; itrc += traceStep) {
				String label = "" + (traceBuffer.originalTraceNumber(itrc));
				labelWidth = metrics.stringWidth(label);
				int xpix = (int) displayPanel.getViewPanel().xModel2View(0, itrc);
				g.drawString(label, xpix - labelWidth / 2, ypix);
				g.drawLine(xpix, ymin, xpix, ymax);
			}
		}
		if (mouseX < 0 || mouseY < 0) {
			return;
		}
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(2));
		g.drawLine(mouseX, 0, mouseX, height);
	}

	public void paintTrackingLine(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		repaint();
	}

	public void settingsChanged(ViewSettings viewSettings) {

	}
}
