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
	private static Logger logger = LogManager.getLogger(User.class);
	String token;

	@Test(priority = 1, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
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

	@Test(priority = 2)
	public void vendor_get_booking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");

		Response response = VendorEndPoints.vendorgetbooking(context);
		response.then().log().all();
		// print status line
		Assert.assertEquals(response.statusCode(), 200);
		response.then().statusCode(200).log().all();
		logger.info("vendor_get_booking is shown successfully.");

	}

}
