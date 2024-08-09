package HomeLyf.Utilities;

import java.io.IOException;

import org.testng.annotations.DataProvider;

public class DataProviderClass {
	static String path = System.getProperty("user.dir") + "\\testData\\Homelyf_userDetails.xlsx";
	static UserUtility lu = new UserUtility(path);

	@org.testng.annotations.DataProvider(name = "Data")
	public static String[][] getAllData() throws IOException {

		int rownum = lu.getRowCount("user");
		int colcount = lu.getCellCount("user", 1);
		String apiData[][] = new String[rownum][colcount];

		for (int i = 1; i < rownum; i++) {
			for (int j = 0; j < 5 - 1; j++) {
				apiData[i][j] = lu.getCellData("user", i, j);
				System.out.println(apiData[i][j]);
			}
		}
		return apiData;
	}

	@DataProvider(name = "Vendordata")
	public static String[][] getcustomerData() throws IOException {

		int rownum = lu.getRowCount("user");
		String apiData[][] = new String[rownum][18];

		for (int i = 1; i <= rownum; i++) {
			for (int j = 0; j < 18; j++) {
				apiData[i - 1][j] = lu.getCellData("user", i, j);
				//System.out.println("Given data: " + apiData[i - 1][j]);
			}
		}
		return apiData;
	}

	@org.testng.annotations.DataProvider(name = "useremailAndPassword")
	public static Object[][] getPassword() throws IOException {
		int rownum = lu.getRowCount("user");
		Object[][] emailAndPassword = new Object[rownum - 1][2];
		for (int i = 1; i < rownum; i++) {
			emailAndPassword[i - 1][0] = lu.getCellData("user", i, 1);
			emailAndPassword[i - 1][1] = lu.getCellData("user", i, 3);
			System.out.println(emailAndPassword[i - 1][0] + "/n" + emailAndPassword[i - 1][1]);
		}
		return emailAndPassword;
	}

	@org.testng.annotations.DataProvider(name = "userEmailAndNewPass")
	public static Object[][] getemailAndNewPass() throws IOException {

		int rownum = lu.getRowCount("Sheet1");
		Object[][] emailAndNewpass = new Object[rownum - 1][2];

		for (int i = 1; i < rownum; i++) {
			emailAndNewpass[i - 1][0] = lu.getCellData("Sheet1", i, 1);
			emailAndNewpass[i - 1][1] = lu.getCellData("Sheet1", i, 4);
			System.out.println(emailAndNewpass[1][1]);
		}
		return emailAndNewpass;
	}

//	------------------------------------------HomeLyf-----------------------------------------------//
	@DataProvider(name = "userlogin")
	public static String[][] getEmpId() throws IOException {

		int rownum = lu.getRowCount("user");
//		int colCount = lu.getCellCount("user", 1);

		String apiData[][] = new String[rownum][5];

		for (int i = 1; i <= rownum; i++) {
			for (int j = 1; j <= 5; j++) {
				apiData[i - 1][j - 1] = lu.getCellData("user", i, j);
				System.out.println("Given data: " + apiData[i - 1][j - 1]);
			}
		}
		return apiData;
	}

	@DataProvider(name = "invalidvendordata")
	public static String[][] getcustomerinvalidData() throws IOException {

		int rownum = lu.getRowCount("InvalidData");
		String apiData[][] = new String[rownum][18];

		for (int i = 1; i <= rownum; i++) {
			for (int j = 0; j < 18; j++) {
				apiData[i - 1][j] = lu.getCellData("InvalidData", i, j);

				System.out.println("Given data: " + apiData[i - 1][j]);
			}
		}
		return apiData;
	}

	@DataProvider(name = "emailOTP")
	public static String[][] getEmailOTPId() throws IOException {

		int rownum = lu.getRowCount("user");
//		int colCount = lu.getCellCount("user", 1);

		String apiData[][] = new String[rownum][1];

		for (int i = 1; i <= rownum; i++) {
			apiData[i - 1][0] = lu.getCellData("user", i, 3);
			System.out.println("Given data: " + apiData[i - 1][0]);
		}
		return apiData;
	}

	@DataProvider(name = "invalidemail")
	public static String[][] getInvalidUserEmail() throws IOException {

		int rownum = lu.getRowCount("InvalidData");
//		int colCount = lu.getCellCount("InvalidData", 1);

		String apiData[][] = new String[rownum][1];

		for (int i = 1; i <= rownum; i++) {
			apiData[i - 1][0] = lu.getCellData("InvalidData", i, 3);

			System.out.println("Given data: " + apiData[i - 1][0]);
		}
		return apiData;
	}

