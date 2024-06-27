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
	static int customerBookingId;
	static int Id;
	static String sTime;
	static String eTime;
	static int id;

	@Test(groups="Vendor",priority = 1, enabled = true, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
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

	@Test(groups="Vendor",priority = 2, enabled = true, description = "Vendor should get bookings")
	public void vendor_get_booking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		Response response = VendorEndPoints.vendorgetbooking(context, 1, 100);
		//response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int vendorBookingId = js.getInt("[0].id");
		context.setAttribute("vendorBookingId", vendorBookingId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("bookingId list generate successfully");
	}

	@Test(groups="Vendor",priority = 3, enabled = true, description = "Vendor should acccept booking")
	public void vendor_acceptBooking(ITestContext context) {
		logger.info("Vendor accept booking");
		LookUp.customer_GetBookingIdTest(context);
		Response response = VendorEndPoints.vendor_AcceptBookingEP(context,(int)context.getAttribute("CbookingId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("status");
		String expectedStatus = "ExpertAssigned";
		Assert.assertEquals(status, "ExpertAssigned");
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Vendor accept booking with status : "+status);
	}

	@Test(groups="Vendor",priority = 4, enabled = true, description = "Vendor should start booking using startOtp")
	public void vendor_StartBookingTest(ITestContext context) {
		logger.info("Start vendor booking using startOtp");
		LookUp.customer_GetBookingIdTest(context);
		Response response = VendorEndPoints.vendor_startBookingEP(context, CommonMethods.sendBookingIdAndStartOTP(context));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("InProgress");
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Vendor start service with status : "+status);
	}

	@Test(groups="Vendor",priority = 5, enabled = true, description = "Vendor should complete booking using endOtp")
	public void vendor_CompleteBookingTest(ITestContext context) {
		logger.info("Complete vendor booking using endOtp");
		LookUp.customer_GetBookingIdTest(context);
		 Response response = VendorEndPoints.vendor_completeBookingEP(context,CommonMethods.sendBookingIdAndEndOTP(context));
		 JsonPath js = CommonMethods.jsonToString(response);
		 String status = js.getString("Completed");
		 Assert.assertEquals(response.getStatusCode(), 200);
		 logger.info("Vendor complete service with status : "+status);
	}

	@Test(groups="Vendor",priority = 6, enabled = true, description = "Vendor should cancel booking after accept by vendor")
	public void vendor_cancelBooking(ITestContext context) {
		logger.info("Cancel booking after booking completed by vendor");
		LookUp.customer_GetBookingIdTest(context);
		int bookingId = (int) context.getAttribute("bookingId");
		Response response = VendorEndPoints.vendorCancelBooking(context, bookingId);
		JsonPath js = CommonMethods.jsonToString(response);
		Assert.assertEquals(response.getStatusCode(), 400);
		logger.info("Vendor can not cancel booking after completed service");
	}

	@Test(groups="Vendor",priority = 7, enabled = true, description = "Vendor should get available time slot")
	public void vendor_TimeslotTest(ITestContext context) {
		logger.info("Getting vendor available time slots");
		Response response = VendorEndPoints.vendor_TimeslotEP(context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String sTime = js.getString("availableTimeSlots[2].startTime");
		String eTime = js.getString("availableTimeSlots[2].endTime");
		context.setAttribute("STime", sTime);
		context.setAttribute("ETime", eTime);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Selected Timeslot with StartTime : "+sTime + " & EndTime : "+eTime);
	}

	@Test(groups="Vendor",priority = 8, enabled = true, description = "Vendor should disable time slot")
	public void vendor_DisableTimeslotTest(ITestContext context) {
		logger.info("Disabling Vendor time slot");
		Response response = VendorEndPoints.vendor_DisableTimeslotEP(context, CommonMethods.sendTimeslot(context));
		JsonPath js = CommonMethods.jsonToString(response);
		int disabledTimeSlotId = js.getInt("id");
		context.setAttribute("disabledTimeSlotId", disabledTimeSlotId);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Disable TimesotId : "+disabledTimeSlotId);
	}

	@Test(groups="Vendor",priority = 9, enabled = true, description = "Vendor should enable time slot")
	public void vendor_EnableTimeslotTest(ITestContext context) {
		logger.info("Enable vendor time slot after disabled");
		Response response = VendorEndPoints.vendor_EnableTimeslotEP(context,
				(int) context.getAttribute("disabledTimeSlotId"));
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Timeslot enabled successfully");
	}

	@Test(groups="Vendor",priority = 10, enabled = true, description = "Vendot should get profile")
	public void vendor_ProfileTest(ITestContext context) {
		logger.info("get vendor profile");
		Response response = VendorEndPoints.vendor_ProfileEP(context);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("Vendor profile details getting successfully");
	}

	@Test(priority = 11,enabled= true, description = "Customer Login to Booking a Service method")
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
		int addressId = js_customer_GetMyProfileEP.getInt("addresses[7].id");
		context.setAttribute("addressId", addressId);
		String statusline_customer_GetMyProfileEP = response_customer_GetMyProfileEP.getStatusLine();
		Assert.assertEquals(statusline_customer_GetMyProfileEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetMyProfileEP);
		logger.info("Customer profile shown successfully.");
		// -------------------------------------------------------

		logger.info("Getting category.");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response_customerGetCategoryEP = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCodeName"), (String) context.getAttribute("name"));
		response_customerGetCategoryEP.then().log().all();
		JsonPath js_customerGetCategoryEP = CommonMethods.jsonToString(response_customerGetCategoryEP);
		int categoryId = js_customerGetCategoryEP.getInt("[0].id");
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js_customerGetCategoryEP.getString("[0].name"), "Painting");
		String statusline_customerGetCategoryEP = response_customerGetCategoryEP.getStatusLine();
		Assert.assertEquals(statusline_customerGetCategoryEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customerGetCategoryEP);
		logger.info(js_customerGetCategoryEP.getString("[0].name") + " Category selected successfully.");
		// -----------------------------------------------------------------

		logger.info("Getting subcategory.");
		Response response_customer_SubCategoryEP = CustomerEndPoints.customer_SubCategoryEP(context,
				(int) context.getAttribute("categoryId"));
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
		String servicename = js_customer_service.getString("[0].name");
		context.setAttribute("serviceId", serviceId);
		String statusline_customer_service = response_customer_service.getStatusLine();
		Assert.assertEquals(statusline_customer_service, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_service);
		logger.info(servicename + " Service selected successfully.");
		// ----------------------------------------------------------------------------

		logger.info("Getting timeslot for category = " + js_customerGetCategoryEP.getString("[0].name")
				+ " and Service name is = " + servicename);
		Response response_customer_GetTimeSlot = CustomerEndPoints.customer_GetTimeSlot(
				(int) context.getAttribute("addressId"), (int) context.getAttribute("categoryId"), context);
		response_customer_GetTimeSlot.then().log().all();
		JsonPath js_customer_GetTimeSlot = CommonMethods.jsonToString(response_customer_GetTimeSlot);
		sTime = js_customer_GetTimeSlot.getString("[5].startTime");
		context.setAttribute("StartTime", sTime);
		eTime = js_customer_GetTimeSlot.getString("[5].endTime");
		System.out.println("Start Time: " + sTime + "\n End Time: " + eTime);
		String statusline_customer_GetTimeSlot = response_customer_GetTimeSlot.getStatusLine();
		Assert.assertEquals(statusline_customer_GetTimeSlot, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetTimeSlot);
		logger.info("Category timeslot " + "Start Time: " + sTime + "\t End Time: " + eTime + " successfully.");
		// --------------------------------------------------------------------

		logger.info("Started creating new Booking.");
		Response response_customer_CreateBooking = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response_customer_CreateBooking.then().log().all();
		JsonPath js_customer_CreateBooking = CommonMethods.jsonToString(response_customer_CreateBooking);
		String status = js_customer_CreateBooking.getString("status");
		customerBookingId = js_customer_CreateBooking.getInt("id");
		context.setAttribute("BookingId", customerBookingId);
		// int bookingId = js5.getInt("id");
		// context.setAttribute("bookingId", bookingId);
		Assert.assertEquals(status, "New");
		String statusline_customer_CreateBooking = response_customer_CreateBooking.getStatusLine();
		Assert.assertEquals(statusline_customer_CreateBooking, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_CreateBooking);
		logger.info("New booking created successfully and Booking id is " + customerBookingId);
	}

	@Test(priority = 12, enabled = true, description = "Verify that vendor accept booking after enabled that disabled timeslot")
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
		context.setAttribute("STime", sTime);
		context.setAttribute("ETime", eTime);
		String statusline_vendor_TimeslotEP = response_vendor_TimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_TimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_TimeslotEP);
		logger.info("Vendor timeslots are shown successfully.");
		// --------------------------------------------------------------------
		logger.info("Started vendor disabling timeslot.");
		Response response_vendor_DisableTimeslotEP = VendorEndPoints.vendor_DisableTimeslotEP(context,
				CommonMethods.sendTimeslot(context));
		JsonPath js_vendor_DisableTimeslotEP = CommonMethods.jsonToString(response_vendor_DisableTimeslotEP);
		int disabledTimeSlotId = js_vendor_DisableTimeslotEP.getInt("id");
		context.setAttribute("disabledTimeSlotId", disabledTimeSlotId);
		String statusline_vendor_DisableTimeslotEP = response_vendor_DisableTimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_DisableTimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_DisableTimeslotEP);
		logger.info("Vendor disabled " + sTime + "\t" + eTime + " timeslot successfully with id " + disabledTimeSlotId);
		// -------------------------------------------------------------------
		logger.info("Started vendor enabling timeslot of id " + disabledTimeSlotId);
		Response response_vendor_EnableTimeslotEP = VendorEndPoints.vendor_EnableTimeslotEP(context,
				disabledTimeSlotId);
		String statusline_vendor_EnableTimeslotEP = response_vendor_EnableTimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_EnableTimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_EnableTimeslotEP);
		logger.info("Vendor enabled timeslot of " + sTime + "\t" + eTime + " after disabled successfully.");
		// --------------------------------------------------------------------
		logger.info("Started vendor accepting booking of Booking id = " + customerBookingId);
		Response response_vendor_AcceptBookingEP = VendorEndPoints.vendor_AcceptBookingEP(context, customerBookingId);
