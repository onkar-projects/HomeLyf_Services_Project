package HomeLyf.EndPoints;

import static io.restassured.RestAssured.given;
import org.testng.ITestContext;
import HomeLyf.Payload.BankAccountDetails_Payload;
import HomeLyf.Payload.DisableTimeslot_Payload;
import HomeLyf.Payload.StartAndComplete_Booking_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class VendorEndPoints {
	public static Response vendor_Login(ITestContext context, UserLogin_Payload Payload) {
		Response response = given().contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.account_login);
		return response;
	}

	public static Response vendorgetbooking(ITestContext context, int page, int size) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.queryParam("page", page).queryParam("size", size).log().all().when().get(Routes.vendor_getbooking);
		return response;
	}

	public static Response vendor_MybookingEP(ITestContext context, int page, int size) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.queryParam("page", page).queryParam("size", size).log().all().when().get(Routes.vendor_MyBookingURL);
		return response;
	}

	public static Response vendor_AcceptBookingEP(ITestContext context, int bookingId) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).log().all()
				.when().post(Routes.vendor_acceptBooking, bookingId);
		return response;
	}

	public static Response vendorCancelBooking(ITestContext context, int bookingId) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).log().all()
				.when().post(Routes.vendor_cancelBooking, bookingId);
		return response;
	}

	public static Response vendor_startBookingEP(ITestContext context, StartAndComplete_Booking_Payload Payload) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.body(Payload).log().all().when().post(Routes.vendor_startBooking);
		return response;
	}

	public static Response vendor_completeBookingEP(ITestContext context, StartAndComplete_Booking_Payload Payload) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.body(Payload).log().all().when().post(Routes.vendor_completeBooking);
		return response;
	}

	public static Response vendor_TimeslotEP(ITestContext context) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).log().all()
				.when().get(Routes.vendor_timeslotURL);
		return response;
	}

	public static Response vendor_DisableTimeslotEP(ITestContext context, DisableTimeslot_Payload Payload) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.body(Payload).log().all().when().post(Routes.vendor_disableTimeslot);
		return response;
	}

	public static Response vendor_EnableTimeslotEP(ITestContext context, int id) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).log().all()
				.when().post(Routes.vendor_enableTimeslot, id);
		return response;
	}

	public static Response vendor_ProfileEP(ITestContext context) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).log().all()
				.when().get(Routes.vendor_profile);
		return response;
	}

	public static Response vendoractivebooking(ITestContext context) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).log().all()
				.when().get(Routes.vendor_activeBookingURL);
		return response;
	}

	public static Response vendor_BanckAccountDetailsEP(ITestContext context, BankAccountDetails_Payload payload) {
		String token = (String) context.getAttribute("VToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.body(payload).log().all().when().post(Routes.vendor_bankDetails);
		return response;
	}
}
