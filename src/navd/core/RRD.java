package navd.core;

import java.io.IOException;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
public class RRD {
    private RrdDb rrdDb;
    private Sample sam;
    
    public RRD() throws RrdException, IOException{
        RrdDef rrdDef = new RrdDef("test");
        rrdDef.addDatasource("speed", "COUNTER", 600, Double.NaN, Double.NaN);
        rrdDef.addArchive("AVERAGE", 0.5, 24, 775);
        this.rrdDb = new RrdDb(rrdDef);
        this.sam = this.rrdDb.createSample();
    }
    
    public void addDate(Sample sample, double d, String s) throws IOException, RrdException{
        long time = Util.getTimestamp();
        sample.setTime(time);
        sample.setValue(s, d);
        sample.update();
    }
    
    public RrdDb getRrdDB() {
        return this.rrdDb;
    }
    
    public Sample getSample() {
        return this.sam;
    }
    
}
