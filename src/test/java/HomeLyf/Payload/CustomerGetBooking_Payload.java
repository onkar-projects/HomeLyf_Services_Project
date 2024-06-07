package HomeLyf.Payload;

import java.util.Arrays;
import java.util.List;

public class CustomerGetBooking_Payload {

	String bearertoken;
	List<String> status = Arrays.asList("new", "expertassigned", "inprogress","delayed","cancelled","deleted","completed");

	public String getToken() {
		return bearertoken;
	}
	public void setToken(String token) {
		this.bearertoken = token;
	}
	public List<String> getStatus() {
		return status;
	}
	public void setStatus(List<String> status) {
		this.status = status;
	}
	
}
