package HomeLyf.Payload;

public class StartAndComplete_Booking_Payload {

	
	int bookingId;
	int otp;
	public int getBookingId() {
		return bookingId;
	}
	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	public int getOtp() {
		return otp;
	}
	public void setOtp(int otp) {
		this.otp = otp;
	}
}
