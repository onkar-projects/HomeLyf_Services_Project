package HomeLyf.EndPoints;

public class Routes {

//	USER_API
	public static String base_url = "https://homelyfservices.com";

	public static String account_login = base_url + "/UserAPI/api/Account/Login";
	public static String account_signUp = base_url + "/UserAPI/api/Account/SignUp";
	public static String acccount_emailOTP = base_url + "/UserAPI/api/Account/SendEmailOTP";
	public static String account_verifyEmail = base_url + "/UserAPI/api/Account/VerifyEmail";
	public static String account_forgotPass = base_url + "/UserAPI/api/Account/ForgotPassword";
	public static String account_resetPass = base_url + "/UserAPI/api/Account/ResetPassword";
	public static String account_smsOTP = base_url + "/UserAPI/api/Account/SendSMSOTP";
	public static String account_verifySMS = base_url + "/UserAPI/api/Account/VerifySMS";
	public static String account_googleSignIn = base_url + "/UserAPI/api/Account/GoogleSignIn";
	public static String account_appleSignIn = base_url + "/UserAPI/api/Account/AppleSignIn";

	// Lookup
	public static String getLookupCategoryURL = base_url + "/UserAPI/api/Lookup/Category";
	public static String getLookupCountryURL = base_url + "/UserAPI/api/Lookup/Country";
	public static String getLookupStateURL = base_url + "/UserAPI/api/Lookup/State";
	public static String getLookupCityURL = base_url + "/UserAPI/api/Lookup/City";
	public static String getLookupPostCodeURL = base_url + "/UserAPI/api/Lookup/PostCode";

	// -------------------------------------------Customer
	// Routes----------------------------------------------

	public static String customer_service = base_url + "/CustomerAPI/api/Service/{subCategoryId}";
	public static String customer_Address = base_url + "/CustomerAPI/api/User/address";
	public static String customer_GetTimeShot = base_url + "/CustomerAPI/api/Booking/Timeslots";
	public static String customer_GetBookingURL = base_url + "/CustomerAPI/api/Booking";
	public static String customer_CreateBookingURL = base_url + "/CustomerAPI/api/Booking";
	public static String customer_UpdatePaymentURL = base_url + "/CustomerAPI/api/Booking/UpdatePaymentStatus";
	public static String customer_Calculate = base_url + "/CustomerAPI/api/Booking/Calculate";
	public static String customer_CancelURL = base_url + "/CustomerAPI/api/Booking/Cancel/{bookingId}";
	public static String customer_Reschedule = base_url + "/CustomerAPI/api/Booking/Reschedule";
	public static String customer_GetBookingIdURL = base_url + "/CustomerAPI/api/Booking/{bookingId}";
	public static String customer_MyProfileURL = base_url + "/CustomerAPI/api/User/myprofile";

	// --------------------------------------lookups------------------------------------------------------//

	public static String customer_GetSubCategoryURL = base_url + "/CustomerAPI/api/SubCategory/{categoryId}";
	public static String getPaymentMode = base_url + "/CustomerAPI/api/Lookup/PaymentMode";
	public static String getPaymentStatus = base_url + "/CustomerAPI/api/Lookup/PaymentStatus";
	public static String getBookingStatus = base_url + "/CustomerAPI/api/Lookup/BookingStatus";
	public static String getMyProfileURL = base_url + "/CustomerAPI/api/User/myprofile";
	public static String getCategoryURL = base_url + "/CustomerAPI/api/Category";

	// -------------------------------------------Vendor
	// Routes----------------------------------------------

	public static String vendor_getbooking = base_url + "/VendorAPI/api/Booking";
	public static String vendor_MyBookingURL = base_url + "/VendorAPI/api/Booking/my";
	public static String vendor_acceptBooking = base_url + "/VendorAPI/api/Booking/Accept/{bookingId}";

	public static String vendor_startBooking = base_url + "/VendorAPI/api/Booking/Start";
	public static String vendor_completeBooking = base_url + "/VendorAPI/api/Booking/Complete";
	public static String vendor_cancelBooking = base_url + "/VendorAPI/api/Booking/Cancel/{bookingId}";
	public static String vendor_getBookingId = base_url + "/VendorAPI/api/Booking/{bookingId}";
	public static String vendor_activeBookingURL = base_url + "/VendorAPI/api/Booking/active";
	// Timeslot
	public static String vendor_timeslotURL = base_url + "/VendorAPI/api/Timeslot";
	public static String vendor_disableTimeslot = base_url + "/VendorAPI/api/Timeslot/Disable";
	public static String vendor_enableTimeslot = base_url + "/VendorAPI/api/Timeslot/Enable/{id}";
	// User
	public static String vendor_profile = base_url + "/VendorAPI/api/User/myprofile";
	public static String vendor_bankDetails = base_url + "/VendorAPI/api/User/bankaccount";

	// ------------------------------------------Admin--------------------------------------------------------

	public static String admin_profile = base_url + "/AdminAPI/api/User/myprofile";
}
