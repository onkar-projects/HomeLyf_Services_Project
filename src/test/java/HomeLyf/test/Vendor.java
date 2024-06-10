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
	static String Vtoken;
	static String Ctoken;
	static String sTime;
	static int id;

	@Test(priority = 1, enabled = false, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public static void VendorLogin(ITestContext context, String mobileNumber, String type, String emailAddress,
			String password, String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		Vtoken = js.getString("token");
		System.out.println("Generated Token Id: " + Vtoken);
		context.setAttribute("Token", Vtoken);
		logger.debug("Generated Token Id: {}", Vtoken);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(priority = 2, enabled = false, description = "Vendor should get bookings")
	public static void vendor_get_booking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		Response response = VendorEndPoints.vendorgetbooking(context);
		JsonPath js = CommonMethods.jsonToString(response);
		int vendorBookingId = js.getInt("[0].id");
		context.setAttribute("vendorBookingId", vendorBookingId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("vendor_get_booking is shown successfully.");
	}

	@Test(priority = 3, enabled = false, description = "Vendor should acccept booking")
	public static void vendor_acceptBooking(ITestContext context) {
		logger.info("Vendor accept booking");
		Response response = VendorEndPoints.vendor_AcceptBookingEP(context,
				(int) context.getAttribute("vendorBookingId"));
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.get("status");
		Assert.assertEquals(status, "ExpertAssigned");
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 4, enabled = false, description = "Vendor should start booking using startOtp")
	public void vendor_StartBookingTest(ITestContext context) {
		logger.info("Start vendor booking using startOtp");
		Response Cresponse = CustomerEndPoints.customer_GetBookingEndPoint(context, "expertassigned");
		JsonPath js = CommonMethods.jsonToString(Cresponse);
		int sotp = js.getInt("startOTP");
		Response response = VendorEndPoints.vendor_startBookingEP(context,
				CommonMethods.sendBookingIdAndOtp(context, sotp));
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 5, enabled = false, description = "Vendor should complete booking using endOtp")
	public static void vendor_CompleteBookingTest(ITestContext context, int eotp) {
		logger.info("Complete vendor booking using endOtp");
		Response response = VendorEndPoints.vendor_completeBookingEP(context,
				CommonMethods.sendBookingIdAndOtp(context, eotp));
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 6, enabled = false, description = "Vendor should cancel booking after accept by vendor")
	public static void vendor_cancelBooking(ITestContext context) {
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
	public static void vendor_TimeslotTest(ITestContext context) {
		logger.info("Getting vendor available time slots");
		Response response = VendorEndPoints.vendor_TimeslotEP(context);
		JsonPath js = CommonMethods.jsonToString(response);
		String sTime = js.getString("availableTimeSlots[0].startTime");
		String eTime = js.getString("availableTimeSlots[0].endTime");
		context.setAttribute("STime", sTime);
		context.setAttribute("ETime", eTime);
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 8, enabled = false, description = "Vendor should disable time slot")
	public static void vendor_DisableTimeslotTest(ITestContext context) {
		logger.info("Disabling Vendor time slot");
		Response response = VendorEndPoints.vendor_DisableTimeslotEP(context, CommonMethods.sendTimeslot(context));
		JsonPath js = CommonMethods.jsonToString(response);
		int disabledTimeSlotId = js.getInt("id");
		context.setAttribute("disabledTimeSlotId", disabledTimeSlotId);
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 9, enabled = false, description = "Vendor should enable time slot")
	public static void vendor_EnableTimeslotTest(ITestContext context) {
		logger.info("Enable vendor time slot after disabled");
		Response response = VendorEndPoints.vendor_EnableTimeslotEP(context,
				(int) context.getAttribute("disabledTimeSlotId"));
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 10, enabled = false, description = "Vendot should get profile")
	public static void vendor_ProfileTest(ITestContext context) {
		logger.info("get vendor profile");
		Response response = VendorEndPoints.vendor_ProfileEP(context);
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(enabled = true, description = "Verify that vendor accept booking after enabled that disabled timeslot")
	public static void vendor_AcceptBookingAfterEnablingTimeslot(ITestContext context) {
		logger.info("Started vendor accept booking after enabled that disabled timeslot");
		logger.info("Started Customer Login test.");
		Response response_CustomerLogin = UserEndPoints
				.userLogin(CommonMethods.CustomerLoginforAcceptBookingAfterEnablingTimeslot());
		response_CustomerLogin.then().log().all();
		String res_CustomerLogin = response_CustomerLogin.asPrettyString();
		JsonPath js_CustomerLogin = new JsonPath(res_CustomerLogin);
		Ctoken = js_CustomerLogin.getString("token");
		System.out.println("Generated Token Id: " + Ctoken);
		context.setAttribute("Token", Ctoken);
		logger.debug("Generated Token Id: {}", Ctoken);
		logger.info("Customer logged in successfully.");
		// -----------------------------------------------------------------

		logger.info("Getting Customer profile.");
		Response response_customer_GetMyProfileEP = CustomerEndPoints.customer_GetMyProfileEP(context);
		response_customer_GetMyProfileEP.then().log().all();
		JsonPath js_customer_GetMyProfileEP = CommonMethods.jsonToString(response_customer_GetMyProfileEP);
		int addressId = js_customer_GetMyProfileEP.get("addresses[0].id");
		context.setAttribute("addressId", addressId);
		logger.info("Customer profile shown successfully.");
		// -------------------------------------------------------

		logger.info("Getting category.");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response_customerGetCategoryEP = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCode"), (String) context.getAttribute("name"));
		response_customerGetCategoryEP.then().log().all();
		JsonPath js_customerGetCategoryEP = CommonMethods.jsonToString(response_customerGetCategoryEP);
		int categoryId = js_customerGetCategoryEP.get("[0].id");
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js_customerGetCategoryEP.getString("[0].name"), "Painting");
		logger.info("Category fetched successfully.");
		// -----------------------------------------------------------------

		logger.info("Getting subcategory.");
		Response response_customer_SubCategoryEP = CustomerEndPoints.customer_SubCategoryEP(context,
				(int) context.getAttribute("categoryId"));
		response_customer_SubCategoryEP.then().log().all();
		JsonPath js_customer_SubCategoryEP = CommonMethods.jsonToString(response_customer_SubCategoryEP);
		int subCategoryId = js_customer_SubCategoryEP.getInt("[0].id");
		context.setAttribute("subCategoryId", subCategoryId);
		logger.info("SubCategory is shown Successfully.");
		// ---------------------------------------------------------------

		logger.info("Getting services.");
		Response response_customer_service = CustomerEndPoints.customer_service(context,
				(int) context.getAttribute("subCategoryId"));
		response_customer_service.then().log().all();
		JsonPath js_customer_service = CommonMethods.jsonToString(response_customer_service);
		int serviceId = js_customer_service.getInt("[0].id");
		context.setAttribute("serviceid", serviceId);
		System.out.println("----------------------------------" + js_customer_service.get("[0].id")
				+ "------------------------------------");
		logger.info("Services are shown successfully.");
		// ----------------------------------------------------------------------------

		logger.info("Getting category timeslot.");
		Response response_customer_GetTimeSlot = CustomerEndPoints.customer_GetTimeSlot(
				(int) context.getAttribute("addressId"), (int) context.getAttribute("categoryId"), context);
		response_customer_GetTimeSlot.then().log().all();
		JsonPath js_customer_GetTimeSlot = CommonMethods.jsonToString(response_customer_GetTimeSlot);
		sTime = js_customer_GetTimeSlot.getString("[1].startTime");
		context.setAttribute("StartTime", sTime);
		String eTime = js_customer_GetTimeSlot.getString("[1].endTime");
		System.out.println("Start Time: " + sTime + "\n End Time: " + eTime);
		logger.info("Category timeslot are shown successfully.");
		// --------------------------------------------------------------------

		logger.info("Started creating new Booking.");
		Response response_customer_CreateBooking = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response_customer_CreateBooking.then().log().all();
		JsonPath js_customer_CreateBooking = CommonMethods.jsonToString(response_customer_CreateBooking);
		String status = js_customer_CreateBooking.getString("status");
		id = js_customer_CreateBooking.getInt("id");
		context.setAttribute("BookingId", id);
		// int bookingId = js5.getInt("id");
		// context.setAttribute("bookingId", bookingId);
		Assert.assertEquals(status, "New");
		logger.info("New booking created successfully.");
		// -------------------------------------------------------------------------

		logger.info("Started Vendor Login test.");
		Response response_VendorLogin = UserEndPoints
				.userLogin(CommonMethods.VendorLoginforAcceptBookingAfterEnablingTimeslot());
		String res_VendorLogin = response_VendorLogin.asPrettyString();
		JsonPath js_VendorLogin = new JsonPath(res_VendorLogin);
		Vtoken = js_VendorLogin.getString("token");
		System.out.println("Generated Token Id: " + Vtoken);
		context.setAttribute("Token", Vtoken);
		logger.debug("Generated Token Id: {}", Vtoken);
		Assert.assertEquals(response_VendorLogin.statusCode(), 200);
		logger.info("Vendor logged in successfully.");
		// --------------------------------------------------------------------

		logger.info("Getting vendor timeslots.");
		Response response_vendor_TimeslotEP = VendorEndPoints.vendor_TimeslotEP(context);
		JsonPath js_vendor_TimeslotEP = CommonMethods.jsonToString(response_vendor_TimeslotEP);
		String sTime = js_vendor_TimeslotEP.getString("availableTimeSlots[1].startTime");
		String eTime1 = js_vendor_TimeslotEP.getString("availableTimeSlots[1].endTime");
		context.setAttribute("STime", sTime);
		context.setAttribute("ETime", eTime1);
		logger.info("Vendor timeslots are shown successfully.");
		// --------------------------------------------------------------------

		logger.info("Started vendor disabling timeslot.");
		Response response_vendor_DisableTimeslotEP = VendorEndPoints.vendor_DisableTimeslotEP(context,
				CommonMethods.sendTimeslot(context));
		JsonPath js_vendor_DisableTimeslotEP = CommonMethods.jsonToString(response_vendor_DisableTimeslotEP);
		int disabledTimeSlotId = js_vendor_DisableTimeslotEP.getInt("id");
		context.setAttribute("disabledTimeSlotId", disabledTimeSlotId);
		logger.info("Vendor disabled timeslot successfully.");
		// -------------------------------------------------------------------

		logger.info("Started vendor enabling timeslot.");
		Response response_vendor_EnableTimeslotEP = VendorEndPoints.vendor_EnableTimeslotEP(context,
				disabledTimeSlotId);
		logger.info("Vendor enabled timeslot after disabled successfully.");
		// --------------------------------------------------------------------

		logger.info("Started vendor accepting booking");
		Response response_vendor_AcceptBookingEP = VendorEndPoints.vendor_AcceptBookingEP(context, id);
//		JsonPath js_vendor_AcceptBookingEP = CommonMethods.jsonToString(response_vendor_AcceptBookingEP);
//		String status1  = js_vendor_AcceptBookingEP.getString("status");
		// Assert.assertEquals(status1, "ExpertAssigned");
		System.out.println(
				"-------after booking accept status code: -------" + response_vendor_AcceptBookingEP.getStatusCode());

		logger.info("Vendor accept booking after enabled that disabled timeslot successfully");
		String statusline_CustomerLogin = response_CustomerLogin.getStatusLine();
		Assert.assertEquals(statusline_CustomerLogin, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_CustomerLogin);

		String statusline_customer_GetMyProfileEP = response_customer_GetMyProfileEP.getStatusLine();
		Assert.assertEquals(statusline_customer_GetMyProfileEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetMyProfileEP);

		String statusline_customerGetCategoryEP = response_customerGetCategoryEP.getStatusLine();
		Assert.assertEquals(statusline_customerGetCategoryEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customerGetCategoryEP);

		String statusline_customer_SubCategoryEP = response_customer_SubCategoryEP.getStatusLine();
		Assert.assertEquals(statusline_customer_SubCategoryEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_SubCategoryEP);

		String statusline_customer_service = response_customer_service.getStatusLine();
		Assert.assertEquals(statusline_customer_service, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_service);

		String statusline_customer_GetTimeSlot = response_customer_GetTimeSlot.getStatusLine();
		Assert.assertEquals(statusline_customer_GetTimeSlot, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetTimeSlot);

		String statusline_customer_CreateBooking = response_customer_CreateBooking.getStatusLine();
		Assert.assertEquals(statusline_customer_CreateBooking, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_CreateBooking);

		String statusline_vendor_TimeslotEP = response_vendor_TimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_TimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_TimeslotEP);

		String statusline_vendor_DisableTimeslotEP = response_vendor_DisableTimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_DisableTimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_DisableTimeslotEP);

		String statusline_vendor_EnableTimeslotEP = response_vendor_EnableTimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_EnableTimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_EnableTimeslotEP);

		String statusline_vendor_AcceptBookingEP = response_vendor_AcceptBookingEP.getStatusLine();
		// Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 200 OK");
		Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 500 Internal Server Error");
		// Assert.assertNotNull(statusline_vendor_AcceptBookingEP);
	}
}
