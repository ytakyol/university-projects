/**
 * Main entry point for GigMatch Pro platform.
 */

import java.io.*;
import java.util.Locale;

public class Main {
	
	static Platform platform = new Platform();
	

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 2) {
            System.err.println("Usage: java Main <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                processCommand(line, writer);
            }

        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    static String customerID;
	static String freelancerID;
	static String serviceName;
	static int basePrice;
	static int T;
	static int C;
	static int R;
	static int E;
	static int A;
	static int rating;
	static String newService;
	static int newPrice;
    
    private static void processCommand(String command, BufferedWriter writer)
            throws IOException {
    	
    	

        String[] parts = command.split("\\s+");
        String operation = parts[0];

        try {
            String result = "";

            switch (operation) {
                case "register_customer":
                    // Format: register_customer customerID
                	customerID = parts[1];
                    result = platform.registerCustomer(customerID);
                    break;

                case "register_freelancer":
                    // Format: register_freelancer freelancerID serviceName basePrice T C R E A
                	freelancerID = parts[1];
                    serviceName = parts[2];
                    basePrice = Integer.parseInt(parts[3]);
                    T = Integer.parseInt(parts[4]);
                    C = Integer.parseInt(parts[5]);
                    R = Integer.parseInt(parts[6]);
                    E = Integer.parseInt(parts[7]);
                    A = Integer.parseInt(parts[8]);

                    result = platform.registerFreelancer(freelancerID, serviceName, basePrice, T, C, R, E, A);
                    break;

                case "request_job":
                    // Format: request_job customerID serviceName topK
                	customerID = parts[1];
                    serviceName = parts[2];
                    int topK = Integer.parseInt(parts[3]);

                    result = platform.requestJob(customerID, serviceName, topK);
                    break;

                case "employ_freelancer":
                    // Format: employ_freelancer customerID freelancerID
                	customerID = parts[1];
                    freelancerID = parts[2];

                    result = platform.employFreelancer(customerID, freelancerID);
                    break;

                case "complete_and_rate":
                    // Format: complete_and_rate freelancerID rating
                	freelancerID = parts[1];
                    rating = Integer.parseInt(parts[2]);

                    result = platform.completeAndRate(freelancerID, rating);
                    break;

                case "cancel_by_freelancer":
                    // Format: cancel_by_freelancer freelancerID
                	freelancerID = parts[1];

                    result = platform.cancelByFreelancer(freelancerID);
                    break;

                case "cancel_by_customer":
                    // Format: cancel_by_customer customerID freelancerID
                	customerID = parts[1];
                    freelancerID = parts[2];

                    result = platform.cancelByCustomer(customerID, freelancerID);
                    break;

                case "blacklist":
                    // Format: blacklist customerID freelancerID
                	customerID = parts[1];
                    freelancerID = parts[2];

                    result = platform.blacklist(customerID, freelancerID);
                    break;

                case "unblacklist":
                    // Format: unblacklist customerID freelancerID
                	customerID = parts[1];
                    freelancerID = parts[2];

                    result = platform.unblacklist(customerID, freelancerID);
                    break;

                case "change_service":
                    // Format: change_service freelancerID newService newPrice
                	freelancerID = parts[1];
                    newService = parts[2];
                    newPrice = Integer.parseInt(parts[3]);

                    result = platform.changeService(freelancerID, newService, newPrice);
                    break;

                case "simulate_month":
                    // Format: simulate_month
                	result = platform.simulateMonth();
                    break;

                case "query_freelancer":
                    // Format: query_freelancer freelancerID
                	freelancerID = parts[1];

                    result = platform.queryFreelancer(freelancerID);
                    break;

                case "query_customer":
                    // Format: query_customer customerID
                	customerID = parts[1];

                    result = platform.queryCustomer(customerID);
                    break;

                case "update_skill":
                    // Format: update_skill freelancerID T C R E A
                	
                	freelancerID = parts[1];
                    T = Integer.parseInt(parts[2]);
                    C = Integer.parseInt(parts[3]);
                    R = Integer.parseInt(parts[4]);
                    E = Integer.parseInt(parts[5]);
                    A = Integer.parseInt(parts[6]);

                    result = platform.updateSkill(freelancerID, T, C, R, E, A);
                    break;

                default:
                    result = "Unknown command: " + operation;
            }

            writer.write(result);
            writer.newLine();

        } catch (Exception e) {
            writer.write("Error processing command: " + command);
            writer.newLine();
        }
    }
}

