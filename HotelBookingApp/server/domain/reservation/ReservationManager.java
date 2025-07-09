package server.domain.reservation;

import server.DaoFactory;
import server.domain.payment.Payment;
import server.domain.payment.PaymentManager;
import server.domain.room.RoomManager;
import server.util.DateUtil;
import java.util.Date;
import java.util.List;

public class ReservationManager {
    private ReservationDao reservationDao = DaoFactory.getInstance().getReservationDao();
    private RoomManager roomManager = new RoomManager();
    private PaymentManager paymentManager = new PaymentManager();

    // 予約処理
    public String createReservation(Date checkIn, Date checkOut, String chosenRoomNumber) throws Exception {
        // 念のため、選択された部屋がまだ利用可能か再チェック
        List<String> availableRooms = roomManager.getAvailableRooms(checkIn, checkOut);
        if (!availableRooms.contains(chosenRoomNumber)) {
            throw new Exception("Sorry, room " + chosenRoomNumber + " is no longer available.");
        }
        
        String reservationNumber = String.valueOf(System.currentTimeMillis());
        Reservation reservation = new Reservation(reservationNumber, chosenRoomNumber, checkIn, checkOut);
        reservationDao.save(reservation);

        System.out.println("[LOG] Reservation CREATED: " + reservationNumber + " for Room " + chosenRoomNumber + " from " + DateUtil.convertToString(checkIn) + " to " + DateUtil.convertToString(checkOut));
        return "SUCCESS:Reservation complete.;Reservation Number: " + reservationNumber + ";Room Number: " + chosenRoomNumber;
    }

    // チェックイン処理
    public String checkIn(String reservationNumber) {
        Reservation reservation = reservationDao.findByNumber(reservationNumber);
        if (reservation == null) {
            return "ERROR:Reservation not found.";
        }
        reservation.setStatus(Reservation.ReservationStatus.CHECKED_IN);
        reservationDao.save(reservation);
        paymentManager.createPayment(reservationNumber, reservation.getCheckInDate(), reservation.getCheckOutDate());
        System.out.println("[LOG] Guest CHECKED IN: Reservation " + reservationNumber + ", Room " + reservation.getRoomNumber());
        return "SUCCESS:Check-in complete.;Your room number is " + reservation.getRoomNumber();
    }

    // チェックアウト処理
    public String checkOut(String reservationNumber) {
        Reservation reservation = reservationDao.findByNumber(reservationNumber);
        if (reservation == null) {
            return "ERROR:Reservation not found.";
        }
        Payment payment = paymentManager.processPayment(reservationNumber);
        reservation.setStatus(Reservation.ReservationStatus.CHECKED_OUT);
        reservationDao.save(reservation);
        System.out.println("[LOG] Guest CHECKED OUT: Reservation " + reservationNumber + ", Room " + reservation.getRoomNumber() + ", Amount: " + payment.getAmount() + " JPY.");
        return "SUCCESS:Check-out complete.;The total amount is " + payment.getAmount() + " JPY.";
    }

    // キャンセル処理
    public String cancelReservation(String reservationNumber) {
        Reservation reservation = reservationDao.findByNumber(reservationNumber);
        if (reservation == null) {
            return "ERROR:Reservation not found.";
        }
        if (reservation.getStatus() != Reservation.ReservationStatus.CREATED) {
            return "ERROR:Cannot cancel a reservation that has already been checked in.";
        }
        reservation.setStatus(Reservation.ReservationStatus.CANCELED);
        reservationDao.save(reservation);
        System.out.println("[LOG] Reservation CANCELED: " + reservationNumber);
        return "SUCCESS:Reservation canceled.";
    }
}