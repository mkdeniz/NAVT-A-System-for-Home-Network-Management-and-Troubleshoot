import java.io.*;
import java.util.*;

public class dnsTest {
    
    static Process process;
    
    public static String runTest(String h){
        try {
            List<String> commands = new ArrayList<>();
            commands.add("/bin/ping");
            commands.add("-c");
            commands.add("3");
            commands.add(h);
            ProcessBuilder pb = new ProcessBuilder(commands);
            
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