package server.domain.payment;

public class Payment {
    private String reservationNumber;
    private long amount;
    private boolean isPaid;

    public Payment(String reservationNumber, long amount) {
        this.reservationNumber = reservationNumber;
        this.amount = amount;
        this.isPaid = false;
    }

    // ★★★ この公開メソッドを追加 ★★★
    public String getReservationNumber() {
        return reservationNumber;
    }

    public long getAmount() {
        return amount;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}