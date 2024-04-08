
package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Payload.ForgotPassword_Payload;
import HomeLyf.Payload.RestPass_Payload;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import Validations.ErrorValidation;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class User {

	String token;

	@Test(priority = 1, dataProvider = "Vendordata", dataProviderClass = DataProviderClass.class)
	public void userSignUp(String name, String mobileNumber, String type, String emailAddress, String password,
			String Id, String scategories, String spostcodes, String addharnum, String exp, String addressname,
			String addresstype, String line1, String line2, String line3, String location, String postid,
			String cityid) {

		Response response = UserEndPoints.signUP(
				CommonMethods.signUpData(name, mobileNumber, type, emailAddress, password, Id, scategories, spostcodes,
						addharnum, exp, addressname, addresstype, line1, line2, line3, location, postid, cityid));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}
	
	@Test(priority = 2, dataProvider = "invalidvendordata", dataProviderClass = DataProviderClass.class)

	public void invaliduserSignUp(String name, String mobileNumber, String type, String emailAddress, String password,
			String Id, String scategories, String spostcodes, String addharnum, String exp, String addressname,
			String addresstype, String line1, String line2, String line3, String location, String postid,
			String cityid) {
		Response response = UserEndPoints.signUP(
				CommonMethods.invaliduserSignUp(name, mobileNumber, type, emailAddress, password, Id, scategories, spostcodes,
					addharnum, exp, addressname, addresstype, line1, line2, line3, location, postid, cityid));
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
	}

	@Test(priority = 3, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin(String mobileNumber, String type, String emailAddress, String password, String location) {

		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);

		token = js.getString("token");
		System.out.println("Generated Token Id: " + token);

		Assert.assertEquals(response.statusCode(), 200);
	}

	@Test(priority = 4, dataProvider = "invalid_userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin_With_Invalid_Data(String mobileNumber, String type, String emailAddress, String password,
			String location) {
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

	}

	@Test(priority = 5, dataProvider = "emailOTP", dataProviderClass = DataProviderClass.class)
	public void sendEmailOTP(String emailAddress) {

		Response response = UserEndPoints.sendEmailOTP(CommonMethods.sendEmailOTP(emailAddress));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}

	@Test(priority = 6, dataProvider = "invalidemail", dataProviderClass = DataProviderClass.class)
	public void sendInvalidEmail(String emailAddress) {

		Response response = UserEndPoints.sendEmailOTP(CommonMethods.sendInvalidEmail(emailAddress));
		response.then().log().all();

		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		String email = js.getString("errors.EmailAddress[0]");

		Assert.assertEquals(email, ErrorValidation.emailAddress);
		Assert.assertEquals(response.statusCode(), 400);
	}

	@Test(priority = 7, dataProvider = "useremailAndMobile", dataProviderClass = DataProviderClass.class)
	public void forgot_Pass(String mobileNumber, String emailAddres) {

		Response response = UserEndPoints.forgotPass(CommonMethods.forgot_Pass(mobileNumber, emailAddres));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}

	@Test(priority = 8, dataProvider = "InvaliduseremailAndMobile", dataProviderClass = DataProviderClass.class)
	public void forgot_PassInvalidData(String mobileNumber, String emailAddres) {

		Response response = UserEndPoints.forgotPass(CommonMethods.forgot_Pass(mobileNumber, emailAddres));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		String Mnum = js.getString("errors.MobileNumber[0]");
		String email = js.getString("errors.EmailAddress[0]");

		Assert.assertEquals(Mnum, ErrorValidation.mobileNumber);
		Assert.assertEquals(email, ErrorValidation.emailAddress);

		Assert.assertEquals(response.statusCode(), 400);

	}

}
