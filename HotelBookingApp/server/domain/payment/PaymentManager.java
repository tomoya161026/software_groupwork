package server.domain.payment;

import java.util.Date;
import server.DaoFactory;
import server.util.DateUtil;

public class PaymentManager {
    private static final long RATE_PER_NIGHT = 10000;
    private PaymentDao paymentDao = DaoFactory.getInstance().getPaymentDao();

    public Payment createPayment(String reservationNumber, Date checkIn, Date checkOut) {
        long nights = DateUtil.calculateDaysBetween(checkIn, checkOut);
        long amount = nights * RATE_PER_NIGHT;
        Payment payment = new Payment(reservationNumber, amount);
        paymentDao.save(payment);
        return payment;
    }

    public Payment processPayment(String reservationNumber) {
        Payment payment = paymentDao.findByReservationNumber(reservationNumber);
        if (payment != null) {
            payment.setPaid(true);
            paymentDao.save(payment);
        }
        return payment;
    }
}