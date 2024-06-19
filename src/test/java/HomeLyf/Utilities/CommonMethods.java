package HomeLyf.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.testng.ITestContext;
import HomeLyf.Payload.Address;
import HomeLyf.Payload.BookingServices;
import HomeLyf.Payload.Calculator_Payload;
import HomeLyf.Payload.CreateCustomerBookingPayload;
import HomeLyf.Payload.CustomerPaymentStatus_payload;
import HomeLyf.Payload.DisableTimeslot_Payload;
import HomeLyf.Payload.ForgotPassword_Payload;
import HomeLyf.Payload.Reschedule_Payload;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.StartAndComplete_Booking_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.VendorDetail;
import HomeLyf.test.LookUp;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CommonMethods {
	public static SignUP_Payload signup;
	public static VendorDetail vendorDetail;
	public static Address address;
	public static List<Integer> scategorie;
	public static List<Integer> spostcode;
	public static UserLogin_Payload userlogin;
	public static SendEmailOTP_Payload sendemail;
	public static ForgotPassword_Payload forgotPassword;
	public static List<Integer> bookingservicelist;
	public static BookingServices bookingServices2;
	public static BookingServices bookingServices;
	public static Calculator_Payload cal;
	public static CreateCustomerBookingPayload custBooking;
	public static DisableTimeslot_Payload disabletimeslot;
	public static StartAndComplete_Booking_Payload startCompleteBooking;
	public static Reschedule_Payload reschedule;
	public static CustomerPaymentStatus_payload paymentstat;

	public static JsonPath jsonToString(Response response) {
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		return js;
	}

	public static SignUP_Payload signUpData(String name, String mobileNumber, String type, String emailAddress,
			String password, String Id, String scategories, String spostcodes, String addharnum, String exp,
			String addressname, String addresstype, String line1, String line2, String line3, String location,
			String postid, String cityid) {
		address = new Address();
		address.setName(addressname);
		address.setType(addresstype);
		address.setLine1(line1);
		address.setLine2(line2);
		address.setLine3(line3);
		address.setLocation(location);
		address.setPostcodeId(Integer.parseInt(postid));
		address.setCityID(Integer.parseInt(cityid));

		vendorDetail = new VendorDetail();
		vendorDetail.setId(Integer.parseInt(Id));
		scategorie = new ArrayList<Integer>();
		scategorie.add(Integer.parseInt(scategories));
		spostcode = new ArrayList<Integer>();
		spostcode.add(Integer.parseInt(spostcodes));
		vendorDetail.setServiceCategories(scategorie);
		vendorDetail.setServicePostCodes(spostcode);
		vendorDetail.setAadharNumber(Long.parseLong(addharnum));
		vendorDetail.setExperience(exp);
		vendorDetail.setAddress(address);

		signup = new SignUP_Payload();
		signup.setName(name);
		signup.setMobileNumber(Long.parseLong(mobileNumber));
		signup.setType(type);
		signup.setEmailAddress(emailAddress);
		signup.setPassword(password);
		signup.setVendorsDetail(vendorDetail);
		return signup;
	}

	public static SignUP_Payload invaliduserSignUp(String name, String mobileNumber, String type, String emailAddress,
			String password, String Id, String scategories, String spostcodes, String addharnum, String exp,
			String addressname, String addresstype, String line1, String line2, String line3, String location,
			String postid, String cityid) {
		signup = new SignUP_Payload();
		signup.setMobileNumber(Long.parseLong(mobileNumber));
		signup.setEmailAddress(emailAddress);
		signup.setPassword(password);
		return signup;
	}

	public static UserLogin_Payload userLogin(String mobileNumber, String type, String emailAddress, String password,
			String location) {
		userlogin = new UserLogin_Payload();

		userlogin.setEmailAddress(emailAddress);
		userlogin.setMobileNumber(Long.parseLong(mobileNumber));
		userlogin.setPassword(password);
		userlogin.setType(type);
		userlogin.setLocation(location);
		return userlogin;
	}

	public UserLogin_Payload userLogin_With_Invalid_Data(String mobileNumber, String type, String emailAddress,
			String password, String location) {
		userlogin = new UserLogin_Payload();

		userlogin.setEmailAddress(emailAddress);
		userlogin.setMobileNumber(Long.parseLong(mobileNumber));
		userlogin.setPassword(password);
		userlogin.setType(type);
		userlogin.setLocation(location);
		return userlogin;
	}

	public static SendEmailOTP_Payload sendEmailOTP(String emailAddress) {
		sendemail = new SendEmailOTP_Payload();
		sendemail.setEmailAddress(emailAddress);
		return sendemail;
	}

	public static SendEmailOTP_Payload sendInvalidEmail(String emailAddress) {
		sendemail = new SendEmailOTP_Payload();
		sendemail.setEmailAddress(emailAddress);
		return sendemail;
	}

	public static ForgotPassword_Payload forgot_Pass(String mobileNumber, String emailAddres) {
		forgotPassword = new ForgotPassword_Payload();
		forgotPassword.setMobileNumber(Long.parseLong(mobileNumber));
		forgotPassword.setEmailAddress(emailAddres);
		return forgotPassword;
	}

	public ForgotPassword_Payload forgot_PassInvalidData(String mobileNumber, String emailAddres) {
		forgotPassword = new ForgotPassword_Payload();
		forgotPassword.setMobileNumber(Long.parseLong(mobileNumber));
		forgotPassword.setEmailAddress(emailAddres);
		return forgotPassword;
	}

	public static Address address_details(String name, String type, String lineOne, String lineTwo, String lineThree,
			String location, ITestContext context) {
		Address address = new Address();

		LookUp.getPostCode(context);
		LookUp.getCity(context);

		address.setName(name);
		address.setType(type);
		address.setLine1(lineOne);
		address.setLine2(lineTwo);
		address.setLine3(lineThree);
		address.setLocation(location);
		address.setPostcodeId((int) context.getAttribute("postCodeId"));
		address.setCityID((int) context.getAttribute("cityId"));
		return address;
	}

	public static CreateCustomerBookingPayload createBooking(ITestContext context) {
		custBooking = new CreateCustomerBookingPayload();
		bookingServices = new BookingServices();
		bookingServices.setQuantity(1);

		bookingServices.setServiceID((int) context.getAttribute("serviceId"));
		custBooking.setBookingServices(Collections.singletonList(bookingServices));
		custBooking.setScheduledOn((String) context.getAttribute("StartTime"));
		custBooking.setAddressID((int) context.getAttribute("addressId"));
		return custBooking;
	}
	
	public static List<Calculator_Payload> calculateData(ITestContext context) {
		cal = new Calculator_Payload();
		cal.setQuantity(1);
		cal.setServiceID((int) context.getAttribute("serviceid"));
		List<Calculator_Payload> list = new ArrayList<Calculator_Payload>();
		list.add(cal);
		return list;
	}

	public static CustomerPaymentStatus_payload updatePaymentStatusData(ITestContext context) {
		paymentstat = new CustomerPaymentStatus_payload();
		String[] paymentMode = { "cash", "upi", "card", "other" };
		String[] paymentStatus = { "pending", "inprogress", "delayed", "cancelled", "completed", "refundingprogress",
				"refunded" };

		paymentstat.setBookingID((int) context.getAttribute("bookingId"));
		paymentstat.setPaymentStatus(paymentStatus[1]);
		paymentstat.setPaymentMode(paymentMode[3]);
		return paymentstat;
	}

	public static StartAndComplete_Booking_Payload sendBookingIdAndOtp(ITestContext context) {
		startCompleteBooking = new StartAndComplete_Booking_Payload();
		startCompleteBooking.setBookingId((int) context.getAttribute("customerBookingId"));
		startCompleteBooking.setOtp((int) context.getAttribute("c_startOTP"));
		return startCompleteBooking;
	}
	
	public static DisableTimeslot_Payload sendTimeslot(ITestContext context,String sTime, String eTime) {
		disabletimeslot = new DisableTimeslot_Payload();
		disabletimeslot.setId(0);
		disabletimeslot.setStartTime(sTime);
		disabletimeslot.setEndTime(eTime);
		return disabletimeslot;
	}

	public static DisableTimeslot_Payload sendTimeslot(ITestContext context) {
		disabletimeslot = new DisableTimeslot_Payload();
		disabletimeslot.setId(0);
		disabletimeslot.setStartTime((String) context.getAttribute("STime"));
		disabletimeslot.setEndTime((String) context.getAttribute("ETime"));
		return disabletimeslot;
	}
	
	public static UserLogin_Payload VendorLoginformultiplescenario() {
		userlogin = new UserLogin_Payload();
		userlogin.setEmailAddress("nevixo9520@ociun.com");
		userlogin.setMobileNumber(Long.parseLong("4312345433"));
		userlogin.setPassword("String@123");
		userlogin.setType("V");
		userlogin.setLocation("pune");
		return userlogin;
	}

	public static UserLogin_Payload CustomerLoginformultiplescenario() {
		userlogin = new UserLogin_Payload();
		userlogin.setEmailAddress("f9iupld30y@elatter.com");
		userlogin.setMobileNumber(Long.parseLong("6029860554"));
		userlogin.setPassword("HomeLyf@123");
		userlogin.setType("C");
		userlogin.setLocation("pune");
		return userlogin;
	}

	public static Reschedule_Payload CustomerReschedule_Payload(ITestContext context, int bookingId,
			String scheduleTime) {
		reschedule = new Reschedule_Payload();
		reschedule.setBookingId(bookingId);
		reschedule.setScheduledOn(scheduleTime);
		return reschedule;
	}

	public static UserLogin_Payload customer_Login() {
		userlogin = new UserLogin_Payload();
		userlogin.setEmailAddress("XGw7waDRKByh@tempsmtp.com");
		userlogin.setMobileNumber(Long.parseLong("9848592613"));
		userlogin.setPassword("HomeLyf@123");
		userlogin.setType("c");
		userlogin.setLocation("Pune");
		return userlogin;
	}

	public static UserLogin_Payload vendor_Login() {
		userlogin = new UserLogin_Payload();
		userlogin.setEmailAddress("dason.sava@floodouts.com");
		userlogin.setMobileNumber(Long.parseLong("9657400368"));
		userlogin.setPassword("Electv@233");
		userlogin.setType("v");
		userlogin.setLocation("Pune");
		return userlogin;
	}

	public static StartAndComplete_Booking_Payload sendBookingIdAndOtpforStartandEndService(ITestContext context) {
		startCompleteBooking = new StartAndComplete_Booking_Payload();
		startCompleteBooking.setBookingId((int) context.getAttribute("BookingId"));
		startCompleteBooking.setOtp((int) context.getAttribute("StartOTP"));
		return startCompleteBooking;
	}

	public static StartAndComplete_Booking_Payload sendBookingIdAndOtpforStartandEndServic(ITestContext context,
			int bookingId, int startOTP) {
		startCompleteBooking = new StartAndComplete_Booking_Payload();
		startCompleteBooking.setBookingId(bookingId);
		startCompleteBooking.setOtp(startOTP);
		return startCompleteBooking;
	}
}
