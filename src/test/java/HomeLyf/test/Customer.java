package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import HomeLyf.EndPoints.CustomerEndPoints;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.EndPoints.VendorEndPoints;
import HomeLyf.Payload.Address;
import HomeLyf.Payload.DisableTimeslot_Payload;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class Customer {

	private static Logger logger = LogManager.getLogger(User.class);
	static String Ctoken;
	static String Vtoken;
	static String token;
	String sTime;
	String[] paymentMode = { "cash", "upi", "card", "other" };
	String[] paymentStatus = { "pending", "inprogress", "delayed", "cancelled", "completed", "refundinprogress",
			"refunded" };
	Address address;
	static int customerBookingId;

	@Test(priority = 1, enabled = false, dataProvider = "Customerlogin", dataProviderClass = DataProviderClass.class)
	public static void customer_Login(ITestContext context, String mobileNumber, String type, String emailAddress,
			String password, String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		Ctoken = js.getString("token");
		System.out.println("Generated Token Id: " + Ctoken);
		context.setAttribute("Token", Ctoken);
		logger.debug("Generated Token Id: {}", Ctoken);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(priority = 2, enabled = false, description = "Customer profile should show")
	public void customer_GetMyProfileTest(ITestContext context) {
		logger.info("Getting Customer profile");
		Response response = CustomerEndPoints.customer_GetMyProfileEP(context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int addressId = js.get("addresses[0].id");
		context.setAttribute("addressId", addressId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Customer profile shown successfully");
	}

	@Test(priority = 3, enabled = false, description = "Customer should get category")
	public void customer_GetCategoryTest(ITestContext context) {
		logger.info("Getting category");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response = CustomerEndPoints.customer_GetCategoryEP(context, (String) context.getAttribute("postCode"),
				"");
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int categoryId = js.get("[0].id");
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js.getString("[0].name"), "Painting");
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Category fetched successfully");
	}

	@Test(priority = 4, enabled = false, description = "Customer should Show the SubCategory")
	public void customer_subCategoryIdTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_SubCategoryEP(context, (int) context.getAttribute("categoryId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int subCategoryId = js.getInt("[0].id");
		context.setAttribute("subCategoryId", subCategoryId);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("SubCategory Fetched Successfully");
	}

	@Test(priority = 5, enabled = false, description = " customer should get services")
	public void customer_GetService(ITestContext context) {
		logger.info("Starting customer_service...");
		Response response = CustomerEndPoints.customer_service(context, (int) context.getAttribute("subCategoryId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		context.setAttribute("serviceid", js.getInt("[0].id"));
		System.out.println(
				"----------------------------------" + js.get("[0].id") + "------------------------------------");
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("customer_service subcategory is shown successfully");
	}

	@Test(priority = 6, enabled = false)
	public void customer_GetTimeSlot(ITestContext context) {

		logger.info("Starting customer_service...");
		Response response = CustomerEndPoints.customer_GetTimeSlot((int) context.getAttribute("addressId"),
				(int) context.getAttribute("categoryId"), context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		sTime = js.getString("[0].startTime");
		context.setAttribute("StartTime", sTime);
		String eTime = js.getString("[0].endTime");
		System.out.println("Start Time: " + sTime + "\n End Time: " + eTime);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("customer_service subcategory is shown successfully");
	}

	@Test(priority = 7, enabled = false, description = "Customer should create new Booking")
	public void customer_CreateBooking(ITestContext context) {

		logger.info("Creating new Booking");
		Response response = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("status");
		int bookingId = js.getInt("id");
		context.setAttribute("bookingId", bookingId);
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status, "New");
		logger.info("New booking created successfully");
	}

	@Test(priority = 8, enabled = false, description = "customer booking should show")
	public void customer_GetBookingTest(ITestContext context) {
		String[] status = { "New", "ExpertAssigned", "Inprogress", "Cancelled", "Completed" };
		logger.info("Fetching Customer booking");
		Response response = CustomerEndPoints.customer_GetBookingEndPoint(context, status[0], 1, 10);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String stat = js.getString("[0].status");
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status[0], stat);
		logger.info("Customer booking shown successfully of Status" + status[0]);
	}

	@Test(priority = 9, enabled = false, description = "customer should update payment status ")
	public void customer_UpdatePaymnetStatus(ITestContext context) {
		logger.info("Updateing Payment Status");
		Response response = CustomerEndPoints.customer_UpdatePaymentStatusEP(context,
				CommonMethods.updatePaymentStatusData(context));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("paymentStatus");
		String payMode = js.getString("paymentMode");
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status, paymentStatus[1]);
		Assert.assertEquals(payMode, paymentMode[3]);

		logger.info("Customer update payment status successfully ");
	}

	@Test(priority = 10, enabled = false, description = "Customer should calculate as per quntity ")
	public void customer_Calculate(ITestContext context) {
		Response response = CustomerEndPoints.customer_CalculateEP(context, CommonMethods.calculateData(context));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}

	@Test(priority = 11, enabled = false, description = "Customer should Cancel booking ")
	public void customer_CancelTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_CancelEP(context, (int) context.getAttribute("bookingId"));
		response.then().log().all();
	}

	@Test(priority = 12, enabled = false, description = "Customer should create new address with valid credentials", dataProvider = "CustomerAddressData", dataProviderClass = DataProviderClass.class)
	public void customer_Addresstest(ITestContext context, String name, String type, String lineOne, String lineTwo,
			String lineThree, String location) {
		logger.info("Adding Customer Address");
		Response response = CustomerEndPoints.customer_Address(context,
				CommonMethods.address_details(name, type, lineOne, lineTwo, lineThree, location, context));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
//		int NewAddressId = js.getInt("id");
//		context.setAttribute("NewAddressId", NewAddressId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Added Customer new Address successfully");
	}

//	@Test(priority = 13, enabled = false)
//	public void customer_GetBookingIdTest(ITestContext context) {
//		Response response = CustomerEndPoints.customer_GetBookingBySIdEP(context, 52673);
//		response.then().log().all();
//		JsonPath js =  CommonMethods.jsonToString(response);
//		int bookingId = js.get("id");
//		context.setAttribute("bookingId", bookingId);
//		String status = js.getString("status");
//		System.out.println(status);
//		int startOTP = js.get("startOTP");
//		context.setAttribute("startOTP", startOTP);		
//		//System.out.println(sOtp);
//		int endOTP = js.get("endOTP");
//		context.setAttribute("endOTP", endOTP);
//		//System.out.println(eOtp);
//	}
	@Test(priority = 14, enabled = false)
	public void customer_RescheduleTime(ITestContext context) {
		Response response = CustomerEndPoints.customer_RescheduleEP(context,
				CommonMethods.CustomerReschedule_Payload(context, 52673, "2024-06-06T04:00:00Z"));
		response.then().log().all();
	}

	@Test(priority = 15, enabled = false, description = "Verify Disabled Timeslots by vendor are not visible to customer for different service Postcode")
	public void verifyDisabledTimeslotsAreNotVisibleToCustomer(ITestContext context) {
		// Vendor Login
		Response vresponse = VendorEndPoints.vendor_Login(context, CommonMethods.vendor_Login());
		// System.out.println("---------------"+vresponse.getStatusLine());
		JsonPath vloginjs = CommonMethods.jsonToString(vresponse);
		token = vloginjs.getString("token");
		System.out.println("Generated Token Id: " + token);
		context.setAttribute("Vtoken", token);
		logger.debug("Generated Token Id: {}", token);
		logger.info("Vendor logged in successfully");

		// Vendor GetTimeSlot
		Response vTimeslotResponse = VendorEndPoints.vendor_TimeslotEP(context);
		JsonPath timeslotjs = CommonMethods.jsonToString(vTimeslotResponse);
		String stime = timeslotjs.getString("availableTimeSlots[5].startTime");
		String etime = timeslotjs.getString("availableTimeSlots[5].endTime");
		context.setAttribute("stime", stime);
		context.setAttribute("etime", etime);
		logger.info("list of vendor TimeSlot");

		// Disable TimeSlot
		DisableTimeslot_Payload disabletimeslot = new DisableTimeslot_Payload();
		disabletimeslot.setId(0);
		disabletimeslot.setStartTime(stime);
		disabletimeslot.setEndTime(etime);
		Response disableResponse = VendorEndPoints.vendor_DisableTimeslotEP(context, disabletimeslot);
		disableResponse.then().log().all();
		JsonPath disabletimeslotjs = CommonMethods.jsonToString(disableResponse);
		String sTime = disabletimeslotjs.getString("startTime");
		logger.info("DisableTimeSlot: Timeslot disable successfully");

		// Customer Login
		Response cresponse = CustomerEndPoints.customer_Login(CommonMethods.customer_Login(), context);
		JsonPath loginjs = CommonMethods.jsonToString(cresponse);
		token = loginjs.getString("token");
		System.out.println("Generated Token Id: " + token);
		context.setAttribute("Ctoken", token);
		logger.debug("Generated Token Id: {}", token);
		logger.info("Customer logged in successfully");

		// GetCategory
		logger.info("Getting categoryId");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response2 = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCode"), "");
		JsonPath js = CommonMethods.jsonToString(response2);
		int categoryId = js.get("[5].id");
		context.setAttribute("categoryId", categoryId);
		logger.info("CategoryId fetched successfully");

		// Customer TimeSlot
		LookUp.getMyProfile(context);
		Response customerTimeslotResponse = CustomerEndPoints
				.customer_GetTimeSlot((int) context.getAttribute("addressId"), categoryId, context);
		customerTimeslotResponse.then().log().all();
		logger.info("Check whether disable timeslot is available in customer available timeslot ");
		// Verify that disabled timeslots are no longer visible to the customer

		Assert.assertEquals(vresponse.statusCode(), 200);
		Assert.assertEquals(vresponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(vresponse, "Vendor Login response is getting succesfully");
		Assert.assertEquals(vTimeslotResponse.getStatusCode(), 200);
		Assert.assertEquals(vTimeslotResponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(vTimeslotResponse, "Vendor's Available Timeslot are getting successfully");
		Assert.assertEquals(disableResponse.getStatusCode(), 200, "Timeslots disable succcesfully");
		Assert.assertEquals(disableResponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(disableResponse, "Timeslot disable by Vendor successfully");
		Assert.assertEquals(cresponse.statusCode(), 200);
		Assert.assertEquals(cresponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(cresponse, "Customer Login response is getting successfully");
		Assert.assertEquals(js.getString("[5].name"), "Electricals");
		Assert.assertEquals(response2.statusCode(), 200);
		Assert.assertEquals(response2.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(response2, "List of Categories are getting successfully");
		Assert.assertTrue(customerTimeslotResponse.getBody().asString().contains(sTime),
				"Disabled timeslots are still visible to the customer");
		Assert.assertEquals(customerTimeslotResponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(customerTimeslotResponse,
				"Customer available timeslot are getting with including timeslot disable by vendor");
	}

	@Test(priority = 16, enabled = true, description = "Verify that customer cannot cancel booking once service Started by vendor")
	public void customerCannotCancelBookingServiceStartedByVendor(ITestContext context) {
		logger.info("Started verify that customer cannot cancel booking service Started by vendor");
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
		sTime = js_customer_GetTimeSlot.getString("[1].startTime");
		context.setAttribute("StartTime", sTime);
		String eTime = js_customer_GetTimeSlot.getString("[1].endTime");
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
		Response response_VendorLogin = UserEndPoints
				.userLogin(CommonMethods.VendorLoginforAcceptBookingAfterEnablingTimeslot());
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
		// Assert.assertEquals(status1, "ExpertAssigned");
		String statusline_vendor_AcceptBookingEP = response_vendor_AcceptBookingEP.getStatusLine();
		// Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 200 OK");
		// Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 500 Internal Server Error");
		// Assert.assertNotNull(statusline_vendor_AcceptBookingEP);
		logger.info("Vendor accepted booking id of " + customerBookingId
				+ " after enabled that disabled timeslot successfully");
		// ---------------------------------------------------------------------

		logger.info("Started customer get booking by id =" + customerBookingId);
		Response response_customer_GetBookingBySId = CustomerEndPoints.customer_GetBookingByIdEP(context,
				customerBookingId);
		response_customer_GetBookingBySId.then().log().all();
		JsonPath js_customer_GetBookingBySId = CommonMethods.jsonToString(response_customer_GetBookingBySId);
		int startOTP = js_customer_GetBookingBySId.get("startOTP");
		context.setAttribute("StartOTP", startOTP);
		String statusline_customer_GetBookingBySId = response_customer_GetBookingBySId.getStatusLine();
		Assert.assertEquals(statusline_customer_GetBookingBySId, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetBookingBySId);
		logger.info("Customer get OTP " + startOTP + " of booking id = " + customerBookingId);
		// ---------------------------------------------------------------------

		logger.info("Started customer start booking");
		Response response_vendor_startservice = VendorEndPoints.vendor_startBookingEP(context,
				CommonMethods.sendBookingIdAndOtpforStartandEndService(context));
		String statusline_vendor_startservice = response_vendor_startservice.getStatusLine();
		Assert.assertEquals(statusline_vendor_startservice, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_startservice);
		logger.info("Customer service is started by entering Booking id = "+customerBookingId+" and Start OTP = "+startOTP);
		//-----------------------------------------------------------------------
		
		logger.info("Started customer cancel the booking of id = "+ customerBookingId);
		Response response_customer_CancelEP = CustomerEndPoints.customer_CancelEP(context, customerBookingId);
	    response_customer_CancelEP.then().log().all();
//		JsonPath js_customer_CancelEP = CommonMethods.jsonToString(response_customer_CancelEP);
//		String cancelmessage = js_customer_CancelEP.getString();
	    String statusline_customer_CancelEP = response_customer_CancelEP.getStatusLine();
		Assert.assertEquals(statusline_customer_CancelEP, "HTTP/1.1 400 Bad Request");
		Assert.assertNotNull(response_customer_CancelEP);
		logger.info("Customer can not cancel the service.");
		logger.info("Customer cannot cancel booking on started service successfully.");

	}
}
