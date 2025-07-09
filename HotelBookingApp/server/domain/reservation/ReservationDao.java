package server.domain.reservation;

import java.util.List;

public interface ReservationDao {
    void save(Reservation reservation);
    Reservation findByNumber(String reservationNumber);
    List<Reservation> findAll();
    void delete(String reservationNumber);
}