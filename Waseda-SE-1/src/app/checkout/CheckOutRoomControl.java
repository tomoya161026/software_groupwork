package app.checkout;

import app.AppException;
import app.ManagerFactory;
import domain.payment.Payment;
import domain.payment.PaymentException;
import domain.payment.PaymentManager;
import domain.room.RoomException;
import domain.room.RoomManager;
import java.util.Date;

public class CheckOutRoomControl {
	public Payment checkOut(String roomNumber) throws AppException {
		try {
			RoomManager roomManager = ManagerFactory.getInstance().getRoomManager();
			Date stayingDate = roomManager.removeCustomer(roomNumber);

			PaymentManager paymentManager = ManagerFactory.getInstance().getPaymentManager();
			paymentManager.consumePayment(stayingDate, roomNumber);
			return paymentManager.getPaymentForNotification(stayingDate, roomNumber);
		} catch (RoomException e) {
			throw new AppException("Failed to check-out", e);
		} catch (PaymentException e) {
			throw new AppException("Failed to check-out", e);
		}
	}
}