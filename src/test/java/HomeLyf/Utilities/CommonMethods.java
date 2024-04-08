package HomeLyf.Utilities;

import java.util.ArrayList;
import java.util.List;
import HomeLyf.Payload.Address;
import HomeLyf.Payload.ForgotPassword_Payload;
import HomeLyf.Payload.SendEmailOTP_Payload;
import HomeLyf.Payload.SignUP_Payload;
import HomeLyf.Payload.UserLogin_Payload;
import HomeLyf.Payload.VendorDetail;
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
	
	public static SignUP_Payload invaliduserSignUp(String name, String mobileNumber, String type, String emailAddress, String password,
			String Id, String scategories, String spostcodes, String addharnum, String exp, String addressname,
			String addresstype, String line1, String line2, String line3, String location, String postid,
			String cityid) {
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
}
