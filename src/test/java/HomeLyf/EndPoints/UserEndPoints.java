package HomeLyf.EndPoints;

import HomeLyf.Payload.ForgotPassword_Payload;
import HomeLyf.Payload.RestPass_Payload;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class UserEndPoints {

	public static Response signUP(SignUP_Payload Payload) {
		Response response = given().contentType(ContentType.JSON).body(Payload).log().all().when()
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

	public static Response user_getLookupCategoryEP() {
		Response response = given().contentType(ContentType.JSON).when().log().all().get(Routes.getLookupCategoryURL);
		return response;
	}

	public static Response user_getLookupCountryEP() {
		Response response = given().contentType(ContentType.JSON).when().log().all().get(Routes.getLookupCountryURL);
		return response;
	}

	public static Response user_getLookupStateEP() {
		Response response = given().contentType(ContentType.JSON).when().log().all().get(Routes.getLookupStateURL);
		return response;
	}

	public static Response user_getLookupCityEP() {
		Response response = given().contentType(ContentType.JSON).when().log().all().get(Routes.getLookupCityURL);
		return response;
	}

	public static Response user_getLookupPostCodeEP() {
		Response response = given().contentType(ContentType.JSON).when().log().all().get(Routes.getLookupPostCodeURL);
		return response;
	}

}
