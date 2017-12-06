package org.processmining.plugins.cnmining;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * Questa classe si occupa di generare l'aspetto grafico 
 * di un pannello utilizzato per il caricamento del file xml
 * contenente i vincoli di precedenza, utili all'algoritmo
 * implementato 
 */

public class ConstraintsViewPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean constraintsEnabled = false;
	private String filename = ""; // nome del file scelto
	
	/*
	 * Costruisci il controllo grafico
	 */
	public ConstraintsViewPanel(){
		setBackground(Color.GRAY);
    
		final JFileChooser filebrowser = new JFileChooser(".");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Constraints", new String[] { "xml" });
		filebrowser.setFileFilter(filter);
    
		JLabel lblEnableConstraints = new JLabel("ENABLE CONSTRAINTS");
		lblEnableConstraints.setFont(new Font("Lucida Grande", 1, 12));
    
		final JCheckBox checkBox = new JCheckBox("");
    
		final JButton btnSelect = new JButton("Select input file");
		btnSelect.setEnabled(false);
    
	    checkBox.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e) {
				if (checkBox.isSelected())
	    		{
	    			btnSelect.setEnabled(true);
	    			ConstraintsViewPanel.this.constraintsEnabled = true;
	    		}
	    		else
	    		{
	    			btnSelect.setEnabled(false);
	    			ConstraintsViewPanel.this.constraintsEnabled = false;
	    		}
				
			}
	    });
    	final JLabel label = new JLabel("");
    
	    btnSelect.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		int returnVal = filebrowser.showOpenDialog(ConstraintsViewPanel.this.getParent());
	    		if (returnVal == 0)
	    		{
	    			System.out.println("You chose to open this file: " + filebrowser.getSelectedFile().getName());
	    			label.setText(filebrowser.getSelectedFile().getAbsolutePath());
	    			ConstraintsViewPanel.this.filename = filebrowser.getSelectedFile().getAbsolutePath();
	    		}
	    	}
	    });
	    GroupLayout groupLayout = new GroupLayout(this);
	    groupLayout.setHorizontalGroup( 
    	  groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	      .addGroup(groupLayout.createSequentialGroup()
	      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	      .addGroup(groupLayout.createSequentialGroup()
	      .addContainerGap()
	      .addComponent(lblEnableConstraints)
	      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	      .addComponent(checkBox))
	      .addGroup(GroupLayout.Alignment.TRAILING, groupLayout.createSequentialGroup()
	      .addContainerGap(24, 32767)
	      .addComponent(label, -2, 570, -2)))
	      .addContainerGap(334, 32767))
	      .addGroup(groupLayout.createSequentialGroup()
	      .addGap(85)
	      .addComponent(btnSelect)
	      .addContainerGap(373, 32767)));
    
	    groupLayout.setVerticalGroup(
    	  groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	      .addGroup(groupLayout.createSequentialGroup()
	      .addGap(10)
	      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	      .addComponent(lblEnableConstraints)
	      .addComponent(checkBox))
	      .addGap(12)
	      .addComponent(btnSelect)
	      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	      .addComponent(label, -2, 19, -2)
	      .addContainerGap(23, 32767)));
    
	    setLayout(groupLayout);
	}
	
	public boolean areConstraintsEnabled()
	{
		return this.constraintsEnabled && filename.equals("") == false;
 	}
  
	public String getFilename()
	{
		return this.filename;
	}
}
