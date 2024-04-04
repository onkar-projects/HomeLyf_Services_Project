
package HomeLyf.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class User {

	public static Logger Logger;
	SignUP_Payload signup;
	VendorDetail vendorDetail;
	Address address;
	UserLogin_Payload userlogin;
	RestPass_Payload resetpass;
	SendEmailOTP_Payload sendemail;
	ForgotPassword_Payload forgotPassword;
	String token;
	List<Integer> scategorie;
	List<Integer> spostcode;

	@BeforeTest
	public void data() {

		signup = new SignUP_Payload();
		vendorDetail = new VendorDetail();
		address = new Address();
		signup = new SignUP_Payload();
		userlogin = new UserLogin_Payload();
		sendemail = new SendEmailOTP_Payload();
		forgotPassword = new ForgotPassword_Payload();
		Logger = LogManager.getLogger(User.class);
		Logger = LogManager.getLogger("HomeLyf_Services_Project");
		scategorie = new ArrayList<Integer>();
		spostcode = new ArrayList<Integer>();

	}

	@Test(priority = 1, dataProvider = "Vendordata", dataProviderClass = DataProviderClass.class)

	public void userSignUp(String name, String mobileNumber, String type, String emailAddress, String password, String Id,
			String scategories, String spostcodes, String addharnum, String exp, String addressname,
			String addresstype, String line1, String line2, String line3, String location, String postid, String cityid
			){

		signup.setName(name);
		signup.setMobileNumber(Long.parseLong(mobileNumber));
		signup.setType(type);
		signup.setEmailAddress(emailAddress);
		signup.setVendorsDetail(vendorDetail);
		
		vendorDetail.setId(Integer.parseInt(Id));
		scategorie.add(Integer.parseInt(scategories));
		spostcode.add(Integer.parseInt(spostcodes));
		vendorDetail.setServiceCategories(scategorie);
		vendorDetail.setServicePostCodes(spostcode);
		vendorDetail.setAadharNumber(Long.parseLong(addharnum));
		vendorDetail.setExperience(exp);
		vendorDetail.setAddress(address);

		address.setName(addressname);
		address.setType(addresstype);
		address.setLine1(line1);
		address.setLine2(line2);
		address.setLine3(line3);
		address.setLocation(location);
		address.setPostcodeId(Integer.parseInt(postid));
		address.setCityID(Integer.parseInt(cityid));
		signup.setPassword(password);
	
		Response response = UserEndPoints.signUP(signup);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		Logger.info("User Sign Up executed.");
	}

	@Test(priority = 2, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
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
		Logger.info("User Login executed.");
	}

	@Test(priority = 3, dataProvider = "emailOTP", dataProviderClass = DataProviderClass.class)
	public void sendEmailOTP(String emailAddress) {

		sendemail.setEmailAddress(emailAddress);

		Response response = UserEndPoints.sendEmailOTP(sendemail);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		Logger.info("User Send Email OTP executed.");
	}

	@Test(priority = 4, dataProvider = "useremailAndMobile", dataProviderClass = DataProviderClass.class)
	public void forgot_Pass(String mobileNumber, String emailAddres) {

		forgotPassword.setMobileNumber(Long.parseLong(mobileNumber));
		forgotPassword.setEmailAddress(emailAddres);
		Response response = UserEndPoints.forgotPass(forgotPassword);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		Logger.info("User Forgot Password executed.");
	}

	@Test(priority = 5, dataProvider = "", dataProviderClass = DataProviderClass.class)
	public void resetPass() {
		resetpass.setEmailAddress("dhondekalyani@gmail.com");
		resetpass.setMobileNumber(2345677865L);
		resetpass.setOneTimePass("wertyui");
		resetpass.setPassword("Kalyani@123");
		resetpass.setConfirmpass("Kalyani@123");
		
		Response response = UserEndPoints.resetPass(resetpass);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		Logger.info("User Reset Password executed.");
	}
}
