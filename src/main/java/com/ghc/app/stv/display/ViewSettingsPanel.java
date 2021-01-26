package com.ghc.app.stv.display;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.ghc.app.general.ColorMap;
import com.ghc.app.general.RectIcon;
import com.ghc.app.general.Standard;
import com.ghc.app.general.StvConst;
import com.ghc.app.seis.ISeismicTraceBuffer;
import com.ghc.app.stv.StvFrame;

public class ViewSettingsPanel extends JPanel {

	private StvFrame stvFrame = null;
	private ViewSettings viewSettings = null;

	private JTextField textZoomVert;
	private JTextField textZoomHorz;
	private JTextField textZoomVertCM;
	private JTextField textZoomHorzCM;

	private JCheckBox boxShowHorzLines = new JCheckBox("Show time lines");
	// private JCheckBox boxAutoHorzLines = new JCheckBox("Automatic settings");
	private JTextField textMinorInc;
	private JTextField textMajorInc;
	private JLabel labelMinorInc;
	private JLabel labelMajorInc;

	private JTextField textTraceClip;
	// private JTextField _textAGC;


	private JCheckBox boxShowZeroLines = new JCheckBox("Show time lines");
	private JCheckBox boxTraceClip = new JCheckBox();

	private JCheckBox boxWiggle;
	private JCheckBox boxPosFill;
	private JCheckBox boxNegFill;
	private JCheckBox boxUseVarColor;
	private JCheckBox boxHighlight;
	private JButton buttonColorWiggle;
	private JButton buttonColorWigglePos;
	private JButton buttonColorWiggleNeg;
	private JButton buttonColorWiggleHighlight;
	// Group
	private JRadioButton buttonWiggleLinear;
	private JRadioButton buttonWiggleCubic;

	private JButton buttonAutoScalar;
	private JButton buttonAutoRange;
	private JLabel labelMinValue;
	private JLabel labelMaxValue;
	private JTextField textScalar;
	private JTextField textTraceScalar;
	private JTextField textMinValue;
	private JTextField textMaxValue;
	// Group
	private JRadioButton buttonScalar;
	private JRadioButton buttonRange;
	private JRadioButton buttonTrace;
	// group
	private JRadioButton buttonMaximum;
	private JRadioButton buttonAverage;

	// Group
	private JRadioButton buttonPolarityNormal;
	private JRadioButton buttonPolarityReversed;

	private JCheckBox boxVA;
	private JLabel labelColorMap;
	private JComboBox comboColorMapVA;
	private JLabel labelVIInterpolation;
	// Group
	private JRadioButton buttonVADiscrete;
	private JRadioButton buttonVAVertical;
	private JRadioButton buttonVA2DSpline;

	// Color/color map selectors
	private JComboBox _comboColorMapWiggle;

	private JComboBox _comboPlotDir;
	private JLabel _labelPlotDir;


