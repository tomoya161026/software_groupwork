package server.domain.room;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import server.DaoFactory;
import server.domain.reservation.Reservation;
import server.domain.reservation.ReservationDao;

public class RoomManager {
    private RoomDao roomDao = DaoFactory.getInstance().getRoomDao();
    private ReservationDao reservationDao = DaoFactory.getInstance().getReservationDao();

    public List<String> getAvailableRooms(Date checkIn, Date checkOut) {
        List<Room> allRooms = roomDao.findAll();
        List<Reservation> allReservations = reservationDao.findAll();

        // Find all rooms that are booked during the requested period
        List<String> bookedRoomNumbers = allReservations.stream()
                .filter(r -> r.getStatus() != Reservation.ReservationStatus.CANCELED && r.getStatus() != Reservation.ReservationStatus.CHECKED_OUT)
                .filter(r -> checkIn.before(r.getCheckOutDate()) && checkOut.after(r.getCheckInDate()))
                .map(Reservation::getRoomNumber)
                .distinct()
                .collect(Collectors.toList());
        
        System.out.println("[LOG] Checking availability for " + checkIn + " - " + checkOut + ". Booked rooms: " + bookedRoomNumbers);

        // Filter out the booked rooms from the list of all rooms and return the result
        return allRooms.stream()
                .map(Room::getRoomNumber)
                .filter(roomNumber -> !bookedRoomNumbers.contains(roomNumber))
                .sorted() // ★★★ この一行を追加して、部屋番号をソートします ★★★
                .collect(Collectors.toList());
    }
}