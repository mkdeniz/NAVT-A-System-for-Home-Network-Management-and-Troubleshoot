import java.io.*;
import java.util.*;

public class dnsTest {
    
    public static void runTest(String h){
        try {
            List<String> commands = new ArrayList<>();
            commands.add("/bin/ping");
            commands.add("-c");
            commands.add("5");
            commands.add(h);
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process process;
            process = pb.start();
       
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String s;
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null)
                System.out.println(s);
            
        } catch (IOException ex) {
                System.out.print(ex);
        }
    }
}