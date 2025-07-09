package server;

import server.domain.payment.PaymentManager;
import server.domain.reservation.ReservationManager;
import server.domain.room.RoomManager;

public class ManagerFactory {
    private static final ManagerFactory instance = new ManagerFactory();
    private final ReservationManager reservationManager = new ReservationManager();

    private ManagerFactory() {}
    public static ManagerFactory getInstance() { return instance; }
    public ReservationManager getReservationManager() { return reservationManager; }
}