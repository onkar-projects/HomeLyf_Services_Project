package HomeLyf.Payload;

import java.util.List;

public class CustomerBooking_Payload
{
	private List<Booking_Services> bookingServices;
	
	private String scheduledOn;
    private int addressID;

	
	public List<Booking_Services> getBookingServices()
	{
		return bookingServices;
	}
	public void setBookingServices(List<Booking_Services> bookingServices) 
	{
		this.bookingServices = bookingServices;
	}
	
    public String getScheduledOn()
	{
		return scheduledOn;
	}
	public void setScheduledOn(String scheduledOn)
	{
		this.scheduledOn = scheduledOn;
	}
	public int getAddressID() 
	{
		return addressID;
	}
	public void setAddressID(int addressID)
	{
		this.addressID = addressID;
	}
	public CustomerBooking_Payload(List<Booking_Services> bookingServices, String scheduledOn, int addressID) {
		super();
		this.bookingServices = bookingServices;
		this.scheduledOn = scheduledOn;
		this.addressID = addressID;
	}
	public CustomerBooking_Payload() {
		super();
	}
	
	
	

}