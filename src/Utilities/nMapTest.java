package Utilities;

import java.io.*;
import java.util.*;

public class nMapTest {
    
    static Process process;
    
    public static String runTest(String h){
    String cmd = "nmap -NP " + h;
    Runtime run = Runtime.getRuntime();
    String result = "ARP Cache: \n";
 
    try {
        Process proc = run.exec(cmd);
        proc.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        while ((line = buf.readLine()) != null) {
            result += line + "\n";
        }
    } catch (IOException | InterruptedException ex) {
        System.out.println(ex.getMessage());
    }
 
    return (result);
}
    }
