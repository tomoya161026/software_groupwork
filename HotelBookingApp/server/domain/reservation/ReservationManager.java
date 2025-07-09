package server.domain.reservation;

import server.DaoFactory;
import server.domain.payment.Payment;
import server.domain.payment.PaymentManager;
import server.domain.room.RoomManager;
import server.util.DateUtil;
import java.util.Date;

public class ReservationManager {
    private ReservationDao reservationDao = DaoFactory.getInstance().getReservationDao();
    private RoomManager roomManager = new RoomManager();
    private PaymentManager paymentManager = new PaymentManager();

    // 予約処理
    public String createReservation(Date checkIn, Date checkOut) throws Exception {
        String roomNumber = roomManager.findAvailableRoom(checkIn, checkOut);
        if (roomNumber == null) {
            throw new Exception("Sorry, no rooms are available for that period.");
        }
        String reservationNumber = String.valueOf(System.currentTimeMillis());
        Reservation reservation = new Reservation(reservationNumber, roomNumber, checkIn, checkOut);
        reservationDao.save(reservation);
        return "SUCCESS:Reservation complete.;Reservation Number: " + reservationNumber + ";Room Number: " + roomNumber;
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
        reservationDao.save(reservation); // or delete(reservationNumber)
        return "SUCCESS:Reservation canceled.";
    }
}