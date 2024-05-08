package HomeLyf.EndPoints;

public class Routes {

//	USER_API
	public static String base_url = "https://homelyf.co.in";
	
	public static String account_login = base_url + "/UserAPI/api/Account/Login";
	public static String account_signUp = base_url + "/UserAPI/api/Account/SignUp";
	public static String  acccount_emailOTP= base_url + "/UserAPI/api/Account/SendEmailOTP";
	public static String  account_verifyEmail= base_url + "/UserAPI/api/Account/VerifyEmail";
	public static String  account_forgotPass= base_url + "/UserAPI/api/Account/ForgotPassword";
	public static String  account_resetPass= base_url + "/UserAPI/api/Account/ResetPassword";
	public static String  account_smsOTP= base_url + "/UserAPI/api/Account/SendSMSOTP";
	public static String account_verifySMS= base_url + "/UserAPI/api/Account/VerifySMS";
	public static String account_googleSignIn= base_url + "/UserAPI/api/Account/GoogleSignIn";
	public static String account_appleSignIn= base_url + "/UserAPI/api/Account/AppleSignIn";

	//Lookup
		public static String getLookupCategory = base_url + "/UserAPI/api/Lookup/Category";
	
	//-------------------------------------------Customer Routes----------------------------------------------

	
	
	public static String customer_service = base_url+"/CustomerAPI/api/Service/{subCategoryId}";
	public static String customer_Address = base_url +"/CustomerAPI/api/User/address";
	//SubCategory
		public static String getsubCategoryId = base_url + "/CustomerAPI/api/SubCategory/{categoryId}";
	
	
	//-------------------------------------------Vendor Routes----------------------------------------------
	
	
	
	public static String vendor_getbooking = base_url+"/VendorAPI/api/Booking";
	public static String vendor_GET_TimeSlot=base_url+"/VendorAPI/api/Timeslot";
	public static String vendor_POST_TimeSlot_Disable=base_url+"/VendorAPI/api/Timeslot/Disable";
	public static String vendor_POST_TimeSlot_Enable=base_url+"/VendorAPI/api/Timeslot/Enable/{id}";
	public static String cancelPartnerBooking = base_url + "/VendorAPI/api/Booking/Cancel/{bookingId}";
}

