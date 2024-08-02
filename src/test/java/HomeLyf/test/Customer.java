package HomeLyf.test;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

public class Customer {
	private static Logger logger = LogManager.getLogger(User.class);
	static String Ctoken;
	static String Vtoken;
	static String token;
	static String sTime;
	static String eTime;
	static DateTimeFormatter formatter;
	static ZonedDateTime indianTime;
	static ZonedDateTime edTime;
	static ZonedDateTime edbTime;
	static String endtime;
	static String endBuffertime;
	String[] paymentMode = { "cash", "upi", "card", "other" };
	String[] paymentStatus = { "pending", "inprogress", "delayed", "cancelled", "completed", "refundinprogress",
			"refunded" };
	Address address;
	static int customerBookingId;

	@Test(groups = "Customer", priority = 1, enabled = true, dataProvider = "Customerlogin", dataProviderClass = DataProviderClass.class)
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
		context.setAttribute("CToken", Ctoken);
		logger.debug("Generated Token Id: {}", Ctoken);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(groups = "Customer", priority = 2, enabled = true, description = "Customer profile should show")
	public void customer_GetMyProfileTest(ITestContext context) {
		logger.info("Getting Customer profile");
		Response response = CustomerEndPoints.customer_GetMyProfileEP(context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int addressId = js.getInt("addresses[0].id");
//		System.out.println("addressId is ="+addressId);
		context.setAttribute("addressId", addressId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Customer profile shown successfully");
	}

	@Test(groups = "Customer", priority = 3, enabled = true, description = "Customer should get category")
	public void customer_GetCategoryTest(ITestContext context) {
		logger.info("Getting category");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCode2"), "");
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int categoryId = js.get("[0].id");
		System.out.println("Category is = " + categoryId);
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js.getString("[0].name"), "Painting");
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("categoryId is: " + categoryId);
	}

	@Test(groups = "Customer", priority = 4, enabled = true, description = "Customer should Show the SubCategory")
	public void customer_subCategoryIdTest(ITestContext context) {
		logger.info("customer getting subCategoryId");
		Response response = CustomerEndPoints.customer_SubCategoryEP(context, (int) context.getAttribute("categoryId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int subCategoryId = js.getInt("[0].id");
		System.out.println("subCategoryId is = " + subCategoryId);
		context.setAttribute("subCategoryId", subCategoryId);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("SubCategoryId is: " + subCategoryId);
	}

	@Test(groups = "Customer", priority = 5, enabled = true, description = " customer should get services")
	public void customer_GetService(ITestContext context) {
		logger.info("Customer getting serviceId...");
		Response response = CustomerEndPoints.customer_service(context, (int) context.getAttribute("subCategoryId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int serviceId = js.getInt("[0].id");
		context.setAttribute("serviceId", js.getInt("[0].id"));
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("serviceId is: " + serviceId);
	}

	@Test(groups = "Customer", priority = 6, enabled = true)
	public void customer_GetTimeSlot(ITestContext context) {
		logger.info("customer getting available Timeslot");
		Response response = CustomerEndPoints.customer_GetTimeSlot((int) context.getAttribute("addressId"),
				(int) context.getAttribute("categoryId"), context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		sTime = js.getString("[22].startTime");
		context.setAttribute("StartTime", sTime);
		String eTime = js.getString("[22].endTime");
		System.out.println("Start Time: " + sTime + "\n End Time: " + eTime);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("customer_service subcategory is shown successfully");
	}

	@Test(groups = "Customer", priority = 7, enabled = true, description = "Customer should create new Booking")
	public void customer_CreateBooking(ITestContext context) {
		logger.info("Creating new Booking");
		Response response = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("status");
		int bookingId = js.getInt("id");
		context.setAttribute("BookingId", bookingId);
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status, "New");
		logger.info("New booking created successfully");
	}

	@Test(groups = "Customer", priority = 8, enabled = true, description = "customer booking should show")
	public void customer_GetBookingTest(ITestContext context) {
		String[] status = { "New", "ExpertAssigned", "Inprogress", "Cancelled", "Completed" };
		logger.info("Fetching Customer booking");
		Response response = CustomerEndPoints.customer_GetBookingEndPoint(context, status[0], 1, 1000);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String stat = js.getString("[0].status");
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status[0], stat);
		logger.info("Customer booking shown successfully of Status" + status[0]);
	}

	@Test(groups = "Customer", priority = 9, enabled = false, description = "customer should update payment status ")
	public void customer_UpdatePaymnetStatus(ITestContext context) {
		logger.info("Updating Payment Status");
		Response response = CustomerEndPoints.customer_UpdatePaymentStatusEP(context, (int) context.getAttribute("BookingId"));
		logger.info("Customer can not update payment status ");
	}

	@Test(groups = "Customer", priority = 10, enabled = true, description = "Customer should calculate as per quntity ")
	public void customer_Calculate(ITestContext context) {
		logger.info(" Customer calculate service based on quantity");
		Response response = CustomerEndPoints.customer_CalculateEP(context, CommonMethods.calculateData(context));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Calculate service price based on quantity successfully");
	}

	@Test(groups = "Customer", priority = 11, enabled = true, description = "Customer should reschedule the booking by entering booking id.")
	public void customer_RescheduleTime(ITestContext context) {
		logger.info("Customer reschedule time after creating booking");
		Response response = CustomerEndPoints.customer_GetTimeSlot((int) context.getAttribute("addressId"),
				(int) context.getAttribute("categoryId"), context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		sTime = js.getString("[14].startTime");
		context.setAttribute("RescheduledSTime", sTime);
		Response response_reschedule = CustomerEndPoints.customer_RescheduleEP(context,
				CommonMethods.CustomerReschedule_Payload(context, (int) context.getAttribute("BookingId"),
						(String) context.getAttribute("RescheduledSTime")));
		response_reschedule.then().log().all();
		logger.info("Reschedule timeslot successfully at " + (int) context.getAttribute("BookingId") + " of booking id "
				+ (String) context.getAttribute("StartTime"));
	}

	@Test(groups = "Customer", priority = 12, enabled = true, description = "Customer should create new address with valid credentials", dataProvider = "CustomerAddressData", dataProviderClass = DataProviderClass.class)
	public void customer_Addresstest(ITestContext context, String name, String type, String lineOne, String lineTwo,
			String lineThree, String location) {
		logger.info("Adding Customer Address");
		Response response = CustomerEndPoints.customer_Address(context,
				CommonMethods.address_details(name, type, lineOne, lineTwo, lineThree, location, context));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Added Customer new Address successfully");
	}

	@Test(groups = "Customer", priority = 13, enabled = true, description = "Customer should get startOTP and endOTP after entering BookiId")
	public static void customer_GetBookingIdTest(ITestContext context) {
		System.out.println("Booking id is "+(int) context.getAttribute("BookingId"));
		Response response = CustomerEndPoints.customer_GetBookingByIdEP(context,
				(int) context.getAttribute("BookingId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int bookingId = js.get("id");
		context.setAttribute("BookingId", bookingId);
		String status = js.getString("status");
		System.out.println(status);
		int startOTP = js.get("startOTP");
		context.setAttribute("startOTP", startOTP);
		System.out.println("startOTP is " + startOTP);
		int endOTP = js.get("endOTP");
		context.setAttribute("endOTP", endOTP);
		System.out.println("endOTP is " + endOTP);
		logger.info("Booking is accepted by vendor and StartOTP is " + startOTP + " and EndOTP is " + endOTP);

		// System.out.println(eOtp);
		logger.info("Booking is got by id " + bookingId);
	}

	@Test(groups = "Customer", priority = 14, enabled = false, description = "Customer should Cancel booking ")
	public void customer_CancelTest(ITestContext context) {
		logger.info("Customer cancel booking before accept by vendor");
		Response response = CustomerEndPoints.customer_CancelEP(context, (int) context.getAttribute("BookingId"));
		response.then().log().all();
		logger.info("Customer cancelled booking of bookingId : " + (int) context.getAttribute("BookingId"));
	}

	// $*****
	@Test(priority = 15, enabled = true, description = "Verify Disabled Timeslots by vendor are not visible to customer for different service Postcode")
	public void verifyDisabledTimeslotsAreNotVisibleToCustomer(ITestContext context) {
		// ---------------------------VendorLogin----------------------------------------
		logger.info("Vendor start login");
		Response vresponse = VendorEndPoints.vendor_Login(context, CommonMethods.vendor_Login());
		// System.out.println("---------------"+vresponse.getStatusLine());
		JsonPath vloginjs = CommonMethods.jsonToString(vresponse);
		String Vtoken = vloginjs.getString("token");
		System.out.println("Generated Token Id: " + Vtoken);
		context.setAttribute("VToken", Vtoken);
		logger.debug("Generated Token Id: {}", Vtoken);
		logger.info("Vendor logged in successfully");
		Assert.assertEquals(vresponse.statusCode(), 200);
		Assert.assertEquals(vresponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(vresponse, "Vendor Login response is getting succesfully");
		// ------------------------------VendorGetTimeSlot--------------------------------------
		logger.info("Getting Vendor Available Timeslot");
		Response vTimeslotResponse = VendorEndPoints.vendor_TimeslotEP(context);
		JsonPath timeslotjs = CommonMethods.jsonToString(vTimeslotResponse);
		String stime = timeslotjs.getString("availableTimeSlots[1].startTime");
		String etime = timeslotjs.getString("availableTimeSlots[1].endTime");
		context.setAttribute("stime", stime);
		context.setAttribute("etime", etime);
		logger.info("list of vendor TimeSlot displayed with startTime " + stime + " endTime " + etime);
		Assert.assertEquals(vTimeslotResponse.getStatusCode(), 200);
		Assert.assertEquals(vTimeslotResponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(vTimeslotResponse, "Vendor's Available Timeslot are getting successfully");
		// ------------------------------DisableTimeSlot--------------------------------------------
		logger.info("Vendor disabling Timeslot");
		DisableTimeslot_Payload disabletimeslot = new DisableTimeslot_Payload();
		disabletimeslot.setId(0);
		disabletimeslot.setStartTime(stime);
		disabletimeslot.setEndTime(etime);
		Response disableResponse = VendorEndPoints.vendor_DisableTimeslotEP(context, disabletimeslot);
		disableResponse.then().log().all();
		JsonPath disabletimeslotjs = CommonMethods.jsonToString(disableResponse);
		String sTime = disabletimeslotjs.getString("startTime");
		String eTime = disabletimeslotjs.getString("endTime");
		int id = disabletimeslotjs.getInt("id");
		System.out.println();
		logger.info("DisableTimeSlot:startTime " + sTime + " endTime  " + eTime + " id " + id);
		Assert.assertEquals(disableResponse.getStatusCode(), 200, "Timeslots disable succcesfully");
		Assert.assertEquals(disableResponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(disableResponse, "Timeslot disable by Vendor successfully");
		// ----------------------------CustomerLogin----------------------------------------------
		logger.info("Customer start login");
		Response cresponse = CustomerEndPoints.customer_Login(CommonMethods.customer_Login(), context);
		JsonPath loginjs = CommonMethods.jsonToString(cresponse);
		Ctoken = loginjs.getString("token");
		System.out.println("Generated Token Id: " + Ctoken);
		context.setAttribute("CToken", Ctoken);
		logger.debug("Generated Token Id: {}", Ctoken);
		logger.info("Customer logged in successfully");
		Assert.assertEquals(cresponse.statusCode(), 200);
		Assert.assertEquals(cresponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(cresponse, "Customer Login response is getting successfully");
		// -----------------------------GetCategory---------------------------------------------
		logger.info("Getting category.");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response_customerGetCategoryEP = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCode2"), "");
		response_customerGetCategoryEP.then().log().all();
		JsonPath js_customerGetCategoryEP = CommonMethods.jsonToString(response_customerGetCategoryEP);
		int categoryId = js_customerGetCategoryEP.get("[5].id");
		context.setAttribute("categoryId", categoryId);
		logger.info("CategoryId fetched successfully " + categoryId);
		Assert.assertEquals(js_customerGetCategoryEP.getString("[5].name"), "Electricals");
		Assert.assertEquals(response_customerGetCategoryEP.statusCode(), 200);
		Assert.assertEquals(response_customerGetCategoryEP.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customerGetCategoryEP, "List of Categories are getting successfully");
		// -----------------------------------CustomerMyProfile------------------------------
		// Customer Address
		// LookUp.getMyProfile(context);
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

		// ----------------------------------CustomerTimeSlot------------------------------------
		logger.info("Customer available Timeslot");
		Response response_customer_GetTimeSlot = CustomerEndPoints.customer_GetTimeSlot(
				(int) context.getAttribute("addressId"), (int) context.getAttribute("categoryId"), context);
		response_customer_GetTimeSlot.then().log().all();
//			JsonPath customerTimeslotjs = CommonMethods.jsonToString(customerTimeslotResponse);
//			customerTimeslotjs.get("[].sTime");
		logger.info("Check whether disable timeslot is available in customer available timeslot:" + sTime);
		Assert.assertEquals(response_customer_GetTimeSlot.getBody().asString().contains(sTime), true, sTime);
		Assert.assertEquals(response_customer_GetTimeSlot.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetTimeSlot,
				"Customer available timeslot are getting with including timeslot disable by vendor");
	}

	@Test(priority = 16, enabled = true, description = "Verify that customer cannot cancel booking once service Started by vendor")
	public void customerCannotCancelBookingServiceStartedByVendor(ITestContext context) {
		// ----------------------------CustomerLogin---------------------------------------------
		logger.info("Started verify that customer cannot cancel booking service Started by vendor");
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
		// -------------------------------CustomerMyProfile----------------------------------
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
		// -------------------------------CustomerSubCategoryId-------------------------------------
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
		logger.info(subCategoryId + "SubCategoryId is getting Successfully.");
		// ------------------------------CustomerServiceId---------------------------------------
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
		// -------------------------------CustomerGetTimeslot---------------------------------------------
		logger.info("Getting timeslot for category = " + js_customerGetCategoryEP.getString("[3].name")
				+ " and Service name is = " + servicename);
		Response response_customer_GetTimeSlot = CustomerEndPoints.customer_GetTimeSlot(
				(int) context.getAttribute("addressId"), (int) context.getAttribute("categoryId3"), context);
		response_customer_GetTimeSlot.then().log().all();
		JsonPath js_customer_GetTimeSlot = CommonMethods.jsonToString(response_customer_GetTimeSlot);
		sTime = js_customer_GetTimeSlot.getString("[31].startTime");
		context.setAttribute("StartTime", sTime);
		eTime = js_customer_GetTimeSlot.getString("[31].endTime");
		context.setAttribute("EndTime", eTime);
		System.out.println("Start Time: " + sTime + "\nEnd Time: " + eTime);
		String statusline_customer_GetTimeSlot = response_customer_GetTimeSlot.getStatusLine();
		Assert.assertEquals(statusline_customer_GetTimeSlot, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetTimeSlot);
		logger.info("Category timeslot " + "Start Time: " + sTime + "\tEnd Time: " + eTime + " successfully.");
		System.out.println("address is " + (int) context.getAttribute("addressId"));
		System.out.println("category is " + (int) context.getAttribute("categoryId3"));
		// --------------------------------------------------------------------

		logger.info("Started creating new Booking.");
		System.out.println((int) context.getAttribute("serviceId"));
		// System.out.println(Collections.singletonList(bookingServices));
		System.out.println((int) context.getAttribute("addressId"));
		System.out.println((String) context.getAttribute("StartTime"));
		Response response_customer_CreateBooking = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response_customer_CreateBooking.then().log().all();
		JsonPath js_customer_CreateBooking = CommonMethods.jsonToString(response_customer_CreateBooking);
		String status = js_customer_CreateBooking.getString("status");
		customerBookingId = js_customer_CreateBooking.getInt("id");
		context.setAttribute("BookingId", customerBookingId);
		Assert.assertEquals(status, "New");
		String statusline_customer_CreateBooking = response_customer_CreateBooking.getStatusLine();
		Assert.assertEquals(statusline_customer_CreateBooking, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_CreateBooking);
		logger.info("New booking created successfully and Booking id is " + customerBookingId);
		// --------------------------------VendorLogin----------------------------------------------
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

		logger.info("Getting vendor timeslots.");
		Response response_vendor_TimeslotEP = VendorEndPoints.vendor_TimeslotEP(context);
		JsonPath js_vendor_TimeslotEP = CommonMethods.jsonToString(response_vendor_TimeslotEP);
		response_vendor_TimeslotEP.then().log().all();
		String statusline_vendor_TimeslotEP = response_vendor_TimeslotEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_TimeslotEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_TimeslotEP);
		logger.info("Vendor timeslots are shown successfully.");
		// -------------------------------------------------------------------------

		logger.info("Started vendor accepting booking of Booking id = " + customerBookingId);
		Response response_vendor_AcceptBookingEP = VendorEndPoints.vendor_AcceptBookingEP(context, customerBookingId);
		JsonPath js_vendor_AcceptBookingEP = CommonMethods.jsonToString(response_vendor_AcceptBookingEP);
		String status1 = js_vendor_AcceptBookingEP.getString("status");
		Assert.assertEquals(status1, "ExpertAssigned");
		String statusline_vendor_AcceptBookingEP = response_vendor_AcceptBookingEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(statusline_vendor_AcceptBookingEP);
		logger.info("Vendor accepted booking id of " + customerBookingId + " successfully");
		// --------------------------------CustomerGetBookingId-----------------------------------------
		logger.info("Get StartOTP through customerGetBooking by Id =" + customerBookingId);
		Response response_customer_GetBookingBySId = CustomerEndPoints.customer_GetBookingByIdEP(context,
				customerBookingId);
		response_customer_GetBookingBySId.then().log().all();
		JsonPath js_customer_GetBookingBySId = CommonMethods.jsonToString(response_customer_GetBookingBySId);
		int startOTP = js_customer_GetBookingBySId.getInt("startOTP");
		context.setAttribute("startOTP", startOTP);
		String statusline_customer_GetBookingBySId = response_customer_GetBookingBySId.getStatusLine();
		Assert.assertEquals(statusline_customer_GetBookingBySId, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetBookingBySId);
		logger.info("Customer get OTP " + startOTP + " of booking id = " + customerBookingId);
		// ---------------------------------------------------------------------
		logger.info("Started customer booking by Vendor");
		Response response_vendor_startservice = VendorEndPoints.vendor_startBookingEP(context,
				CommonMethods.sendBookingIdAndOtpforStartandEndService(context));
		String statusline_vendor_startservice = response_vendor_startservice.getStatusLine();
		Assert.assertEquals(statusline_vendor_startservice, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_vendor_startservice);
		logger.info("Customer service is started by entering Booking id = " + customerBookingId + " and Start OTP = "
				+ startOTP);
		// --------------------------------CustomerCancelBooking------------------------------------------
		logger.info("Started customer cancel the booking of id = " + customerBookingId);
		Response response_customer_CancelEP = CustomerEndPoints.customer_CancelEP(context, customerBookingId);
		response_customer_CancelEP.then().log().all();
		JsonPath js_customer_CancelEP = CommonMethods.jsonToString(response_customer_CancelEP);
		String cancelmessage = js_customer_CancelEP.toString();
		String statusline_customer_CancelEP = response_customer_CancelEP.getStatusLine();
		Assert.assertEquals(statusline_customer_CancelEP, "HTTP/1.1 400 Bad Request");
		Assert.assertNotNull(response_customer_CancelEP);
		logger.info("Customer can not cancel the service.");
		logger.info("Customer cannot cancel booking of id " + customerBookingId
				+ " on started service and response message is " + cancelmessage);
	}
	
    //Monika
	@Test(priority = 17, enabled = true, description = "Validate that the customer cancels the booking one hour before the time slot")
	public void customerCancelBookingServiceOneHourBeforeTimeslot(ITestContext context)
			throws ParseException, InterruptedException {
		logger.info("Started validate that the customer cancels the booking one hour before the scheduled time slot");
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
		sTime = js_customer_GetTimeSlot.getString("[50].startTime");
		context.setAttribute("StartTime", sTime);
		eTime = js_customer_GetTimeSlot.getString("[50].endTime");
		context.setAttribute("EndTime", eTime);
		System.out.println("Start Time: " + sTime + "\nEnd Time: " + eTime);
		String statusline_customer_GetTimeSlot = response_customer_GetTimeSlot.getStatusLine();
		Assert.assertEquals(statusline_customer_GetTimeSlot, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetTimeSlot);
		logger.info("Category timeslot " + "Start Time: " + sTime + "\tEnd Time: " + eTime + " successfully.");
		System.out.println("address is " + (int) context.getAttribute("addressId"));
		System.out.println("category is " + (int) context.getAttribute("categoryId3"));
		// --------------------------------------------------------------------

		logger.info("Started creating new Booking.");
		System.out.println((int) context.getAttribute("serviceId"));
		// System.out.println(Collections.singletonList(bookingServices));
		System.out.println((int) context.getAttribute("addressId"));
		System.out.println((String) context.getAttribute("StartTime"));
		Response response_customer_CreateBooking = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response_customer_CreateBooking.then().log().all();
		JsonPath js_customer_CreateBooking = CommonMethods.jsonToString(response_customer_CreateBooking);
		String status = js_customer_CreateBooking.getString("status");
		customerBookingId = js_customer_CreateBooking.getInt("id");
		context.setAttribute("BookingId", customerBookingId);
		Assert.assertEquals(status, "New");
		String statusline_customer_CreateBooking = response_customer_CreateBooking.getStatusLine();
		Assert.assertEquals(statusline_customer_CreateBooking, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_CreateBooking);
		logger.info("New booking created successfully and Booking id is " + customerBookingId);
		// --------------------------------------------------------------------------------

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
		JsonPath js_vendor_AcceptBookingEP = CommonMethods.jsonToString(response_vendor_AcceptBookingEP);
		String status1 = js_vendor_AcceptBookingEP.getString("status");
		Assert.assertEquals(status1, "ExpertAssigned");
		String TIME = js_vendor_AcceptBookingEP.getString("scheduledOn");
		context.setAttribute("ScheduledOn", TIME);
		String statusline_vendor_AcceptBookingEP = response_vendor_AcceptBookingEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(statusline_vendor_AcceptBookingEP);
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		ZonedDateTime indianTime = ZonedDateTime.parse(sTime, formatter);
		System.out.println("Schedule time is : " + indianTime);
		ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("UTC"));
		System.out.println("Current Time in UTC/GMT : " + currentTime);
		ZonedDateTime oneHourBeforeSchduleTime = indianTime.minusHours(1);
		System.out.println("One hour before: " + oneHourBeforeSchduleTime.toString());
		if (currentTime.isBefore(oneHourBeforeSchduleTime)) {
			logger.info("Started customer cancel the booking of id = " + customerBookingId);
			Response response_customer_CancelEP = CustomerEndPoints.customer_CancelEP(context, customerBookingId);
			response_customer_CancelEP.then().log().all();
			JsonPath js_customer_CancelEP = CommonMethods.jsonToString(response_customer_CancelEP);
			String cancelmessage = js_customer_CancelEP.toString();
			String statusline_customer_CancelEP = response_customer_CancelEP.getStatusLine();
			Assert.assertEquals(statusline_customer_CancelEP, "HTTP/1.1 200 OK");
			Assert.assertNotNull(response_customer_CancelEP);
			logger.info("Customer can cancel the service.");
			logger.info(
					"Customer cancel booking of id " + customerBookingId + " one hour before of scheduled timeslot.");
		} else {
			System.out.println("Customer cannot cancel the booking");
			logger.info(
					"Customer cannot cancel the booking as the scheduled timeslot is less than one hour or it is a past timeslot.");
		}
	}

	// Mayuri..................
	@Test(priority = 18, enabled = true, description = "Verify that customer cancel booking before vendor accepts that booking")
	public void customerCancelBookingBeforeVendorAccept(ITestContext context) {
		// Customer Login
		logger.info("Customer starts to login..........");
		Response cresponse = CustomerEndPoints.customer_Login(CommonMethods.customer_Login(), context);
		JsonPath loginjs = CommonMethods.jsonToString(cresponse);
		cresponse.then().log().all();
		String Ctoken = loginjs.getString("token");
		System.out.println("Generated Token Id: " + Ctoken);
		context.setAttribute("CToken", Ctoken);
		logger.debug("Generated Token Id: {}", Ctoken);
		logger.info("Customer logged in successfully");
		Assert.assertEquals(cresponse.statusCode(), 200);
		Assert.assertEquals(cresponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(cresponse, "Customer Login response is getting successfully");

		LookUp.createBooking(context);

		logger.info("Customer is creating a booking........");
		Response response_customer_CreateBooking = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response_customer_CreateBooking.then().log().all();
		JsonPath js_customer_CreateBooking = CommonMethods.jsonToString(response_customer_CreateBooking);
		String status = js_customer_CreateBooking.getString("status");
		customerBookingId = js_customer_CreateBooking.getInt("id");
		context.setAttribute("BookingId", customerBookingId);
		Assert.assertEquals(status, "New");
		String statusline_customer_CreateBooking = response_customer_CreateBooking.getStatusLine();
		Assert.assertEquals(statusline_customer_CreateBooking, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_CreateBooking);
		logger.info("New booking created successfully and Booking id is " + customerBookingId);

		logger.info("Customer cancel booking.......");
		Response response = CustomerEndPoints.customer_CancelEP(context, (int) context.getAttribute("BookingId"));
		response.then().log().all();
		logger.info("Customer cancel booking successfully before vendor accept the booking of id "
				+ (int) context.getAttribute("BookingId"));
	}

	// Monika
	@Test(priority = 19, enabled = true, description = "Validate that the customer cannot cancels the booking 15 min before the scheduled time slot")
	public void customerCancelBookingService15minuteBeforeTimeslot(ITestContext context) {
		logger.info(
				"Started validate that the customer trying to cancel the booking 15 minute before the scheduled time slot");
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

		ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("GMT"));
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		String formattedTime = currentTime.format(formatter1);
		String currentTimeinGMT = formattedTime + "Z";
		System.out.println("Formatted Current Time: " + formattedTime);
		logger.info("Fetching Customer booking");
		String[] status = { "New", "ExpertAssigned", "Inprogress", "Cancelled", "Completed" };
		Response response = CustomerEndPoints.customer_GetBookingEndPoint(context, status[1], 1, 1000);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String stat = js.getString("[1].status");
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status[1], stat);
		logger.info("Customer booking shown successfully of Status" + status[1]);
		JsonPath jsonpath = response.jsonPath();

		// Extract array and check size
		List<Object> dataArray = jsonpath.getList("data");
		int arraySize = dataArray.size();
		JsonPath js1 = CommonMethods.jsonToString(response);
		// for loop for checking scheduled timeslot with before 15 minute of currenttime
		// in GMT
		for (int i = 0; i < arraySize; i++) {
			String scTime = js.getString("[" + i + "].scheduledOn");
			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
			ZonedDateTime indianTime = ZonedDateTime.parse(scTime + "Z", formatter);
			// IndianTime = scheduled time
			System.out.println("Schedule time is : " + indianTime);
			ZonedDateTime fifteenminuteBeforeScheduleTime = indianTime.minusMinutes(15);
			// fifteenminuteBeforeScheduleTime = sceduled time - 15 minute
			System.out.println("15 minute before time is : " + fifteenminuteBeforeScheduleTime.toString());
			boolean ab = currentTimeinGMT.equals(fifteenminuteBeforeScheduleTime);
			System.out.println(ab);
			if (currentTime.isBefore(indianTime)
					// current = current time in GMT it is checking current time with 15 minute
					// before of scheduled time

					&& currentTimeinGMT.equals(fifteenminuteBeforeScheduleTime.toString())) {
				int ithbookingid = js.getInt("[" + i + "].id");
				logger.info("Started customer cancel the booking of id = " + ithbookingid);
				Response response_customer_CancelEP = CustomerEndPoints.customer_CancelEP(context, ithbookingid);
				response_customer_CancelEP.then().log().all();
				JsonPath js_customer_CancelEP = CommonMethods.jsonToString(response_customer_CancelEP);
				String cancelmessage = js_customer_CancelEP.toString();
				String statusline_customer_CancelEP = response_customer_CancelEP.getStatusLine();
				Assert.assertEquals(statusline_customer_CancelEP, "HTTP/1.1 400 Bad Request");
				Assert.assertNotNull(response_customer_CancelEP);
				System.out.println("Booking is cancelled of booking id is " + ithbookingid);
				logger.info("Service is cancelled of booking id is " + ithbookingid + " before 15 minutes that is "
						+ fifteenminuteBeforeScheduleTime + " of scheduled timeslot " + indianTime);
				return;
			} else {
				continue;
			}
		}
		logger.info(
				"Booking is not present customer should create booking then validate that the customer cannot cancels the booking 15 min before the scheduled time slot");
		System.out.println(
				"Booking is not present customer should create booking then validate that the customer cannot cancels the booking 15 min before the scheduled time slot");
		// -----------------------------------------------------------------------------------

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
		sTime = js_customer_GetTimeSlot.getString("[41].startTime");
		context.setAttribute("StartTime", sTime);
		eTime = js_customer_GetTimeSlot.getString("[41].endTime");
		context.setAttribute("EndTime", eTime);
		System.out.println("Start Time: " + sTime + "\nEnd Time: " + eTime);
		String statusline_customer_GetTimeSlot = response_customer_GetTimeSlot.getStatusLine();
		Assert.assertEquals(statusline_customer_GetTimeSlot, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_GetTimeSlot);
		logger.info("Category timeslot " + "Start Time: " + sTime + "\tEnd Time: " + eTime + " successfully.");
		System.out.println("address is " + (int) context.getAttribute("addressId"));
		System.out.println("category is " + (int) context.getAttribute("categoryId3"));
		// --------------------------------------------------------------------

		logger.info("Started creating new Booking.");
		System.out.println((int) context.getAttribute("serviceId"));
		System.out.println((int) context.getAttribute("addressId"));
		System.out.println((String) context.getAttribute("StartTime"));
		Response response_customer_CreateBooking = CustomerEndPoints.customer_CreateBookingEndPoint(context,
				CommonMethods.createBooking(context));
		response_customer_CreateBooking.then().log().all();
		JsonPath js_customer_CreateBooking = CommonMethods.jsonToString(response_customer_CreateBooking);
		String bookingstatus = js_customer_CreateBooking.getString("status");
		customerBookingId = js_customer_CreateBooking.getInt("id");
		context.setAttribute("BookingId", customerBookingId);
		Assert.assertEquals(bookingstatus, "New");
		String statusline_customer_CreateBooking = response_customer_CreateBooking.getStatusLine();
		Assert.assertEquals(statusline_customer_CreateBooking, "HTTP/1.1 200 OK");
		Assert.assertNotNull(response_customer_CreateBooking);
		logger.info("New booking created successfully and Booking id is " + customerBookingId);
		// -------------------------------------------------------------------------

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
		JsonPath js_vendor_AcceptBookingEP = CommonMethods.jsonToString(response_vendor_AcceptBookingEP);
		String status1 = js_vendor_AcceptBookingEP.getString("status");
		Assert.assertEquals(status1, "ExpertAssigned");
		String TIME = js_vendor_AcceptBookingEP.getString("scheduledOn");
		context.setAttribute("ScheduledOn", TIME);
		String statusline_vendor_AcceptBookingEP = response_vendor_AcceptBookingEP.getStatusLine();
		Assert.assertEquals(statusline_vendor_AcceptBookingEP, "HTTP/1.1 200 OK");
		Assert.assertNotNull(statusline_vendor_AcceptBookingEP);
		logger.info("Vendor accepted booking of Booking id = " + customerBookingId);
		// -------------------------------------------------------------------

		ZonedDateTime currentTime1 = ZonedDateTime.now(ZoneId.of("GMT"));
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		String formattedTime1 = currentTime1.format(formatter2);
		String currentTimeinGMT1 = formattedTime1 + "Z";
		System.out.println("Formatted Current Time: " + formattedTime1);
		logger.info("Starting vendor_get_booking...");
		String[] status2 = { "New", "ExpertAssigned", "Inprogress", "Cancelled", "Completed" };
		Response response1 = VendorEndPoints.vendor_MybookingEP(context, status2[1], 1, 1000);
		response1.then().log().all();
		JsonPath jsonpath2 = response1.jsonPath();

		// Extract array and check size
		List<Object> dataArray1 = jsonpath2.getList("data");
		int arraySize1 = dataArray1.size();
		JsonPath js2 = CommonMethods.jsonToString(response1);
		// for loop for checking scheduled timeslot with before 15 minute of currenttime
		// in GMT
		for (int i = 0; i < arraySize1; i++) {
			String scTime = js.getString("[" + i + "].scheduledOn");
			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
			ZonedDateTime indianTime = ZonedDateTime.parse(scTime + "Z", formatter);
			// IndianTime = scheduled time
			System.out.println("Schedule time is : " + indianTime);
			ZonedDateTime fifteenminuteBeforeScheduleTime = indianTime.minusMinutes(15);
			// fifteenminuteBeforeScheduleTime = sceduled time - 15 minute
			System.out.println("15 minute before time is : " + fifteenminuteBeforeScheduleTime.toString());
			boolean ab = currentTimeinGMT1.equals(fifteenminuteBeforeScheduleTime);
			System.out.println(ab);
			if (currentTimeinGMT1.equals(fifteenminuteBeforeScheduleTime.toString())) {
				// current = current time in GMT it is checking current time with 15 minute
				// before of scheduled time
				int ithbookingid = js.getInt("[" + i + "].id");
				logger.info("Started customer cancel the booking of id = " + ithbookingid);
				Response response_customer_CancelEP = CustomerEndPoints.customer_CancelEP(context, ithbookingid);
				response_customer_CancelEP.then().log().all();
				JsonPath js_customer_CancelEP = CommonMethods.jsonToString(response_customer_CancelEP);
				String cancelmessage = js_customer_CancelEP.toString();
				String statusline_customer_CancelEP = response_customer_CancelEP.getStatusLine();
				Assert.assertEquals(statusline_customer_CancelEP, "HTTP/1.1 400 Bad Request");
				Assert.assertNotNull(response_customer_CancelEP);
				System.out.println("Booking is cancelled of booking id is " + ithbookingid);
				logger.info("Service is cancelled of booking id is " + ithbookingid + " before 15 minutes that is "
						+ fifteenminuteBeforeScheduleTime + " of scheduled timeslot " + indianTime);
			} else {
				continue;
			}
		}
		logger.info("Cancellation is not occur as CurrentTime is " + currentTimeinGMT
				+ " not equal to 15 minute before scheduled timeslot of any accepted booking.");
	}
}