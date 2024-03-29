package HomeLyf.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Payload.Address;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.VendorDetail;
import io.restassured.response.Response;

public class User {

	SignUP_Payload signup;
	VendorDetail vendorDetail;
	Address address;
	List<Integer> serviceCategories;
	UserLogin_Payload userlogin;
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

	@Test(priority = 1)
	public void userSignUp() {

		signup.setEmailAddress("Onkar@123");
		signup.setName("onkarClassic");
		signup.setMobileNumber("1234567890");
		signup.setType("C");
		signup.setEmailAddress("cojeto6952@otemdi.com");
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
		signup.setPassword("Onkar@123");

		Response response = UserEndPoints.signUP(signup);
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
	}
	@Test
	public void userLogin() {
		userlogin.setEmailAddress("galetoy998@shaflyn.com");
		userlogin.setMobileNumber(7815124556L);
		userlogin.setType("v");
		userlogin.setPassword("Kalyani@12");
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
	
	

}
