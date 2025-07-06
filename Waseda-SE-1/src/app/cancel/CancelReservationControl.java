/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package app.cancel;

import java.util.Date;
import app.AppException;
import app.ManagerFactory;
import domain.reservation.ReservationException;
import domain.reservation.ReservationManager;
import domain.room.RoomException;
import domain.room.RoomManager;

/**
 * Control class for Cancel Reservation
 *
 */
public class CancelReservationControl {

    public void cancel(String reservationNumber) throws AppException {
        try {
            ReservationManager reservationManager = ManagerFactory.getInstance().getReservationManager();
            RoomManager roomManager = ManagerFactory.getInstance().getRoomManager();

            // Step 1: Delete the reservation and get the staying date
            Date stayingDate = reservationManager.deleteReservation(reservationNumber);

            // Step 2: Update room availability (increase count by 1)
            int availableQtyOfChange = 1;
            roomManager.updateRoomAvailableQty(stayingDate, availableQtyOfChange);

        } catch (ReservationException e) {
            AppException exception = new AppException("Failed to delete reservation", e);
            exception.getDetailMessages().add(e.getMessage());
            exception.getDetailMessages().addAll(e.getDetailMessages());
            throw exception;
        } catch (RoomException e) {
            // This is a compensating transaction problem.
            // For this system's level, we'll just report the error.
            AppException exception = new AppException("Failed to delete reservation (could not update room availability)", e);
            exception.getDetailMessages().add(e.getMessage());
            exception.getDetailMessages().addAll(e.getDetailMessages());
            throw exception;
        }
    }
}