	public ViewSettingsPanel(StvFrame stvFrame) {
		super(new GridBagLayout());
		this.stvFrame = stvFrame;
		this.viewSettings = stvFrame.getTraceContext().getViewSettings();
		setBackground(StvConst.DEFAULT_COLOR);

		// setMinimumSize(new Dimension(0, 0));
		//setPreferredSize(new Dimension(180, 50));
		Insets insets = new Insets(0, 0, 0, 0);
		Insets insetsIndent = new Insets(0, 10, 0, 0);
		Insets insetsHigh = new Insets(2, 0, 0, 0);
		GridBagConstraints gbc;

		int n = 1;
		ButtonGroup moduleRadioGroup = null;
		JRadioButton[] moduleRadioButton = null;

		Font myFont = new Font("SansSerif", Font.PLAIN, 12);
		Color myColor = Color.BLUE;
		JPanel modulePanel = new JPanel(new GridLayout());

		int iRow = 0;

		// wiggle plot
		boxWiggle = new JCheckBox("Show", viewSettings.showWiggle);
		boxWiggle.setToolTipText("Show wiggle trace");
		boxPosFill = new JCheckBox("Pos. fill", viewSettings.isPosFill);
		boxPosFill.setToolTipText("Fill positive wiggle");
		boxNegFill = new JCheckBox("Neg. fill", viewSettings.isNegFill);
		boxNegFill.setToolTipText("Fill negative wiggle");
		boxHighlight = new JCheckBox("Highlight", viewSettings.isHighlight);
		boxHighlight.setToolTipText("Highlight wiggle with a color");
		boxUseVarColor = new JCheckBox("Use variable fill color");
		boxUseVarColor.setToolTipText("Use variable color scale to fill wiggle traces");
		boxShowZeroLines = new JCheckBox("Show zero lines", viewSettings.showZeroLines);
		boxShowZeroLines.setToolTipText("Display straight line at each seismic trace (wiggle trace with zero amplitude)");

		buttonColorWiggle = new JButton(new RectIcon(3 * Standard.ICON_SIZE, Standard.ICON_SIZE, viewSettings.wiggleColor));
		buttonColorWiggle.setToolTipText("Select color for wiggle line");
		buttonColorWigglePos = new JButton(new RectIcon(3 * Standard.ICON_SIZE, Standard.ICON_SIZE, viewSettings.wiggleColorPos));
		buttonColorWigglePos.setToolTipText("Select color for positive wiggle fill");
		buttonColorWiggleNeg = new JButton(new RectIcon(3 * Standard.ICON_SIZE, Standard.ICON_SIZE, viewSettings.wiggleColorNeg));
		buttonColorWiggleNeg.setToolTipText("Select color for negative wiggle fill");
		buttonColorWiggleHighlight = new JButton(
				new RectIcon(3 * Standard.ICON_SIZE, Standard.ICON_SIZE, viewSettings.wiggleColorHighlight));
		buttonColorWiggleHighlight.setToolTipText("Select color to highlight wiggle");

		modulePanel = new JPanel(new GridBagLayout());

		JPanel jPanel = new JPanel(new GridLayout(1, 2, 0, 0));
		boxWiggle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				viewSettings.showWiggle = (e.getStateChange() == ItemEvent.SELECTED);
				wiggleChanged();
				fireEventSettingsChanged();
			}
		});
		jPanel.add(boxWiggle);
		buttonColorWiggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				Color color = b.getBackground();
				Color colorNew = JColorChooser.showDialog(stvFrame, "Select color", color);
				if (colorNew != null) {
					((RectIcon) b.getIcon()).setColor(colorNew);
					b.repaint();
					viewSettings.wiggleColor = colorNew;
					fireEventSettingsChanged();
				}
			}
		});
		buttonColorWiggle.setEnabled(boxWiggle.isSelected());
		jPanel.add(buttonColorWiggle);

		int jRow = 0;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(jPanel, gbc);

		jPanel = new JPanel(new GridLayout(1, 2, 0, 0));
		boxPosFill.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				viewSettings.isPosFill = (e.getStateChange() == ItemEvent.SELECTED);
				wiggleChanged();
				fireEventSettingsChanged();
			}
		});
		jPanel.add(boxPosFill);

		buttonColorWigglePos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				Color color = b.getBackground();
				Color colorNew = JColorChooser.showDialog(stvFrame, "Select color", color);
				if (colorNew != null) {
					((RectIcon) b.getIcon()).setColor(colorNew);
					b.repaint();
					viewSettings.wiggleColorPos = colorNew;
					fireEventSettingsChanged();
				}
			}
		});
		buttonColorWigglePos.setEnabled(boxPosFill.isSelected());
		jPanel.add(buttonColorWigglePos);

		jRow++;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(jPanel, gbc);

		jPanel = new JPanel(new GridLayout(1, 2, 0, 0));
		boxNegFill.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				viewSettings.isNegFill = (e.getStateChange() == ItemEvent.SELECTED);
				wiggleChanged();
				fireEventSettingsChanged();
			}
		});
		jPanel.add(boxNegFill);


		buttonColorWiggleNeg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				Color color = b.getBackground();
				Color colorNew = JColorChooser.showDialog(stvFrame, "Select color", color);
				if (colorNew != null) {
					((RectIcon) b.getIcon()).setColor(colorNew);
					b.repaint();
					viewSettings.wiggleColorNeg = colorNew;
					fireEventSettingsChanged();
				}
			}
		});
		buttonColorWiggleNeg.setEnabled(boxNegFill.isSelected());
		jPanel.add(buttonColorWiggleNeg);

		jRow++;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(jPanel, gbc);

		jPanel = new JPanel(new GridLayout(1, 2, 0, 0));
		boxHighlight.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				viewSettings.isHighlight = (e.getStateChange() == ItemEvent.SELECTED);
				wiggleChanged();
				fireEventSettingsChanged();
			}
		});
		boxHighlight.setEnabled(boxWiggle.isSelected());
		jPanel.add(boxHighlight);

		buttonColorWiggleHighlight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				Color color = b.getBackground();
				Color colorNew = JColorChooser.showDialog(stvFrame, "Select color", color);
				if (colorNew != null) {
					((RectIcon) b.getIcon()).setColor(colorNew);
					b.repaint();
					viewSettings.wiggleColorHighlight = colorNew;
					fireEventSettingsChanged();
				}
			}
		});
		buttonColorWiggleHighlight.setEnabled(boxHighlight.isSelected() && boxWiggle.isSelected());
		jPanel.add(buttonColorWiggleHighlight);

		jRow++;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(jPanel, gbc);

		boxShowZeroLines.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				viewSettings.showZeroLines = (e.getStateChange() == ItemEvent.SELECTED);
				wiggleChanged();
				fireEventSettingsChanged();
			}
		});
		jRow++;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(boxShowZeroLines, gbc);

		modulePanel.setBorder(BorderFactory.createTitledBorder(null, "Wiggle plot",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, myFont, myColor));
		gbc = new GridBagConstraints(0, iRow, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsHigh, 0, 0);
		add(modulePanel, gbc);

		////////////////////////////////////
		modulePanel = new JPanel(new GridBagLayout());
		buttonWiggleLinear = new JRadioButton("Linear", viewSettings.wiggleType == StvConst.WIGGLE_TYPE_LINEAR);
		buttonWiggleCubic = new JRadioButton("Cubic", viewSettings.wiggleType == StvConst.WIGGLE_TYPE_CUBIC);
		buttonWiggleLinear.setToolTipText("Use linear interpolation between samples");
		buttonWiggleCubic.setToolTipText("Use cubic interpolation between samples");

		moduleRadioGroup = new ButtonGroup();
		moduleRadioButton = new JRadioButton[] {
				buttonWiggleLinear, buttonWiggleCubic
		};
		n = moduleRadioButton.length;
		jPanel = new JPanel(new GridLayout(1, n, 0, 0));
		for (int i = 0; i < n; i++) {
			final int j = i;
			moduleRadioButton[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (((JRadioButton) event.getSource()).isSelected()) {
						if (j == 0) {
							viewSettings.wiggleType = StvConst.WIGGLE_TYPE_LINEAR;
						} else if (j == 1) {
							viewSettings.wiggleType = StvConst.WIGGLE_TYPE_CUBIC;
						}
						fireEventSettingsChanged();
					}
				}
			});
			moduleRadioGroup.add(moduleRadioButton[i]);
			jPanel.add(moduleRadioButton[i]);
		}
		jRow = 0;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(jPanel, gbc);

		iRow++;
		modulePanel.setBorder(BorderFactory.createTitledBorder(null, "Wiggle line interpolation",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, myFont, myColor));
		gbc = new GridBagConstraints(0, iRow, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsHigh, 0, 0);
		// add(modulePanel, gbc);

		////////////////////////////////////
		modulePanel = new JPanel(new GridBagLayout());

		boxVA = new JCheckBox("Show VI", viewSettings.isVADisplay);
		boxVA.setToolTipText("Show variable intensity");

		labelVIInterpolation = new JLabel("VI Interpolation");
		labelVIInterpolation.setToolTipText("Select Interpolation algorithm for VI");

		labelColorMap = new JLabel("VI color map");
		labelColorMap.setToolTipText("Select color map for VI");

		comboColorMapVA = new JComboBox();
		comboColorMapVA.setRenderer(new ComboCellRenderer());
		for (int imap = 0; imap < ColorMap.NUM_DEFAULT_MAPS; imap++) {
			ColorMap map = new ColorMap(imap, ColorMap.COLOR_MAP_TYPE_32BIT);
			map.setMinMax(0.0, 1.0);
			comboColorMapVA.addItem(new ComboItem(new RectIcon(3 * Standard.ICON_SIZE, Standard.ICON_SIZE, map), map));
		}
		comboColorMapVA.setSelectedIndex(viewSettings.vaColorMap.getDefaultMapIndex());

		buttonVADiscrete = new JRadioButton("None", viewSettings.vaType == StvConst.VA_TYPE_DISCRETE);
		buttonVAVertical = new JRadioButton("Wiggle line algorithm", viewSettings.vaType == StvConst.VA_TYPE_VERTICAL);
		buttonVA2DSpline = new JRadioButton("2D spline algorithm", viewSettings.vaType == StvConst.VA_TYPE_2DSPLINE);
		buttonVADiscrete.setToolTipText("Plot each sample as a discrete rectangle with constant color");
		buttonVAVertical.setToolTipText("Interpolate each trace vertically, using the specified wiggle line interpolation (linear/cubic)");
		buttonVA2DSpline.setToolTipText("Use 2D spline interpolation");

		boxVA.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				viewSettings.isVADisplay = (e.getStateChange() == ItemEvent.SELECTED);
				vaChanged();
				fireEventSettingsChanged();
			}
		});
		jRow = 0;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(boxVA, gbc);

		jRow++;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insetsIndent, 0, 0);
		modulePanel.add(labelColorMap, gbc);

		comboColorMapVA.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Object source = e.getSource();
					if (source instanceof JComboBox) {
						JComboBox cb = (JComboBox) source;
						viewSettings.vaColorMap.resetColors(((ComboItem) cb.getSelectedItem()).map);
					}
				}
				vaChanged();
				fireEventSettingsChanged();
			}
		});

		jRow++;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 20, 0, 0), 0,
				0);
		modulePanel.add(comboColorMapVA, gbc);

		jRow++;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insetsIndent, 0, 0);
		modulePanel.add(labelVIInterpolation, gbc);

		moduleRadioGroup = new ButtonGroup();
		moduleRadioButton = new JRadioButton[] {
				buttonVADiscrete, buttonVAVertical, buttonVA2DSpline
		};
		n = moduleRadioButton.length;
		jPanel = new JPanel(new GridLayout(n, 1, 0, 0));
		for (int i = 0; i < n; i++) {
			final int j = i;
			moduleRadioButton[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (((JRadioButton) event.getSource()).isSelected()) {
						if (j == 0) {
							viewSettings.vaType = StvConst.VA_TYPE_DISCRETE;
						} else if (j == 1) {
							viewSettings.vaType = StvConst.VA_TYPE_VERTICAL;
						} else if (j == 2) {
							viewSettings.vaType = StvConst.VA_TYPE_2DSPLINE;
						}
						vaChanged();
						fireEventSettingsChanged();
					}
				}
			});
			moduleRadioGroup.add(moduleRadioButton[i]);
			jPanel.add(moduleRadioButton[i]);
		}
		jRow++;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 15, 0, 0), 0,
				0);
		modulePanel.add(jPanel, gbc);

		iRow++;
		modulePanel.setBorder(BorderFactory.createTitledBorder(null, "Variable Intensity (VI) plot",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, myFont, myColor));
		gbc = new GridBagConstraints(0, iRow, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsHigh, 0, 0);
		add(modulePanel, gbc);

		//////////////////////////////////
		modulePanel = new JPanel(new GridBagLayout());

		buttonPolarityNormal = new JRadioButton("Normal", viewSettings.polarity == StvConst.POLARITY_NORMAL);
		buttonPolarityReversed = new JRadioButton("Reversed", viewSettings.polarity == StvConst.POLARITY_REVERSED);
		buttonPolarityNormal.setToolTipText("<html><b>Right</b>-hand side of wiggle is <b>positive</b>");
		buttonPolarityReversed.setToolTipText("<html><b>Left</b>-hand side of wiggle is <b>positive</b>");

		moduleRadioGroup = new ButtonGroup();
		moduleRadioButton = new JRadioButton[] {
				buttonPolarityNormal, buttonPolarityReversed
		};
		n = moduleRadioButton.length;
		jPanel = new JPanel(new GridLayout(1, n, 0, 0));
		for (int i = 0; i < n; i++) {
			final int j = i;
			moduleRadioButton[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (((JRadioButton) event.getSource()).isSelected()) {
						if (j == 0) {
							viewSettings.polarity = StvConst.POLARITY_NORMAL;
						} else if (j == 1) {
							viewSettings.polarity = StvConst.POLARITY_REVERSED;
						}
						fireEventSettingsChanged();
					}
				}
			});
			moduleRadioGroup.add(moduleRadioButton[i]);
			jPanel.add(moduleRadioButton[i]);
		}
		jRow = 0;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(jPanel, gbc);

		iRow++;
		modulePanel.setBorder(BorderFactory.createTitledBorder(null, "Polarity",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, myFont, myColor));
		gbc = new GridBagConstraints(0, iRow, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsHigh, 0, 0);
		add(modulePanel, gbc);

		/////////////// Amplitude scaling ///////////////////
		modulePanel = new JPanel(new GridBagLayout());

		buttonScalar = new JRadioButton("Scalar", viewSettings.scaleType == StvConst.SCALE_TYPE_SCALAR);
		buttonRange = new JRadioButton("Range", viewSettings.scaleType == StvConst.SCALE_TYPE_RANGE);
		buttonTrace = new JRadioButton("Full trace", viewSettings.scaleType == StvConst.SCALE_TYPE_TRACE);

		buttonScalar.setToolTipText("Scale all trace data by constant scalar");
		buttonRange.setToolTipText("Scale all trace data by specified min/max range");
		buttonTrace.setToolTipText("Equalize each trace to an amplitude of 1, then scale by the specified trace scalar.");

		textScalar = new JTextField(viewSettings.dispScalar + "");
		textScalar.setToolTipText("Constant scalar to apply to seismic amplitudes (for display only)");
		buttonAutoScalar = new JButton("Auto Set");
		buttonAutoScalar.setMargin(null);
		buttonAutoScalar.setToolTipText("Automatically estimate the best scalar");
		buttonAutoScalar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ISeismicTraceBuffer traceBuffer = stvFrame.getTraceContext().getTraceBuffer();
				if (traceBuffer != null) {
					int numTraces = traceBuffer.numTraces();
					int step = (int) (numTraces / 5) + 1;
					float meanAbs = 0.0f;
					int counter = 0;
					for (int itrc = 0; itrc < numTraces; itrc += step) {
						float[] samples = traceBuffer.samples(itrc);
						for (int isamp = 0; isamp < samples.length; isamp++) {
							if (samples[isamp] == 0.0f)
								continue;
							meanAbs += Math.abs(samples[isamp]);
							counter += 1;
						}
					}
					if (meanAbs != 0 && counter != 0) {
						meanAbs /= (float) counter;
						textScalar.setText("" + 0.2f / meanAbs);
						if (scalingChanged()) {
							fireEventSettingsChanged();
						}
					} 
				}
			}
		});
		labelMinValue = new JLabel("Min: ", JLabel.RIGHT);
		textMinValue = new JTextField(viewSettings.minValue + "");
		textMinValue.setToolTipText("Minimum value to scale all traces");
		labelMaxValue = new JLabel("Max: ", JLabel.RIGHT);
		textMaxValue = new JTextField(viewSettings.maxValue + "");
		textMaxValue.setToolTipText("maximum value to scale all traces");
		textTraceScalar = new JTextField(viewSettings.fullTraceScalar + "");
		textTraceScalar.setToolTipText("Constant scalar to apply after full trace equalization");

		buttonAutoRange = new JButton("Auto Set");
		buttonAutoRange.setMargin(null);
		buttonAutoRange.setToolTipText("Automatically set min/max range values based on all traces");
		buttonAutoRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ISeismicTraceBuffer buffer = stvFrame.getTraceContext().getTraceBuffer();
				if (buffer != null) {
					textMinValue.setText("" + buffer.minTotalAmplitude());
					textMaxValue.setText("" + buffer.maxTotalAmplitude());
					if (scalingChanged()) {
						fireEventSettingsChanged();
					}
				}
			}
		});

		buttonAverage = new JRadioButton("Average", viewSettings.traceScaling == StvConst.TRACE_SCALING_AVERAGE);
		buttonMaximum = new JRadioButton("Max", viewSettings.traceScaling == StvConst.TRACE_SCALING_MAXIMUM);
		buttonAverage.setToolTipText("Equalize data by trace average (absolute) amplitude");
		buttonMaximum.setToolTipText("Equalize data by trace maximum (absolute) amplitude");
		buttonMaximum.setMargin(new Insets(0, 20, 0, 0));
		moduleRadioGroup = new ButtonGroup();
		moduleRadioButton = new JRadioButton[] {
				buttonMaximum, buttonAverage
		};
		n = moduleRadioButton.length;
		for (int i = 0; i < n; i++) {
			final int j = i;
			moduleRadioButton[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (((JRadioButton) event.getSource()).isSelected()) {
						if (j == 0) {
							viewSettings.traceScaling = StvConst.TRACE_SCALING_MAXIMUM;
						} else if (j == 1) {
							viewSettings.traceScaling = StvConst.TRACE_SCALING_AVERAGE;
						}
						if (scalingChanged()) {
							fireEventSettingsChanged();
						}
					}
				}
			});
			moduleRadioGroup.add(moduleRadioButton[i]);
		}

		moduleRadioGroup = new ButtonGroup();
		moduleRadioButton = new JRadioButton[] {
				buttonTrace, buttonRange, buttonScalar
		};
		n = moduleRadioButton.length;
		jPanel = new JPanel(new GridLayout(n + 4, 2, 0, 0));
		for (int i = 0; i < n; i++) {
			final int j = i;
			moduleRadioButton[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (((JRadioButton) event.getSource()).isSelected()) {
						if (j == 2) {
							viewSettings.scaleType = StvConst.SCALE_TYPE_SCALAR;
						} else if (j == 1) {
							viewSettings.scaleType = StvConst.SCALE_TYPE_RANGE;
						} else if (j == 0) {
							viewSettings.scaleType = StvConst.SCALE_TYPE_TRACE;
						}
						if (scalingChanged()) {
							fireEventSettingsChanged();
						}
					}
				}
			});
			moduleRadioGroup.add(moduleRadioButton[i]);
			jPanel.add(moduleRadioButton[i]);
			if (j == 2) {
				jPanel.add(buttonAutoScalar);
				jPanel.add(new JLabel(""));
				jPanel.add(textScalar);
			} else if (j == 1) {
				jPanel.add(buttonAutoRange);
				jPanel.add(labelMinValue);
				jPanel.add(textMinValue);
				jPanel.add(labelMaxValue);
				jPanel.add(textMaxValue);
			} else if (j == 0) {
				jPanel.add(textTraceScalar);
				jPanel.add(buttonMaximum);
				jPanel.add(buttonAverage);
			}

		}
		scalingChanged();
		jRow = 0;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(jPanel, gbc);

		iRow++;
		modulePanel.setBorder(BorderFactory.createTitledBorder(null, "Amplitude scaling",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, myFont, myColor));
		gbc = new GridBagConstraints(0, iRow, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsHigh, 0, 0);
		add(modulePanel, gbc);

		/////////////// Zoom settings ///////////////////
		modulePanel = new JPanel(new GridBagLayout());

		textZoomVert = new JTextField(viewSettings.zoomVert + "");
		textZoomHorz = new JTextField(viewSettings.zoomHorz + "");
		textTraceClip = new JTextField(viewSettings.traceClip + "");
		textZoomVert.setToolTipText("Screen pixels per sample");
		textZoomHorz.setToolTipText("Screen pixels per trace");
		textTraceClip.setToolTipText("Number of traces where wiggle is clipped");

		jPanel = new JPanel(new GridLayout(3, 2, 0, 0));
		jPanel.add(new JLabel("Pixels/Sample ", JLabel.RIGHT));
		jPanel.add(textZoomVert);
		jPanel.add(new JLabel("Pixels/Trace ", JLabel.RIGHT));
		jPanel.add(textZoomHorz);
		jPanel.add(new JLabel("Trace clip", JLabel.RIGHT));
		jPanel.add(textTraceClip);

		jRow = 0;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(jPanel, gbc);

		iRow++;
		modulePanel.setBorder(BorderFactory.createTitledBorder(null, "Zoom settings",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, myFont, myColor));
		gbc = new GridBagConstraints(0, iRow, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsHigh, 0, 0);
		add(modulePanel, gbc);

		/////////////// Time axis settings ///////////
		modulePanel = new JPanel(new GridBagLayout());

		labelMinorInc = new JLabel("Minor inc: ", JLabel.RIGHT);
		labelMajorInc = new JLabel("Major inc: ", JLabel.RIGHT);
		labelMinorInc.setToolTipText("Minor increment in  micro seconds");
		labelMajorInc.setToolTipText("Major increment in  micro seconds");

		textMinorInc = new JTextField(viewSettings.timeLineMinorInc + "");
		textMajorInc = new JTextField(viewSettings.timeLineMajorInc + "");
		textMinorInc.setToolTipText("Minor increment for time axis annotation");
		textMajorInc.setToolTipText("Major increment for time axis annotation");

		boxShowHorzLines = new JCheckBox("Show time lines", viewSettings.showTimeLines);
		boxShowHorzLines.setToolTipText("Show time lines on top of seismic display");
		boxShowHorzLines.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				viewSettings.showTimeLines = (e.getStateChange() == ItemEvent.SELECTED);
				fireEventSettingsChanged();
			}
		});
		jRow = 0;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(boxShowHorzLines, gbc);

		// boxAutoHorzLines = new JCheckBox("Automatic settings",
		// viewSettings.isTimeLinesAuto);
		// boxAutoHorzLines.setToolTipText("Automatically set time annotation
		// increments");
		// boxAutoHorzLines.addItemListener(new ItemListener() {
		// public void itemStateChanged(ItemEvent e) {
		// viewSettings.isTimeLinesAuto = (e.getStateChange() == ItemEvent.SELECTED);
		// if(timeAxisChanged()) {
		// fireEventSettingsChanged();
		// }
		// }
		// });
		// jRow++;
		// gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
		// GridBagConstraints.BOTH, insets, 0, 0);
		// modulePanel.add(boxAutoHorzLines, gbc);

		jPanel = new JPanel(new GridLayout(2, 2, 0, 0));
		jPanel.add(labelMinorInc);
		jPanel.add(textMinorInc);
		jPanel.add(labelMajorInc);
		jPanel.add(textMajorInc);
		jRow++;
		gbc = new GridBagConstraints(0, jRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0);
		modulePanel.add(jPanel, gbc);

		iRow++;
		modulePanel.setBorder(BorderFactory.createTitledBorder(null, "Time axis settings",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, myFont, myColor));
		gbc = new GridBagConstraints(0, iRow, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insetsHigh, 0, 0);
		add(modulePanel, gbc);

		iRow++;

		JButton buttonApply = new JButton("<html><b>Apply text inputs</b>");
		buttonApply.setToolTipText("For button actions, automatically applied");
		buttonApply.setBorder(new LineBorder(Color.GREEN, 3));
		buttonApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (zoomChanged() && timeAxisChanged() && scalingChanged()) {
					fireEventSettingsChanged();
				}
			}
		});

		gbc = new GridBagConstraints(0, iRow, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, insets, 0, 0);
		add(buttonApply, gbc);

	}

	public void fireEventSettingsChanged() {
		stvFrame.getViewPanel().fireEventSettingsChanged();
	}

	private boolean zoomChanged() {
		try {
			String text;
			text = textZoomVert.getText();
			float zoomVert = Float.parseFloat(text);
			text = textZoomHorz.getText();
			float zoomHorz = Float.parseFloat(text);
			text = textTraceClip.getText();
			float traceClip = Float.parseFloat(text);

			if (zoomVert > 0 && zoomHorz > 0) {
				viewSettings.zoomVert = zoomVert;
				viewSettings.zoomHorz = zoomHorz;
			} else {
				return showErrorMessage("Zoom settings", "Both pixels values are positive");
			}
			if (traceClip > 1.0) {
				viewSettings.traceClip = traceClip;
			} else {
				return showErrorMessage("Zoom settings", "Trace clip value > 1.0");
			}
		} catch (NumberFormatException e) {
			return showErrorMessage("Zoom settings", e.getMessage());
		}
		return true;
	}

	private boolean timeAxisChanged() {
		try {
			String text;
			text = textMinorInc.getText();
			float min = Float.parseFloat(text);
			text = textMajorInc.getText();
			float max = Float.parseFloat(text);

			if (min > 0 && max > 0 && max >= min) {
				viewSettings.timeLineMinorInc = min;
				viewSettings.timeLineMajorInc = max;
			} else {
				return showErrorMessage("Time axis settings", "Both increments are positive and the major >= the minor");
			}
		} catch (NumberFormatException e) {
			return showErrorMessage("Time axis settings", e.getMessage());
		}
		return true;
	}

	private boolean showErrorMessage(String sectionName, String message) {
		JOptionPane.showMessageDialog(stvFrame, sectionName + " : " + message,
				"Error", JOptionPane.ERROR_MESSAGE);
		return false;
	}

	private boolean scalingChanged() {
		boolean selectedScalar = buttonScalar.isSelected();
		textScalar.setEnabled(selectedScalar);
		buttonAutoScalar.setEnabled(selectedScalar);

		boolean selectedRange = buttonRange.isSelected();
		buttonAutoRange.setEnabled(selectedRange);
		labelMinValue.setEnabled(selectedRange);
		labelMaxValue.setEnabled(selectedRange);
		textMinValue.setEnabled(selectedRange);
		textMaxValue.setEnabled(selectedRange);

		boolean selectedTrace = buttonTrace.isSelected();
		textTraceScalar.setEnabled(selectedTrace);
		buttonMaximum.setEnabled(selectedTrace);
		buttonAverage.setEnabled(selectedTrace);

		// if (selectedTrace) {
		// buttonVA2DSpline.setEnabled(false);
		// } else if (boxVA.isSelected()) {
		// buttonVA2DSpline.setEnabled(true);
		// }

		try {
			String text;
			if (selectedScalar) {
				text = textScalar.getText();
				viewSettings.dispScalar = Float.parseFloat(text);
			}
			if (selectedRange) {
				text = textMinValue.getText();
				float min = Float.parseFloat(text);
				text = textMaxValue.getText();
				float max = Float.parseFloat(text);

				if (max > min) {
					viewSettings.minValue = min;
					viewSettings.maxValue = max;
				} else {
					return showErrorMessage("Amplitude scaling", "max >= min");
				}
			}
			if (selectedTrace) {
				text = textTraceScalar.getText();
				viewSettings.fullTraceScalar = Float.parseFloat(text);
			}
		} catch (NumberFormatException e) {
			return showErrorMessage("Amplitude scaling", e.getMessage());
		}

		return true;
	}

	private void wiggleChanged() {
		buttonColorWiggle.setEnabled(boxWiggle.isSelected());
		buttonColorWigglePos.setEnabled(boxPosFill.isSelected());
		buttonColorWiggleNeg.setEnabled(boxNegFill.isSelected());
		buttonColorWiggleHighlight.setEnabled(boxHighlight.isSelected() && boxWiggle.isSelected());
		boxHighlight.setEnabled(boxWiggle.isSelected());
	}

	private void vaChanged() {
		boolean isSelected = boxVA.isSelected();
		labelVIInterpolation.setEnabled(isSelected);
		buttonVADiscrete.setEnabled(isSelected);
		buttonVAVertical.setEnabled(isSelected);
		buttonVA2DSpline.setEnabled(isSelected);
		labelColorMap.setEnabled(isSelected);
		comboColorMapVA.setEnabled(isSelected);
		// buttonTrace.setEnabled(!_buttonVA2DSpline.isSelected());
	}

	class ComboCellRenderer extends DefaultListCellRenderer {
		public ComboCellRenderer() {
		}

		public Component getListCellRendererComponent(JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			JLabel label = (JLabel) comp;
			ComboItem item = (ComboItem) value;
			label.setText(item.toString());
			label.setIcon(item.icon);
			return this;
		}
	}

	class ComboItem {
		public RectIcon icon;
		public ColorMap map;

		public ComboItem(RectIcon iconIn, ColorMap mapIn) {
			icon = iconIn;
			map = mapIn;
		}

		public String toString() {
			int colorMapIndex = map.getDefaultMapIndex();
			if (colorMapIndex < ColorMap.NUM_DEFAULT_MAPS) {
				return ColorMap.textDefaultMap(colorMapIndex);
			} else {
				return ("User_defined");
			}
		}
	}

}
