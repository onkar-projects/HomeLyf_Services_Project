package HomeLyf.EndPoints;
import static io.restassured.RestAssured.*;
import com.fasterxml.jackson.core.util.RequestPayload
import HomeLyf.Payload.RestPass_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.userSendEmailOtp_Payload;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserEndPoints {

	public static Response signUP(SignUP_Payload Payload) {
		Response response = given()
				.contentType(ContentType.JSON)
				.body(Payload)
				.log().all()
				.when().post(Routes.account_signUp);
		
		return response;
	}
	
	public static Response userLogin(UserLogin_Payload Payload) {
		Response response = given()
		.contentType(ContentType.JSON)
		.body(Payload)
		.log().all()
		.when().post(Routes.account_login);
		
		return response;
	}
	

	public static Response resetPass(RestPass_Payload Payload) {
		Response response = given()
		.contentType(ContentType.JSON)
		.body(Payload)
		.log().all()
		.when().post(Routes.acccount_emailOTP);
		
		return response;
	}


}
