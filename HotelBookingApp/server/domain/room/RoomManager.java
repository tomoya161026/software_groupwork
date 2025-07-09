package server.domain.room;

import java.util.Date;
import java.util.List;
import server.DaoFactory;
import server.domain.reservation.Reservation;

public class RoomManager {
    private RoomDao roomDao = DaoFactory.getInstance().getRoomDao();
    private server.domain.reservation.ReservationDao reservationDao = DaoFactory.getInstance().getReservationDao();

    public String findAvailableRoom(Date checkIn, Date checkOut) {
        List<Room> allRooms = roomDao.findAll();
        List<Reservation> allReservations = reservationDao.findAll();

        for (Room room : allRooms) {
            boolean isBooked = false;
            for (Reservation reservation : allReservations) {
                if (reservation.getRoomNumber().equals(room.getRoomNumber())) {
                    // 予約期間が重複しているかチェック
                    if (checkIn.before(reservation.getCheckOutDate()) && checkOut.after(reservation.getCheckInDate())) {
                        isBooked = true;
                        break;
                    }
                }
            }
            if (!isBooked) {
                return room.getRoomNumber(); // 利用可能な最初の部屋を返す
            }
        }
        return null; // 空室なし
    }
}