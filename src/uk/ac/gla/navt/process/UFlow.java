/**
 * A UDP Flow object class which extend Flow Class to allow flow creation 
 * and statistical value calculation such as variance and mean of the flow 
 * features.
 * 
 * @Author Mehmet Kemal Deniz
 * @Date 27/03/2014

*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.Timer;

public class UFlow extends Flow{
    //Timer to end UDP flows
    Timer timer;
    
    public UFlow(String s, String d, int sp, int dp, long t){
        super(s, d, sp, dp, t);//Same as Flow
        timer = new Timer(10000, close);
        timer.start();
    }
 
    /**
     * Timer method to end flow
     * 
     */
    ActionListener close = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            End();
            destroy();
        }
    };
    
    /**
     * A Method to remove Timer
     * 
     */
    public void destroy(){
        timer.stop();
    }
    
    @Override
    public double meanTime() {
        double sum = 0;
        int l = arrivalTimes.size();
        for (int i = 0; i < l; i++) {
            sum = sum + arrivalTimes.get(i);
        }
        return (sum/(l));
            
    }
    
    @Override
    public double meanPacket() {
        double sum = 0;
        int l = packetSizes.size();
        for (int i = 0; i < l; i++) {
            sum = sum + packetSizes.get(i);
        }
        return (sum/(l));
            
    }
    
    @Override
    public double varTime() {
        double sum = 0;
        int l = arrivalTimes.size();
        double mean = this.meanTime();
        for (int i = 0; i < l; i++){
            sum = sum + Math.pow((mean - arrivalTimes.get(i)), 2.0);
        }
        return Math.sqrt((sum/(l)))*0.001;
    }
    
    @Override
    public double varPacket() {
        double m = 0;
        double v;
        int l = packetSizes.size();
        double mean = this.meanPacket();
        for (int i = 0; i < l; i++){
            m = m + Math.pow((mean-packetSizes.get(i)), 2.0);
        }
        return (m/(l))/1024;
    }
    
    @Override
    public void updateByte(long b) {
        if (this.open) {
            timer.restart();//Update timer
            Date tmp = new Date();
            double intV = tmp.getTime() - this.end.getTime();
            end = tmp;
            arrivalTimes.add(intV);
            packetSizes.add((double)b);
            traffic = this.traffic + b;
        }
    } 
}


