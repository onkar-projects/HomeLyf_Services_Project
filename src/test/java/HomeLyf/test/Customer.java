package HomeLyf.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import HomeLyf.EndPoints.CustomerEndPoints;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.Payload.Booking_Services;
import HomeLyf.Payload.CustomerBooking_Payload;
import HomeLyf.Payload.UpdatePaymentStatus_Payload;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Customer
{
	String bearertoken;
	String startTime;
	String name;
	UpdatePaymentStatus_Payload updatepaymentstatus;
	private static Logger logger = LogManager.getLogger(User.class);


	@Test(priority = 1, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin(String mobileNumber, String type, String emailAddress, String password, String location)
	{
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);

		bearertoken= js.getString("token");
//		System.out.println("Generated Token Id: " + token);
		logger.debug("Generated Token Id: {}", bearertoken);

		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}
	
	@Test(priority = 2, dataProvider = "Booking_Timeslots", dataProviderClass = DataProviderClass.class ,dependsOnMethods= {"userLogin"})
	public void Booking_Timeslots(String categoryId, String addressID) {
		logger.info("Starting Customer Timeslots test...");
		Response response = CustomerEndPoints.Timeslots(bearertoken,Integer.parseInt(categoryId), Integer.parseInt(addressID));
		response.then().log().all();
		String res = response.asPrettyString();
		//System.out.println("---------------------Pretty Responce-----------------------------------");
		//System.out.println(res);
		//System.out.println("---------------------Pretty Responce END-----------------------------------");
		JsonPath js = new JsonPath(res);
		startTime = js.get("[6].startTime");
		System.out.println("Customer timeslots: "+ startTime);
		
		logger.info("customer timeslots open successfully");
	}
	
	@Test(priority = 3, dataProvider = "customerBooking", dataProviderClass = DataProviderClass.class)
	public void Customer_Booking(String serviceID,String quantity,String addressId )
	{
		logger.info("Starting Customer Booking ...");
		Booking_Services bs=new Booking_Services(Integer.parseInt(serviceID), Integer.parseInt(quantity));
		List<Booking_Services> bsList = new ArrayList<>();
		bsList.add(bs);
		CustomerBooking_Payload cp=new CustomerBooking_Payload( bsList,startTime,Integer.parseInt(addressId));
		Response response = CustomerEndPoints.Booking(bearertoken,cp);	
				response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Customer Booking successfully");
	}
	@Test(priority=4)
	public String paymentstaus()
	{
		logger.info("Payment status......");
		Response response=CustomerEndPoints.paymentstatus(bearertoken);
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		name = js.get("[2].name");
		System.out.println("Customer payment status: "+ name);
		
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Payment status are ");
		return name;
	}
	@Test(priority=5)
	public String paymentMode()
	{
		logger.info("Payment mode.........");
		Response response=CustomerEndPoints.paymentMode(bearertoken);
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		name = js.get("[0].name");
		System.out.println("Customer payment mode: "+ name);
		
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Payment Modes are  :");
		return name;
	}
	@Test(priority=6)
	public void bookingstatus()
	{
		logger.info("Booking Status.........");
		Response response=CustomerEndPoints.BookingStatus(bearertoken);
		response.then().log().all();
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		name = js.get("[1].name");
		System.out.println("Customer Booking Status "+ name);
		
		Assert.assertEquals(response.statusCode(), 200);
		logger.info("Booking Status :  :");
		
	}

	@Test(priority=7)
	
		public void UpdatepaymentStatus( )
		{
			logger.info("Update payment Status......" );
			UpdatePaymentStatus_Payload updatepaymentstatus = new UpdatePaymentStatus_Payload();
			updatepaymentstatus.setBookingID(91);
			updatepaymentstatus.setPaymentStatus(paymentstaus());
			updatepaymentstatus.setPaymentMode(paymentMode());
			Response response=CustomerEndPoints.updatepaymentstatus(bearertoken,updatepaymentstatus);
			 response.then().log().all();
	}
	
		
	}

















