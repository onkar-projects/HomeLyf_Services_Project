package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.EndPoints.VendorEndPoints;
import HomeLyf.Payload.Vendor_Timeslotdisable;
import HomeLyf.Utilities.CommonMethods;
import HomeLyf.Utilities.DataProviderClass;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Vendor {
	private static Logger logger = LogManager.getLogger(User.class);
	String token;
	String sTime;
	String eTime;
	int disableid;
	int bookingId;

	@Test(priority = 1, dataProvider = "userlogin", dataProviderClass = DataProviderClass.class)
	public void userLogin(ITestContext context, String mobileNumber, String type, String emailAddress, String password,
			String location) {
		logger.info("Starting userLogin test...");
		Response response = UserEndPoints
				.userLogin(CommonMethods.userLogin(mobileNumber, type, emailAddress, password, location));
		response.then().log().all();
		String res  = response.asPrettyString();
		JsonPath js = new JsonPath(res);

		token = js.getString("token");
		System.out.println("Generated Token Id: " + token);
		context.setAttribute("Token", token);
		logger.debug("Generated Token Id: {}", token);

		Assert.assertEquals(response.statusCode(), 200);
		logger.info("User logged in successfully");
	}

	@Test(priority = 2, enabled = false)
	public void vendor_get_booking(ITestContext context) {
		logger.info("Starting vendor_get_booking...");
		Response response = VendorEndPoints.vendorgetbooking(context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		bookingId = js.get("[2].id");
		System.out.println("BookingID: "+bookingId);
		
		// print status line
		Assert.assertEquals(response.statusCode(), 200);
		response.then().statusCode(200).log().all();
		logger.info("vendor_get_booking is shown successfully.");

	}
	
	@Test(priority = 6, enabled = true)
	public void vendor_cancelBooking(ITestContext context) {
		Response response = VendorEndPoints.vendorCancelBooking(context,10172);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String status = js.get("status");
		System.out.println("Status: "+status);
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(status, "New");
		System.out.println("bookingId cancel successfully");
		
		
	}

	@Test(priority = 8, description = "Display Available Timeslots of Vendor ")
	public void Vendor_Timeslot() {
		logger.info("Started TimeSlot of Vendor  ");
		Response response = VendorEndPoints.vendor_Timeslot(token);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		sTime = js.get("availableTimeSlots[3].startTime");
		eTime = js.get("availableTimeSlots[3].endTime");
		System.out.println("Timeslots: " + sTime);
		System.out.println("Timeslot: " + eTime);

		logger.info("Vendor timeslots open successfully");

	}

	@Test(priority = 9, description = "Given time slot shoulb be disabled")
	public void vendorDisableTimeslot() {
		logger.info("DisableTime Slot.......");

		Vendor_Timeslotdisable disabletime = new Vendor_Timeslotdisable();
		disabletime.setStartTime(sTime);
		disabletime.setEndTime(eTime);
		Response response = VendorEndPoints.vendor_TimeslotDisable(token, disabletime);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		disableid = js.get("id");
		logger.info("TimeSlot Disable Sucessfully ......");
	}

	@Test(priority = 10, description = "Given time slot should be enbled")
	public void vendorenable_timeslot() {
		Response response = VendorEndPoints.vendor_Timeslotenable(token, disableid);
		response.then().log().all();
		logger.info("Given time slot enble sucessfully");
	}

}
