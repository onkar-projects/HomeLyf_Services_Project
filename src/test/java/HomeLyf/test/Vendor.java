package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.EndPoints.VendorEndPoints;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Vendor {
	private static Logger logger = LogManager.getLogger(User.class);
	String token;
	int bookingId;

	@Test(priority = 1, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin(ITestContext context, String mobileNumber, String type, String emailAddress, String password,
			String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res  = response.asPrettyString();
		JsonPath js = new JsonPath(res);

		token = js.getString("token");
		System.out.println("Generated Token Id: " + token);
		context.setAttribute("Token", token);
		logger.debug("Generated Token Id: {}", token);

		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(priority = 2, enabled = false)
	public void vendor_get_booking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		Response response = VendorEndPoints.vendorgetbooking(context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		bookingId = js.get("[2].id");
		System.out.println("BookingID: "+bookingId);
		
		// print status line
		Assert.assertEquals(response.statusCode(), 200);
		response.then().statusCode(200).log().all();
		logger.info("vendor_get_booking is shown successfully.");

	}
	
	@Test(priority = 6, enabled = true)
	public void vendor_cancelBooking(ITestContext context) {
		Response response = VendorEndPoints.vendorCancelBooking(context,10172);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.get("status");
		System.out.println("Status: "+status);
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(status, "New");
		System.out.println("bookingId cancel successfully");
		
		
	}

}
