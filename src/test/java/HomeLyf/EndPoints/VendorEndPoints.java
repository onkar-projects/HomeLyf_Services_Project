package HomeLyf.EndPoints;

import static io.restassured.RestAssured.given;

import org.testng.ITestContext;

import HomeLyf.Payload.UserLogin_Payload;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class VendorEndPoints {
	public static Response userLogin(UserLogin_Payload Payload, ITestContext context) {
		Response response = given().contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.account_login);

		return response;
	}

	public static Response vendorgetbooking(ITestContext context) {
		String token = (String) context.getAttribute("Token");

		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.log().all().when().get(Routes.vendor_getbooking);

		return response;
	}
}
