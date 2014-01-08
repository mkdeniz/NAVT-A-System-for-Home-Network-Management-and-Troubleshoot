package second;

/*
 *  Copyright 2006 Corey Goldberg (cgoldberg _at_ gmail.com)
 *
 *  This file is part of NetPlot.
 *
 *  NetPlot is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  NetPlot is distributed in the hope that it will be useful,
 *  but without any warranty; without even the implied warranty of
 *  merchantability or fitness for a particular purpose.  See the
 *  GNU General Public License for more details.
 */
 
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class About extends JDialog implements ActionListener {
    
    JButton jbtOK = new JButton("OK");
    
    public About() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Color bgColor = panel.getBackground();
        getContentPane().add(panel, BorderLayout.CENTER);
        
        // add logo
        
        JPanel logoPanel = new JPanel();
        logoPanel.add(new JLabel(new ImageIcon("lib/NetPlot.png")));
        panel.add(logoPanel);
        
        JPanel textPanel = new JPanel();
        String strHelp = "NetPlot - Network Latency Monitor\n\n" +
            "Copyright 2014 - Mehmet Kemal Deniz\n\n" +
            "University of Glasgow - 4th Year Project" ;
        JTextArea jtaHelpText = new JTextArea(strHelp, 15, 30);
        jtaHelpText.setLineWrap(true);
        jtaHelpText.setWrapStyleWord(true);
        jtaHelpText.setEditable(false);
        jtaHelpText.setBackground(bgColor);
        textPanel.add(jtaHelpText);
        panel.add(textPanel);
        
        JPanel buttonPanel = new JPanel();
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(jbtOK);
        jbtOK.addActionListener(this); 
        
        setTitle("About");
        setBounds(200, 250, 250, 265);
        setVisible(true);    
    }
    
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == jbtOK) {
            dispose();
        }
    }
    
}