package server.domain.payment;

import java.util.Date;
import server.DaoFactory;
import server.util.DateUtil;

public class PaymentManager {
    private static final long RATE_PER_NIGHT = 10000;
    private PaymentDao paymentDao = DaoFactory.getInstance().getPaymentDao();

    public Payment createPayment(String reservationNumber, Date checkIn, Date checkOut) {
        long nights = DateUtil.calculateDaysBetween(checkIn, checkOut);
        if (nights == 0) nights = 1; // 1泊の場合は1日として計算
        long amount = nights * RATE_PER_NIGHT;
        Payment payment = new Payment(reservationNumber, amount);
        paymentDao.save(payment);
        System.out.println("[LOG] Payment CREATED for Reservation " + reservationNumber + " with amount " + amount);
        return payment;
    }

    public Payment processPayment(String reservationNumber) {
        Payment payment = paymentDao.findByReservationNumber(reservationNumber);
        if (payment != null) {
            payment.setPaid(true);
            paymentDao.save(payment);
            System.out.println("[LOG] Payment PROCESSED for Reservation " + reservationNumber);
        }
        return payment;
    }
}