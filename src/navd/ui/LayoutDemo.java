import java.awt.Color;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class LayoutDemo extends JFrame
{

	public LayoutDemo()
	{
		this.setTitle("LayoutDemo Test");
		this.setSize(1280 , 960);
		this.setLayout(null);
		BilesenleriEkle(this.getContentPane());
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void BilesenleriEkle(Container pencere)
	{
		JPanel solPanel = new JPanel();
		JPanel sagPanel = new JPanel();
		JPanel ustPanel = new JPanel();
		JPanel altPanel = new JPanel();
		JPanel ortaPanel = new JPanel();
		
		pencere.add(solPanel);
		solPanel.setBackground(Color.BLUE);
		solPanel.setBounds(20, 20, 180, 820);
		
		pencere.add(ustPanel);
		ustPanel.setBackground(Color.RED);
		ustPanel.setBounds(210, 20, 800, 150);
		
		pencere.add(sagPanel);
		sagPanel.setBackground(Color.GREEN);
		sagPanel.setBounds(1020, 20, 180, 820);

		pencere.add(ortaPanel);
		ortaPanel.setBackground(Color.YELLOW);
		ortaPanel.setBorder(new TitledBorder("Orta Panel"));
		ortaPanel.setLayout(null);
		ortaPanel.setBounds(210, 180, 800, 500);
		
		pencere.add(altPanel);
		altPanel.setBackground(Color.CYAN);
		altPanel.setBounds(210, 690, 800, 150);
		
		JButton buton1 = new JButton("Buton1");
		ortaPanel.add(buton1);
		buton1.setBounds(20 ,30 ,150 ,40);
	}
	
	public static void main(String[] args)
	{
		new LayoutDemo();
	}

}