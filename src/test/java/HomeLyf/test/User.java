package HomeLyf.test;


import java.util.ArrayList;
import java.util.List;


import org.testng.Assert;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

	SignUP_Payload signup;
	VendorDetail vendorDetail;
	Address address;
	List<Integer> serviceCategories;
	UserLogin_Payload userlogin;
	RestPass_Payload resetpass;
	SendEmailOTP_Payload sendemail;
	ForgotPassword_Payload forgotPassword;
	String token;
	
	public static final Logger logger=LogManager.getLogger("HomeLyf_Services_Project");

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
		
		  
		//obtain logger
		//logger = LogManager.getLogger(User.class);
	}

	@Test(priority = 1)

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

	@Test(priority = 2, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin(String mobileNumber, String type, String emailAddress, String password, String location) {
		
		userlogin.setEmailAddress(emailAddress);
		userlogin.setMobileNumber(Long.parseLong(mobileNumber));
		userlogin.setPassword(password);
		userlogin.setType(type);
		userlogin.setLocation(location);
        Response response = UserEndPoints.userLogin(userlogin);
		response.then().log().all();
		String responsebody = response.asPrettyString();
		JsonPath jsonpath = new JsonPath(responsebody);
        token = jsonpath.getString("token");
		System.out.println("Generated TokenId: " + token);
         Assert.assertEquals(response.statusCode(), 200);
	
	}

	@Test(priority = 3, dataProvider = "emailOTP", dataProviderClass = DataProviderClass.class)
	public void sendEmailOTP(String emailAddress) {
		
		
		
		sendemail.setEmailAddress(emailAddress);

		Response response = UserEndPoints.sendEmailOTP(sendemail);
//		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("otp send successfully");
		logger.error("otp not send successfully");
		
	}

	@Test(priority = 4, dataProvider = "useremailAndMobile", dataProviderClass = DataProviderClass.class)
	public void forgot_Pass(String mobileNumber, String emailAddres) {

		
		forgotPassword.setMobileNumber(Long.parseLong(mobileNumber));
		forgotPassword.setEmailAddress(emailAddres);
		Response response = UserEndPoints.forgotPass(forgotPassword);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		
		
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
	}
	
	/*@AfterTest
	public void afterTest() throws Exception  {
		EmailUtils emailUtils = new EmailUtils();
		Properties pro = new Properties();
		pro.load(new FileInputStream("C:/Users/DELL/Documents/HomeLyf_Services/HomeLyf_Services_Project/Config/config-Email.properties"));
		List<String> attachments = new ArrayList<>();
		attachments.add("C:/Users/DELL/Documents/HomeLyf_Services/HomeLyf_Services_Project/test-output/emailable-report.html");
		emailUtils.sendUsingGmail(pro, "Test Execution Results", "Hi Team, Execution is successful", attachments);
		
		
	}*/

}
