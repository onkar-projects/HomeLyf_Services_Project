package HomeLyf.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Payload.Address;
import HomeLyf.Payload.ForgotPassword_Payload;
import HomeLyf.Payload.RestPass_Payload;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.VendorDetail;
import HomeLyf.Utilities.DataProviderClass;
import TestValidation.UserValidations;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class User {

	SignUP_Payload signup;
	VendorDetail vendorDetail;
	Address address;
	List<Integer> serviceCategories;
	UserLogin_Payload userlogin;
	RestPass_Payload resetpass;
	SendEmailOTP_Payload sendemail;
	ForgotPassword_Payload forgotPassword;
	String token;
	String resetPassOTP = "kDYezzG!";

	private static Logger log = LogManager.getLogger(User.class);

	@BeforeTest
	public void data() {

		signup = new SignUP_Payload();
		vendorDetail = new VendorDetail();
		address = new Address();
		serviceCategories = new ArrayList<>();
		signup = new SignUP_Payload();
		userlogin = new UserLogin_Payload();
		sendemail = new SendEmailOTP_Payload();
		forgotPassword = new ForgotPassword_Payload();
		resetpass = new RestPass_Payload();
		BasicConfigurator.configure();
	}

	@Test(priority = 1, dataProvider = "", dataProviderClass = DataProviderClass.class)
	public void userSignUp() {

		signup.setEmailAddress("repara8922@mnsaf.com");
		signup.setName("Mahi");
		signup.setMobileNumber("2345677865");
		signup.setType("a");
		signup.setEmailAddress("repara8922@mnsaf.com");
		vendorDetail.setId(0);
		vendorDetail.setAadharNumber(123456789012L);
		address.setCityID(1);
		address.setLine1("pune");
		address.setLine2("pune");
		address.setLine3("pune");
		address.setType("swiggy");
		address.setName("omNIvas");
		address.setPostcodeId(1);
		address.setLocation("pune");
		vendorDetail.setAddress(address);
		vendorDetail.setExperience("3");
		serviceCategories.add(1);
		vendorDetail.setServiceCategories(serviceCategories);
		signup.setVendorsDetail(vendorDetail);
		signup.setPassword("Mahi@123456");

		Response response = UserEndPoints.signUP(signup);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}

	@Test(description = "User Login as vender, admin and cut", priority = 2, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin(String mobileNumber, String type, String emailAddress, String password, String location) {

		userlogin.setEmailAddress(emailAddress);
		userlogin.setMobileNumber(Long.parseLong(mobileNumber));
		userlogin.setPassword(password);
		userlogin.setType(type);
		userlogin.setLocation(location);

		Response response = UserEndPoints.userLogin(userlogin);
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);

		token = js.getString("token");
		System.out.println("Generated Token Id: " + token);
		Assert.assertEquals(response.statusCode(), 200);
	}

	@Test(priority = 3, dataProvider = "emailOTP", dataProviderClass = DataProviderClass.class)
	public void sendEmailOTP(String emailAddress) {

		sendemail.setEmailAddress(emailAddress);
		log.info("Sending OTP to Mail "+emailAddress);
		Response response = UserEndPoints.sendEmailOTP(sendemail);
		int status = response.getStatusCode();
		Assert.assertEquals(status, 200);

		log.info("OTP send successfully ");
		log.error("OTP send successfully ");
		log.trace("OTP send successfully ");
		log.debug("OTP send successfully ");
		log.warn("OTP send successfully ");
	}

	@Test(priority = 4, dataProvider = "useremailAndMobile", dataProviderClass = DataProviderClass.class)
	public void forgot_Pass(String mobileNumber, String emailAddres) {

		forgotPassword.setMobileNumber(Long.parseLong(mobileNumber));
		forgotPassword.setEmailAddress(emailAddres);

		Response response = UserEndPoints.forgotPass(forgotPassword);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}

	@Test(priority = 5, dataProvider = "resetPassword", dataProviderClass = DataProviderClass.class)
	public void resetPass(String mobileNubmer, String email, String password, String confirmpass) {

		resetpass.setMobileNumber(Long.parseLong(mobileNubmer));
		resetpass.setEmailAddress(email);
		resetpass.setOneTimePass(resetPassOTP);
		resetpass.setPassword(password);
		resetpass.setConfirmpass(confirmpass);

		Response response = UserEndPoints.resetPass(resetpass);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}

}
