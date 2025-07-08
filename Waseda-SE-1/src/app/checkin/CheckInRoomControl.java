package app.checkin;

import app.AppException;
import app.ManagerFactory;
import domain.payment.PaymentException;
import domain.payment.PaymentManager;
import domain.reservation.ReservationException;
import domain.reservation.ReservationManager;
import domain.room.RoomException;
import domain.room.RoomManager;
import java.util.Date;

public class CheckInRoomControl {
	public String checkIn(String reservationNumber) throws AppException {
		try {
			ReservationManager reservationManager = ManagerFactory.getInstance().getReservationManager();
			Date stayingDate = reservationManager.consumeReservation(reservationNumber);

			RoomManager roomManager = ManagerFactory.getInstance().getRoomManager();
			String roomNumber = roomManager.assignCustomer(stayingDate);

			// ★★★ 正しい支払い作成処理 ★★★
			PaymentManager paymentManager = ManagerFactory.getInstance().getPaymentManager();
			paymentManager.createPayment(stayingDate, roomNumber);

			return roomNumber;
		} catch (ReservationException e) {
			throw new AppException("Failed to check-in", e);
		} catch (RoomException e) {
			throw new AppException("Failed to check-in", e);
		} catch (PaymentException e) {
			throw new AppException("Failed to check-in", e);
		}
	}
}