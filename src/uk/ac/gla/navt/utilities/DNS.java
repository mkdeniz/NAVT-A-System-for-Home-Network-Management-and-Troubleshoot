package uk.ac.gla.navt.utilities;

/**
* A program static class to provide UNIX ping tool
* 
* @Author Mehmet Kemal Deniz
* @Date 27/03/2014
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DNS {
    
    static Process process;
    
    /**
     * 
     * Default Constructor
     * 
     * @param h address of host
     * @param t number of requests
     * 
     */
    public static String runTest(String h, String t){
        try {
            //Create commnad according to the host and the number of tries
            ArrayList<String> commands = new ArrayList<>();
            commands.add("/bin/ping");
            commands.add("-c");
            commands.add(t);
            commands.add(h);
            ProcessBuilder pb = new ProcessBuilder(commands);
            
            //Start Proccess and proccess utput
            process = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            String results;
            while ((s = stdInput.readLine()) != null) {
                if (s.contains("ping statistics ---")){
                    results = stdInput.readLine();
                    return results;
                }
                
            }
        } catch (IOException ex) {
                System.out.print(ex);
        }   
        return null;
    }
}
