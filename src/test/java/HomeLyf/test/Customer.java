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

public class Customer {

	private static Logger logger = LogManager.getLogger(User.class);
	static String Ctoken;
	static String token;
	static String statusine_CustomerLogin;
	String sTime;
	String[] paymentMode = { "cash", "upi", "card", "other" };
	String[] paymentStatus = { "pending", "inprogress", "delayed", "cancelled", "completed", "refundinprogress",
			"refunded" };
	Address address;

	@Test(priority = 1, enabled = true, dataProvider = "Customerlogin", dataProviderClass = DataProviderClass.class)
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

	@Test(priority = 2, enabled = true, description = "Customer profile should show")
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

	@Test(priority = 3, enabled = true, description = "Customer should get category")
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

	@Test(priority = 4, enabled = true, description = "Customer should Show the SubCategory")
	public void customer_subCategoryIdTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_SubCategoryEP(context, (int) context.getAttribute("categoryId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int subCategoryId = js.getInt("[0].id");
		context.setAttribute("subCategoryId", subCategoryId);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("SubCategory Fetched Successfully");
	}

	@Test(priority = 5, enabled = true, description = " customer should get services")
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

	@Test(priority = 6, enabled = true)
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

	@Test(priority = 7, enabled = true, description = "Customer should create new Booking")
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

	@Test(priority = 8, enabled = true, description = "customer booking should show")
	public void customer_GetBookingTest(ITestContext context) {
		String[] status = { "New", "expertassigned", "inprogress", "cancelled", "completed" };
		logger.info("Fetching Customer booking");
		Response response = CustomerEndPoints.customer_GetBookingEndPoint(context, status[0], 1, 10);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String stat = js.getString("[0].status");
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status[0], stat);
		logger.info("Customer booking shown successfully of Status" + status[0]);
	}

	@Test(priority = 9, enabled = true, description = "customer should update payment status ")
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

	@Test(priority = 10, enabled = true, description = "Customer should calculate as per quntity ")
	public void customer_Calculate(ITestContext context) {
		Response response = CustomerEndPoints.customer_CalculateEP(context, CommonMethods.calculateData(context));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}

	@Test(priority = 11, enabled = true, description = "Customer should Cancel booking ")
	public void customer_CancelTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_CancelEP(context, (int) context.getAttribute("bookingId"));
		response.then().log().all();
	}

	@Test(priority = 12, enabled = true, description = "Customer should create new address with valid credentials", dataProvider = "CustomerAddressData", dataProviderClass = DataProviderClass.class)
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
	@Test(priority = 14, enabled = true)
	public void customer_RescheduleTime(ITestContext context)
	{
		Response response = CustomerEndPoints.customer_RescheduleEP(context,
				CommonMethods.CustomerReschedule_Payload(context, 52673, "2024-06-06T04:00:00Z"));
		response.then().log().all();
	}

	@Test(priority = 15, enabled = true, description = "Verify Disabled Timeslots by vendor are not visible to customer for different service Postcode")
	public void verifyDisabledTimeslotsAreNotVisibleToCustomer(ITestContext context) {
		// Vendor Login
		Response vresponse = VendorEndPoints.vendor_Login(context, CommonMethods.vendor_Login());
		// System.out.println("---------------"+vresponse.getStatusLine());
		JsonPath vloginjs = CommonMethods.jsonToString(vresponse);
		String Vtoken = vloginjs.getString("token");
		System.out.println("Generated Token Id: " + token);
		context.setAttribute("VToken", Vtoken);
		logger.debug("Generated Token Id: {}", Vtoken);
		logger.info("Vendor logged in successfully");
		Assert.assertEquals(vresponse.statusCode(), 200);
		Assert.assertEquals(vresponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(vresponse, "Vendor Login response is getting succesfully");

		// Vendor GetTimeSlot
		Response vTimeslotResponse = VendorEndPoints.vendor_TimeslotEP(context);
		JsonPath timeslotjs = CommonMethods.jsonToString(vTimeslotResponse);
		String stime = timeslotjs.getString("availableTimeSlots[5].startTime");
		String etime = timeslotjs.getString("availableTimeSlots[5].endTime");
		context.setAttribute("stime", stime);
		context.setAttribute("etime", etime);
		logger.info("list of vendor TimeSlot");
		Assert.assertEquals(vTimeslotResponse.getStatusCode(), 200);
		Assert.assertEquals(vTimeslotResponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(vTimeslotResponse, "Vendor's Available Timeslot are getting successfully");

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
		Assert.assertEquals(disableResponse.getStatusCode(), 200, "Timeslots disable succcesfully");
		Assert.assertEquals(disableResponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(disableResponse, "Timeslot disable by Vendor successfully");

		// Customer Login
		Response cresponse = CustomerEndPoints.customer_Login(CommonMethods.customer_Login(), context);
		JsonPath loginjs = CommonMethods.jsonToString(cresponse);
		String Ctoken = loginjs.getString("token");
		System.out.println("Generated Token Id: " + token);
		context.setAttribute("CToken", Ctoken);
		logger.debug("Generated Token Id: {}", Ctoken);
		logger.info("Customer logged in successfully");
		Assert.assertEquals(cresponse.statusCode(), 200);
		Assert.assertEquals(cresponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(cresponse, "Customer Login response is getting successfully");

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
		Assert.assertEquals(js.getString("[5].name"), "Electricals");
		Assert.assertEquals(response2.statusCode(), 200);
		Assert.assertEquals(response2.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(response2, "List of Categories are getting successfully");

		// Customer TimeSlot
		LookUp.getMyProfile(context);
		Response customerTimeslotResponse = CustomerEndPoints
				.customer_GetTimeSlot((int) context.getAttribute("addressId"), categoryId, context);
		customerTimeslotResponse.then().log().all();
		logger.info("Check whether disable timeslot is available in customer available timeslot ");
		// Verify that disabled timeslots are no longer visible to the customer
		Assert.assertTrue(customerTimeslotResponse.getBody().asString().contains(sTime),
				"Disabled timeslots are still visible to the customer");
		Assert.assertEquals(customerTimeslotResponse.statusLine(), "HTTP/1.1 200 OK");
		Assert.assertNotNull(customerTimeslotResponse,
				"Customer available timeslot are getting with including timeslot disable by vendor");
	}
	
	@Test(priority = 16, enabled = true, description = "Verify that customer cannot cancel booking  service Started by vendor")
	
		public void VerifyCustomerCannotCancelBookingServiceStartedByVendor(ITestContext context)
		{
		logger.info("Started Verify that Customer can not cancel booking service started by vendor");
		logger.info("Started Customer login test");
		Response response_CustomerLogin=UserEndPoints.userLogin(CommonMethods.CustomerLoginforAcceptBookingAfterEnablingTimeslot());
		response_CustomerLogin.then().log().all();  
		String res_CustomerLogin=response_CustomerLogin.asPrettyString();
		JsonPath js_CustomerLogin=new JsonPath(res_CustomerLogin);
		Ctoken=js_CustomerLogin.getString(token)
;
		System.out.println("Generate token Id " + Ctoken );
		context.setAttribute("Ctoken", Ctoken);
		String statusline_CustomerLogin=response_CustomerLogin.getStatusLine();
		Assert.assertEquals(statusine_CustomerLogin,"HTTP/1.1 200 OK");
		Assert.assertNotNull(response_CustomerLogin);
		logger.debug("Generate Token Id :  ()",Ctoken);
		logger.info("Customer logger in sucessfully");
		
		
		}
}