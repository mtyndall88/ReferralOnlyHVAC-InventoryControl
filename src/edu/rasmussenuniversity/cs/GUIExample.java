package edu.rasmussenuniversity.cs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

//Future extension: Optional Swing demo. Not used in this console-based application.

public class GUIExample {
	
	public void OpenGui() {
		JFrame frame = new JFrame("Product Tracking System");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(800,800);
		frame.setResizable(false);
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10,10,10,5);
		
		JLabel productLabel = new JLabel("Product Name");
		c.gridx = 0;
		c.gridy = 0;
		panel.add(productLabel, c);
		
		JTextField productName = new JTextField(20);
		c.gridx = 1;
		c.gridy = 0;
		panel.add(productName, c);
		
		JLabel descLabel = new JLabel("Description");
		c.gridx = 0;
		c.gridy = 1;
		panel.add(descLabel, c);
		
		JTextField descText = new JTextField(20);
		c.gridx = 1;
		c.gridy = 1;
		panel.add(descText, c);
		
		JLabel searchLabel = new JLabel("Search by Name");
		c.gridx = 0;
		c.gridy = 2;
		panel.add(searchLabel, c);
		
		JTextField inputText = new JTextField(20);
		c.gridx = 1;
		c.gridy = 2;
		panel.add(inputText, c);
		
		JButton getAllBtn = new JButton("Get All Products");
		c.gridx = 2;
		c.gridy = 0;
		panel.add(getAllBtn, c);
		
		JButton searchBtn = new JButton("Search by Name");
		c.gridx = 2;
		c.gridy = 2;
		panel.add(searchBtn, c);
		
		JTextArea resultText = new JTextArea(10, 20);
		c.gridx = 1;
		c.gridy = 3;
		panel.add(resultText, c);
		
		searchBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Product productActions = new Product();
				try {
					String str = productActions.searchProduct(inputText.getText());
					resultText.setText(str);
				}
				catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
		});
		
		getAllBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Product productActions = new Product();
				try {
					String str = productActions.getAllProducts();
					resultText.setText(str);
				} 
				catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
		});
		
		frame.add(panel, BorderLayout.WEST);
		frame.setVisible(true);
		
	}

}
