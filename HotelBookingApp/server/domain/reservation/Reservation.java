package server.domain.reservation;

import java.util.Date;

public class Reservation {
    public enum ReservationStatus { CREATED, CHECKED_IN, CHECKED_OUT, CANCELED }

    private String reservationNumber;
    private String roomNumber;
    private Date checkInDate;
    private Date checkOutDate;
    private ReservationStatus status;

    public Reservation(String reservationNumber, String roomNumber, Date checkInDate, Date checkOutDate) {
        this.reservationNumber = reservationNumber;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.CREATED;
    }

    public String getReservationNumber() { return reservationNumber; }
    public String getRoomNumber() { return roomNumber; }
    public Date getCheckInDate() { return checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}