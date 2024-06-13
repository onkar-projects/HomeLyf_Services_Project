package HomeLyf.Payload;

import java.util.List;

public class CreateCustomerBookingPayload {

	List<BookingServices> bookingServices; 
	String scheduledOn;
	int addressID;
	
	public List<BookingServices> getBookingServices() {
		return bookingServices;
	}
	public void setBookingServices(List<BookingServices> ls) {
		this.bookingServices = ls;
	}
	public String getScheduledOn() {
		return scheduledOn;
	}
	public void setScheduledOn(String scheduledOn) {
		this.scheduledOn = scheduledOn;
	}
	public int getAddressID() {
		return addressID;
	}
	public void setAddressID(int addressID) {
		this.addressID = addressID;
	}


}
