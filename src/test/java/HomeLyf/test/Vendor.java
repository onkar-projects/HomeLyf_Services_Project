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
	String token;

	@Test(priority = 1, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin(ITestContext context, String mobileNumber, String type, String emailAddress, String password,
			String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		token = js.getString("token");
		System.out.println("Generated Token Id: " + token);
		context.setAttribute("Token", token);
		logger.debug("Generated Token Id: {}", token);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(priority = 2, enabled = true, description = "Vendor should get bookings")
	public void vendor_get_booking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		Response response = VendorEndPoints.vendorgetbooking(context);
		JsonPath js = CommonMethods.jsonToString(response);
		int vendorBookingId = js.getInt("[0].id");
		context.setAttribute("vendorBookingId", vendorBookingId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("vendor_get_booking is shown successfully.");
	}

	@Test(priority = 3, enabled = true, description = "Vendor should acccept booking")
	public void vendor_acceptBooking(ITestContext context) {
		logger.info("Vendor accept booking");
		Response response = VendorEndPoints.vendor_AcceptBookingEP(context,
				(int) context.getAttribute("vendorBookingId"));
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.get("status");
		Assert.assertEquals(status, "ExpertAssigned");
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 4, enabled = true, description = "Vendor should start booking using startOtp")
	public void vendor_StartBookingTest(ITestContext context) {
		logger.info("Start vendor booking using startOtp");
		Response Cresponse = CustomerEndPoints.customer_GetBookingEndPoint(context, "expertassigned");
		JsonPath js = CommonMethods.jsonToString(Cresponse);
		int sotp = js.getInt("startOTP");
		Response response = VendorEndPoints.vendor_startBookingEP(context,
				CommonMethods.sendBookingIdAndOtp(context, sotp));
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 5, enabled = true, description = "Vendor should complete booking using endOtp")
	public void vendor_CompleteBookingTest(ITestContext context, int eotp) {
		logger.info("Complete vendor booking using endOtp");
		Response response = VendorEndPoints.vendor_completeBookingEP(context,
				CommonMethods.sendBookingIdAndOtp(context, eotp));
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 6, enabled = true, description = "Vendor should cancel booking after accept by vendor")
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

	@Test(priority = 7, enabled = true, description = "Vendor should get available time slot")
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

	@Test(priority = 8, enabled = true, description = "Vendor should disable time slot")
	public void vendor_DisableTimeslotTest(ITestContext context) {
		logger.info("Disabling Vendor time slot");
		Response response = VendorEndPoints.vendor_DisableTimeslotEP(context, CommonMethods.sendTimeslot(context));
		JsonPath js = CommonMethods.jsonToString(response);
		int disabledTimeSlotId = js.getInt("id");
		context.setAttribute("disabledTimeSlotId", disabledTimeSlotId);
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 9, enabled = true, description = "Vendor should enable time slot")
	public void vendor_EnableTimeslotTest(ITestContext context) {
		logger.info("Enable vendor time slot after disabled");
		Response response = VendorEndPoints.vendor_EnableTimeslotEP(context,
				(int) context.getAttribute("disabledTimeSlotId"));
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 10, enabled = true, description = "Vendot should get profile")
	public void vendor_ProfileTest(ITestContext context) {
		logger.info("get vendor profile");
		Response response = VendorEndPoints.vendor_ProfileEP(context);
		Assert.assertEquals(response.getStatusCode(), 200);
	}

}
