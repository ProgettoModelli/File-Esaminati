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

/**
 * 
 * @author Utente
 */
public class PannelloConstraints extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean constraintsEnabled = false;
	private String filePath = "";
  
	public PannelloConstraints()
	{
		setBackground(Color.GRAY);
    
		final JFileChooser chooser = new JFileChooser(".");
   		FileNameExtensionFilter filter = new FileNameExtensionFilter("Constraints", new String[] { "xml" });
   		chooser.setFileFilter(filter);
    
   		JLabel lblEnableConstraints = new JLabel("ENABLE CONSTRAINTS");
   		lblEnableConstraints.setFont(new Font("Lucida Grande", 1, 12));
    
   		final JCheckBox checkBox = new JCheckBox("");
    
   		final JButton btnSelect = new JButton("Select input file");
   		btnSelect.setEnabled(false);
    
   		checkBox.addActionListener(new ActionListener()
   		{
   			public void actionPerformed(ActionEvent e)
   			{
   				if (checkBox.isSelected())
   				{
   					btnSelect.setEnabled(true);
   					PannelloConstraints.this.constraintsEnabled = true;
   				}
   				else
   				{
   					btnSelect.setEnabled(false);
   					PannelloConstraints.this.constraintsEnabled = false;
   				}
   			}
   		});
   		final JLabel label = new JLabel("");
    
   		btnSelect.addActionListener(new ActionListener()
   		{
   			public void actionPerformed(ActionEvent e)
   			{
   				int returnVal = chooser.showOpenDialog(PannelloConstraints.this.getParent());
   				if (returnVal == 0)
   				{
   					System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
   					label.setText(chooser.getSelectedFile().getAbsolutePath());
   					PannelloConstraints.this.filePath = chooser.getSelectedFile().getAbsolutePath();
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
  
	public boolean isConstraintsEnabled()
	{
		return this.constraintsEnabled;
	}
  
	public void setConstraintsEnabled(boolean constraintsEnabled)
	{
		this.constraintsEnabled = constraintsEnabled;
	}
  
	public String getFilePath()
	{
		return this.filePath;
	}
}
