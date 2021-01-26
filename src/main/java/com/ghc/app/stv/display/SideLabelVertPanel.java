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
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.ghc.app.general.StvConst;
import com.ghc.app.seis.ISeismicTraceBuffer;
import com.ghc.app.stv.StvFrame;

public class SideLabelVertPanel extends JPanel {

	protected StvFrame stvFrame = null;
	protected DisplayPanel displayPanel = null;
	protected ViewPanel viewPanel = null;
	protected ViewSettings viewSettings = null;

	private Graphics2D graphic2D = null;

	protected BufferedImage bitmap = null;
	protected int width;
	protected int height;

	private final int MIN_INTERVAL = 40;
	private final int MAX_INTERVAL = 80;

	private DecimalFormat decimalFormat;
	private int numDecimals;
	private double horzLineMinorInc1 = 100;
	private double horzLineMajorInc1 = 500;

	private int sampleIntScalar = 1000;
	private int mouseX = -1;
	private int mouseY = -1;

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(StvConst.DEFAULT_WIDTH_SIDELABEL, 300);
	}

	public SideLabelVertPanel(DisplayPanel displayPanel) {
		super();
		this.displayPanel = displayPanel;
		stvFrame = displayPanel.getStvFrame();
		viewPanel = stvFrame.getViewPanel();
		viewSettings = stvFrame.getTraceContext().getViewSettings();
		setMinimumSize(new Dimension(0, 0));
		setPreferredSize(new Dimension(StvConst.DEFAULT_WIDTH_SIDELABEL, 0));
		resetPlotDirection();
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
		setNumDecimals(calNumDecimals(viewSettings));
	}

	protected double getSampleInt() {
		return stvFrame.getSampleInt();
	}

	protected ISeismicTraceBuffer getTraceBuffer() {
		return stvFrame.getTraceBuffer();
	}

	public void setNumDecimals(int numDecimals) {
		this.numDecimals = numDecimals;
		String s = new String("0");
		if (numDecimals > 0) {
			s += ".0";
		}
		for (int i = 1; i < numDecimals; i++) {
			s += "0";
		}
		decimalFormat = new DecimalFormat(s);
	}

	public void resetPreferredSize(int newHeight) {
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_VERTICAL) {
			setPreferredSize(new Dimension(0, newHeight));
		} else {
			setPreferredSize(new Dimension(newHeight, 0));
		}
	}

	public void resetPlotDirection() {
		if (viewSettings.plotDirection == StvConst.PLOT_DIR_VERTICAL) {
			setPreferredSize(new Dimension(StvConst.DEFAULT_WIDTH_SIDELABEL, 0));
		} else {
			setPreferredSize(new Dimension(0, StvConst.DEFAULT_WIDTH_SIDELABEL));
		}
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
		String text = "Time [seconds]";
		// Plot Axis label
		g.rotate(-Math.PI / 2);
		int labelWidth = metrics.stringWidth(text);
		int startPos = -height / 2 - labelWidth / 2;
		g.setColor(Color.black);
		g.drawString(text, startPos, StvConst.DEFAULT_LABEL_FONTSIZE + 2);
		g.rotate(Math.PI / 2);

		ISeismicTraceBuffer traceBuffer = getTraceBuffer();
		if (getTraceBuffer() == null) {
			return;
		}

		// g.setFont(new Font("Fixed", Font.PLAIN, StvConst.DEFAULT_LABEL_FONTSIZE));
		double sampleInt = getSampleInt();
		int sampMinorInc = (int) (viewSettings.timeLineMinorInc / sampleInt + 0.5f);
		int sampMajorInc = (int) (viewSettings.timeLineMajorInc / sampleInt + 0.5f);
		if (sampMinorInc <= 0) {
			sampMinorInc = 1;
		}
		if (sampMajorInc <= 0) {
			sampMajorInc = 1;
		}

		int minSamp = (int) viewPanel.yView2Model(-1);
		int maxSamp = (int) viewPanel.yView2Model(height + 1) + 1;
		int numSamples = traceBuffer.numSamples();
		maxSamp = Math.min(numSamples - 1, maxSamp);
		int firstSamp = ((int) (minSamp / sampMinorInc - 0.1f) + 1) * sampMinorInc;

		g.setStroke(new BasicStroke(1));
		g.setColor(Color.black);

		int xmin = width - 4 - insets.right;
		int xmax = width - 1 - insets.right;

		int labelHeight = metrics.getHeight();
		int xpixText = insets.left + 1 + labelHeight - 4;

		// if( _showLines ) {
		for (int isamp = firstSamp; isamp < maxSamp; isamp += sampMinorInc) {
			int ypix = (int) viewPanel.yModel2View(isamp);
			String label = decimalFormat.format((double) (isamp) * getSampleInt() / (double) sampleIntScalar);
			labelWidth = metrics.stringWidth(label);
			int xpix = Math.max(xmin - labelWidth - 2, xpixText);
			if (isamp % sampMajorInc != 0) {
				g.drawString(label, xpix, ypix + StvConst.DEFAULT_LABEL_FONTSIZE / 2 - 1);
				g.drawLine(xmin, ypix, xmax, ypix);
			} else {
				g.setStroke(new BasicStroke(2));
				g.drawString(label, xpix, ypix + StvConst.DEFAULT_LABEL_FONTSIZE / 2 - 1);
				g.drawLine(xmin - 1, ypix, xmax, ypix);
				g.setStroke(new BasicStroke(1));
			}
		}

		if (mouseX < 0 || mouseY < 0) {
			return;
		}
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(2));
		g.drawLine(0, mouseY, width, mouseY);
	}

	public void paintTrackingLine(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		repaint();
	}

	public void settingsChanged(ViewSettings viewSettings) {
		setNumDecimals(calNumDecimals(viewSettings));
		repaint();
	}

	private int calNumDecimals(ViewSettings viewSettings) {
		int k1 = numOfNonZeroDigits(viewSettings.timeLineMinorInc + "");
		int k2 = numOfNonZeroDigits(viewSettings.timeLineMajorInc + "");
		return k1 > k2 ? k1 : k2;
	}

	private int numOfNonZeroDigits(String s) {
		int k = 0;
		for (int i = s.length() - 1; i >= 0; i--) {
			char c = s.charAt(i);
			if (c == '0' || c == '.') {
				k++;
			} else {
				break;
			}
		}
		return s.length() - k;
	}
}