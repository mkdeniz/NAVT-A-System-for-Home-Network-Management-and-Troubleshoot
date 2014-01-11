/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 *
 * @author root
 */
public class Error extends JDialog{
    
    Error(String s){
        
        super();
        this.add(new JLabel(s));
        this.setSize(150, 150);
        this.setVisible(true);
    }
}