	@DataProvider(name = "useremailAndMobile")
	public static String[][] getEmailandMobile() throws IOException {
		int rownum = lu.getRowCount("user");
		String[][] emailAndPassword = new String[rownum][2];

		for (int i = 1; i <= rownum; i++) {
			emailAndPassword[i - 1][0] = lu.getCellData("user", i, 1);
			emailAndPassword[i - 1][1] = lu.getCellData("user", i, 3);
			System.out.println(emailAndPassword[i - 1][0] + "/n" + emailAndPassword[i - 1][1]);
		}
		return emailAndPassword;
	}

	@DataProvider(name = "InvaliduseremailAndMobile")
	public static String[][] getInvalidEmailandMobile() throws IOException {
		int rownum = lu.getRowCount("InvalidData");
		String[][] emailAndPassword = new String[rownum][2];

		for (int i = 1; i <= rownum; i++) {
			emailAndPassword[i - 1][0] = lu.getCellData("InvalidData", i, 1);
			emailAndPassword[i - 1][1] = lu.getCellData("InvalidData", i, 3);
			System.out.println(emailAndPassword[i - 1][0] + "/n" + emailAndPassword[i - 1][1]);
		}
		return emailAndPassword;
	}

	@DataProvider(name = "invalid_userlogin")
	public static String[][] getInvalidUser() throws IOException {

		int rownum = lu.getRowCount("InvalidData");
//	int colCount = lu.getCellCount("InvalidData", 1);

		String apiData[][] = new String[rownum][5];

		for (int i = 1; i <= rownum; i++) {
			for (int j = 1; j <= 5; j++) {
				apiData[i - 1][j - 1] = lu.getCellData("InvalidData", i, j);
				System.out.println("Given data: " + apiData[i - 1][j - 1]);
			}
		}
		return apiData;
	}

	@DataProvider(name = "Customerlogin")
	public static String[][] getCustomerEmp() throws IOException {

		int rownum = lu.getRowCount("Customer");
//	int colCount = lu.getCellCount("user", 1);

		String apiData[][] = new String[rownum][5];

		for (int i = 1; i <= rownum; i++) {
			for (int j = 1; j <= 5; j++) {
				apiData[i - 1][j - 1] = lu.getCellData("Customer", i, j);
				//System.out.println("Given data: " + apiData[i - 1][j - 1]);
			}
		}
		return apiData;
	}

	@DataProvider(name = "CustomerAddressData")
	public static String[][] customerAddressData() throws IOException {

		int rownum = lu.getRowCount("user");
		int colcount = lu.getCellCount("user", rownum);
		String apiData[][] = new String[rownum][6];

		for (int i = 1; i <= rownum; i++) {
			for (int j = 10; j <= 15; j++) {
				apiData[i - 1][j - 10] = lu.getCellData("user", i, j);

				System.out.println("Given data: " + apiData[i - 1][j - 10]);
			}
		}
		return apiData;
	}

	@DataProvider(name = "custTimeSlot")
	public static String[][] custTimeSlot() throws IOException {

		int rownum = lu.getRowCount("Customer");
		String apiData[][] = new String[rownum][1];

		for (int i = 1; i <= rownum; i++) {
//				apiData[i-1][0] = lu.getCellData("Customer", i,18);
			apiData[i - 1][0] = lu.getCellData("Customer", i, 19);
			//System.out.println("Given data: " + apiData[i - 1][0]);
//			System.out.println("Given data: "+ apiData[i-1][1]);
		}
		return apiData;
	}

	@DataProvider(name = "bookingDetails")
	public static String[][] custBookingDetails() throws IOException {

		int rownum = lu.getRowCount("Customer");
		String apiData[][] = new String[rownum][2];

		for (int i = 1; i <= rownum; i++) {
			apiData[i - 1][0] = lu.getCellData("Customer", i, 20);
			apiData[i - 1][1] = lu.getCellData("Customer", i, 21);
//			System.out.println("Given data: " + apiData[i - 1][0]);
//			System.out.println("Given data: " + apiData[i - 1][1]);
		}
		return apiData;
	}

	@DataProvider(name = "UnverifiedVendor")
	public static String[][] unverifiedVendor() throws IOException {
		int rownum = lu.getRowCount("UnverifiedVendor");
		String apiData[][] = new String[rownum][5];
		for (int i = 1; i <= rownum; i++) {
			for (int j = 0; j < 5; j++) {
				apiData[i - 1][j] = lu.getCellData("UnverifiedVendor", i, j);
				//System.out.println("Given data: " + apiData[i - 1][j]);
			}
		}
		return apiData;
	}
}