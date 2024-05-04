package HomeLyf.Payload;

public class UpdatePaymentStatus_Payload {

	int id;
	int bookingID;
	String paymentStaus;
	String paymentMode;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBookingID() {
		return bookingID;
	}
	public void setBookingID(int bookingID) {
		this.bookingID = bookingID;
	}
	public String getPaymentStaus() {
		return paymentStaus;
	}
	public void setPaymentStaus(String paymentStaus) {
		this.paymentStaus = paymentStaus;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
}
