package com.app.util;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.springframework.beans.factory.annotation.Autowired;

import com.app.service.EmployeeService;

public class ImportContacts {
	
	@Autowired
    public EmployeeService service;
	
	public void setService(EmployeeService service) {
		this.service = service;
	}
	
    public static void main(String[] args) {
        new ImportContacts();        
    }

 public ImportContacts() {
	 EventQueue.invokeLater(new Runnable() {
     @Override
     public void run() {
    	 try {
    		 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	 } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
    		  ex.printStackTrace();
    	 }        
    	 JFrame frame = new JFrame("Select a file");
         frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         frame.add(new TestPane());
         frame.setSize(300, 60);       
         frame.setLocationRelativeTo(null);
         frame.setAlwaysOnTop(true);
         frame.setVisible(true);    
         }
       });
    }

 public class TestPane extends JPanel {
	 
	 private static final long serialVersionUID = 1L;
	 private JButton open;       
     private JFileChooser chooser;

     public TestPane() {        	
    	 setLayout(new BorderLayout());
         open = new JButton("Open");        
         add(open, BorderLayout.SOUTH);
         open.addActionListener(new ActionListener() {        
		@Override
         public void actionPerformed(ActionEvent e) {                    	
        	 if (chooser == null) {
        		 chooser = new JFileChooser();
            	 chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            	 chooser.setAcceptAllFileFilterUsed(false);
            	 chooser.addChoosableFileFilter(new FileFilter() {
            	 @Override
            	 public boolean accept(File f) {
            		 return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt") || f.getName().toLowerCase().endsWith(".csv");
            	 }
            	 @Override
            	 public String getDescription() {
            		 return ("Text Files (*.txt) or CSV Files (*.csv)");                                
            	 }
            	});
            }

             switch (chooser.showOpenDialog(TestPane.this)) {
             	case JFileChooser.APPROVE_OPTION:
             		try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {                              
             			String text = null;   
                        while ((text = br.readLine()) != null) {  
                        	try {
								service.addUserFromFile(text);
							} catch (SQLException e1) {								
								e1.printStackTrace();
							}  
                        }    
                        final JDialog dialog = new JDialog();
                    	dialog.setAlwaysOnTop(true);    
                    	JOptionPane.showMessageDialog(dialog, "File Imported Successfully");   
                    } catch (IOException exp) {
                           exp.printStackTrace();
                           JOptionPane.showMessageDialog(TestPane.this, "Failed to read file", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                 break;
                 }
              }
          });
        }
    }
}