

package HomeLyf.EndPoints;

import HomeLyf.Payload.Address;
import HomeLyf.Payload.ForgotPassword_Payload;
import HomeLyf.Payload.RestPass_Payload;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.VendorDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.annotations.BeforeTest;

public class UserEndPoints {
	
	public static Response signUP(SignUP_Payload Payload) {
		
		Response response =  given().contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.account_signUp);

		return response;
	}

	public static Response userLogin(UserLogin_Payload Payload) {
		Response response = given().contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.account_login);

		return response;
	}

	public static Response sendEmailOTP(SendEmailOTP_Payload Payload) {
		Response response = given().contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.acccount_emailOTP);

		return response;
	}

	public static Response forgotPass(ForgotPassword_Payload Payload) {
		Response response = given().contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.account_forgotPass);

		return response;
	}

	public static Response resetPass(RestPass_Payload Payload) {
		Response response = given().contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.account_resetPass);

		return response;
	}

}

