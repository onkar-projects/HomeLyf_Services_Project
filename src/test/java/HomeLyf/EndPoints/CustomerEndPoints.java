package HomeLyf.EndPoints;

import static io.restassured.RestAssured.given;
import java.util.List;
import org.testng.ITestContext;
import HomeLyf.Payload.Address;
import HomeLyf.Payload.Calculator_Payload;
import HomeLyf.Payload.CreateCustomerBookingPayload;
import HomeLyf.Payload.CustomerPaymentStatus_payload;
import HomeLyf.Payload.Reschedule_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CustomerEndPoints {
	public static Response userLogin(UserLogin_Payload Payload, ITestContext context) {
		Response response = given().contentType(ContentType.JSON).body(Payload).when().post(Routes.account_login);
		return response;
	}

	public static Response customer_Login(UserLogin_Payload Payload, ITestContext context) {
		Response response = given().contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.account_login);
		return response;
	}

	public static Response customer_MyProfile(ITestContext context) {
		String token = (String) context.getAttribute("CToken");

		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).when()
				.get(Routes.customer_MyProfileURL);
		return response;
	}

	public static Response customer_service(ITestContext context, int subCategoryId) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).when()
				.get(Routes.customer_service, subCategoryId);
		return response;
	}

	public static Response customer_Address(ITestContext context, Address payload) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.body(payload).when().post(Routes.customer_Address);
		return response;
	}

	public static Response customer_GetTimeSlot(int addressID, int categoryId, ITestContext context) {
		String token = (String) context.getAttribute("CToken");

		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.queryParam("addressID", addressID).queryParam("categoryId", categoryId).log().all().when()
				.get(Routes.customer_GetTimeShot);
		return response;
	}

	public static Response customer_GetBookingEndPoint(ITestContext context, String status, int page, int size) {
		String token = (String) context.getAttribute("CToken");

		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.queryParam("status", status).queryParam("page", page).queryParam("size", size).when()
				.get(Routes.customer_GetBookingURL);
		return response;
	}

	public static Response customer_CreateBookingEndPoint(ITestContext context, CreateCustomerBookingPayload payload) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.body(payload).when().post(Routes.customer_CreateBookingURL);
		return response;
	}

//	public static Response customer_UpdatePaymentStatusEP(ITestContext context, CustomerPaymentStatus_payload payload) {
//		String token = (String) context.getAttribute("CToken");
//		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
//				.body(payload).when().put(Routes.customer_UpdatePaymentURL);
//		return response;
//	}
	
	public static Response customer_UpdatePaymentStatusEP(ITestContext context, int paymentId) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.when().put(Routes.customer_UpdatePaymentURL,paymentId);
		return response;
	}

	public static Response customer_CalculateEP(ITestContext context, List<Calculator_Payload> payload) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.body(payload).when().post(Routes.customer_Calculate);
		return response;
	}

	public static Response customer_CancelEP(ITestContext context, int bookingId) {
		String CToken = (String) context.getAttribute("CToken");

		Response response = given().header("Authorization", "Bearer " + CToken).contentType(ContentType.JSON).when()
				.log().all().post(Routes.customer_CancelURL, bookingId);
		return response;
	}

	public static Response customer_SubCategoryEP(ITestContext context, int categoryId) {
		String token = (String) context.getAttribute("CToken");

		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).when()
				.get(Routes.customer_GetSubCategoryURL, categoryId);
		return response;
	}

	public static Response customer_PaymentModeEP(ITestContext context) {
		String token = (String) context.getAttribute("CToken");

		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).when()
				.get(Routes.getPaymentMode);
		return response;
	}

	public static Response customer_PaymentStatusEP(ITestContext context) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).when()
				.get(Routes.getPaymentStatus);
		return response;
	}

	public static Response customer_BookingStatusEP(ITestContext context) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).when()
				.get(Routes.getBookingStatus);
		return response;
	}

	public static Response customer_GetMyProfileEP(ITestContext context) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).when()
				.get(Routes.getMyProfileURL);
		return response;
	}

	public static Response customer_GetCategoryEP(ITestContext context, String postcode, String search) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).queryParam("postcode", postcode)
				.queryParam("search", search).contentType(ContentType.JSON).when().get(Routes.getCategoryURL);
		return response;
	}

	public static Response customer_RescheduleEP(ITestContext context, Reschedule_Payload payload) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.body(payload).log().all().when().put(Routes.customer_Reschedule);
		return response;
	}

	public static Response customer_GetBookingByIdEP(ITestContext context, int bookingId) {
		String token = (String) context.getAttribute("CToken");
		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON)
				.pathParam("bookingId", bookingId).when().get(Routes.customer_GetBookingIdURL);
		return response;
	}
}
