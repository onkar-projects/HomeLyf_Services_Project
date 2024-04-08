
package HomeLyf.test;

import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Payload.Address;
import HomeLyf.Payload.ForgotPassword_Payload;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.VendorDetail;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderclass;
import Validations.ErrorValidation;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class User {
	
//	SignUP_Payload signup;
//	VendorDetail vendorDetail;
//	Address address;
//	List<Integer> serviceCategories;
//	UserLogin_Payload userlogin;
//	SendEmailOTP_Payload sendemail;
//	ForgotPassword_Payload forgotPassword;
//	String token;
//
//	@BeforeTest
//	public void data() {
//
//		signup = new SignUP_Payload();
//		vendorDetail = new VendorDetail();
//		address = new Address();
//		serviceCategories = new ArrayList<>();
//		signup = new SignUP_Payload();
//		userlogin = new UserLogin_Payload();
//		sendemail = new SendEmailOTP_Payload();
//		forgotPassword = new ForgotPassword_Payload();
//	}
//	
	@Test
	public void user() {
		System.out.println("onkar");
	}

//	@Test(priority = 1, dataProvider = "Vendordata", dataProviderClass = DataProviderclass.class)
//	public void userSignUp(String name, String mobileNumber, String type, String emailAddress, String password,
//			String Id, String scategories, String spostcodes, String addharnum, String exp, String addressname,
//			String addresstype, String line1, String line2, String line3, String location, String postid,
//			String cityid) {
//
//		Response response = UserEndPoints.signUP(
//				CommonMethods.signUpData(name, mobileNumber, type, emailAddress, password, Id, scategories, spostcodes,
//						addharnum, exp, addressname, addresstype, line1, line2, line3, location, postid, cityid));
//		response.then().log().all();
//		Assert.assertEquals(response.statusCode(), 200);
//	}


}
