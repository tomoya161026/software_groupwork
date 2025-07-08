package app.cui;

import app.AppException;
import app.cancel.CancelReservationForm;
import app.checkin.CheckInRoomForm;
import app.checkout.CheckOutRoomForm;
import app.reservation.ReserveRoomForm;
import domain.payment.Payment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import util.DateUtil;

public class CUI {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	private void execute() throws IOException {
		try {
			while (true) {
				System.out.println("\nMenu");
				System.out.println("1: Reservation");
				System.out.println("2: Check-in");
				System.out.println("3: Check-out");
				System.out.println("4: Cancel Reservation");
				System.out.println("9: End");
				System.out.print("> ");
				int selectMenu;
				try {
					selectMenu = Integer.parseInt(reader.readLine());
				} catch (NumberFormatException e) {
					selectMenu = 0;
				}
				if (selectMenu == 9) break;

				switch (selectMenu) {
					case 1: reserveRoom(); break;
					case 2: checkInRoom(); break;
					case 3: checkOutRoom(); break;
					case 4: cancelReservation(); break;
					default: System.out.println("Invalid menu selection."); break;
				}
			}
			System.out.println("Ended");
		} catch (AppException e) {
			System.err.println("Error");
			System.err.println(e.getFormattedDetailMessages(LINE_SEPARATOR));
		} finally {
			reader.close();
		}
	}

	private void reserveRoom() throws IOException, AppException {
		System.out.println("Input arrival date in the form of yyyy/MM/dd");
		System.out.print("> ");
		String dateStr = reader.readLine();
		Date stayingDate = DateUtil.convertToDate(dateStr);
		if (stayingDate == null) {
			System.out.println("Invalid input");
			return;
		}
		ReserveRoomForm reserveRoomForm = new ReserveRoomForm();
		reserveRoomForm.setStayingDate(stayingDate);
		String reservationNumber = reserveRoomForm.submitReservation();
		System.out.println("Reservation has been completed.");
		System.out.println("Arrival (staying) date is " + DateUtil.convertToString(stayingDate) + ".");
		System.out.println("Reservation number is " + reservationNumber + ".");
	}

	private void checkInRoom() throws IOException, AppException {
		System.out.println("Input reservation number");
		System.out.print("> ");
		String reservationNumber = reader.readLine();
		if (reservationNumber == null || reservationNumber.isEmpty()) {
			System.out.println("Invalid reservation number");
			return;
		}
		CheckInRoomForm checkInRoomForm = new CheckInRoomForm();
		checkInRoomForm.setReservationNumber(reservationNumber);
		String roomNumber = checkInRoomForm.checkIn();
		System.out.println("Check-in has been completed.");
		System.out.println("Room number is " + roomNumber + ".");
	}

	private void checkOutRoom() throws IOException, AppException {
		System.out.println("Input room number");
		System.out.print("> ");
		String roomNumber = reader.readLine();
		if (roomNumber == null || roomNumber.isEmpty()) {
			System.out.println("Invalid room number");
			return;
		}
		CheckOutRoomForm checkoutRoomForm = new CheckOutRoomForm();
		checkoutRoomForm.setRoomNumber(roomNumber);
		Payment payment = checkoutRoomForm.checkOut();
		System.out.println("Check-out has been completed.");
		System.out.println("Total amount: " + payment.getAmount() + " yen.");
	}

	private void cancelReservation() throws IOException, AppException {
		System.out.println("Input reservation number to delete");
		System.out.print("> ");
		String reservationNumber = reader.readLine();
		if (reservationNumber == null || reservationNumber.isEmpty()) {
			System.out.println("Invalid reservation number");
			return;
		}
		CancelReservationForm cancelReservationForm = new CancelReservationForm();
		cancelReservationForm.setReservationNumber(reservationNumber);
		cancelReservationForm.cancel();
		System.out.println("Reservation has been deleted.");
	}

	public static void main(String[] args) throws Exception {
		new CUI().execute();
	}
}