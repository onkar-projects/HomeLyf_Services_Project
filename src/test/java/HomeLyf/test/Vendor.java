package HomeLyf.test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.concurrent.TimeUnit;

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
	static int BookingId;
	static int Id;
	static String StartTime;
	static String EndTime;
	static int id;

	@Test(groups = "Vendor", priority = 1, enabled = true, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public static void VendorLogin(ITestContext context, String mobileNumber, String type, String emailAddress,
			String password, String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		Vtoken = js.getString("token");
		System.out.println("Generated Token Id: " + Vtoken);
		context.setAttribute("VToken", Vtoken);
		logger.debug("Generated Token Id: {}", Vtoken);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(groups = "Vendor", priority = 2, enabled = false, description = "Vendor should get bookings")
	public void vendor_get_booking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		Response response = VendorEndPoints.vendorgetbooking(context, 1, 1000);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int BookingId = js.getInt("[1].id");
		System.out.println(BookingId);
		context.setAttribute("BookingId", BookingId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("bookingId list generated successfully");
	}

	@Test(groups = "Vendor", priority = 3, enabled = false, description = "Vendor should get booking details through bookingId")
	public void vendor_getBookingByID(ITestContext context) {
		logger.info("Getting Booking details by Id");
		Response response = VendorEndPoints.vendor_GetBookingByIdEP(context, (int) context.getAttribute("BookingId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		response.then().log().all();
//		int BookingId = js.getInt("id");
//		context.setAttribute("BookingId", BookingId);
		logger.info("Get booking details: " + js.getString("services[0].name") + "with bookingId: " + (int) context.getAttribute("BookingId"));
	}

	@Test(priority = 4, enabled = true, description = "Vendor should get my booking")
	public void vendor_myBooking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		String[] status = { "New", "ExpertAssigned", "Inprogress", "Cancelled", "Completed" };
		Response response = VendorEndPoints.vendor_MybookingEP(context, status[1], 1, 1000);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String scTime = js.getString("scheduledOn");
	}

	@Test(groups = "Vendor", priority = 5, enabled = false, description = "Vendor should acccept booking")
	public void vendor_acceptBooking(ITestContext context) {
		logger.info("Vendor accept booking");
		Response response = VendorEndPoints.vendor_AcceptBookingEP(context, (int) context.getAttribute("BookingId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("status");
		Assert.assertEquals(status, "ExpertAssigned");
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Vendor accept booking with status : " + status);
	}

	@Test(groups = "Vendor", priority = 6, enabled = true, description = "Vendor should start booking using startOtp")
	public void vendor_StartBookingTest(ITestContext context) {
		logger.info("Start vendor booking using startOtp");
		
		LookUp.customer_GetBookingByIdTest(context);
		Response response = VendorEndPoints.vendor_startBookingEP(context, CommonMethods.sendBookingIdAndOtp(context));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("InProgress");
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Vendor start service with status : " + status);
	}

	@Test(groups = "Vendor", priority = 7, enabled = false, description = "Vendor should complete booking using endOtp")
	public void vendor_CompleteBookingTest(ITestContext context) {
		logger.info("Complete vendor booking using endOtp");
		LookUp.customer_GetBookingByIdTest(context);
		Response response = VendorEndPoints.vendor_completeBookingEP(context,
				CommonMethods.sendBookingIdAndOtp(context));
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("Completed");
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Vendor complete service with status : " + status);
	}

	@Test(groups = "Vendor", priority = 8, enabled = false, description = "Vendor should cancel booking after accept by vendor")
	public void vendor_cancelBooking(ITestContext context) {
		logger.info("Cancel booking after booking completed by vendor");
		LookUp.customer_GetBookingByIdTest(context);
		int bookingId = (int) context.getAttribute("bookingId");
		Response response = VendorEndPoints.vendorCancelBooking(context, bookingId);
		JsonPath js = CommonMethods.jsonToString(response);
		Assert.assertEquals(response.getStatusCode(), 400);
		logger.info("Vendor can not cancel booking after completed service");
	}

	@Test(groups = "Vendor", priority = 9, enabled = false, description = "Vendor should get available time slot")
	public void vendor_TimeslotTest(ITestContext context) {
		logger.info("Getting vendor available time slots");
		Response response = VendorEndPoints.vendor_TimeslotEP(context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String sTime = js.getString("availableTimeSlots[2].startTime");
		String eTime = js.getString("availableTimeSlots[2].endTime");
		context.setAttribute("StartTime", sTime);
		context.setAttribute("EndTime", eTime);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Selected Timeslot with StartTime : " + sTime + " & EndTime : " + eTime);
	}

	@Test(groups = "Vendor", priority = 10, enabled = false, description = "Vendor should have disable time slot")
	public void vendor_DisableTimeslotTest(ITestContext context) {
		logger.info("Disabling Vendor time slot");
		Response response = VendorEndPoints.vendor_DisableTimeslotEP(context, CommonMethods.sendTimeslot(context));
		JsonPath js = CommonMethods.jsonToString(response);
		int disabledTimeSlotId = js.getInt("id");
		context.setAttribute("disabledTimeSlotId", disabledTimeSlotId);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Disable TimesotId : " + disabledTimeSlotId);
	}

	@Test(groups = "Vendor", priority = 11, enabled = false, description = "Vendor should have enable time slot")
	public void vendor_EnableTimeslotTest(ITestContext context) {
		logger.info("Enable vendor time slot after disabled");
		Response response = VendorEndPoints.vendor_EnableTimeslotEP(context,
				(int) context.getAttribute("disabledTimeSlotId"));
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Timeslot enabled successfully");
	}

	@Test(groups = "Vendor", priority = 12, enabled = false, description = "Vendot should get profile")
	public void vendor_ProfileTest(ITestContext context) {
		logger.info("get vendor profile");
		Response response = VendorEndPoints.vendor_ProfileEP(context);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Vendor profile details getting successfully");
	}

	//Monika
	@Test(priority = 13, enabled = false, description = "Customer Login to Booking a Service method")
	public static void customerLogintoBookingService(ITestContext context) {
		logger.info("Started Customer Login test.");
		Response response_CustomerLogin = UserEndPoints.userLogin(CommonMethods.CustomerLoginformultiplescenario());
		response_CustomerLogin.then().log().all();
		String res_CustomerLogin = response_CustomerLogin.asPrettyString();
		JsonPath js_CustomerLogin = new JsonPath(res_CustomerLogin);
		Ctoken = js_CustomerLogin.getString("token");
		System.out.println("Generated Token Id: " + Ctoken);
		context.setAttribute("CToken", Ctoken);
		String statusline_CustomerLogin = response_CustomerLogin.getStatusLine();
		Assert.assertEquals(statusline_CustomerLogin, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_CustomerLogin);
		logger.debug("Generated Token Id: {}", Ctoken);
		logger.info("Customer logged in successfully.");
		// -----------------------------------------------------------------
		logger.info("Getting Customer profile.");
		Response response_customer_GetMyProfileEP = CustomerEndPoints.customer_GetMyProfileEP(context);
		response_customer_GetMyProfileEP.then().log().all();
		JsonPath js_customer_GetMyProfileEP = CommonMethods.jsonToString(response_customer_GetMyProfileEP);
		int addressId = js_customer_GetMyProfileEP.getInt("addresses[1].id");
		context.setAttribute("addressId", addressId);
		String statusline_customer_GetMyProfileEP = response_customer_GetMyProfileEP.getStatusLine();
		Assert.assertEquals(statusline_customer_GetMyProfileEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetMyProfileEP);
		logger.info("Customer profile shown successfully.");
		System.out.println("address id is " + addressId);
		// -------------------------------------------------------

		logger.info("Getting category.");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response_customerGetCategoryEP = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCode2"), (String) context.getAttribute("name3"));
		response_customerGetCategoryEP.then().log().all();
		JsonPath js_customerGetCategoryEP = CommonMethods.jsonToString(response_customerGetCategoryEP);
		int categoryId = js_customerGetCategoryEP.get("[4].id");
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js_customerGetCategoryEP.getString("[3].name"), "Carpentry");
		String statusline_customerGetCategoryEP = response_customerGetCategoryEP.getStatusLine();
		Assert.assertEquals(statusline_customerGetCategoryEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customerGetCategoryEP);
		logger.info(js_customerGetCategoryEP.getString("[3].name") + " Category selected successfully.");
		System.out.println(js_customerGetCategoryEP.getString("[3].name"));
		// -----------------------------------------------------------------

		logger.info("Getting subcategory.");
		Response response_customer_SubCategoryEP = CustomerEndPoints.customer_SubCategoryEP(context,
				(int) context.getAttribute("categoryId3"));
		response_customer_SubCategoryEP.then().log().all();
		JsonPath js_customer_SubCategoryEP = CommonMethods.jsonToString(response_customer_SubCategoryEP);
		int subCategoryId = js_customer_SubCategoryEP.getInt("[0].id");
		context.setAttribute("subCategoryId", subCategoryId);
		String statusline_customer_SubCategoryEP = response_customer_SubCategoryEP.getStatusLine();
		Assert.assertEquals(statusline_customer_SubCategoryEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_SubCategoryEP);
		logger.info("SubCategory is shown Successfully.");
		// ---------------------------------------------------------------

		logger.info("Getting services.");
		Response response_customer_service = CustomerEndPoints.customer_service(context,
				(int) context.getAttribute("subCategoryId"));
		response_customer_service.then().log().all();
		JsonPath js_customer_service = CommonMethods.jsonToString(response_customer_service);
		int serviceId = js_customer_service.getInt("[0].id");
		int serviceDuration = js_customer_service.getInt("[0].duration");
		String servicename = js_customer_service.getString("[0].name");
		context.setAttribute("serviceId", serviceId);
		String statusline_customer_service = response_customer_service.getStatusLine();
		Assert.assertEquals(statusline_customer_service, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_service);
		logger.info(servicename + " Service selected successfully.");
		// ----------------------------------------------------------------------------

		logger.info("Getting timeslot for category = " + js_customerGetCategoryEP.getString("[3].name")
				+ " and Service name is = " + servicename);
		Response response_customer_GetTimeSlot = CustomerEndPoints.customer_GetTimeSlot(
				(int) context.getAttribute("addressId"), (int) context.getAttribute("categoryId3"), context);
		response_customer_GetTimeSlot.then().log().all();
		JsonPath js_customer_GetTimeSlot = CommonMethods.jsonToString(response_customer_GetTimeSlot);
		StartTime = js_customer_GetTimeSlot.getString("[18].startTime");
		context.setAttribute("StartTime", StartTime);
		EndTime = js_customer_GetTimeSlot.getString("[18].endTime");
		context.setAttribute("EndTime", EndTime);
		System.out.println("Start Time: " + StartTime + "\n End Time: " + EndTime);
		String statusline_customer_GetTimeSlot = response_customer_GetTimeSlot.getStatusLine();
		Assert.assertEquals(statusline_customer_GetTimeSlot, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetTimeSlot);
		logger.info("Category timeslot " + "Start Time: " + StartTime + "\t End Time: " + EndTime + " successfully.");
		// --------------------------------------------------------------------

		logger.info("Started creating new Booking.");
		Response response_customer_CreateBooking = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response_customer_CreateBooking.then().log().all();
		JsonPath js_customer_CreateBooking = CommonMethods.jsonToString(response_customer_CreateBooking);
		String status = js_customer_CreateBooking.getString("status");
		BookingId = js_customer_CreateBooking.getInt("id");
		context.setAttribute("BookingId", BookingId);
		// int bookingId = js5.getInt("id");
		// context.setAttribute("bookingId", bookingId);
		Assert.assertEquals(status, "New");
		String statusline_customer_CreateBooking = response_customer_CreateBooking.getStatusLine();
		Assert.assertEquals(statusline_customer_CreateBooking, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_CreateBooking);
		logger.info("New booking created successfully and Booking id is " + BookingId);
	}

	//Monika
	@Test(priority = 14, enabled = false, description = "Verify that vendor accept booking after enabled that disabled timeslot")
	public void vendor_AcceptBookingAfterEnablingTimeslot(ITestContext context) {
		logger.info("Started vendor accept booking after enabled that disabled timeslot");
		Vendor.customerLogintoBookingService(context);
		// -------------------------------------------------------------------------
		logger.info("Started Vendor Login test.");
		Response response_VendorLogin = UserEndPoints.userLogin(CommonMethods.VendorLoginformultiplescenario());
		JsonPath js_VendorLogin = CommonMethods.jsonToString(response_VendorLogin);
		Vtoken = js_VendorLogin.getString("token");
		System.out.println("Generated Token Id: " + Vtoken);
		context.setAttribute("VToken", Vtoken);
		logger.debug("Generated Token Id: {}", Vtoken);
		Assert.assertEquals(response_VendorLogin.statusCode(), 200);
		logger.info("Vendor logged in successfully.");
		// --------------------------------------------------------------------
		logger.info("Getting vendor timeslots.");
		Response response_vendor_TimeslotEP = VendorEndPoints.vendor_TimeslotEP(context);
		JsonPath js_vendor_TimeslotEP = CommonMethods.jsonToString(response_vendor_TimeslotEP);
		response_vendor_TimeslotEP.then().log().all();
//		String sTime1 = js_vendor_TimeslotEP.getString("availableTimeSlots[3].startTime");
//		String eTime1 = js_vendor_TimeslotEP.getString("availableTimeSlots[3].endTime");
//		context.setAttribute("StartTime", StartTime);
//		context.setAttribute("EndTime", EndTime);
		String statusline_vendor_TimeslotEP = response_vendor_TimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_TimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_TimeslotEP);
		logger.info("Vendor timeslots are shown successfully.");
		// --------------------------------------------------------------------
		logger.info("Started vendor disabling timeslot.");
		Response response_vendor_DisableTimeslotEP = VendorEndPoints.vendor_DisableTimeslotEP(context,
				CommonMethods.sendTimeslot(context));
		JsonPath js_vendor_DisableTimeslotEP = CommonMethods.jsonToString(response_vendor_DisableTimeslotEP);
		String ab = response_vendor_DisableTimeslotEP.asPrettyString();
		System.out.println(ab);
		int disabledTimeSlotId = js_vendor_DisableTimeslotEP.getInt("id");
		context.setAttribute("disabledTimeSlotId", disabledTimeSlotId);
		String statusline_vendor_DisableTimeslotEP = response_vendor_DisableTimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_DisableTimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_DisableTimeslotEP);
		logger.info("Vendor disabled " + StartTime + "\t" + EndTime + " timeslot successfully with id " + disabledTimeSlotId);
		// -------------------------------------------------------------------
		logger.info("Started vendor enabling timeslot of id " + disabledTimeSlotId);
		Response response_vendor_EnableTimeslotEP = VendorEndPoints.vendor_EnableTimeslotEP(context,
				disabledTimeSlotId);
		String statusline_vendor_EnableTimeslotEP = response_vendor_EnableTimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_EnableTimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_EnableTimeslotEP);
		logger.info("Vendor enabled timeslot of " + StartTime + "\t" + EndTime + " after disabled successfully.");
		// --------------------------------------------------------------------
		logger.info("Started vendor accepting booking of Booking id = " + (int) context.getAttribute("BookingId"));
		Response response_vendor_AcceptBookingEP = VendorEndPoints.vendor_AcceptBookingEP(context,
				(int) context.getAttribute("BookingId"));
		JsonPath js_vendor_AcceptBookingEP = CommonMethods.jsonToString(response_vendor_AcceptBookingEP);
		String status1 = js_vendor_AcceptBookingEP.getString("status");
		Assert.assertEquals(status1, "ExpertAssigned");
		System.out.println(
				"-------after booking accept status code: -------" + response_vendor_AcceptBookingEP.getStatusCode());
		String statusline_vendor_AcceptBookingEP = response_vendor_AcceptBookingEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(statusline_vendor_AcceptBookingEP);
		logger.info("Vendor accepted booking id of " + BookingId
				+ " after enabled that disabled timeslot successfully");
	}

	//Monika
	@Test(priority = 15, enabled = false, description = "Verify that vendor cannot accept booking of timeslot YYYY-MM-DDT12:30:00Z for other customer as vendor already have accepted booking of timeslot YYYY-MM-DDT12:00:00Z due to +15 minute buffer time.")
	public void vendor_cantAcceptBookingduetoBufferTimeslot(ITestContext context) {
		logger.info(
				"Verify that vendor cannot accept booking of timeslot YYYY-MM-DDT12:30:00Z for other customer as vendor already have accepted booking of timeslot YYYY-MM-DDT12:00:00Z due to +15 minute buffer time.");
		Vendor.customerLogintoBookingService(context);
		// -----------------------------------------------------------------------
		logger.info("Started Vendor Login test.");
		Response response_VendorLogin = UserEndPoints.userLogin(CommonMethods.VendorLoginformultiplescenario());
		String res_VendorLogin = response_VendorLogin.asPrettyString();
		JsonPath js_VendorLogin = new JsonPath(res_VendorLogin);
		Vtoken = js_VendorLogin.getString("token");
		System.out.println("Generated Token Id: " + Vtoken);
		context.setAttribute("VToken", Vtoken);
		logger.debug("Generated Token Id: {}", Vtoken);
		Assert.assertEquals(response_VendorLogin.statusCode(), 200);
		logger.info("Vendor logged in successfully.");
		// --------------------------------------------------------------------
		logger.info("Started vendor accepting booking of Booking id = " + (int) context.getAttribute("BookingId"));
		Response response_vendor_AcceptBookingEP = VendorEndPoints.vendor_AcceptBookingEP(context,
				(int) context.getAttribute("BookingId"));
		JsonPath js_vendor_AcceptBookingEP = CommonMethods.jsonToString(response_vendor_AcceptBookingEP);
		String status1 = js_vendor_AcceptBookingEP.getString("status");
		Assert.assertEquals(status1, "ExpertAssigned");
		System.out.println(
				"-------after booking accept status code: -------" + response_vendor_AcceptBookingEP.getStatusCode());
		String statusline_vendor_AcceptBookingEP = response_vendor_AcceptBookingEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(statusline_vendor_AcceptBookingEP);
		logger.info("Vendor accepted booking id of " + (int) context.getAttribute("BookingId") + " successfully");
		// ---------------------------------------------------------------------
		logger.info("Getting Customer profile.");
		Response response_customer_GetMyProfileEP = CustomerEndPoints.customer_GetMyProfileEP(context);
		response_customer_GetMyProfileEP.then().log().all();
		JsonPath js_customer_GetMyProfileEP = CommonMethods.jsonToString(response_customer_GetMyProfileEP);
		int addressId = js_customer_GetMyProfileEP.getInt("addresses[1].id");
		context.setAttribute("addressId", addressId);
		String statusline_customer_GetMyProfileEP = response_customer_GetMyProfileEP.getStatusLine();
		Assert.assertEquals(statusline_customer_GetMyProfileEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetMyProfileEP);
		logger.info("Customer profile shown successfully.");
		System.out.println("address id is " + addressId);
		// -------------------------------------------------------

		logger.info("Getting category.");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response_customerGetCategoryEP = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCode2"), (String) context.getAttribute("name3"));
		response_customerGetCategoryEP.then().log().all();
		JsonPath js_customerGetCategoryEP = CommonMethods.jsonToString(response_customerGetCategoryEP);
		int categoryId = js_customerGetCategoryEP.get("[4].id");
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js_customerGetCategoryEP.getString("[3].name"), "Carpentry");
		String statusline_customerGetCategoryEP = response_customerGetCategoryEP.getStatusLine();
		Assert.assertEquals(statusline_customerGetCategoryEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customerGetCategoryEP);
		logger.info(js_customerGetCategoryEP.getString("[3].name") + " Category selected successfully.");
		System.out.println(js_customerGetCategoryEP.getString("[3].name"));
		// -----------------------------------------------------------------

		logger.info("Getting subcategory.");
		Response response_customer_SubCategoryEP = CustomerEndPoints.customer_SubCategoryEP(context,
				(int) context.getAttribute("categoryId3"));
		response_customer_SubCategoryEP.then().log().all();
		JsonPath js_customer_SubCategoryEP = CommonMethods.jsonToString(response_customer_SubCategoryEP);
		int subCategoryId = js_customer_SubCategoryEP.getInt("[0].id");
		context.setAttribute("subCategoryId", subCategoryId);
		String statusline_customer_SubCategoryEP = response_customer_SubCategoryEP.getStatusLine();
		Assert.assertEquals(statusline_customer_SubCategoryEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_SubCategoryEP);
		logger.info("SubCategory is shown Successfully.");
		// ---------------------------------------------------------------

		logger.info("Getting services.");
		Response response_customer_service = CustomerEndPoints.customer_service(context,
				(int) context.getAttribute("subCategoryId"));
		response_customer_service.then().log().all();
		JsonPath js_customer_service = CommonMethods.jsonToString(response_customer_service);
		int serviceId = js_customer_service.getInt("[0].id");
		int serviceDuration = js_customer_service.getInt("[0].duration");
		String servicename = js_customer_service.getString("[0].name");
		context.setAttribute("serviceId", serviceId);
		String statusline_customer_service = response_customer_service.getStatusLine();
		Assert.assertEquals(statusline_customer_service, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_service);
		logger.info(servicename + " Service selected successfully.");
		// ----------------------------------------------------------------------------

		logger.info("Getting timeslot for category = " + js_customerGetCategoryEP.getString("[3].name")
				+ " and Service name is = " + servicename);
		Response response_customer_GetTimeSlot = CustomerEndPoints.customer_GetTimeSlot(
				(int) context.getAttribute("addressId"), (int) context.getAttribute("categoryId3"), context);
		response_customer_GetTimeSlot.then().log().all();
		JsonPath js_customer_GetTimeSlot = CommonMethods.jsonToString(response_customer_GetTimeSlot);
		String sTime1 = js_customer_GetTimeSlot.getString("[17].startTime");
		context.setAttribute("StartTime", sTime1);
		String eTime1 = js_customer_GetTimeSlot.getString("[17].endTime");
		System.out.println("Start Time: " + sTime1 + "\n End Time: " + eTime1);
		String statusline_customer_GetTimeSlot = response_customer_GetTimeSlot.getStatusLine();
		Assert.assertEquals(statusline_customer_GetTimeSlot, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetTimeSlot);
		logger.info("Category timeslot " + "Start Time: " + sTime1 + "\t End Time: " + eTime1 + " successfully.");
		// --------------------------------------------------------------------

		logger.info("Started creating new Booking.");
		Response response_customer_CreateBooking = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response_customer_CreateBooking.then().log().all();
		JsonPath js_customer_CreateBooking = CommonMethods.jsonToString(response_customer_CreateBooking);
		String status = js_customer_CreateBooking.getString("status");
		Id = js_customer_CreateBooking.getInt("id");
		context.setAttribute("BookingId", Id);
		Assert.assertEquals(status, "New");
		String statusline_customer_CreateBooking = response_customer_CreateBooking.getStatusLine();
		Assert.assertEquals(statusline_customer_CreateBooking, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_CreateBooking);
		logger.info("New booking created successfully and Booking id is " + Id);
		// ---------------------------------------------------------------------

		logger.info("Started vendor accepting booking of Booking id = " + Id);
		Response response_vendor_AcceptBookingEP2 = VendorEndPoints.vendor_AcceptBookingEP(context, Id);
		String statusline_vendor_AcceptBookingEP2 = response_vendor_AcceptBookingEP2.getStatusLine();
		Assert.assertEquals(statusline_vendor_AcceptBookingEP2, "HTTP/1.1 400 Bad Request");
		logger.info("Vendor is not able to accept booking of id = " + Id);
		logger.info("Vendor cannot accept booking of timeslot " + sTime1
				+ " for other customer as vendor already have accepted booking of timeslot " + StartTime
				+ " due to +15 minute buffer time.");
	}

	// $******
	@Test(priority = 16, enabled = false, description = "Verify that vendor disabled timeslot after started service")
	public void vendorDisabledTimeslotAfterStartService(ITestContext context) {

		// -------------------------------CreateNewbooking------------------------------------------
		// LookUp.createBookingBasedOnAvailableTimeslot(context);
		
		// ----------------------------customergetBookingById----------------------------------------
		logger.info("Getting StartOTP through Customer Get Booking By Id");
		Response customerBookingById_res = CustomerEndPoints.customer_GetBookingByIdEP(context,
				(int) context.getAttribute("bookingId"));
		customerBookingById_res.then().log().all();
		JsonPath customerbookingIdjs = CommonMethods.jsonToString(customerBookingById_res);
		int startOTP = customerbookingIdjs.get("startOTP");
		System.out.println(startOTP);
		context.setAttribute("startOTP", startOTP);
		int customerBookingId = customerbookingIdjs.get("id");
		context.setAttribute("bookingId", customerBookingId);
		Assert.assertEquals(customerBookingById_res.statusCode(), 200);
		Assert.assertEquals(customerBookingById_res.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(customerBookingById_res, "startOTP getting succesfully");
		logger.info("Start OTP for BookingId " + customerBookingId + " is " + startOTP);
		// ------------------------------VendorStartService-----------------------------------------
		logger.info("Starting Service for BookingId " + customerBookingId + " and startOTP " + startOTP);
		Response startService_response = VendorEndPoints.vendor_startBookingEP(context,
				CommonMethods.sendBookingIdAndOtp(context));
		startService_response.then().log().all();
		JsonPath startservicejs = CommonMethods.jsonToString(startService_response);
		String statusafterstartservice = startservicejs.getString("status");
		String scheduledOn = startservicejs.getString("scheduledOn");
		Assert.assertEquals(startService_response.getStatusCode(), 200);
		Assert.assertEquals(startService_response.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(startService_response, "Service started succesfully");
		logger.info("Service started succesfully for BookingId " + customerBookingId);
		// -------------------------------VendorGetTimeslot---------------------------------------------
		logger.info("Vendor getting available Timeslot");
		Response vendorTimeslot_res = VendorEndPoints.vendor_TimeslotEP(context);
		vendorTimeslot_res.then().log().all();
		JsonPath vendorTimeslotjs = CommonMethods.jsonToString(vendorTimeslot_res);
		String endTime = null;
		for (int i = 0; i < 20; i++) {
			if (scheduledOn.equalsIgnoreCase(vendorTimeslotjs.getString("bookedTimeSlots[" + i + "].startTime"))) {
				endTime = vendorTimeslotjs.getString("bookedTimeSlots[" + i + "].endTime");
			}
		}
		System.out.println("endTime " + endTime);
		Assert.assertEquals(vendorTimeslot_res.getStatusCode(), 200);
		Assert.assertEquals(vendorTimeslot_res.getStatusLine(), "HTTP/1.1 200 OK");
		logger.info("Available Timeslot : " + "startTime: " + scheduledOn + " endTime: " + endTime);
		// -------------------------------VendorDisableTimeSlot-------------------------------------------
		logger.info("Venodr Disable Timeslot");
		Response disableTimeslot_res = VendorEndPoints.vendor_DisableTimeslotEP(context,
				CommonMethods.sendTimeslot(context, scheduledOn, endTime));
		disableTimeslot_res.then().log().all();
		JsonPath disableTimeslotjs = CommonMethods.jsonToString(disableTimeslot_res);
		Assert.assertEquals(disableTimeslot_res.getStatusCode(), 400);
		Assert.assertEquals(disableTimeslotjs.getString("[0]"), "Cannot diable already booked timeslot");
		Assert.assertNotNull(disableTimeslot_res, "getting disable timeslot response successfully");
		logger.info("Disable Timeslot: " + disableTimeslotjs.getString("[0]"));
	}

	// $*****
	@Test(priority = 17, enabled = false, description = "Verify that Vendor Accept Service From Differnt Category Service")
	public void verifyVendorAcceptServiceFromDifferntCategoryService(ITestContext context) {
		// --------------------------------Customerlogin----------------------------------
		logger.info("Customer Login start");
		Response cresponse = CustomerEndPoints.customer_Login(CommonMethods.customer_Login(), context);
		JsonPath cloginjs = CommonMethods.jsonToString(cresponse);
		String Ctoken = cloginjs.getString("token");
		context.setAttribute("CToken", Ctoken);
		logger.debug("Generated Token Id: {}", Ctoken);
		logger.info("Customer logged in successfully " + Ctoken);
		Assert.assertEquals(cresponse.statusCode(), 200);
		Assert.assertEquals(cresponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(cresponse, "Customer Login response is getting succesfully");
		// -------------------------------Get categoryId------------------------------
		logger.info("Getting categoryId");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response1 = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCode1"), "");
		response1.then().log().all();
		JsonPath js1 = CommonMethods.jsonToString(response1);
		int categoryId = js1.getInt("[4].id");
		String categoryName = js1.getString("[4].name");
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js1.getString("[4].name"), "Cleaning");
		Assert.assertEquals(response1.statusCode(), 200);
		logger.info("CategoryId fetched successfully :" + categoryId + " of category " + categoryName);
		// ----------------------------GetsubCategoryId--------------------------------------
		logger.info("Getting subCategoryId");
		Response response2 = CustomerEndPoints.customer_SubCategoryEP(context,
				(int) context.getAttribute("categoryId"));
		response2.then().log().all();
		JsonPath js2 = CommonMethods.jsonToString(response2);
		int subCategoryId = js2.getInt("[1].id");
		String subCategoryName = js2.getString("[1].name");
		context.setAttribute("subCategoryId", subCategoryId);
		Assert.assertEquals(response2.getStatusCode(), 200);
		logger.info("SubCategoryId Fetched Successfully :" + subCategoryId + " of subCategory :" + subCategoryName);
		// ---------------------------Get
		// customerServiceId------------------------------------
		logger.info("Starting customer_service");
		Response response3 = CustomerEndPoints.customer_service(context, (int) context.getAttribute("subCategoryId"));
		response3.then().log().all();
		JsonPath js3 = CommonMethods.jsonToString(response3);
		int serviceId = js3.getInt("[0].id");
		String serviceName = js3.getString("[0].name");
		context.setAttribute("serviceId", serviceId);
		Assert.assertEquals(response3.statusCode(), 200);
		logger.info("customer_service subcategory is shown successfully " + serviceId + " of service " + serviceName);
		// -----------------------------Customerprofile---------------------------------------
		logger.info("Getting Customer profile");
		Response response4 = CustomerEndPoints.customer_GetMyProfileEP(context);
		response4.then().log().all();
		JsonPath js4 = CommonMethods.jsonToString(response4);
		int addressId = js4.get("addresses[0].id");
		context.setAttribute("addressId", addressId);
		Assert.assertEquals(response4.statusCode(), 200);
		logger.info("Customer profile shown successfully " + addressId);
		// ---------------------Get CustomerTimeslot-----------------------------------------
		logger.info("Getting Customer Timeslot for book service");
		Response response5 = CustomerEndPoints.customer_GetTimeSlot((int) context.getAttribute("addressId"),
				(int) context.getAttribute("categoryId"), context);
		response5.then().log().all();
		JsonPath js5 = CommonMethods.jsonToString(response5);
		String sTime = js5.getString("[3].startTime");
		context.setAttribute("StartTime", sTime);
		String eTime = js5.getString("[3].endTime");
		System.out.println("Start Time: " + sTime + "\n End Time: " + eTime);
		Assert.assertEquals(response5.statusCode(), 200);
		logger.info("Available booking Timeslot with startTime " + sTime + " endTime " + eTime);
		// -------------------Create new booking-----------------------------------------
		logger.info("Creating new Booking");
		Response response6 = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response6.then().log().all();
		JsonPath js6 = CommonMethods.jsonToString(response6);
		String categoryname = js6.getString("categoryName");
		int bookingId = js6.getInt("id");
		context.setAttribute("bookingId", bookingId);
		Assert.assertEquals(response6.statusCode(), 200);
		Assert.assertEquals(js6.getString("status"), "New");
		logger.info("New booking created successfully with bookingId :" + bookingId);
		// ----------------------------------VendorLogin---------------------------------------
		logger.info("Vendor Login start");
		Response vresponse = VendorEndPoints.vendor_Login(context, CommonMethods.vendor_Login());
		JsonPath vloginjs = CommonMethods.jsonToString(vresponse);
		String Vtoken = vloginjs.getString("token");
		context.setAttribute("VToken", Vtoken);
		logger.debug("Generated Token Id: {}", Vtoken);
		logger.info("Vendor logged in successfully");
		Assert.assertEquals(vresponse.statusCode(), 200);
		Assert.assertEquals(vresponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(vresponse, "Vendor Login response is getting succesfully");
		// -------------------------VendorAcceptBooking-----------------------------------
		logger.info("Vendor accept booking with BookingId " + bookingId);
		Response acceptbooking_response = VendorEndPoints.vendor_AcceptBookingEP(context,
				(int) context.getAttribute("bookingId"));
		acceptbooking_response.then().log().all();
		Assert.assertEquals(acceptbooking_response.getStatusCode(), 400);
		Assert.assertEquals(acceptbooking_response.contentType(), "application/json; charset=utf-8");
		Assert.assertNotNull(acceptbooking_response, "Cannot accept booking");
		logger.info("vendor not accepted booking for different category " + categoryname + " with id " + bookingId);


	}

	// -----------------VendorAcceptBooking15minBeforeTimeSlot----------------------------
	// -----
	@Test(priority = 18, description = "verify that Vendor Accept Booking before 15 minute time slot")
    public void vendor_acceptBookingBeforeTimeSlot(ITestContext context) {
        // --------------------------------Customerlogin----------------------------------
        logger.info("Customer Login start");
        Response cresponse = CustomerEndPoints.customer_Login(CommonMethods.customer_Login(), context);
        JsonPath cloginjs = CommonMethods.jsonToString(cresponse);
        String Ctoken = cloginjs.getString("token");
        context.setAttribute("CToken", Ctoken);
        logger.debug("Generated Token Id: {}", Ctoken);
        logger.info("Customer logged in successfully {}", Ctoken);
        Assert.assertEquals(cresponse.statusCode(), 200);
        Assert.assertEquals(cresponse.statusLine(), "HTTP/1.1 200 OK");
        Assert.assertNotNull(cresponse, "Customer Login response is getting successfully");

        // -----------------------------Customerprofile---------------------------------------
        logger.info("Getting Customer profile");
        Response response4 = CustomerEndPoints.customer_GetMyProfileEP(context);
        response4.then().log().all();
        JsonPath js4 = CommonMethods.jsonToString(response4);
        int addressId = js4.get("addresses[0].id");
        context.setAttribute("addressId", addressId);
        Assert.assertEquals(response4.statusCode(), 200);
        logger.info("Customer profile shown successfully {}", addressId);

        // ------------------customerSelectingCategory--------------------
        logger.info("Getting category.");
        LookUp.getPostCode(context);
        LookUp.getCategory(context);
        Response response_customerGetCategoryEP = CustomerEndPoints.customer_GetCategoryEP(context,
                (String) context.getAttribute("postCode"), (String) context.getAttribute("name"));
        response_customerGetCategoryEP.then().log().all();
        JsonPath js_customerGetCategoryEP = CommonMethods.jsonToString(response_customerGetCategoryEP);
        int categoryId = js_customerGetCategoryEP.getInt("[0].id");
        context.setAttribute("categoryId", categoryId);
        Assert.assertEquals(js_customerGetCategoryEP.getString("[0].name"), "Painting");
        Assert.assertEquals(response_customerGetCategoryEP.statusLine(), "HTTP/1.1 200 OK");
        Assert.assertNotNull(response_customerGetCategoryEP);
        logger.info("{} Category selected successfully.", js_customerGetCategoryEP.getString("[0].name"));

        // -----------------------------------------------------------------

        logger.info("Getting subcategory.");
        Response response_customer_SubCategoryEP = CustomerEndPoints.customer_SubCategoryEP(context, categoryId);
        response_customer_SubCategoryEP.then().log().all();
        JsonPath js_customer_SubCategoryEP = CommonMethods.jsonToString(response_customer_SubCategoryEP);
        int subCategoryId = js_customer_SubCategoryEP.getInt("[0].id");
        context.setAttribute("subCategoryId", subCategoryId);
        Assert.assertEquals(response_customer_SubCategoryEP.statusLine(), "HTTP/1.1 200 OK");
        Assert.assertNotNull(response_customer_SubCategoryEP);
        logger.info("SubCategory is shown Successfully.");

        // ------------------customerServiceId------------------------------------
        logger.info("Starting customer_service");
        Response response3 = CustomerEndPoints.customer_service(context, subCategoryId);
        response3.then().log().all();
        JsonPath js3 = CommonMethods.jsonToString(response3);
        int serviceId = js3.getInt("[0].id");
        String serviceName = js3.getString("[0].name");
        context.setAttribute("serviceId", serviceId);
        Assert.assertEquals(response3.statusCode(), 200);
        logger.info("customer_service subcategory is shown successfully {} of service {}", serviceId, serviceName);

        // ---------------------Get CustomerTimeslot-----------------------------------------
        logger.info("Getting Customer Timeslot for book service");
        Response response5 = CustomerEndPoints.customer_GetTimeSlot(addressId, categoryId, context);
        response5.then().log().all();
        JsonPath js5 = CommonMethods.jsonToString(response5);
        String sTime = js5.getString("[3].startTime");
        String eTime = js5.getString("[3].endTime");
        context.setAttribute("StartTime", sTime);
        logger.info("Start Time: {} | End Time: {}", sTime, eTime);
        Assert.assertEquals(response5.statusCode(), 200);
        logger.info("Available booking Timeslot with startTime {} endTime {}", sTime, eTime);

        // -------------------Create new booking-----------------------------------------
        logger.info("Creating new Booking");
        Response response6 = CustomerEndPoints.customer_CreateBookingEndPoint(context,
                CommonMethods.createBooking(context));
        response6.then().log().all();
        JsonPath js6 = CommonMethods.jsonToString(response6);
        int bookingId = js6.getInt("id");
        String categoryname = js6.getString("categoryName");
        context.setAttribute("bookingId", bookingId);
        Assert.assertEquals(response6.statusCode(), 200);
        Assert.assertEquals(js6.getString("status"), "New");
        logger.info("New booking created successfully with bookingId: {}", bookingId);

        // ----------------------------------VendorLogin---------------------------------------
        logger.info("Vendor Login start");
        Response vresponse = VendorEndPoints.vendor_Login(context, CommonMethods.vendor_LoginV());
        JsonPath vloginjs = CommonMethods.jsonToString(vresponse);
        String Vtoken = vloginjs.getString("token");
        context.setAttribute("VToken", Vtoken);
        logger.debug("Generated Token Id: {}", Vtoken);
        logger.info("Vendor logged in successfully");
        Assert.assertEquals(vresponse.statusCode(), 200);
        Assert.assertEquals(vresponse.statusLine(), "HTTP/1.1 200 OK");
        Assert.assertNotNull(vresponse, "Vendor Login response is getting successfully");

        // ----vendor accept booking 15 minutes before---------
        logger.info("Vendor accepting booking before the time slot");
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime startTimeSlotDate = LocalDateTime.parse(sTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        long timeDifferenceMillis = java.time.Duration.between(currentTime, startTimeSlotDate).toMillis();
        System.out.println("current time is"+timeDifferenceMillis);
        long fifteenMinutesMillis = TimeUnit.MINUTES.toMillis(15);
        System.out.println("current time is"+fifteenMinutesMillis);

        if (  timeDifferenceMillis < fifteenMinutesMillis) {
            Response response = VendorEndPoints.vendor_AcceptBookingEP(context, bookingId);
            response.then().log().all();
            JsonPath js = CommonMethods.jsonToString(response);
            String status = js.getString("status");
           Assert.assertEquals(status, "ExpertAssigned", "Status mismatch");
            Assert.assertEquals(response.getStatusCode(), 200, "HTTP status code mismatch");
        } else {
           logger.info("Current time is not within 15 minutes before the start time slot, cannot accept booking");
            Assert.fail("Cannot accept booking, current time is not within 15 minutes before the start time slot");
        }
	}


	// $********
	@Test(priority = 19, enabled = false, dataProvider = "UnverifiedVendor", dataProviderClass = DataProviderClass.class)
	public void validateUnverifiedVendorFlow(String emailAddress, String mobileNumber, String password, String type,
			String location, ITestContext context) {
		logger.info("Starting userLogin test...");
		Response Vresponse = UserEndPoints
				.userLogin(CommonMethods.userLogin(emailAddress, mobileNumber, password, type, location));
		String res = Vresponse.asPrettyString();
		JsonPath js = new JsonPath(res);
		Vtoken = js.getString("token");
		System.out.println("Generated Token Id: " + Vtoken);
		context.setAttribute("VToken", Vtoken);
		logger.debug("Generated Token Id: {}", Vtoken);
		Assert.assertEquals(Vresponse.statusCode(), 200);
		logger.info("User logged in successfully");

		// Step 2: Get bookings for the vendor
		Response getBooking_response = VendorEndPoints.vendorgetbooking(context, 1, 100);
		getBooking_response.then().log().all();

		if (getBooking_response.getStatusCode() == 200) {
			JsonPath getBooking_js = CommonMethods.jsonToString(getBooking_response);
			// Check if there are bookings available
			if (getBooking_js.getList("$").size() > 0) {
				int bookingId = getBooking_js.getInt("[0].id");
				System.out.println(bookingId);

				// Attempt to accept the booking
				Response acceptBooking_response = VendorEndPoints.vendor_AcceptBookingEP(context, bookingId);
				acceptBooking_response.then().log().all();
				Assert.assertTrue(true, "Vendor is not verified, cannot accept booking");
				logger.error("Vendor is not verified, cannot accept booking");
			} else {
				// Handle case when no bookings are available
				System.out.println("No bookings available for this vendor.");
				logger.error("No bookings available for this vendor");
				// Optionally, you might want to log or assert this condition
			}
		} else {
			// Handle unexpected status code if needed
			System.out.println("Failed to retrieve bookings. Status code: " + getBooking_response.getStatusCode());
			// Optionally, you might want to log or assert this condition
		}
	}
}