package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import HomeLyf.EndPoints.CustomerEndPoints;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.EndPoints.VendorEndPoints;
import HomeLyf.Utilities.CommonMethods;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class LookUp {

	private static Logger logger = LogManager.getLogger(LookUp.class);

	public static void getCategory(ITestContext context) {
		logger.info("Getting category");
		Response response = UserEndPoints.user_getLookupCategoryEP();
		JsonPath js = CommonMethods.jsonToString(response);
		String name = js.getString("[0].name");
		int CategoryId = js.getInt("[0].id");
		context.setAttribute("name", name);
		context.setAttribute("categoryId1", CategoryId);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Category fetched successfully");
	}

	public static void getCountry(ITestContext context) {
		logger.info("Getting Country");
		Response response = UserEndPoints.user_getLookupCountryEP();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath js = CommonMethods.jsonToString(response);
		String name = js.getString("[0].name");
		context.setAttribute("name", name);
		logger.info("Country fetched successfully");
	}

	public static void getState(ITestContext context) {
		logger.info("Getting State");
		Response response = UserEndPoints.user_getLookupStateEP();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath js = CommonMethods.jsonToString(response);
		String name = js.getString("[0].name");
		context.setAttribute("name", name);
		Assert.assertEquals(js.getString("[0].name"), "Maharashtra");
		logger.info("State fetched successfully");
	}

	public static void getCity(ITestContext context) {
		logger.info("Getting City");
		Response response = UserEndPoints.user_getLookupCityEP();
		JsonPath js = CommonMethods.jsonToString(response);
		String name = js.getString("[0].name");
		context.setAttribute("name", name);
		int cityId = js.getInt("[0].id");
		context.setAttribute("cityId", cityId);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("City fetched successfully");
	}

	public static void getPostCode(ITestContext context) {
		logger.info("Getting PostCode from LookUp");
		Response response = UserEndPoints.user_getLookupPostCodeEP();
		JsonPath js = CommonMethods.jsonToString(response);
		String postCode = js.getString("[0].name");
		int postCodeId = js.getInt("[0].id");
		context.setAttribute("postCode", postCode);
		context.setAttribute("postCodeId", postCodeId);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("PostCode fetched successfully");
	}

	public static void getPaymentStatus(ITestContext context) {

		Response response = CustomerEndPoints.customer_PaymentStatusEP(context);
		JsonPath js = CommonMethods.jsonToString(response);
		String paymentStatus = js.getString("[0].name");
		context.setAttribute("paymentStatus", paymentStatus);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Payment Status are fetched successfully");

	}

	public static void customer_GetPaymentModeTest(ITestContext context) {

		Response response = CustomerEndPoints.customer_PaymentModeEP(context);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Payment Mode fetched successfully");
	}

	public static void customer_GetBookingStatusTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_BookingStatusEP(context);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Booking Status are fetched successfully");
	}
//-----------------------------------------------------------------------------------------------------------

	public static void vendorgetMybooking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		int vendorBookingId = 0;
		Response response = VendorEndPoints.vendor_MybookingEP(context);
		JsonPath js = CommonMethods.jsonToString(response);
		for (int i = 0; i <= 10; i++) {
			String status = js.getString("[" + i + "].status");
			if (status.equalsIgnoreCase("ExpertAssigned")) {
				vendorBookingId = js.getInt("[" + i + "].id");
				break;
			}
		}
		context.setAttribute("vendorAcceptBookingId", vendorBookingId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("vendor_get_booking is shown successfully.");
	}

	/*
	 * public static void customerGetBooking(ITestContext context) {
	 * logger.info(""); //String[] status = {"New", "expertassigned", "inprogress",
	 * "cancelled", "completed"}; logger.info("Fetching Customer booking"); int
	 * vendorBookingId; String startOtp; String endOtp; Response response =
	 * CustomerEndPoints.customer_GetBookingEndPoint(context, "expertassigned");
	 * response.then().log().all(); JsonPath js =
	 * CommonMethods.jsonToString(response); vendorBookingId = js.getInt("[0].id");
	 * startOtp = js.getString("[0].startOTP"); endOtp = js.getString("[0].endOTP");
	 * System.out.println("################### "
	 * +vendorBookingId+" ###################");
	 * context.setAttribute("vendorAcceptBookingId", vendorBookingId);
	 * context.setAttribute("startOtp", startOtp); context.setAttribute("endOtp",
	 * endOtp);
	 * 
	 * String stat = js.getString("[0].status"); Assert.assertEquals(stat,
	 * "ExpertAssigned"); Assert.assertEquals(response.statusCode(), 200);
	 * logger.info("Customer booking shown successfully of Status" + stat); }
	 */
}
