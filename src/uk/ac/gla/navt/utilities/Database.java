package uk.ac.gla.navt.utilities;

/**
* A class to proivde database and graph creation
* 
* @Author Mehmet Kemal Deniz
* @Date 27/03/2014
*/

import java.awt.Color;
import java.io.IOException;
import java.util.Date;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraphDef;

public class Database {
    
    /**
     *
     * Default Constructor
     * 
     */
    public Database() {
    }
    
    /**
     *
     * A method to create bandwidth database
     * 
     * @throws java.io.IOException
     * @throws org.jrobin.core.RrdException 
     * 
     */
    public void prepareRRDB() throws IOException, RrdException {
        RrdDef rrdDef = new RrdDef("file.rrd", 5);
        rrdDef.addDatasource("download", "GAUGE", 2, Double.NaN, Double.NaN);
        rrdDef.addDatasource("upload", "GAUGE", 2, Double.NaN, Double.NaN);
        rrdDef.addArchive("AVERAGE", 0.5, 1, 600);
        RrdDb rrdDb = new RrdDb(rrdDef, RrdBackendFactory.getFactory("MEMORY"));
        rrdDb.close();
    }
    
    /**
     *
     * A method to create classification database
     * 
     * @throws java.io.IOException
     * @throws org.jrobin.core.RrdException 
     *
     */
    public void prepareRRDC() throws IOException, RrdException {
        RrdDef rrdDef = new RrdDef("classification.rrd");
        rrdDef.addDatasource("WEB", "COUNTER", 600, Double.NaN, Double.NaN);
        rrdDef.addDatasource("BULK", "COUNTER", 600, Double.NaN, Double.NaN);
        rrdDef.addDatasource("P2P", "COUNTER", 600, Double.NaN, Double.NaN);
        rrdDef.addDatasource("SERVICE", "COUNTER", 600, Double.NaN, Double.NaN);
        rrdDef.addDatasource("OTHER", "COUNTER", 600, Double.NaN, Double.NaN);
        rrdDef.addArchive("AVERAGE", 0.5, 4, 1000);
        RrdDb rrdDb = new RrdDb(rrdDef);
        rrdDb.close();
    }
    
    /**
     *
     * A method to create bandwidth graph
     * 
     * @return RRD graph definition
     * @throws org.jrobin.core.RrdException 
     *
     */
    public RrdGraphDef prepareBandwith() throws RrdException {
        RrdGraphDef gDef = new RrdGraphDef();
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 300000);
        gDef.setTimePeriod(startTime, endTime);
        gDef.setImageBorder(Color.WHITE, 0);
        gDef.setTitle("Bandiwth");
        gDef.setVerticalLabel("Speed");
        gDef.setTimeAxisLabel("Time");
        gDef.datasource("download", "file.rrd", "download", "AVERAGE", "MEMORY");
        gDef.area("download", Color.RED, "Download");
        gDef.gprint("download", "MAX", "max = @2 kb/s");
        gDef.gprint("download", "AVERAGE", "avg = @2 kb/s@l");
        gDef.datasource("upload", "file.rrd", "upload", "AVERAGE", "MEMORY");
        gDef.area("upload", Color.GREEN, "Upload");
        gDef.gprint("upload", "MAX", "max = @2 kb/s");
        gDef.gprint("upload", "AVERAGE", "avg = @2 kb/s@l");
        gDef.time("@l@lTime period: @t", "MMM dd, yyyy    HH:mm:ss", startTime);
        gDef.time("to  @t@c", "HH:mm:ss");
        return gDef;
    }
}
