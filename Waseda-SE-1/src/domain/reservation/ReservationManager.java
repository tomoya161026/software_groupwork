/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package domain.reservation;

import domain.DaoFactory;
import java.util.Calendar;
import java.util.Date;

/**
 * Manager for reservations<br>
 * */
public class ReservationManager {
	
	public String createReservation(Date stayingDate) throws ReservationException,
			NullPointerException {
		if (stayingDate == null) {
			throw new NullPointerException("stayingDate");
		}

		Reservation reservation = new Reservation();
		String reservationNumber = generateReservationNumber();
		reservation.setReservationNumber(reservationNumber);
		reservation.setStayingDate(stayingDate);
		reservation.setStatus(Reservation.RESERVATION_STATUS_CREATE);

		ReservationDao reservationDao = getReservationDao();
		reservationDao.createReservation(reservation);
		return reservationNumber;
	}

	private synchronized String generateReservationNumber() {
		Calendar calendar = Calendar.getInstance();
		try {
			Thread.sleep(10);
		}
		catch (Exception e) {
		}
		return String.valueOf(calendar.getTimeInMillis());
	}

	public Date consumeReservation(String reservationNumber) throws ReservationException,
			NullPointerException {
		if (reservationNumber == null) {
			throw new NullPointerException("reservationNumber");
		}

		ReservationDao reservationDao = getReservationDao();
		Reservation reservation = reservationDao.getReservation(reservationNumber);
		//If corresponding reservation does not exist
		if (reservation == null) {
			ReservationException exception = new ReservationException(
					ReservationException.CODE_RESERVATION_NOT_FOUND);
			exception.getDetailMessages().add("reservation_number[" + reservationNumber + "]");
			throw exception;
		}
		//If reservation has been consumed already
		if (reservation.getStatus().equals(Reservation.RESERVATION_STATUS_CONSUME)) {
			ReservationException exception = new ReservationException(
					ReservationException.CODE_RESERVATION_ALREADY_CONSUMED);
			exception.getDetailMessages().add("reservation_number[" + reservationNumber + "]");
			throw exception;
		}

		Date stayingDate = reservation.getStayingDate();
		reservation.setStatus(Reservation.RESERVATION_STATUS_CONSUME);
		reservationDao.updateReservation(reservation);
		return stayingDate;
	}

	public Date deleteReservation(String reservationNumber) throws ReservationException, NullPointerException {
		if (reservationNumber == null) {
			throw new NullPointerException("reservationNumber");
		}

		ReservationDao reservationDao = getReservationDao();
		Reservation reservation = reservationDao.getReservation(reservationNumber);

		// Check 1: Reservation must exist
		if (reservation == null) {
			throw new ReservationException(ReservationException.CODE_RESERVATION_NOT_FOUND);
		}

		// Check 2: Reservation must be in 'create' state to be deletable
		if (!Reservation.RESERVATION_STATUS_CREATE.equals(reservation.getStatus())) {
			ReservationException exception = new ReservationException(ReservationException.CODE_CANNOT_DELETE);
			exception.getDetailMessages().add("reservation_number[" + reservationNumber + "]");
			exception.getDetailMessages().add("current_status[" + reservation.getStatus() + "]");
			throw exception;
		}

		Date stayingDate = reservation.getStayingDate();

		// Delete the reservation from DB
		reservationDao.deleteReservation(reservationNumber);

		return stayingDate;
	}

	private ReservationDao getReservationDao() {
		return DaoFactory.getInstance().getReservationDao();
	}
}