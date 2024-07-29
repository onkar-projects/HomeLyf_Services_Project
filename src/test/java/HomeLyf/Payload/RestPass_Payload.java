package HomeLyf.Payload;

public class RestPass_Payload {

	String emailAddress;
	long mobileNumber;
	String oneTimePass;
	String password;
	String confirmpass;

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getOneTimePass() {
		return oneTimePass;
	}

	public void setOneTimePass(String oneTimePass) {
		this.oneTimePass = oneTimePass;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmpass() {
		return confirmpass;
	}

	public void setConfirmpass(String confirmpass) {
		this.confirmpass = confirmpass;
	}
}