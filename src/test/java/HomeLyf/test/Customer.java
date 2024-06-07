package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import HomeLyf.EndPoints.CustomerEndPoints;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class Customer {

	public static String bearertoken;
	private static Logger logger = LogManager.getLogger(User.class);
	
	
	@Test(priority = 1, dataProvider = "userlogindata", dataProviderClass = DataProviderClass.class)
	public void userLogin(String mobileNumber, String type, String emailAddress, String password, String location) {
		
		logger.info("Starting userLogin test...");
		
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
	
		String res = response.asPrettyString();
		
		JsonPath js = new JsonPath(res);

		this.bearertoken = js.getString("token");
		
		System.out.println("Generated Token Id: " + bearertoken);
		
		logger.debug("Generated Token Id: {}", bearertoken);

//		Assert.assertEquals(response.statusCode(), 200);
		
		logger.info("User logged in successfully");
	}
	
	@Test(priority = 2, dataProvider = "Booking_Status", dataProviderClass = DataProviderClass.class)
	public void Booking_Status(String Status) {
		logger.info("Starting Customer booking test...");
		String token;
		ITestContext context;
		//Response response = CustomerEndPoints.Booking( Status);
		Response response=CustomerEndPoints.CustomerGetBooking(bearertoken, Status);
		response.then().log().all();
		JsonPath js = new JsonPath(bearertoken);
		this.bearertoken = js.getString("token");
		//System.out.println("Token:=>" + bearertoken);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("customer Booking status successfully");
		//context.setAttribute(bearertoken, response);
	}
	
	
}

