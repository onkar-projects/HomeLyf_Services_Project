package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import HomeLyf.EndPoints.CustomerEndPoints;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Payload.Address;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Customer {
	
	private static Logger logger = LogManager.getLogger(User.class);
	String token;
	String sTime;
	String[] paymentMode = { "cash", "upi", "card", "other"};
	String[] paymentStatus = {"pending", "inprogress", "delayed", "cancelled","completed","refundinprogress","refunded"};
	Address address;

	
	@Test(priority = 1, dataProvider = "Customerlogin",dataProviderClass = DataProviderClass.class)
	public void userLogin(ITestContext context, String mobileNumber, String type, String emailAddress, String password,
			String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);

		token = js.getString("token");
		System.out.println("Generated Token Id: " + token);
		context.setAttribute("Token", token);
		logger.debug("Generated Token Id: {}", token);

		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(priority = 2,enabled = true, description = "Customer profile should show")
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

	@Test(priority = 3 ,enabled = true, description = "Customer should get category")
	public void customer_GetCategoryTest(ITestContext context) {
		logger.info("Getting category");
		LookUp.getPostCode(context);
		LookUp.getCategory(context);
		Response response = CustomerEndPoints.customer_GetCategoryEP(context,(String)context.getAttribute("postCode") ,(String)context.getAttribute("name"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int categoryId = js.get("[0].id");
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js.getString("[0].name"),"Painting");
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Category fetched successfully");
	}
	
	@Test(priority = 4,enabled = true, description = "Customer should Show the SubCategory")
	public void customer_subCategoryIdTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_SubCategoryEP(context, (int)context.getAttribute("categoryId"));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		int subCategoryId = js.getInt("[0].id");
		context.setAttribute("subCategoryId",subCategoryId);
		Assert.assertEquals(response.getStatusCode(), 200);
		logger.info("SubCategory Fetched Successfully");
	}
	
	@Test(priority = 5,enabled = true, description = " customer should get services")
	public void customer_GetService(ITestContext context) {

		logger.info("Starting customer_service...");
		Response response = CustomerEndPoints.customer_service(context, (int)context.getAttribute("subCategoryId") );
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		context.setAttribute("serviceid", js.getInt("[0].id"));
		System.out.println("----------------------------------"+js.get("[0].id")+"------------------------------------");
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("customer_service subcategory is shown successfully");
	}
	
	@Test(priority = 6,enabled = true)
	public void customer_GetTimeSlot(ITestContext context) {

		logger.info("Starting customer_service...");
		Response response = CustomerEndPoints.customer_GetTimeSlot((int)context.getAttribute("addressId"),(int)context.getAttribute("categoryId"),context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		sTime = js.getString("[0].startTime");
		context.setAttribute("StartTime", sTime);
		String eTime = js.getString("[0].endTime");
		System.out.println("Start Time: " + sTime + "\n End Time: " + eTime);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("customer_service subcategory is shown successfully");

	}
	
	
	@Test(priority = 7,description = "Customer should create new Booking")
	public void customer_CreateBooking(ITestContext context) {

		logger.info("Creating new Booking");
		Response response = CustomerEndPoints.customer_CreateBookingEndPoint(context, CommonMethods.createBooking(context));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("status");
		int bookingId = js.getInt("id");
		context.setAttribute("bookingId", bookingId);
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status, "New");
		logger.info("New booking created successfully");
	}

	@Test(priority = 8, description = "customer booking should show")
	public void customer_GetBookingTest(ITestContext context) {
		String[] status = {"New", "expertassigned", "inprogress", "cancelled", "completed"};
		logger.info("Fetching Customer booking");
		Response response = CustomerEndPoints.customer_GetBookingEndPoint(context, status[1]);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String stat = js.getString("[0].status");
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status[0], stat);
		logger.info("Customer booking shown successfully of Status" + status[0]);
	}
	
	@Test(priority = 9,enabled = true, description = "customer should update payment status ")
	public void customer_UpdatePaymnetStatus(ITestContext context) {
		logger.info("Updateing Payment Status");
		
	
		Response response = CustomerEndPoints.customer_UpdatePaymentStatusEP(context, CommonMethods.updatePaymentStatusData(context));
		response.then().log().all();
		
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.getString("paymentStatus");
		String payMode = js.getString("paymentMode");
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(status, paymentStatus[1]);
		Assert.assertEquals(payMode, paymentMode[3]);
		
		logger.info("Customer update payment status successfully ");
	}
	
	@Test(priority = 10,description = "Customer should calculate as per quntity ")
	public void customer_Calculate(ITestContext context) {
		
		Response response = CustomerEndPoints.customer_CalculateEP(context, CommonMethods.calculateData(context));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}
	
	@Test(priority = 11, description = "Customer should Cancel booking ")
	public void customer_CancelTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_CancelEP(context, (int)context.getAttribute("bookingId"));
		response.then().log().all();
	}
	
	@Test(priority = 12,enabled=false,description = "Customer should create new address with valid credentials", dataProvider = "CustomerAddressData", dataProviderClass = DataProviderClass.class)
	public void customer_Addresstest(ITestContext context, String name, String type, String lineOne, String lineTwo,
			String lineThree, String location) {
		logger.info("Adding Customer Address");
		Response response = CustomerEndPoints.customer_Address(context,
				CommonMethods.address_details(name, type, lineOne, lineTwo, lineThree, location,context));
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
//		int NewAddressId = js.getInt("id");
//		context.setAttribute("NewAddressId", NewAddressId);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Added Customer new Address successfully");

	}

}

