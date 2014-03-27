/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.gla.navt.utilities;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraphDef;

/**
 *
 * @author root
 */
public class Database {
    
    protected String file;
    
    public Database(String f) {
        file = f;
    }
    
    public void prepareRRDB() throws IOException, RrdException {
        RrdDef rrdDef = new RrdDef(this.file, 5);
        rrdDef.addDatasource("a", "GAUGE", 2, Double.NaN, Double.NaN);
        rrdDef.addDatasource("b", "GAUGE", 2, Double.NaN, Double.NaN);
        rrdDef.addArchive("AVERAGE", 0.5, 1, 600);
        RrdDb rrdDb = new RrdDb(rrdDef, RrdBackendFactory.getFactory("MEMORY"));
        rrdDb.close();
    }
    
    public void prepareRRDC() throws IOException, RrdException {
        RrdDef rrdDef = new RrdDef("classification.rrd");
        rrdDef.addDatasource("WEB", "GAUGE", 600, Double.NaN, Double.NaN);
        rrdDef.addDatasource("BULK", "GAUGE", 600, Double.NaN, Double.NaN);
        rrdDef.addDatasource("P2P", "GAUGE", 600, Double.NaN, Double.NaN);
        rrdDef.addDatasource("SERVICE", "GAUGE", 600, Double.NaN, Double.NaN);
        rrdDef.addArchive("AVERAGE", 0.5, 1, 1000);
        RrdDb rrdDb = new RrdDb(rrdDef);
        rrdDb.close();
    }
    
    public void test(int N) throws IOException, RrdException, InterruptedException {
        RrdDb rrdDb = new RrdDb("classification.rrd");
        Sample sample = rrdDb.createSample();
        for (int i = 1; i<N; i++){
            sample.setTime(Util.getTimestamp());
                    sample.setValue("WEB", i);
                    if (N % 2 == 0){
                    sample.setValue("BULK", i);}
                    if (N > 30){
                    sample.setValue("P2P", i);}
                    sample.setValue("SERVICE", i*2);
                    sample.update();
                    Thread.sleep(1200);
        }
        rrdDb.close();
    }
    
    public RrdGraphDef prepareBandwith() throws RrdException {
        RrdGraphDef gDef = new RrdGraphDef();
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 300000);
        gDef.setTimePeriod(startTime, endTime);
        gDef.setImageBorder(Color.WHITE, 0);
        gDef.setTitle("Bandiwth");
        gDef.setVerticalLabel("Speed");
        gDef.setTimeAxisLabel("Time");
        gDef.datasource("a", file, "a", "AVERAGE", "MEMORY");
        gDef.area("a", Color.RED, "Download");
        gDef.gprint("a", "MAX", "max = @2 kb/s");
        gDef.gprint("a", "AVERAGE", "avg = @2 kb/s@l");
        gDef.datasource("b", file, "b", "AVERAGE", "MEMORY");
        gDef.area("b", Color.GREEN, "Upload");
        gDef.gprint("b", "MAX", "max = @2 kb/s");
        gDef.gprint("b", "AVERAGE", "avg = @2 kb/s@l");
        gDef.time("@l@lTime period: @t", "MMM dd, yyyy    HH:mm:ss", startTime);
        gDef.time("to  @t@c", "HH:mm:ss");
        return gDef;
    }
}
