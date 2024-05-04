package HomeLyf.Payload;

public class Booking_Services
{
	private int serviceID;
	private int quantity;
	
	public int getServiceID()
	{
		return serviceID;
	}
	public void setServiceID(int serviceID) {
		this.serviceID = serviceID;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Booking_Services(int serviceID, int quantity) {
		super();
		this.serviceID = serviceID;
		this.quantity = quantity;
	}
	
	
}
