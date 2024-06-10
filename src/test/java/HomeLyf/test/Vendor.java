package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import HomeLyf.EndPoints.CustomerEndPoints;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.EndPoints.VendorEndPoints;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Vendor {
	private static Logger logger = LogManager.getLogger(Vendor.class);
	static String token;

	@Test(priority = 1, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public static void vendor_Login(ITestContext context, String mobileNumber, String type, String emailAddress, String password,
			String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		token = js.getString("token");
		System.out.println("Generated Token Id: " + token);
		context.setAttribute("Vtoken", token);
		logger.debug("Generated Token Id: {}", token);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(priority = 2, enabled = false, description = "Vendor should get bookings")
	public void vendor_get_booking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		Response response = VendorEndPoints.vendorgetbooking(context, 1, 10);
		JsonPath js = CommonMethods.jsonToString(response);
		int vendorBookingId = js.getInt("[0].id");
		context.setAttribute("vendorBookingId", vendorBookingId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("vendor_get_booking is shown successfully.");
	}

	@Test(priority = 3, enabled = false, description = "Vendor should acccept booking")
	public void vendor_acceptBooking(ITestContext context) {
		logger.info("Vendor accept booking");
		
		Response response = VendorEndPoints.vendor_AcceptBookingEP(context,(int) context.getAttribute("vendorAcceptBookingId"));
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.get("status");
		Assert.assertEquals(status, "ExpertAssigned");
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 4, enabled = false, description = "Vendor should start booking using startOtp")
	public  void vendor_StartBookingTest(ITestContext context) {
		logger.info("Start vendor booking using startOtp");
		Response C_GetBookingResponse = CustomerEndPoints.customer_GetBookingByIdEP(context, (int) context.getAttribute("vendorAcceptBookingId"));
		C_GetBookingResponse.then().log().all();
		JsonPath jsgetBooking = CommonMethods.jsonToString(C_GetBookingResponse);
		int customerBookingId = jsgetBooking.getInt("id");
		int startOTP = jsgetBooking.getInt("startOTP");
		context.setAttribute("c_startOTP", startOTP);
		context.setAttribute("customerBookingId", customerBookingId);
//		Response Cresponse = CustomerEndPoints.customer_GetBookingByIdEP(context, 52673);
//		JsonPath js = CommonMethods.jsonToString(Cresponse);
//		int startOTP = js.get("startOTP");
//	int sOTP = 	(int) context.getAttribute("startOTP");
		Response response = VendorEndPoints.vendor_startBookingEP(context, CommonMethods.sendBookingIdAndOtp(context));
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 5, enabled = false, description = "Vendor should complete booking using endOtp")
	public void vendor_CompleteBookingTest(ITestContext context) {
		logger.info("Complete vendor booking using endOtp");

		int bookingId = 0;
		Response C_GetBooking = CustomerEndPoints.customer_GetBookingByIdEP(context, 52736);
		JsonPath jsgetBooking = CommonMethods.jsonToString(C_GetBooking);
		int customerBookingId = jsgetBooking.getInt("id");

		Response C_response = CustomerEndPoints.customer_GetBookingByIdEP(context, customerBookingId);
		JsonPath js = CommonMethods.jsonToString(C_response);
		// int bookingId = js.getInt("id");
		int endOTP = js.get("endOTP");
		// context.setAttribute("endOTP", endOTP);
		// Response response =
		// VendorEndPoints.vendor_completeBookingEP(context,CommonMethods.sendBookingIdAndOtp(context,
		// endOTP));
		// Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 6, enabled = false, description = "Vendor should cancel booking after accept by vendor")
	public void vendor_cancelBooking(ITestContext context) {
		logger.info("Cancel booking accept by vendor");
		// LookUp.customerGetBooking(context);
		LookUp.vendorgetMybooking(context);
		int bookingId = (int) context.getAttribute("vendorAcceptBookingId");
		Response response = VendorEndPoints.vendorCancelBooking(context, bookingId);
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.get("status");
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(status, "New");
		logger.info("BookingId " + bookingId + " is cancelled Successfully");

	}

	@Test(priority = 7, enabled = false, description = "Vendor should get available time slot")
	public void vendor_TimeslotTest(ITestContext context) {
		logger.info("Getting vendor available time slots");
		Response response = VendorEndPoints.vendor_TimeslotEP(context);
		JsonPath js = CommonMethods.jsonToString(response);
		String sTime = js.getString("availableTimeSlots[1].startTime");
		String eTime = js.getString("availableTimeSlots[1].endTime");
		context.setAttribute("STime", sTime);
		context.setAttribute("ETime", eTime);
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 8, enabled = false, description = "Vendor should disable time slot")
	public void vendor_DisableTimeslotTest(ITestContext context) {
		logger.info("Disabling Vendor time slot");
		Response response = VendorEndPoints.vendor_DisableTimeslotEP(context, CommonMethods.sendTimeslot(context));
		JsonPath js = CommonMethods.jsonToString(response);
		int disabledTimeSlotId = js.getInt("id");
		context.setAttribute("disabledTimeSlotId", disabledTimeSlotId);
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 9, enabled = false, description = "Vendor should enable time slot")
	public void vendor_EnableTimeslotTest(ITestContext context) {
		logger.info("Enable vendor time slot after disabled");
		Response response = VendorEndPoints.vendor_EnableTimeslotEP(context,
				(int) context.getAttribute("disabledTimeSlotId"));
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 10, enabled = false, description = "Vendot should get profile")
	public void vendor_ProfileTest(ITestContext context) {
		logger.info("get vendor profile");
		Response response = VendorEndPoints.vendor_ProfileEP(context);
		Assert.assertEquals(response.getStatusCode(), 200);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
