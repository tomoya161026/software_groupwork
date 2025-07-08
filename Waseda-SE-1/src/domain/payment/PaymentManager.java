package domain.payment;

import domain.DaoFactory;
import java.util.Date;

public class PaymentManager {
	private static final int RATE_PER_DAY = 8000;
	private PaymentDao getPaymentDao() {
		return DaoFactory.getInstance().getPaymentDao();
	}

	public void createPayment(Date stayingDate, String roomNumber) throws PaymentException {
		Payment payment = new Payment();
		payment.setStayingDate(stayingDate);
		payment.setRoomNumber(roomNumber);
		payment.setAmount(RATE_PER_DAY);
		payment.setStatus(Payment.PAYMENT_STATUS_CREATE);
		getPaymentDao().createPayment(payment);
	}

	public void consumePayment(Date stayingDate, String roomNumber) throws PaymentException {
		PaymentDao paymentDao = getPaymentDao();
		Payment payment = paymentDao.getPayment(stayingDate, roomNumber);
		if (payment == null) {
			throw new PaymentException(PaymentException.CODE_PAYMENT_NOT_FOUND);
		}
		if (payment.getStatus().equals(Payment.PAYMENT_STATUS_CONSUME)) {
			throw new PaymentException(PaymentException.CODE_PAYMENT_ALREADY_CONSUMED);
		}
		payment.setStatus(Payment.PAYMENT_STATUS_CONSUME);
		paymentDao.updatePayment(payment);
	}
	
	// ★★★ 料金通知用に支払い情報を取得するメソッド ★★★
	public Payment getPaymentForNotification(Date stayingDate, String roomNumber) throws PaymentException {
		Payment payment = getPaymentDao().getPayment(stayingDate, roomNumber);
		if (payment == null) {
			throw new PaymentException(PaymentException.CODE_PAYMENT_NOT_FOUND);
		}
		return payment;
	}
}