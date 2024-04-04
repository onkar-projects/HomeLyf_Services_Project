package HomeLyf.Payload;

public class SignUP_Payload {

	String name;
	long mobileNumber;
	String type;
	String emailAddress;
	VendorDetail vendorDetail;
	String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(long num) {
		this.mobileNumber = num;
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

	public VendorDetail getVendorDetail() {
		return vendorDetail;
	}

	public void setVendorsDetail(VendorDetail vendorDetail) {
		this.vendorDetail = vendorDetail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


}
