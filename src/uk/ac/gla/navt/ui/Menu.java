/**
* A menu class to provide options
* 
* @Author Mehmet Kemal Deniz
* @Date 27/03/2014
*/

package uk.ac.gla.navt.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
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
                @Override
                public void actionPerformed(ActionEvent ev) {
                    About a = new About();    
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
