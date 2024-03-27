package HomeLyf.Payload;

public class SignUP_Payload {
	
	String name;
	String mobileNumber;
	String type;
	String emailAddress;
	VendorDetail vendorsDetail;
	String password;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public VendorDetail getVendorsDetail() {
		return vendorsDetail;
	}
	public void setVendorsDetail(VendorDetail vendorsDetail) {
		this.vendorsDetail = vendorsDetail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
