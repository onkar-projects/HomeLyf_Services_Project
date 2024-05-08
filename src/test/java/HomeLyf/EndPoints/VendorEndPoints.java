package HomeLyf.EndPoints;

import static io.restassured.RestAssured.given;

import org.testng.ITestContext;

import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.Vendor_Timeslotdisable;
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
	public static Response vendor_Timeslot(String token)

	{
		Response response = given().header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON).log().all()
				.when()
				.get(Routes.vendor_GET_TimeSlot);
		return response;

	}

	public static Response vendor_TimeslotDisable(String token, Vendor_Timeslotdisable payload) {
		Response response = given().header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON).body(payload).log().all()
				.when()
				.post(Routes.vendor_POST_TimeSlot_Disable);
		return response;
	}

	public static Response vendor_Timeslotenable(String token, int id) {
		Response response = given().header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON).log().all()
				.when()
				.post(Routes.vendor_POST_TimeSlot_Enable, id);
		return response;
	}

}
