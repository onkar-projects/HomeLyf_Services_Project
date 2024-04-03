package HomeLyf.test;

import static io.restassured.RestAssured.given;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import HomeLyf.EndPoints.Routes;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Payload.Address;
import HomeLyf.Payload.RestPass_Payload;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.VendorDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class User {

	
	SignUP_Payload signup;
	VendorDetail vendorDetail;
	Address address;
	List<Integer> serviceCategories;
	UserLogin_Payload userlogin;
	RestPass_Payload resetpass;
	SendEmailOTP_Payload sendemail;
	
	@BeforeTest
	public void data() {
	    signup = new SignUP_Payload();
		vendorDetail = new VendorDetail();
		address = new Address();
		serviceCategories = new ArrayList<>();
		signup = new SignUP_Payload();
		userlogin = new UserLogin_Payload();
		sendemail = new SendEmailOTP_Payload();
	}
	
	@Test(priority = 1, enabled = false)

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

	  /*Response response = UserEndPoints.signUP(signup);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);*/
	}

//	@Test(priority = 1)
//	public void userSignUpforVendor() throws FileNotFoundException {
//
//        Response response = UserEndPoints.signUP();
//		response.then().log().all();
//		Assert.assertEquals(response.statusCode(), 200);
//	}
	@Test (priority = 2)
	public void userLogin() {
		userlogin.setEmailAddress("repara8922@mnsaf.com");
		userlogin.setMobileNumber(2345677865L);
		userlogin.setType("a");
		userlogin.setPassword("Mahi@123456");
		userlogin.setLocation("Pune");
		
		Response response = UserEndPoints.userLogin(userlogin);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		
	}
	@Test
	public void sendEmailOTP() {
		sendemail.setEmailAddress("yagigi8204@otemdi.com");
		sendemail.setMobileNumber(9860562353L);		
		Response response  = UserEndPoints.sendEmailOTP(sendemail);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}
	
	@Test
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
	
	
	
	

}
