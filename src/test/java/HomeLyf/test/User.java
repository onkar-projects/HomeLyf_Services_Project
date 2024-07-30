package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import Validations.ErrorValidation;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class User {

	String token;
	private static Logger logger = LogManager.getLogger(User.class);

	@Test(priority = 1, dataProvider = "Vendordata", dataProviderClass = DataProviderClass.class)

	public void userSignUp(String name, String mobileNumber, String type, String emailAddress, String password,
			String Id, String scategories, String spostcodes, String addharnum, String exp, String addressname,
			String addresstype, String line1, String line2, String line3, String location, String postid,
			String cityid) {
		logger.info("Starting userSignUp test...");
		Response response = UserEndPoints.signUP(
				CommonMethods.signUpData(name, mobileNumber, type, emailAddress, password, Id, scategories, spostcodes,
						addharnum, exp, addressname, addresstype, line1, line2, line3, location, postid, cityid));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User signed up successfully");
	}

	@Test(priority = 2, dataProvider = "invalidvendordata", dataProviderClass = DataProviderClass.class)

	public void invaliduserSignUp(String name, String mobileNumber, String type, String emailAddress, String password,
			String Id, String scategories, String spostcodes, String addharnum, String exp, String addressname,
			String addresstype, String line1, String line2, String line3, String location, String postid,
			String cityid) {
		Response response = UserEndPoints.signUP(CommonMethods.invaliduserSignUp(name, mobileNumber, type, emailAddress,
				password, Id, scategories, spostcodes, addharnum, exp, addressname, addresstype, line1, line2, line3,
				location, postid, cityid));
		logger.info("Starting invaliduserSignUp test...");
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		String Mnum = js.getString("errors.MobileNumber[0]");
		String email = js.getString("errors.EmailAddress[0]");
		String pass = js.getString("errors.Password[0]");

		Assert.assertEquals(Mnum, ErrorValidation.mobileNumber);
		Assert.assertEquals(email, ErrorValidation.emailAddress);
		Assert.assertEquals(pass, ErrorValidation.password);
		Assert.assertEquals(response.statusCode(), 400);
		logger.warn("Invalid user sign up attempted, expected validation errors");
	}

	@Test(priority = 3, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin(String mobileNumber, String type, String emailAddress, String password, String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		token = js.getString("token");
//		System.out.println("Generated Token Id: " + token);
		logger.debug("Generated Token Id: {}", token);
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(priority = 4, dataProvider = "invalid_userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin_With_Invalid_Data(String mobileNumber, String type, String emailAddress, String password,
			String location) {
		logger.info("Starting userLogin_With_Invalid_Data test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		String Mnum = js.getString("errors.MobileNumber[0]");
		String email = js.getString("errors.EmailAddress[0]");
		String pass = js.getString("errors.Password[0]");
		Assert.assertEquals(Mnum, ErrorValidation.mobileNumber);
		Assert.assertEquals(email, ErrorValidation.emailAddress);
		Assert.assertEquals(pass, ErrorValidation.password);
		Assert.assertEquals(response.statusCode(), 400);
		logger.warn("User login attempted with invalid data");
	}

	@Test(priority = 5, dataProvider = "emailOTP", dataProviderClass = DataProviderClass.class)
	public void sendEmailOTP(String emailAddress) {
		logger.info("Starting sendEmailOTP test...");
		Response response = UserEndPoints.sendEmailOTP(CommonMethods.sendEmailOTP(emailAddress));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Email OTP sent successfully");
	}

	@Test(priority = 6, dataProvider = "invalidemail", dataProviderClass = DataProviderClass.class)
	public void sendInvalidEmail(String emailAddress) {

		logger.info("Starting sendInvalidEmail test...");
		Response response = UserEndPoints.sendEmailOTP(CommonMethods.sendInvalidEmail(emailAddress));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		String email = js.getString("errors.EmailAddress[0]");
		logger.debug("Response emailId: " + email);
		Assert.assertEquals(email, ErrorValidation.emailAddress);
		Assert.assertEquals(response.statusCode(), 400);
		logger.warn("Sending email with invalid email address attempted");
	}

	@Test(priority = 7, dataProvider = "useremailAndMobile", dataProviderClass = DataProviderClass.class)
	public void forgot_Pass(String mobileNumber, String emailAddres) {
		logger.info("Starting forgot_Pass test...");
		Response response = UserEndPoints.forgotPass(CommonMethods.forgot_Pass(mobileNumber, emailAddres));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Password reset request sent successfully");
	}

	@Test(priority = 8, dataProvider = "InvaliduseremailAndMobile", dataProviderClass = DataProviderClass.class)
	public void forgot_PassInvalidData(String mobileNumber, String emailAddres) {
		logger.info("Starting forgot_PassInvalidData test...");
		Response response = UserEndPoints.forgotPass(CommonMethods.forgot_Pass(mobileNumber, emailAddres));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		String Mnum = js.getString("errors.MobileNumber[0]");
		String email = js.getString("errors.EmailAddress[0]");
		Assert.assertEquals(Mnum, ErrorValidation.mobileNumber);
		Assert.assertEquals(email, ErrorValidation.emailAddress);
		Assert.assertEquals(response.statusCode(), 400);
		logger.warn("Password reset request attempted with invalid data");
	}

	@Test(priority = 9, description = "Customer should get Category")
	public void customer_GetLookupCategoryTest(ITestContext context) {
		logger.info("Getting category");
		Response response = UserEndPoints.user_getLookupCategoryEP();
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath js = CommonMethods.jsonToString(response);
		System.out.println("------------------------------");
		System.out.println(js.getString("[0].id"));
		System.out.println("------------------------------");
		int categotyId = js.getInt("[0].id");
		context.setAttribute("categoryID", categotyId);
		Assert.assertEquals(js.getString("[0].id"), "1");
		logger.info("Category fetched successfully");
	}

	@Test(priority = 10, description = "Customer should get Country")
	public void customer_GetLookupCountryTest() {
		logger.info("Getting Country");
		Response response = UserEndPoints.user_getLookupCountryEP();
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath js = CommonMethods.jsonToString(response);
		System.out.println("------------------------------");
		System.out.println(js.getString("[0].name"));
		System.out.println("------------------------------");
		Assert.assertEquals(js.getString("[0].name"), "India");
		logger.info("Country fetched successfully");
	}

	@Test(priority = 11, description = "Customer should get State")
	public void customer_GetLookupStateTest() {
		logger.info("Getting State");
		Response response = UserEndPoints.user_getLookupStateEP();
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath js = CommonMethods.jsonToString(response);
		System.out.println("------------------------------");
		System.out.println(js.getString("[0].name"));
		System.out.println("------------------------------");
		Assert.assertEquals(js.getString("[0].name"), "Maharashtra");
		logger.info("State fetched successfully");
	}

	@Test(priority = 12, description = "Customer should get City")
	public void customer_GetLookupCityTest() {
		logger.info("Getting City");
		Response response = UserEndPoints.user_getLookupCityEP();
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath js = CommonMethods.jsonToString(response);
		System.out.println("------------------------------");
		System.out.println(js.getString("[0].name"));
		System.out.println("------------------------------");
		Assert.assertEquals(js.getString("[0].name"), "Pune");
		logger.info("City fetched successfully");
	}

	@Test(priority = 13, description = "Customer should get PostCode")
	public void customer_GetLookupPostCodeTest() {
		logger.info("Getting PostCode");
		Response response = UserEndPoints.user_getLookupPostCodeEP();
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath js = CommonMethods.jsonToString(response);
		System.out.println("------------------------------");
		System.out.println(js.getString("[0].name"));
		System.out.println("------------------------------");
		Assert.assertEquals(js.getString("[0].name"), "411002");
		logger.info("PostCode fetched successfully");
	}
}