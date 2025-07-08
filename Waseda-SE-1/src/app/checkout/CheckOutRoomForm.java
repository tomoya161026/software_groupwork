package app.checkout;

import app.AppException;
import domain.payment.Payment;

public class CheckOutRoomForm {
	private CheckOutRoomControl checkOutRoomControl = new CheckOutRoomControl();
	private String roomNumber;

	public Payment checkOut() throws AppException {
		return checkOutRoomControl.checkOut(roomNumber);
	}
	public String getRoomNumber() { return roomNumber; }
	public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
}