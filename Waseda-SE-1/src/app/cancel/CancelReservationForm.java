/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package app.cancel;

import app.AppException;

/**
 * Form class for Cancel Reservation
 *
 */
public class CancelReservationForm {

    private String reservationNumber;
    private final CancelReservationControl cancelReservationControl = new CancelReservationControl();

    public void cancel() throws AppException {
        cancelReservationControl.cancel(reservationNumber);
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }
}