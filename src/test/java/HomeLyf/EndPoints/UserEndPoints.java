package HomeLyf.EndPoints;
import static io.restassured.RestAssured.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.json.JSONObject;
import org.json.JSONTokener;

import HomeLyf.Payload.RestPass_Payload;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.VendorDetail;

//import HomeLyf.Payload.userSendEmailOtp_Payload;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserEndPoints {

	
// public static Response signUP() throws FileNotFoundException {
//		File f = new File (".\\RequestBody.JSON");
//		FileReader fr = new FileReader(f);
//		JSONTokener jt = new JSONTokener(fr);
//		JSONObject data = new JSONObject(jt);
//		Response response = given()
//				.contentType(ContentType.JSON)
//				.body(data.toString())
//				.log().all()
//				.when().post(Routes.account_signUp);
//		
//		return response;
//	}
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

	public static Response sendEmailOTP(SendEmailOTP_Payload Payload) {
		Response response = given()
				.contentType(ContentType.JSON)
				.body(Payload)
				.log().all()
				.when().post(Routes.account_smsOTP);
				
				return response;
	}
}
