/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package second;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 *
 * @author root
 */
public class Menu extends JMenu implements ActionListener{
    
    public Menu(String s){
        super(s);
        JMenuItem menuItem = new JMenuItem("About");
        JMenuItem Help = new JMenuItem("About");
        this.add(Help);
        Help.addActionListener(
            new ActionListener() { 
                public void actionPerformed(ActionEvent ev) {
                    new About();    
                    }
                }
        );
        
        this.addSeparator();
        JMenuItem menuItem2 = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.addActionListener(this);
        add(menuItem2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Exit".equals(e.getActionCommand())){
            System.exit(0);
        }
            
    }
}
