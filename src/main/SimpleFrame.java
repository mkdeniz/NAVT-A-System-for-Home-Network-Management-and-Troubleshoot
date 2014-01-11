package main;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

public class SimpleFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
        RrdGraph graph;
	private static final long serialVersionUID = 1L;
	JButton buttonGO;
	JComboBox comboBox;

	Logger logger = Logger.getLogger(this.getClass().toString());

	public SimpleFrame() throws IOException, RrdException {

		super();
		build();

	}

	private void build() throws IOException, RrdException {
		this.setTitle("Client");
		setSize(640, 480);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(buildPanel());
                this.setVisible(true);

	}

	private JPanel buildPanel() throws IOException, RrdException {
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                
                JTabbedPane tab = new JTabbedPane();
                
                buttonGO = new JButton("GO");
                panel.add(buttonGO, BorderLayout.SOUTH);
                buttonGO.addActionListener(this);
                
                comboBox = new JComboBox();
                panel.add(comboBox, BorderLayout.NORTH);
                
                createCharts();
                doGraph();
                //createDatasets();
                //chartPanel = new ChartPanel(chart);
                //chartPanel2 = new ChartPanel(chart2);
                JPanel j = new JPanel();
                j.add(graph.getChartPanel());
                j.add(new Button());
                tab.addTab("Resp. time", j);
                tab.addTab("Connection losts", graph.getChartPanel());
                
                panel.add(tab, BorderLayout.CENTER);
                return panel;
            
	}

        public void doGraph() throws RrdException, IOException {
        RrdGraphDef graphDef = new RrdGraphDef();
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 9990000);
        graphDef.setTimePeriod(startTime, endTime);
        graph = new RrdGraph(graphDef);
        graph.saveAsGIF("/home/mkdeniz/1.gif");
    }
        
	private void createCharts() {
	/*	chart = ChartFactory.createLineChart("Test Server",
				"Request Frequencies", "Response Time", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		chart2 = ChartFactory.createLineChart("Test Server",
				"Request Frequencies", "Connection Losts",
				dataset2, PlotOrientation.VERTICAL, true, true, false);

	}

	private void createDatasets() {
		dataset = new DefaultCategoryDataset();
		dataset2 = new DefaultCategoryDataset();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		/*Object source = e.getSource();

		if (source == buttonGO) {

			//TestServer ts = (TestServer) comboBox.getSelectedItem();
			buttonGO.setEnabled(false);
			//Thread t = new Thread(ts);

			//t.start();

			try {
				t.join();

				CSV csvReader = new CSV();
				Reader in = new FileReader(new File(CSVTools.FILENAME));
				dataset = csvReader.readCategoryDataset(in);
				in.close();
				in = new FileReader(new File(
						CSVTools.FILENAME_CONNECTION_LOSTS));
				dataset2 = csvReader.readCategoryDataset(in);
				in.close();

				createCharts();

				chartPanel.removeAll();
				chartPanel.setChart(chart);
				chartPanel2.removeAll();
				chartPanel2.setChart(chart2);

				chartPanel.repaint();
				chartPanel2.repaint();

				this.repaint();

			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			} catch (IOException e3) {
				e3.printStackTrace();
			} catch (InterruptedException e4) {
				e4.printStackTrace();
			}
			buttonGO.setEnabled(true);*/

		//}

	}

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}