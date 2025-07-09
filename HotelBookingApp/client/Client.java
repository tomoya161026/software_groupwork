package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Welcome to the Hotel Booking System.");

            while (true) {
                printMenu();
                String userInput = stdIn.readLine();
                if (userInput == null || "9".equals(userInput)) {
                    System.out.println("Thank you for using our system.");
                    break;
                }
                handleMenu(userInput, out, in, stdIn);
            }
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Make a Reservation");
        System.out.println("2. Check In");
        System.out.println("3. Check Out");
        System.out.println("4. Cancel Reservation");
        System.out.println("9. Exit");
        System.out.print("> ");
    }

    private static void handleMenu(String choice, PrintWriter out, BufferedReader in, BufferedReader stdIn) throws IOException {
        switch (choice) {
            case "1":
                handleReservation(out, in, stdIn);
                break;
            case "2":
                handleCheckIn(out, in, stdIn);
                break;
            case "3":
                handleCheckOut(out, in, stdIn);
                break;
            case "4":
                handleCancellation(out, in, stdIn);
                break;
            default:
                System.out.println("Invalid selection. Please try again.");
                break;
        }
    }

    private static void handleReservation(PrintWriter out, BufferedReader in, BufferedReader stdIn) throws IOException {
        System.out.print("Check-in Date (yyyy/MM/dd): ");
        String checkIn = stdIn.readLine();
        System.out.print("Check-out Date (yyyy/MM/dd): ");
        String checkOut = stdIn.readLine();

        out.println("RESERVE:" + checkIn + "," + checkOut);
        System.out.println(in.readLine().replace(";", "\n"));
    }

    private static void handleCheckIn(PrintWriter out, BufferedReader in, BufferedReader stdIn) throws IOException {
        System.out.print("Reservation Number: ");
        String reservationNumber = stdIn.readLine();

        out.println("CHECKIN:" + reservationNumber);
        System.out.println(in.readLine().replace(";", "\n"));
    }

    private static void handleCheckOut(PrintWriter out, BufferedReader in, BufferedReader stdIn) throws IOException {
        System.out.print("Reservation Number: ");
        String reservationNumber = stdIn.readLine();

        out.println("CHECKOUT:" + reservationNumber);
        System.out.println(in.readLine().replace(";", "\n"));
    }

    private static void handleCancellation(PrintWriter out, BufferedReader in, BufferedReader stdIn) throws IOException {
        System.out.print("Reservation Number: ");
        String reservationNumber = stdIn.readLine();

        out.println("CANCEL:" + reservationNumber);
        System.out.println(in.readLine().replace(";", "\n"));
    }
}