package com.ghc.app.stv;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.ghc.app.resources.GhcImageLoader;
import com.ghc.app.utils.GhcLookAndFeel;
import com.ghc.app.utils.StringBuilderPlus;

public class HomePanel extends JPanel {
	Color maroon = new Color(128, 0, 0);
	Color menuLineColor = GhcLookAndFeel.MENU_LINE_COLOR;
	Image backgroundImage = null;
	Image stvImage = null;

	ArrayList<String[]> hotKeys = new ArrayList<String[]>();

	public HomePanel() {
		backgroundImage = GhcImageLoader.getImage(GhcImageLoader.BLUE_SKY);
		stvImage = GhcImageLoader.getImage(GhcImageLoader.STV_APP);

		hotKeys.add(new String[] { "Alt", "Mouse right click", "to select trace and pop-up menu" });
		hotKeys.add(new String[] { "Alt + Shift", "Mouse drag", "release mouse to zoom in the selected area" });
		hotKeys.add(new String[] { "Alt + Shift", "Mouse click", "to zoom out" });

		init();
	}

	public void init() {
		setLayout(new BorderLayout());

		URL appIconUrl = GhcImageLoader.getImageURL(GhcImageLoader.STV_APP);

		JEditorPane jEditorPane = new JEditorPane();
		jEditorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(jEditorPane);
		HTMLEditorKit kit = new HTMLEditorKit();
		jEditorPane.setEditorKit(kit);

		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.addRule("body {color:#000; font-family:times; margin: 40px; }");
		styleSheet.addRule("h1 {color: #800000; font-size: 200%}");
		styleSheet.addRule("h2 {color: #ff0000; margin-left: 60px; margin-top: 2px;}");
		styleSheet.addRule("h3 {font : 18px monaco; margin-left: 60px; }");
		styleSheet.addRule("p {font : 14px monaco; margin-left: 60px; }");
		styleSheet.addRule("table {font : 14px monaco; margin-left: 85px; }");
		styleSheet.addRule("pre {color : green; margin-left: 60px; font : 14px monaco; background-color : #fafafa; }");

		StringBuilderPlus tableSb = new StringBuilderPlus();
		tableSb.appendLine("<table align=\"left\"  border=\"1\" width=\"600\" cellpadding=\"5\" cellspacing=\"0\">");
		for (int i = 0; i < hotKeys.size(); i++) {
			String[] row = hotKeys.get(i);
			tableSb.appendLine("<tr align=\"left\"><td>" + row[0] + "</td>"
					+ "<td>" + row[1] + "</td>"
					+ "<td>" + row[2] + "</td>"
					+ "</tr>");
		}
		tableSb.appendLine("</table>");

		String bulletUrl = GhcImageLoader.getImageURL(GhcImageLoader.BULLET_GREEN).toString();
		String h3String = "<html>\n <body>\n"
				+ "<h3><img src = \"" + bulletUrl + "\" alt=\"pic\" hspace=\"10\"> File >> Open, New, or Recent to load data</h1>\n"
				+ "<h3><img src = \"" + bulletUrl + "\" alt=\"pic\" hspace=\"10\"> Hot keys:</h1>\n";

		String appIconString = GhcImageLoader.getImageURL(GhcImageLoader.STV_APP).toString();
		String contentsString = "<html>\n <body>\n"
				+ "<h1><img src = \"" + appIconString + "\" alt=\"pic\" hspace=\"10\"> Seismic Trace Viewer (STV)</h1>\n"
				+ "<h2><a href='https://library.seg.org/seg-technical-standards'>For SEG-Y data format</a> </h2>\n"
				+ "<p> </p>\n"
				+ "<h2>&emsp&nbsp<a href='https://github.com/donghongp/stv'>Source Code</a> &emsp &emsp "
				+ "<a href='https://github.com/donghongp/stv/tree/main/doc'>User Manual</a>&emsp &emsp "
				+ "<a href='https://sourceforge.net/p/seismic-trace-viewer/wiki/Home/'>Support</a></h2>\n"
				+ h3String + "\n"
				+ tableSb.toString() + "\n"
				+ "<p></p>\n"
				+ "</body>\n";

		Document doc = kit.createDefaultDocument();
		jEditorPane.setDocument(doc);
		jEditorPane.setText(contentsString);
		jEditorPane.addHyperlinkListener(e -> {
			if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.browse(e.getURL().toURI());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		jEditorPane.setOpaque(false);
		JScrollPane jScrollPane = new JScrollPane(jEditorPane) {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Rectangle rect = getVisibleRect();
				g.drawImage(backgroundImage, 0, 0, rect.width, rect.height, this);
			}
		};
		jScrollPane.getViewport().setOpaque(false);
		add(jScrollPane, BorderLayout.CENTER);
	}

	public void paintComponent1(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		Rectangle rect = getVisibleRect();

		int width = rect.width;
		int height = rect.height;
		g2.drawImage(backgroundImage, 0, 0, width, height, this);
		int xpos = 100;
		int ypos = 150;
		g2.drawImage(stvImage, xpos, ypos, this);
		g2.setFont(new Font("Georgia", Font.BOLD, 54));
		g2.setColor(maroon);

		xpos += stvImage.getWidth(this) + 5;
		ypos += stvImage.getHeight(this);
		g2.drawString("Seismic Trace Viewer (STV)", xpos, ypos - 5);

		g2.setFont(new Font("Georgia", Font.PLAIN, 22));
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.black);
		FontMetrics metrics = g2.getFontMetrics(g2.getFont());
		int labelHeight = metrics.getHeight();

		xpos += 5;
		ypos += labelHeight;
		g2.drawString("--- for SEG-Y, SEG-D, SEG-2 data format", xpos, ypos);


		metrics = g2.getFontMetrics(g2.getFont());
		labelHeight = metrics.getHeight();
		ypos += 2 * labelHeight;

		int thickness = 4;
		int size = 10;
		String text = null;
		for (int k = 0; k < 2; k++) {
			g2.setColor(menuLineColor);
			g.fill3DRect(xpos, ypos, size, size, true);
			for (int i = 1; i <= thickness; i++) {
				g.draw3DRect(xpos - i, ypos - i, size + 2 * i - 1, size + 2 * i - 1, true);
			}
			g2.setColor(Color.black);
			if (k == 0) {
				text = "File >> Open, New, or Recent to load data";
			} else if (k == 1) {
				text = "Hot Keys:";
			}
			g2.drawString(text, xpos + size + 10, ypos + size + 2);

			ypos += labelHeight;
		}

		int ix = xpos + size + 10 + 20;
		int iy = ypos + size + 2;
		for (int i = 0; i < hotKeys.size(); i++) {
			String[] row = hotKeys.get(i);
			g2.setColor(Color.red);
			g.fillOval(ix - 10, iy - labelHeight / 2, 10, 10);
			g2.setColor(Color.black);
			g2.drawString(row[0], ix + 10, iy);
			g2.drawString(row[1], ix + 150 + 10, iy);
			g2.drawString(row[2], ix + 350 + 10, iy);
			iy += labelHeight;
		}

	}


}
