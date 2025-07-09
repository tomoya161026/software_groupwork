package server;

import server.domain.payment.InMemoryPaymentDao;
import server.domain.payment.PaymentDao;
import server.domain.reservation.InMemoryReservationDao;
import server.domain.reservation.ReservationDao;
import server.domain.room.InMemoryRoomDao;
import server.domain.room.RoomDao;

public class DaoFactory {
    private static final DaoFactory instance = new DaoFactory();
    private final RoomDao roomDao = new InMemoryRoomDao();
    private final ReservationDao reservationDao = new InMemoryReservationDao();
    private final PaymentDao paymentDao = new InMemoryPaymentDao();

    private DaoFactory() {}
    public static DaoFactory getInstance() { return instance; }
    public RoomDao getRoomDao() { return roomDao; }
    public ReservationDao getReservationDao() { return reservationDao; }
    public PaymentDao getPaymentDao() { return paymentDao; }
}