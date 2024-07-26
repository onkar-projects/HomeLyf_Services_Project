
package HomeLyf.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import HomeLyf.EndPoints.CustomerEndPoints;
import HomeLyf.EndPoints.UserEndPoints;
import HomeLyf.EndPoints.VendorEndPoints;
import HomeLyf.Utilities.CommonMethods;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class LookUp {
	private static Logger log = LogManager.getLogger(LookUp.class);

	public static void getMyProfile(ITestContext context) {
		log.info("Getting MyProfile");
		Response response = CustomerEndPoints.customer_GetMyProfileEP(context);
		JsonPath js = CommonMethods.jsonToString(response);
		int addressId = js.getInt("addresses[0].id");
		int addressId2 = js.getInt("addresses[1].id");
		context.setAttribute("addressId", addressId2);
		//context.setAttribute("addressId", addressId);
		log.info("Customer Profile fetched successfully");
	}

	public static void getCategory(ITestContext context) {
		log.info("Getting category");
		Response response = UserEndPoints.user_getLookupCategoryEP();
		JsonPath js = CommonMethods.jsonToString(response);
		String name = js.getString("[0].name");
		int CategoryId = js.getInt("[0].id");
		context.setAttribute("name", name);
		String name3 = js.getString("[3].name");
		int CategoryId3 = js.getInt("[3].id");
		context.setAttribute("name4", name3);
		int CategoryId4 = js.getInt("[4].id");
		context.setAttribute("categoryId4", CategoryId4);
		context.setAttribute("categoryId3", CategoryId3);
		context.setAttribute("categoryId1", CategoryId);
		String name5  = js.getString("[5].name");
		context.setAttribute("name5", name5);
		Assert.assertEquals(response.getStatusCode(), 200);
		log.info("Category fetched successfully");
	}

	public static void getCountry(ITestContext context) {
		log.info("Getting Country");
		Response response = UserEndPoints.user_getLookupCountryEP();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath js = CommonMethods.jsonToString(response);
		String name = js.getString("[0].name");
		context.setAttribute("name", name);
		log.info("Country is : "+name);
	}

	public static void getState(ITestContext context) {
		log.info("Getting State");
		Response response = UserEndPoints.user_getLookupStateEP();
		Assert.assertEquals(response.getStatusCode(), 200);
		JsonPath js = CommonMethods.jsonToString(response);
		String name = js.getString("[0].name");
		context.setAttribute("name", name);
		Assert.assertEquals(js.getString("[0].name"), "Maharashtra");
		log.info("State is : "+name);
	}

	public static void getCity(ITestContext context) {
		log.info("Getting City");
		Response response = UserEndPoints.user_getLookupCityEP();
		JsonPath js = CommonMethods.jsonToString(response);
		String name = js.getString("[0].name");
		context.setAttribute("name", name);
		int cityId = js.getInt("[0].id");
		context.setAttribute("cityId", cityId);
		Assert.assertEquals(response.getStatusCode(), 200);
		log.info("City is : "+name);
	}

	public static void getPostCode(ITestContext context) {
		log.info("Getting PostCode from LookUp");
		Response response = UserEndPoints.user_getLookupPostCodeEP();
		JsonPath js = CommonMethods.jsonToString(response);
		String postCodeName = js.getString("[0].name");
		int postCodeId = js.getInt("[0].id");
		String postCode1 = js.getString("[1].name");
		String postCode2 = js.getString("[2].name");
		int postCodeId2 = js.getInt("[2].id");
		context.setAttribute("postCode2", postCode2);
		context.setAttribute("postCodeId2", postCodeId2);
		context.setAttribute("postCode1", postCode1);
		context.setAttribute("postCodeName", postCodeName);
		context.setAttribute("postCodeId", postCodeId);
		Assert.assertEquals(response.getStatusCode(), 200);
		log.info("PostCodeName and PostCodeId fetched succesfully");
	}
    //--------------------CustomerLookup-------------------------------------
	public static void getPaymentStatus(ITestContext context) {
		Response response = CustomerEndPoints.customer_PaymentStatusEP(context);
		JsonPath js = CommonMethods.jsonToString(response);
		String paymentStatus = js.getString("[0].name");
		context.setAttribute("paymentStatus", paymentStatus);
		Assert.assertEquals(response.statusCode(), 200);
		log.info("Payment Status is : "+paymentStatus);
	}

	public static void customer_GetPaymentModeTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_PaymentModeEP(context);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		String paymentMode = js.getString("[1].name");
		Assert.assertEquals(response.statusCode(), 200);
		log.info("Payment Mode is : "+paymentMode);
	}

	public static void customer_GetBookingStatusTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_BookingStatusEP(context);
		Assert.assertEquals(response.statusCode(), 200);
		log.info("Booking Status are fetched successfully");
	}

	public static void customer_GetBookingIdTest(ITestContext context) {
		Response response = CustomerEndPoints.customer_GetBookingByIdEP(context, (int) context.getAttribute("bookingId"));
		//response.then().log().all();
		JsonPath js =  CommonMethods.jsonToString(response);
		int bookingId = js.get("id");
		context.setAttribute("bookingId", bookingId);
		String status = js.getString("status");
		int startOTP = js.get("startOTP");
		context.setAttribute("startOTP", startOTP);		
		int endOTP = js.getInt("endOTP");
		context.setAttribute("endOTP", endOTP);
		Assert.assertEquals(response.statusCode(), 200);
		log.info("Booking details with BookingID : "+bookingId+ " ,StartOTP : "+startOTP+ " & EndOTP : "+endOTP);

	}
     //------------------------------------------Vendor--------------------------------------------------
	public static void vendorgetMybooking(ITestContext context) {
		log.info("Starting vendor get booking...");
		int vendorBookingId = 0;
		Response response = VendorEndPoints.vendor_MybookingEP(context, 1, 100);
		response.then().log().all();
		JsonPath js = CommonMethods.jsonToString(response);
		for (int i = 0; i <= 10; i++) {
			String status = js.getString("[" + i + "].status");
			if (status.equalsIgnoreCase("ExpertAssigned")) {
				vendorBookingId = js.getInt("[" + i + "].id");
				break;
			}
		}
		context.setAttribute("BookingId", vendorBookingId);
		Assert.assertEquals(response.statusCode(), 200);
		log.info("vendor_get_booking is shown successfully: "+vendorBookingId);
	}

   //--------------------------CreateBookingByCustomer--------------------------
	public static void TC_createBooking(ITestContext context) {
		log.info("Getting categoryId");
		LookUp.getPostCode(context);
		//LookUp.getCategory(context);
		Response response1 = CustomerEndPoints.customer_GetCategoryEP(context,
				(String) context.getAttribute("postCode1"), "");
		response1.then().log().all();
		JsonPath js1 = CommonMethods.jsonToString(response1);
		int categoryId = js1.getInt("[5].id");
		String categoryName = js1.getString("[5].name");
		context.setAttribute("categoryId", categoryId);
		Assert.assertEquals(js1.getString("[5].name"), "Electricals");
		Assert.assertEquals(response1.statusCode(), 200);
		log.info("CategoryId fetched successfully :" + categoryId + " of category " + categoryName);

		log.info("Getting subCategoryId");
		Response response2 = CustomerEndPoints.customer_SubCategoryEP(context,
				(int) context.getAttribute("categoryId"));
		response2.then().log().all();
		JsonPath js2 = CommonMethods.jsonToString(response2);
		int subCategoryId = js2.getInt("[1].id");
		String subCategoryName = js2.getString("[1].name");
		context.setAttribute("subCategoryId", subCategoryId);
		Assert.assertEquals(response2.getStatusCode(), 200);
		log.info("SubCategoryId Fetched Successfully :" + subCategoryId + " of subCategory :" + subCategoryName);

		log.info("Starting customer_service");
		Response response3 = CustomerEndPoints.customer_service(context, (int) context.getAttribute("subCategoryId"));
		response3.then().log().all();
		JsonPath js3 = CommonMethods.jsonToString(response3);
		int serviceId = js3.getInt("[0].id");
		String serviceName = js3.getString("[0].name");
		context.setAttribute("serviceId", serviceId);
		Assert.assertEquals(response3.statusCode(), 200);
		log.info("customer_service subcategory is shown successfully " + serviceId + " of service " + serviceName);

		log.info("Getting Customer profile");
		Response response4 = CustomerEndPoints.customer_GetMyProfileEP(context);
		response4.then().log().all();
		JsonPath js4 = CommonMethods.jsonToString(response4);
		int addressId = js4.getInt("addresses[0].id");
		context.setAttribute("addressId", addressId);
		Assert.assertEquals(response4.statusCode(), 200);
		log.info("Customer profile shown successfully " + addressId);

		log.info("Getting Customer Timeslot for book service");
		Response response5 = CustomerEndPoints.customer_GetTimeSlot((int) context.getAttribute("addressId"),
				(int) context.getAttribute("categoryId"), context);
		response5.then().log().all();
		JsonPath js5 = CommonMethods.jsonToString(response5);
		String sTime = js5.getString("[3].startTime");
		context.setAttribute("StartTime", sTime);
		String eTime = js5.getString("[3].endTime");
		System.out.println("Start Time: " + sTime + "\n End Time: " + eTime);
		Assert.assertEquals(response5.statusCode(), 200);
		log.info("Available booking Timeslot with startTime " + sTime + " endTime " + eTime);
	}
}
