

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

	
	
	//-------------------------------------------Customer Routes----------------------------------------------

	
	
	public static String customer_service = base_url+"/CustomerAPI/api/Service/{subCategoryId}";
	public static String customer_Address = base_url +"/CustomerAPI/api/User/address";
	
	
	
	//-------------------------------------------Vendor Routes----------------------------------------------
	
	
	
	public static String vendor_getbooking = base_url+"/VendorAPI/api/Booking";
}

