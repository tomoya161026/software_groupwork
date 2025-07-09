package server.domain.payment;

public interface PaymentDao {
    void save(Payment payment);
    Payment findByReservationNumber(String reservationNumber);
}