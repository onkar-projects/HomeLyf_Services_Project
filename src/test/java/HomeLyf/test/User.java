
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

	
	
	@Test(priority = 2, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin(String mobileNumber, String type, String emailAddress, String password, String location) {

		Response response = UserEndPoints.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);

		token = js.getString("token");
		System.out.println("Generated Token Id: " + token);

		Assert.assertEquals(response.statusCode(), 200);	}

	
	
	@Test(priority = 3, dataProvider = "emailOTP", dataProviderClass = DataProviderClass.class)
	public void sendEmailOTP(String emailAddress) {

		Response response = UserEndPoints.sendEmailOTP(CommonMethods.sendEmailOTP(emailAddress));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
}

	
	
	@Test(priority = 4, dataProvider = "useremailAndMobile", dataProviderClass = DataProviderClass.class)
	public void forgot_Pass(String mobileNumber, String emailAddres) {

		Response response = UserEndPoints.forgotPass(CommonMethods.forgot_Pass(mobileNumber,emailAddres));
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}
}
