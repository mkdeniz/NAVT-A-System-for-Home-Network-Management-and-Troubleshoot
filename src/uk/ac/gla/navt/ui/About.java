package uk.ac.gla.navt.ui;
 
/**
* An about panel
* 
* @Author Mehmet Kemal Deniz
* @Date 27/03/2014
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class About extends JDialog implements ActionListener {
    
    JButton Close = new JButton("Close");
    
    public About() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Color bgColor = panel.getBackground();
        getContentPane().add(panel, BorderLayout.CENTER);
        
        JPanel textPanel = new JPanel();
        String strHelp = "Copyright 2014 - Mehmet Kemal Deniz\n\n" +
            "University of Glasgow - 4th Year Project" ;
        JTextArea jtaHelpText = new JTextArea(strHelp, 15, 30);
        jtaHelpText.setLineWrap(true);
        jtaHelpText.setWrapStyleWord(true);
        jtaHelpText.setEditable(false);
        jtaHelpText.setBackground(bgColor);
        textPanel.add(jtaHelpText,BorderLayout.CENTER);
        panel.add(textPanel,BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(Close);
        Close.addActionListener(this); 
        
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