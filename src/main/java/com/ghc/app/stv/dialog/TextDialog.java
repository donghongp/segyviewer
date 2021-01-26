package com.ghc.app.stv.dialog;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.ghc.app.stv.StvFrame;
import com.ghc.app.utils.UiUtil;

public class TextDialog extends JDialog {

	public int width = 800;
	public int height = 900;
	private StvFrame frame = null;
	private String content;

	public TextDialog(JFrame aParent, String aTitle, boolean modal, String content) {
		super(aParent, aTitle, modal);
		width = 800;
		height = 850;
		this.frame = (StvFrame) aParent;
		this.content = content;
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
		JTextArea ta = new JTextArea(content);
		JScrollPane panel = new JScrollPane(ta);
		return panel;
	}
}
