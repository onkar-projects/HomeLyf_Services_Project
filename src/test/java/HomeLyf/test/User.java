package HomeLyf.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Payload.Address;

import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.VendorDetail;
import io.restassured.response.Response;

public class User {

	SignUP_Payload signup;
	VendorDetail vendorDetail;
	Address address;
	List<Integer> serviceCategories;

	@BeforeTest
	public void data() {
		signup = new SignUP_Payload();
		vendorDetail = new VendorDetail();
		address = new Address();
		serviceCategories = new ArrayList<>();
		signup = new SignUP_Payload();
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
		org.testng.Assert.assertEquals(response.statusCode(), 200);
	}

}
