package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import HomeLyf.EndPoints.CustomerEndPoints;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CustomerTests {
	String token;
	
	private static Logger logger = LogManager.getLogger(CustomerTests.class);
	@Test(priority = 1, dataProvider = "CustomerLogindata", dataProviderClass = DataProviderClass.class)
	public void userLogin(String mobileNumber, String type, String emailAddress, String password, String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);

		token = js.getString("token");
//		System.out.println("Generated Token Id: " + token);
		logger.debug("Generated Token Id: {}",token);

		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}
	
	@Test
	public void getLookupCategory() {
		Response response = CustomerEndPoints.lookupCategory();
		response.then().log().all();
		
		
	}
		 
		 
	@Test(priority = 2)
	public void getSubCateogryId() {
		Response response = CustomerEndPoints.getsubCategoryId(token, 2);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		
	}
}
