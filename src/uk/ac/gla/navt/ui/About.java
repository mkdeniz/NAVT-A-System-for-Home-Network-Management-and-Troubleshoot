package uk.ac.gla.navt.ui;
 
/**
* An about panel which extends JDialog
* 
* @Author Mehmet Kemal Deniz
* @Date 27/03/2014
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class About extends JDialog implements ActionListener {
    
    protected JButton Close = new JButton("Close");
    protected JPanel panel = new JPanel();
    
    /**
     * 
     * Default Constructor
     * 
     */
    public About() {
        super();
        
        //Add Panel
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        getContentPane().add(panel, BorderLayout.CENTER);
        
        //Create text area nad add it to panel
        JPanel textPanel = new JPanel();
        String strHelp = "Copyright 2014 - Mehmet Kemal Deniz\n\n" +
            "University of Glasgow - 4th Year Project" ;
        JTextArea textArea = new JTextArea(strHelp, 15, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        Color bgColor = panel.getBackground();
        textArea.setBackground(bgColor);
        textPanel.add(textArea,BorderLayout.CENTER);
        panel.add(textPanel,BorderLayout.CENTER);
        
        //Add clsoe button to panel
        JPanel buttonPanel = new JPanel();
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(Close);
        Close.addActionListener(this); 
        
        //Make it visible
        setTitle("About");
        this.pack();
        setVisible(true);    
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == Close) {
            dispose();
        }
    }
    
}
