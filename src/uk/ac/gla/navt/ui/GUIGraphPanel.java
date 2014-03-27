package uk.ac.gla.navt.ui;
/**
 * A panel to provide graph panel.
 * 
 * @Author Mehmet Kemal Deniz
 * @Date 27/03/2014
 */

import java.awt.*;
import java.io.IOException;
import javax.swing.JPanel;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraph;

public class GUIGraphPanel extends JPanel {
	protected RrdGraph graph;
	protected int width, height;

	public GUIGraphPanel(RrdGraph graph) {
            this.graph = graph;
	}
        
        @Override
	public void paintComponent(Graphics g) {
            try {
                graph.specifyImageSize(true);
                graph.renderImage((Graphics2D)g, width, height);
            }
            catch (RrdException | IOException ex) {
            }
	}
        
	public void setGraphDimension(Dimension d) {
            width = d.width;
            height = d.height;
            repaint();
	}
}
