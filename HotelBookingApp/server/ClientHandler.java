package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import server.domain.reservation.ReservationManager;
import server.util.DateUtil;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ReservationManager reservationManager;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.reservationManager = ManagerFactory.getInstance().getReservationManager();
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
            System.out.println("クライアントとの通信エラー: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ignore
            }
            System.out.println("クライアントとの接続が終了しました: " + clientSocket.getRemoteSocketAddress());
        }
    }

    private String processRequest(String request) {
        String[] parts = request.split(":", 2);
        String command = parts[0];
        String[] args = (parts.length > 1) ? parts[1].split(",") : new String[0];

        try {
            switch (command) {
                case "RESERVE":
                    if (args.length < 2) return "ERROR:日付が不足しています。";
                    Date checkIn = DateUtil.convertToDate(args[0]);
                    Date checkOut = DateUtil.convertToDate(args[1]);
                    if (checkIn == null || checkOut == null || !checkOut.after(checkIn)) {
                        return "ERROR:日付の形式が正しくないか、チェックアウト日がチェックイン日以前です。";
                    }
                    return reservationManager.createReservation(checkIn, checkOut);
                
                case "CHECKIN":
                    if (args.length < 1) return "ERROR:予約番号が不足しています。";
                    return reservationManager.checkIn(args[0]);

                case "CHECKOUT":
                    if (args.length < 1) return "ERROR:予約番号が不足しています。";
                    return reservationManager.checkOut(args[0]);

                case "CANCEL":
                    if (args.length < 1) return "ERROR:予約番号が不足しています。";
                    return reservationManager.cancelReservation(args[0]);
                    
                default:
                    return "ERROR:不明なコマンドです。";
            }
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }
}