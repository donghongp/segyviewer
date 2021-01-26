package com.ghc.app.stv.dialog;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.ghc.app.seis.Header;
import com.ghc.app.seis.HeaderDef;
import com.ghc.app.stv.StvFrame;
import com.ghc.app.utils.UiUtil;

public class TraceHeaderDialog extends JDialog {

	public int width = 400;
	public int height = 500;
	private StvFrame frame = null;
	/// Header description
	private String[] _headerDesc;
	private JTable _headerTable;
	private DefaultTableModel _tableModel;
	/// Conversion of model index to view index. Trace headers are unsorted in trace
	/// buffer, and sorted by name in table view
	private int[] _model2ViewIndexArray;
	private int _numHeaders;

	private HeaderDef[] headerDef;
	private int[] headerValue;

	public TraceHeaderDialog(JFrame aParent, String aTitle, boolean modal, HeaderDef[] headers) {
		super(aParent, aTitle, modal);
		width = 600;
		height = 910;
		this.frame = (StvFrame) aParent;
		this.headerDef = headers;
		_tableModel = new HeaderTableModel(new String[] { "Header value", "Header byte", "Header description" }, headers.length);
		_headerTable = new HeaderTable(_tableModel);
		_headerTable.setRowHeight(25);
		setJTableColumnsWidth(_headerTable, 600, 100, 100, 400);
	}

	public void setHeaderValue(int[] headerValue) {
		this.headerValue = headerValue;
	}

	public void setJTableColumnsWidth(JTable table, int tablePreferredWidth, double... percentages) {
		double total = 0;
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			total += percentages[i];
		}

		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth((int) (tablePreferredWidth * (percentages[i] / total)));
		}
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

	protected JScrollPane createContents() {
		updateHeaderNames(headerDef);
		JScrollPane panel = new JScrollPane(_headerTable);
		return panel;
	}

	public void updateHeaderNames(HeaderDef[] headersIN) {
		_numHeaders = headersIN.length;
		_headerDesc = new String[_numHeaders];
		_tableModel.setRowCount(_numHeaders);

		// Sort input headers (without changing input array), and set index array that
		// converts from
		// 'model' row index to view row index.
		HeaderDef[] headersTMP = new HeaderDef[_numHeaders];
		_model2ViewIndexArray = new int[_numHeaders];
		for (int ih = 0; ih < _numHeaders; ih++) {
			headersTMP[ih] = new HeaderDef(headersIN[ih]);
			headersTMP[ih].index = ih;
		}
		java.util.List<HeaderDef> list = java.util.Arrays.asList(headersTMP);
		java.util.Collections.sort(list);
		for (int ih = 0; ih < _numHeaders; ih++) {
			_model2ViewIndexArray[list.get(ih).index] = ih;
		}

		boolean isHeaderValueGiven = (headerValue == null) ? false : true;

		for (int i = 0; i < _numHeaders; i++) {
			_headerTable.setValueAt(isHeaderValueGiven ? headerValue[i] : new String(""), _model2ViewIndexArray[i], 0);
			_headerTable.setValueAt(headersIN[i].name, _model2ViewIndexArray[i], 1);
			_headerTable.setValueAt(headersIN[i].desc, _model2ViewIndexArray[i], 2);
			_headerDesc[_model2ViewIndexArray[i]] = headersIN[i].desc;
		}
	}

	/**
	 * Update header values
	 * 
	 * @param headerValues Array of header values
	 */
	public void updateValues(Header[] headerValues) {
		if (_numHeaders != headerValues.length) {
			System.err.println("Incorrect number of headers in input: " + _numHeaders + " != "
					+ headerValues.length);
			return;
		}
		for (int i = 0; i < _numHeaders; i++) {
			_headerTable.setValueAt(headerValues[i], _model2ViewIndexArray[i], 0);
		}
	}

	public void updateValues(int[] headerValues) {
		if (_numHeaders != headerValues.length) {
			System.err.println("Incorrect number of headers in input: " + _numHeaders + " != "
					+ headerValues.length);
			return;
		}
		for (int i = 0; i < _numHeaders; i++) {
			_headerTable.setValueAt(headerValues[i], _model2ViewIndexArray[i], 0);
		}
	}

	/**
	 * Header table
	 *
	 */
	public class HeaderTable extends JTable {
		HeaderTable(TableModel model) {
			super(model);
		}

		public String getToolTipText(MouseEvent e) {
			String tip = null;
			java.awt.Point p = e.getPoint();
			int rowIndex = rowAtPoint(p);
			int colIndex = columnAtPoint(p);
			int realColumnIndex = convertColumnIndexToModel(colIndex);

			if (realColumnIndex == 0 && (rowIndex >= 0 && rowIndex < _headerDesc.length)) {
				tip = _headerDesc[rowIndex]; // + getValueAt(rowIndex, colIndex);
			}
			// else if( realColumnIndex == 1 && (rowIndex >= 0 && rowIndex <
			// _headerDesc.length) ) {
			// tip = "Value = " + super.getValueAt( rowIndex, colIndex ); // +
			// getValueAt(rowIndex, colIndex);
			// }
			else {
				tip = super.getToolTipText(e);
			}
			return tip;
		}
	}

	/**
	 * Header table
	 *
	 */
	public class HeaderTableModel extends DefaultTableModel {
		HeaderTableModel(Object[] columnNames, int numColumns) {
			super(columnNames, numColumns);
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

}
