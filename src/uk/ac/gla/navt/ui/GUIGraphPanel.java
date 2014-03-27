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

    /**
     * Default Constructor
     * 
     * @param g RRD graph
     */
    public GUIGraphPanel(RrdGraph g) {
        graph = g;
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

    /**
     * A method to set the size of the graph as according to 
     * the size of main frame
     * 
     * @param  d size of main frame
     */
    public void setGraphDimension(Dimension d) {
        width = d.width;
        height = d.height;
        repaint();
    }
    }
