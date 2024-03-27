package HomeLyf.EndPoints;

import static io.restassured.RestAssured.*;

import HomeLyf.Payload.SignUP_Payload;

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


}