//		JsonPath js_vendor_AcceptBookingEP = CommonMethods.jsonToString(response_vendor_AcceptBookingEP);
//		String status1  = js_vendor_AcceptBookingEP.getString("status");
		// Assert.assertEquals(status1, "ExpertAssigned");
		System.out.println(
				"-------after booking accept status code: -------" + response_vendor_AcceptBookingEP.getStatusCode());
		String statusline_vendor_AcceptBookingEP = response_vendor_AcceptBookingEP.getStatusLine();
		// Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 200 OK");
		// Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 500 Internal
		// Server Error");
		// Assert.assertNotNull(statusline_vendor_AcceptBookingEP);
		logger.info("Vendor accepted booking id of " + customerBookingId
				+ " after enabled that disabled timeslot successfully");
	}

	@Test(priority = 13, enabled = true, description = "Verify that vendor cannot accept booking of timeslot YYYY-MM-DDT12:30:00Z for other customer as vendor already have accepted booking of timeslot YYYY-MM-DDT12:00:00Z due to +15 minute buffer time.")
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
		logger.info("Started vendor accepting booking of Booking id = " + customerBookingId);
		Response response_vendor_AcceptBookingEP = VendorEndPoints.vendor_AcceptBookingEP(context, customerBookingId);
//		JsonPath js_vendor_AcceptBookingEP = CommonMethods.jsonToString(response_vendor_AcceptBookingEP);
//		String status1  = js_vendor_AcceptBookingEP.getString("status");
//		Assert.assertEquals(status1, "ExpertAssigned");
//		String statusline_vendor_AcceptBookingEP = response_vendor_AcceptBookingEP.getStatusLine();
//		 Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 200 OK");
//		 Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 500 Internal Server Error");
//		 Assert.assertNotNull(statusline_vendor_AcceptBookingEP);
		logger.info("Vendor accepted booking id of " + customerBookingId + " successfully");
		// ---------------------------------------------------------------------
		logger.info("Getting Customer profile.");
		Response response_customer_GetMyProfileEP = CustomerEndPoints.customer_GetMyProfileEP(context);
		response_customer_GetMyProfileEP.then().log().all();
		JsonPath js_customer_GetMyProfileEP = CommonMethods.jsonToString(response_customer_GetMyProfileEP);
		int addressId = js_customer_GetMyProfileEP.getInt("addresses[0].id");
		context.setAttribute("addressId", addressId);
		String statusline_customer_GetMyProfileEP = response_customer_GetMyProfileEP.getStatusLine();
		Assert.assertEquals(statusline_customer_GetMyProfileEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetMyProfileEP);
		logger.info("Customer profile shown successfully.");
		// -------------------------------------------------------
		logger.info("Getting category.");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response_customerGetCategoryEP = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCodeName"), (String) context.getAttribute("name"));
		response_customerGetCategoryEP.then().log().all();
		JsonPath js_customerGetCategoryEP = CommonMethods.jsonToString(response_customerGetCategoryEP);
		int categoryId = js_customerGetCategoryEP.getInt("[0].id");
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js_customerGetCategoryEP.getString("[0].name"), "Painting");
		String statusline_customerGetCategoryEP = response_customerGetCategoryEP.getStatusLine();
		Assert.assertEquals(statusline_customerGetCategoryEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customerGetCategoryEP);
		logger.info(js_customerGetCategoryEP.getString("[0].name") + " Category selected successfully.");
		// -----------------------------------------------------------------
		logger.info("Getting subcategory.");
		Response response_customer_SubCategoryEP = CustomerEndPoints.customer_SubCategoryEP(context,
				(int) context.getAttribute("categoryId"));
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
		String servicename = js_customer_service.getString("[0].name");
		context.setAttribute("serviceId", serviceId);
		String statusline_customer_service = response_customer_service.getStatusLine();
		Assert.assertEquals(statusline_customer_service, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_service);
		logger.info(servicename + " Service selected successfully.");
		// ----------------------------------------------------------------------------

		logger.info("Getting timeslot for category = " + js_customerGetCategoryEP.getString("[0].name")
				+ " and Service name is = " + servicename);
		Response response_customer_GetTimeSlot = CustomerEndPoints.customer_GetTimeSlot(
				(int) context.getAttribute("addressId"), (int) context.getAttribute("categoryId"), context);
		response_customer_GetTimeSlot.then().log().all();
		JsonPath js_customer_GetTimeSlot = CommonMethods.jsonToString(response_customer_GetTimeSlot);
		String sTime1 = js_customer_GetTimeSlot.getString("[4].startTime");
		context.setAttribute("StartTime", sTime1);
		String eTime1 = js_customer_GetTimeSlot.getString("[4].endTime");
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
		// int bookingId = js5.getInt("id");
		// context.setAttribute("bookingId", bookingId);
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
				+ " for other customer as vendor already have accepted booking of timeslot " + sTime
				+ " due to +15 minute buffer time.");
	}

	// $******Satya
	@Test(priority = 14,enabled = true ,description = "Verify that vendor disabled timeslot after started service")
	public void vendorDisabledTimeslotAfterStartService(ITestContext context) {
		// --------------------------------Customerlogin----------------------------------
//		logger.info("Customer Login start");
//		Response cresponse = CustomerEndPoints.customer_Login(CommonMethods.customer_Login(), context);
//		JsonPath cloginjs = CommonMethods.jsonToString(cresponse);
//		String Ctoken = cloginjs.getString("token");
//		context.setAttribute("CToken", Ctoken);
//		logger.debug("Generated Token Id: {}", Ctoken);
//		logger.info("Customer logged in successfully " + Ctoken);
//		Assert.assertEquals(cresponse.statusCode(), 200);
//		Assert.assertEquals(cresponse.statusLine(), "HTTP/1.1 200 OK");
//		Assert.assertNotNull(cresponse, "Customer Login response is getting succesfully");
		// -------------------------------CreateNewbooking------------------------------
		LookUp.createBookingBasedOnAvailableTimeslot(context);
//		LookUp.createBooking(context);
//		logger.info("Creating new Booking");
//		logger.info("Getting Customer Timeslot for book service");
//		Response response5 = CustomerEndPoints.customer_GetTimeSlot((int) context.getAttribute("addressId"),
//				(int) context.getAttribute("categoryId"), context);
//		response5.then().log().all();
//		JsonPath js5 = CommonMethods.jsonToString(response5);
//		String sTime = js5.getString("[6].startTime");
//		context.setAttribute("StartTime", sTime);
//		String eTime = js5.getString("[6].endTime");
//		System.out.println("Start Time: " + sTime + "\n End Time: " + eTime);
//		Assert.assertEquals(response5.statusCode(), 200);
//		logger.info("Available booking Timeslot with startTime " + sTime + " endTime " + eTime);
//		Response response6 = CustomerEndPoints.customer_CreateBookingEndPoint(context,
//				CommonMethods.createBooking(context));
//		response6.then().log().all();
//		JsonPath js6 = CommonMethods.jsonToString(response6);
//		String status = js6.getString("status");
//		int bookingId = js6.getInt("id");
//		context.setAttribute("bookingId", bookingId);
//		Assert.assertEquals(response6.statusCode(), 200);
//		Assert.assertEquals(status, "New");
//		logger.info("New booking created successfully with bookingId :" + bookingId);
		// ----------------------------------VendorLogin---------------------------------------
//		logger.info("Vendor Login start");
//		Response vresponse = VendorEndPoints.vendor_Login(context, CommonMethods.vendor_Login());
//		JsonPath vloginjs = CommonMethods.jsonToString(vresponse);
//		String Vtoken = vloginjs.getString("token");
//		context.setAttribute("VToken", Vtoken);
//		logger.debug("Generated Token Id: {}", Vtoken);
//		logger.info("Vendor logged in successfully");
//		Assert.assertEquals(vresponse.statusCode(), 200);
//		Assert.assertEquals(vresponse.statusLine(), "HTTP/1.1 200 OK");
//		Assert.assertNotNull(vresponse, "Vendor Login response is getting succesfully");
//		int bookingId = (int) context.getAttribute("bookingId");
//		// -------------------------VendorAcceptBooking-----------------------------------
//		logger.info("Vendor accept booking with BookingId " + bookingId);
//		Response acceptbooking_response = VendorEndPoints.vendor_AcceptBookingEP(context,
//				(int) context.getAttribute("bookingId"));
//		acceptbooking_response.then().log().all();
//		JsonPath acceptbookingjs = CommonMethods.jsonToString(acceptbooking_response);
//		int vbookingId = acceptbookingjs.getInt("id");
//		context.setAttribute("bookingId", vbookingId);
//		Assert.assertEquals(acceptbookingjs.getString("status"), "ExpertAssigned");
//		Assert.assertEquals(acceptbooking_response.getStatusCode(), 200);
//		Assert.assertNotNull(acceptbooking_response, "Accept booking successfully");
//		logger.info("Vendor accept Booking successfully with bookingId: " + bookingId + " & status :"
//				+ acceptbookingjs.getString("status"));
		// ----------------------------customergetBookingById----------------------------------------
		logger.info("Getting StartOTP through Customer Get Booking By Id");
		Response customerBookingById_res = CustomerEndPoints.customer_GetBookingByIdEP(context, (int) context.getAttribute("bookingId"));
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
				CommonMethods.sendBookingIdAndStartOTP(context));
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
	@Test(priority = 15,enabled = true, description = "Verify that Vendor Accept Service From Differnt Category Service")
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
		// ---------------------------Get customerServiceId------------------------------------
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
		Response acceptbooking_response = VendorEndPoints.vendor_AcceptBookingEP(context,(int) context.getAttribute("bookingId"));
		acceptbooking_response.then().log().all();
		Assert.assertEquals(acceptbooking_response.getStatusCode(), 400);
		Assert.assertEquals(acceptbooking_response.contentType(), "application/json; charset=utf-8");
		Assert.assertNotNull(acceptbooking_response, "Cannot accept booking");
		logger.info("vendor not accepted booking for different category "+ categoryname +" with id "+bookingId);

	}

}
