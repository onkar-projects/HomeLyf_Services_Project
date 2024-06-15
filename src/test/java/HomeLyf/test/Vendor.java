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
	static String sTime;
	static int id;
	// String token;

	@Test(priority = 1, enabled = true, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
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

	@Test(priority = 2, enabled = true, description = "Vendor should get bookings")
	public void vendor_get_booking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		Response response = VendorEndPoints.vendorgetbooking(context, 1, 10);
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
				(int) context.getAttribute("vendorAcceptBookingId"));
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.get("status");
		Assert.assertEquals(status, "ExpertAssigned");
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@Test(priority = 4, enabled = true, description = "Vendor should start booking using startOtp")
	public void vendor_StartBookingTest(ITestContext context) {
		logger.info("Start vendor booking using startOtp");
		Response C_GetBookingResponse = CustomerEndPoints.customer_GetBookingByIdEP(context,
				(int) context.getAttribute("vendorAcceptBookingId"));
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

	@Test(priority = 5, enabled = true, description = "Vendor should complete booking using endOtp")
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
		String sTime = js.getString("availableTimeSlots[0].startTime");
		String eTime = js.getString("availableTimeSlots[0].endTime");
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

	@Test(priority = 11, enabled = true, description = "Verify that vendor accept booking after enabled that disabled timeslot")
	public void vendor_AcceptBookingAfterEnablingTimeslot(ITestContext context) {
		logger.info("Started vendor accept booking after enabled that disabled timeslot");
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
		int addressId = js_customer_GetMyProfileEP.get("addresses[0].id");
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
				(String) context.getAttribute("postCode"), (String) context.getAttribute("name"));
		response_customerGetCategoryEP.then().log().all();
		JsonPath js_customerGetCategoryEP = CommonMethods.jsonToString(response_customerGetCategoryEP);
		int categoryId = js_customerGetCategoryEP.get("[0].id");
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
		context.setAttribute("serviceid", serviceId);
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
		String sTime = js_customer_GetTimeSlot.getString("[3].startTime");
		context.setAttribute("StartTime", sTime);
		String eTime = js_customer_GetTimeSlot.getString("[3].endTime");
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
		logger.info("Vendor enabled timeslot after disabled successfully.");
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

	@Test
	public void vendorDisabledTimeslotAfterStartService(ITestContext context) {
		// Customer login
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
		// Create New booking
		LookUp.createBooking(context);
		logger.info("Creating new Booking");
		Response response6 = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response6.then().log().all();
		JsonPath js6 = CommonMethods.jsonToString(response6);
		String status = js6.getString("status");
		int bookingId = js6.getInt("id");
		context.setAttribute("bookingId", bookingId);
		Assert.assertEquals(response6.statusCode(), 200);
		Assert.assertEquals(status, "New");
		logger.info("New booking created successfully with bookinId" + bookingId);

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

//		// GetBooking
//		logger.info(" vendor GetBooking");
//		Response getbooking_response = VendorEndPoints.vendorgetbooking(context, 1, 10);
//		getbooking_response.then().log().all();
//		
//		JsonPath js = CommonMethods.jsonToString(getbooking_response);
//		for(int i=0;i<100;i++) {
//			int vendorBookingId = js.getInt("["+ i +"].");
//		}
//		int vendorBookingId = js.getInt("[0].id");
//		// String scheduleOnTime = js.getString("[0].scheduledOn");
//		context.setAttribute("vendorBookingId", vendorBookingId);
//		Assert.assertEquals(getbooking_response.statusCode(), 200);
//		logger.info("Vendor get booking with BookingId " + vendorBookingId);

		// AcceptBooking
		logger.info("Vendor accept booking with BookingId " + bookingId);
		Response acceptbooking_response = VendorEndPoints.vendor_AcceptBookingEP(context,
				(int) context.getAttribute("bookingId"));
		acceptbooking_response.then().log().all();
		// Assert.assertEquals(acceptbooking_response.getStatusCode(), 500);
		logger.info("Vendor accept Booking successfully");

		// GetMyBooking
		// LookUp.vendorgetMybooking(context);
//		Response vendorMyBooking_response = VendorEndPoints.vendor_MybookingEP(context, 1, 10);
//		vendorMyBooking_response.then().log().all();
//		JsonPath myBookingjs = CommonMethods.jsonToString(vendorMyBooking_response);
//		for (int i = 0; i <= 5; i++) {
//			String status = myBookingjs.getString("[" + i + "].status");
//			if (status.equalsIgnoreCase("ExpertAssigned")) {
//				vendorBookingId = myBookingjs.getInt("[" + i + "].id");
//				break;
//			}
//		}
//		context.setAttribute("vendorAcceptBookingId", vendorBookingId);
//		System.out.println(vendorBookingId);
//		Assert.assertEquals(vendorMyBooking_response.statusCode(), 200);
//		logger.info("vendor_get_booking is shown successfully");

		// customergetBookingById
		logger.info("Getting StartOTP through Customer Get Booking By Id");
		Response customerBookingById_res = CustomerEndPoints.customer_GetBookingByIdEP(context, bookingId);
		customerBookingById_res.then().log().all();
		JsonPath customerbookingIdjs = CommonMethods.jsonToString(customerBookingById_res);
		int startOTP = customerbookingIdjs.get("startOTP");
		System.out.println(startOTP);
		context.setAttribute("c_startOTP", startOTP);
		int customerBookingId = customerbookingIdjs.get("id");
		context.setAttribute("customerBookingId", customerBookingId);
		Assert.assertEquals(customerBookingById_res.statusCode(), 200);
		Assert.assertEquals(customerBookingById_res.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(customerBookingById_res, "startOTP getting succesfully");
		logger.info("Start OTP for BookingId " + bookingId + " is " + startOTP);

		// Vendor start service
		logger.info("Starting Service for BookingId " + customerBookingId + " and startOTP " + startOTP);
		Response startService_response = VendorEndPoints.vendor_startBookingEP(context,
				CommonMethods.sendBookingIdAndOtp(context));
		startService_response.then().log().all();
		JsonPath startservicejs = CommonMethods.jsonToString(startService_response);
		String statusafterstartservice = startservicejs.getString("status");
		String scheduledOn = startservicejs.getString("scheduledOn");
		// Assert.assertEquals(startservicejs, "InProgress");

		Assert.assertEquals(startService_response.getStatusCode(), 200);
		Assert.assertEquals(startService_response.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(startService_response, "Service started succesfully");
		logger.info("Service started succesfully for BookingId " + customerBookingId);

		// Vendor getTimeSlot
		logger.info("");
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

		// Vendor Disable TimeSlot
		Response disableTimeslot_res = VendorEndPoints.vendor_DisableTimeslotEP(context,
				CommonMethods.sendTimeslot(context, scheduledOn, endTime));
		disableTimeslot_res.then().log().all();
		JsonPath disableTimeslotjs = CommonMethods.jsonToString(disableTimeslot_res);

		Assert.assertEquals(disableTimeslot_res.getStatusCode(), 400);
		Assert.assertEquals(disableTimeslotjs.getString("[0]"), "Cannot diable already booked timeslot");
		//----------------------------------------------------------------------------------------
		@Test(enabled = true,description = "Verify that customer booking of 411002 Postcode is not display to vendor other than 411002 service PostCode ")
		public void customerBookingForOnePostcodeIsNotDisplayToVendorForOtherServicePostCode(ITestContext context)
		{
		
		logger.info("Started Customer Login test.");
		Response response_CustomerLogin = UserEndPoints
				.userLogin(CommonMethods.CustomerLoginforAcceptBookingAfterEnablingTimeslot());
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
		
	//----------------------------------------------------------------------------------------------
		
		logger.info("Getting Customer profile.");
		Response response_customer_GetMyProfileEP = CustomerEndPoints.customer_GetMyProfileEP(context);
		response_customer_GetMyProfileEP.then().log().all();
		JsonPath js_customer_GetMyProfileEP = CommonMethods.jsonToString(response_customer_GetMyProfileEP);
		int addressId = js_customer_GetMyProfileEP.get("addresses[29].id");
		context.setAttribute("addressId", addressId);
		System.out.println("Address Id "+addressId);
		String statusline_customer_GetMyProfileEP = response_customer_GetMyProfileEP.getStatusLine();
		Assert.assertEquals(statusline_customer_GetMyProfileEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetMyProfileEP);
		logger.info("Customer profile shown successfully.");
	// ------------------------------------------------------------------------------------------
		logger.info("Getting category.");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response_customerGetCategoryEP = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCode"), (String) context.getAttribute("name"));
		response_customerGetCategoryEP.then().log().all();
		JsonPath js_customerGetCategoryEP = CommonMethods.jsonToString(response_customerGetCategoryEP);
		int categoryId = js_customerGetCategoryEP.get("[0].id");
		context.setAttribute("categoryId", categoryId);
	     System.out.println("Category Id"+categoryId);
		Assert.assertEquals(js_customerGetCategoryEP.getString("[0].name"), "Painting");
		String statusline_customerGetCategoryEP = response_customerGetCategoryEP.getStatusLine();
		Assert.assertEquals(statusline_customerGetCategoryEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customerGetCategoryEP);
		logger.info(js_customerGetCategoryEP.getString("[0].name")+ " Category selected successfully.");
		// -----------------------------------------------------------------
		logger.info("Getting subcategory.");
		Response response_customer_SubCategoryEP = CustomerEndPoints.customer_SubCategoryEP(context,
				(int) context.getAttribute("categoryId"));
		response_customer_SubCategoryEP.then().log().all();
		JsonPath js_customer_SubCategoryEP = CommonMethods.jsonToString(response_customer_SubCategoryEP);
		int subCategoryId = js_customer_SubCategoryEP.getInt("[0].id");
		context.setAttribute("subCategoryId", subCategoryId);
		System.out.println("SubCategory Id"+subCategoryId);
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
		context.setAttribute("serviceid", serviceId);
		System.out.println("Service Id "+serviceId);
		String statusline_customer_service = response_customer_service.getStatusLine();
		Assert.assertEquals(statusline_customer_service, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_service);
		logger.info(servicename+" Service selected successfully.");
		// ----------------------------------------------------------------------------
		logger.info("Getting timeslot for category = "+ js_customerGetCategoryEP.getString("[0].name")+" and Service name is = "+servicename);
		Response response_customer_GetTimeSlot = CustomerEndPoints.customer_GetTimeSlot(
				(int) context.getAttribute("addressId"), (int) context.getAttribute("categoryId"), context);
		response_customer_GetTimeSlot.then().log().all();
		JsonPath js_customer_GetTimeSlot = CommonMethods.jsonToString(response_customer_GetTimeSlot);
		sTime = js_customer_GetTimeSlot.getString("[3].startTime");
		context.setAttribute("StartTime", sTime);
		String eTime = js_customer_GetTimeSlot.getString("[3].endTime");
		System.out.println("Start Time: " + sTime + "\n End Time: " + eTime);
		String statusline_customer_GetTimeSlot = response_customer_GetTimeSlot.getStatusLine();
		Assert.assertEquals(statusline_customer_GetTimeSlot, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetTimeSlot);
		logger.info("Category timeslot "+"Start Time: " + sTime + "\t End Time: " + eTime+" successfully.");


	}
}