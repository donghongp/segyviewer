package com.ghc.app.stv.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import com.ghc.app.seis.ISeismicTraceBuffer;
import com.ghc.app.stv.StvFrame;
import com.ghc.app.utils.UiUtil;

/**
 * Trace value monitor. Displays all sample values of current seismic trace.
 */
@SuppressWarnings("serial")
public class TraceSampleDialog extends JDialog {
	private StvFrame frame = null;
	private JTable _traceTable;
	private DefaultTableModel _tableModel;
	private JTextField _textTrace;
	private ISeismicTraceBuffer _traceBuffer;
	private int _currentTraceIndex;
	private DecimalFormat floatFormat = new DecimalFormat("0.0");
	private float _sampleInt;
	private int _firstTraceIndex;

	public int width = 400;
	public int height = 500;

	public TraceSampleDialog(JFrame aParent, String aTitle, boolean modal) {
		super(aParent, aTitle, modal);
		width = 400;
		height = 900;
		this.frame = (StvFrame) aParent;
		_currentTraceIndex = 0;
		_firstTraceIndex = 0;

	}

	public void showDialog() {
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setResizable(true);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(UiUtil.getStandardBorder());

		JComponent jc = createContents();
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		jc.setBorder(border);

		panel.add(jc, BorderLayout.CENTER);

		getContentPane().removeAll();
		getContentPane().add(panel);

		setSize(width, height);
		UiUtil.centerOnParentAndShow(this);
		setVisible(true);
	}

	protected JPanel createContents() {
		_tableModel = new DefaultTableModel(new String[] { "Sample index", "Time [ms]", "Sample value" },
				frame.getTraceBuffer().numSamples());
		_traceTable = new JTable(_tableModel);
		_traceTable.setRowHeight(25);
		updateBuffer(frame.getTraceBuffer(), _firstTraceIndex, (float) frame.getSampleInt());

		_textTrace = new JTextField("0");
		_textTrace.setPreferredSize(new Dimension(10, _textTrace.getPreferredSize().height));

		int xp = 0;
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.add(new JLabel(" Trace: "), new GridBagConstraints(
				xp++, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(_textTrace, new GridBagConstraints(
				xp++, 0, 1, 1, 0.5, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		// mainPanel.add(_boxLockTrace, new GridBagConstraints(
		// xp++, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER,
		// GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		// mainPanel.add(_boxLockScroll, new GridBagConstraints(
		// xp++, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER,
		// GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		// mainPanel.add(buttonSave, new GridBagConstraints(
		// xp++, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER,
		// GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(Box.createHorizontalGlue(), new GridBagConstraints(
				xp++, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		JPanel innerPanel = new JPanel(new BorderLayout());
		JScrollPane panel = new JScrollPane(_traceTable);
		innerPanel.add(mainPanel, BorderLayout.NORTH);
		innerPanel.add(panel, BorderLayout.CENTER);
		return innerPanel;
	}

	public void updateBuffer(ISeismicTraceBuffer traceBuffer, int firstTraceIndex, float sampleInt) {
		_sampleInt = sampleInt;
		_traceBuffer = traceBuffer;
		_firstTraceIndex = firstTraceIndex;
		int numSamples = _traceBuffer.numSamples();
		_tableModel.setRowCount(numSamples);

		_currentTraceIndex = 0;
		for (int i = 0; i < _traceBuffer.numSamples(); i++) {
			_traceTable.setValueAt(i + 1, i, 0);
			_traceTable.setValueAt(floatFormat.format(sampleInt * i), i, 1);
			_traceTable.setValueAt(new String(""), i, 2);
		}
	}

	public void updateValues(int traceIndex) {
		_currentTraceIndex = traceIndex;
		float[] samples = _traceBuffer.samples(traceIndex);
		for (int i = 0; i < _traceBuffer.numSamples(); i++) {
			_traceTable.setValueAt(samples[i], i, 2);
		}
		_textTrace.setText("" + (traceIndex + 1 + _firstTraceIndex));

	}

}
