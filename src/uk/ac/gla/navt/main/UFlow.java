package uk.ac.gla.navt.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.Timer;

public class UFlow extends Flow{
    Timer timer;
    
    public UFlow(String s, String d, int sp, int dp, long t){
        super(s,d,sp,dp,t);
        timer = new Timer(10000, updateRRD);
        timer.start();
    }
    
    ActionListener updateRRD = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                End();
                destroy();
            }
        };
    
    public void destroy(){
        timer.stop();
        
    }
    
    @Override
    public double meanTime() {
        double sum = 0;
        int l = arr.size();
        for (int i = 0; i < l; i++) {
            double v = arr.get(i);
            sum = sum + v;
        }
        return (sum/(l));
            
    }
    
    @Override
    public double meanPacket() {
        double sum = 0;
        int l = arr2.size();
        for (int i = 0; i < l; i++) {
            double v = arr2.get(i);
            sum = sum + v;
        }
        return (sum/(l));
            
    }
    
    @Override
    public double varTime() {
        double sum = 0;
        double v;
        int l = arr.size();
        double mean = this.meanTime();
        for (int i = 0; i < l; i++){
            v = arr.get(i);
            sum = sum + Math.pow((mean-v), 2.0);
        }
        return Math.sqrt((sum/(l)))*0.001;
    }
    
    @Override
    public double varPacket() {
        double m = 0;
        double v;
        int l = arr2.size();
        double mean = this.meanPacket();
        for (int i = 0; i < l; i++){
            v = arr2.get(i);
            m = m + Math.pow((mean-v), 2.0);
        }
        return (m/(l))/1024;
    }
    
    @Override
    public void updateByte(long b) {
        if (this.open) {
        timer.restart();
            this.noPack++;
            Date tmp = new Date();
            double intV = tmp.getTime() - this.end.getTime();
            this.end = tmp;
            arr.add(intV);
            double a = b;
            arr2.add(a);
            this.traffic = this.traffic + b;
        }
    } 
}
