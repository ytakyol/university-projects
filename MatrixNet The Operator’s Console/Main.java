import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Main <input_file> <output_file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];
        
        
        int bufferSize = 65536; 

        Manager manager = new Manager();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile), bufferSize);
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile), bufferSize)) {

            String line;
            StringTokenizer st;
            String command;
            String out = null;

            while ((line = reader.readLine()) != null) {
                
                if (line.isEmpty()) continue; 

                st = new StringTokenizer(line);
                
                // Safety check for lines that might be whitespace only but not caught by isEmpty
                if (!st.hasMoreTokens()) continue;

                command = st.nextToken();

                // Optimization 3: Minimized logic inside the loop
                // Dispatch commands
                switch (command) {
                    case "spawn_host":
                        // Parsing logic remains; consider custom parsing only if profiling shows this is the bottleneck
                        out = manager.spawnHost(
                            st.nextToken(), 
                            Integer.parseInt(st.nextToken())
                        );
                        break;

                    case "link_backdoor":
                        out = manager.linkBackdoor(
                            st.nextToken(), st.nextToken(),
                            Integer.parseInt(st.nextToken()),
                            Integer.parseInt(st.nextToken()),
                            Integer.parseInt(st.nextToken())
                        );
                        break;

                    case "seal_backdoor":
                        out = manager.sealBackdoor(st.nextToken(), st.nextToken());
                        break;

                    case "trace_route":
                        out = manager.traceRoute(
                            st.nextToken(), st.nextToken(),
                            Integer.parseInt(st.nextToken()),
                            Integer.parseInt(st.nextToken())
                        );
                        break;

                    case "scan_connectivity":
                        out = manager.scanConnectivity();
                        break;

                    case "simulate_breach":
                        String param1 = st.nextToken();
                        // Optimization 4: Ternary operator for cleaner bytecode
                        out = st.hasMoreTokens() 
                            ? manager.simulateBreach(param1, st.nextToken()) 
                            : manager.simulateBreach(param1);
                        break;

                    case "oracle_report":
                        out = manager.oracleReport();
                        break;

                    default:
                        out = null;
                        break;
                }

                if (out != null) {
                    writer.write(out);
                    writer.newLine();
                }
            }
            // Flush is handled automatically by try-with-resources on close

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}