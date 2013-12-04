
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PingTest {
        private String ipTest;
        private String ipTest2;
        private String pingResult;
        private HashMap<String,Integer> map;

        public PingTest() {
            String ipTest = "8.8.8.8";
            String ipTest2 = "www.google.com";
            String pingResult = "";
            map = new HashMap<String,Integer>();
        }
        
        public Map<String,Integer> runTests() {
            String pingCmd = "ping -c 3 " + ipTest;
            String pingCmd2 = "ping -c 3 " + ipTest2;
            try {
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(pingCmd);

                BufferedReader in = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    pingResult += inputLine;
                }
                in.close();

                } catch (IOException e) {
                    System.out.println(e);
                }
            return this.map;

        }
}