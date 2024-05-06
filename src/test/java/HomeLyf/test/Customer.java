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
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.Validatable;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class Customer {
	private static Logger logger = LogManager.getLogger(User.class);
	String token;
	int addressId;

	@Test(priority = 1, dataProvider = "Customerlogin", dataProviderClass = DataProviderClass.class)
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
	public void customer_service(ITestContext context) {

		logger.info("Starting customer_service...");
		Response response = CustomerEndPoints.customer_service(context);
		response.then().log().all();
		// print status line
		Assert.assertEquals(response.statusCode(), 200);
		response.then().statusCode(200).log().all();
		logger.info("customer_service subcategory is shown successfully");

	}

	@Test(priority = 15, description = "Creating customer new address with valid credentials", dataProvider = "CustomerAddressData", dataProviderClass = DataProviderClass.class)
	public void customer_Addresstest(ITestContext context, String name, String type, String lineOne, String lineTwo,
			String lineThree, String location, String postCodeID, String cityID) {

		logger.info("Adding Customer Address");
		Response response = CustomerEndPoints.customer_Address(context,
				CommonMethods.address_details(name, type, lineOne, lineTwo, lineThree, location, postCodeID, cityID));
		response.then().log().all();
		JsonPath js= CommonMethods.jsonToString(response);
		addressId = js.getInt("id");
		
		Assert.assertEquals(response.statusCode(),200);
		logger.info("Added Customer new Address successfully");

	}

}
