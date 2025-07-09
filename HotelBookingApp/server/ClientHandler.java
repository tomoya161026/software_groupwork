package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import server.domain.reservation.ReservationManager;
import server.domain.room.RoomManager;
import server.util.DateUtil;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ReservationManager reservationManager;
    private final RoomManager roomManager;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.reservationManager = new ReservationManager();
        this.roomManager = new RoomManager();
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = processRequest(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.out.println("[LOG] Communication error with client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {}
            System.out.println("[LOG] Client connection closed: " + clientSocket.getRemoteSocketAddress());
        }
    }

    private String processRequest(String request) {
        String[] parts = request.split(":", 2);
        String command = parts[0];
        String[] args = (parts.length > 1) ? parts[1].split(",") : new String[0];

        try {
            switch (command) {
                case "GET_AVAILABLE_ROOMS":
                    if (args.length < 2) return "ERROR:Missing dates.";
                    Date checkIn = DateUtil.convertToDate(args[0]);
                    Date checkOut = DateUtil.convertToDate(args[1]);
                    if (checkIn == null || checkOut == null || !checkOut.after(checkIn)) {
                        return "ERROR:Invalid date format, or check-out date is not after check-in date.";
                    }
                    List<String> rooms = roomManager.getAvailableRooms(checkIn, checkOut);
                    return "SUCCESS:" + String.join(",", rooms);

                case "RESERVE":
                    if (args.length < 3) return "ERROR:Missing dates or room number.";
                    Date rCheckIn = DateUtil.convertToDate(args[0]);
                    Date rCheckOut = DateUtil.convertToDate(args[1]);
                    String roomNumber = args[2];
                     if (rCheckIn == null || rCheckOut == null || !rCheckOut.after(rCheckIn)) {
                        return "ERROR:Invalid date format, or check-out date is not after check-in date.";
                    }
                    return reservationManager.createReservation(rCheckIn, rCheckOut, roomNumber);

                case "CHECKIN":
                    if (args.length < 1) return "ERROR:Missing reservation number.";
                    return reservationManager.checkIn(args[0]);

                case "CHECKOUT":
                    if (args.length < 1) return "ERROR:Missing reservation number.";
                    return reservationManager.checkOut(args[0]);

                case "CANCEL":
                    if (args.length < 1) return "ERROR:Missing reservation number.";
                    return reservationManager.cancelReservation(args[0]);

                default:
                    return "ERROR:Unknown command.";
            }
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }
}