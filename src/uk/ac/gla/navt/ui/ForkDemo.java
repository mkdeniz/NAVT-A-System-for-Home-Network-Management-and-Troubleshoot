package uk.ac.gla.navt.ui;

import uk.ac.gla.navt.main.ForkWorker;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;
import javax.swing.border.TitledBorder;
 
public class ForkDemo extends JPanel implements ActionListener {
 
  protected JTextField textField = new JTextField(20);
  protected JTextArea output = new JTextArea(25,25);
  protected JButton start = new JButton("START");
  protected JButton stop = new JButton("STOP");
  private ForkWorker forkWorker;
 
  public ForkDemo() {
        super(new GridLayout(1,2));
        JPanel j1 =  new JPanel(new GridLayout(2,3));
        j1.setSize(300, 100);
        JPanel j2 = new JPanel();
        j1.setBorder(new TitledBorder("Options"));
        
        j1.add(output);
        j1.add(textField);
        
        j2.add(start);
        j2.add(stop);
        add(j1);
        add(j2);
        start.addActionListener(this);
        this.setSize(300, 300);
    }
 
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource()==start) {
      doStart();
    }
    if (e.getSource()==stop) {
      doStop();
    }
  }
 
  private void doStart() {
	List<String> commands = new ArrayList<>();
	String st = textField.getText();
    commands.add("traceroute");
    commands.add(st);
    ProcessBuilder builder = new ProcessBuilder(commands);
    builder.redirectErrorStream(true);
    forkWorker = new ForkWorker(output,builder);
    forkWorker.execute();
  }
  
  private void doStop() {
    if (forkWorker!=null) {
      forkWorker.cancel(true);
    }
  }
 
  public static void main(String[] args) {
    ForkDemo demo = new ForkDemo();
    
    JFrame frame = new JFrame();
    JPanel content = new JPanel(new GridBagLayout());
    
    frame.setContentPane(content);
    //frame.setSize(1000, 1000);
    frame.pack();
    frame.setVisible(true);
  }
}