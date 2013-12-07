package navd.ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;

public class SplashScreen extends JFrame {
    private static final String rrd = "file.rrd";
    private static JProgressBar pbar;
    Thread t=null;
    
    private void prepareRrd() throws IOException, RrdException {
        RrdDef rrdDef = new RrdDef(rrd, 5);
        rrdDef.addDatasource("a", "GAUGE", 600, Double.NaN, Double.NaN);
        rrdDef.addArchive("AVERAGE", 0.5, 1, 1000);
        //rrdDef.addDatasource("speed", "COUNTER", 600, Double.NaN, Double.NaN);
        //rrdDef.addArchive("AVERAGE", 0.5, 24, 775);
        RrdDb rrdDb = new RrdDb(rrdDef, RrdBackendFactory.getFactory("MEMORY"));
        rrdDb.close();
    }
    
    public SplashScreen() throws IOException, RrdException
    {
        super("Splash");
        this.prepareRrd();
        setSize(550,549);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setLayout(null);
        pbar=new JProgressBar();
        pbar.setMinimum(0);
        pbar.setMaximum(100);
        pbar.setStringPainted(true);
        add(pbar);
        pbar.setPreferredSize(new Dimension(549,50));
        pbar.setBounds(1,501,549,43);

        Thread t=new Thread(){
            public void run()
            {
                int i=0;
                while(i<=100)
                {
                    pbar.setValue(i);
                    try {
                        sleep(45);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    i++;
                }
            }
        };
        t.start();
        
    }

}