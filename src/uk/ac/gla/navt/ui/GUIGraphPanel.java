package uk.ac.gla.navt.ui;

import javax.swing.*;
import java.awt.*;
import org.jrobin.graph.RrdGraph;

public class GUIGraphPanel extends JPanel {
	private RrdGraph graph;
	private int width, height;

	public GUIGraphPanel(RrdGraph graph) {
            this.graph = graph;
	}

	
	public void paintComponent(Graphics g) {
            try {
                graph.specifyImageSize(true);
                graph.renderImage((Graphics2D)g, width, height);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
	}

	
	public void setGraphDimension(Dimension d) {
            width = d.width;
            height = d.height;
            repaint();
	}
}
