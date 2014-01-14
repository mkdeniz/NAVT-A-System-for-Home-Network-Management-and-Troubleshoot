/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Utilities;

import java.io.IOException;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;

/**
 *
 * @author root
 */
public class RRD {
    
    public static void prepareRRD() throws RrdException, IOException{
            RrdDef def = new RrdDef("/home/mkdeniz/tcp.rdd", 2);
            def.addDatasource("itcp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("ihttp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("iicmp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("iudp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("idns", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("otcp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("ohttp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("oicmp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("oudp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("odns", "GAUGE", 600, 0, Double.NaN);
            def.addArchive("AVERAGE", 0.5, 1, 600);
            def.addArchive("AVERAGE", 0.5, 6, 700);
            def.addArchive("AVERAGE", 0.5, 24, 797);
            def.addArchive("AVERAGE", 0.5, 288, 775);
            def.addArchive("MAX", 0.5, 1, 600);
            def.addArchive("MAX", 0.5, 6, 700);
            def.addArchive("MAX", 0.5, 24, 797);
            def.addArchive("MAX", 0.5, 288, 775);
            RrdDb rrd = new RrdDb(def);
            rrd.close();
        }
    
    public static void prepareRRD2() throws RrdException, IOException{
            RrdDef rrdDef = new RrdDef("/home/mkdeniz/download.rdd", 60);
            rrdDef.addDatasource("download", "GAUGE", 300, 0, Double.NaN);
            rrdDef.addDatasource("upload", "GAUGE", 300, 0, Double.NaN);
            rrdDef.addArchive("AVERAGE", 0.5, 1, 4000);
            rrdDef.addArchive("AVERAGE", 0.5, 6, 4000);
            rrdDef.addArchive("AVERAGE", 0.5, 24, 4000);
            rrdDef.addArchive("AVERAGE", 0.5, 288, 4000);
            RrdDb rrd = new RrdDb(rrdDef);
            rrd.close();
        }
}
