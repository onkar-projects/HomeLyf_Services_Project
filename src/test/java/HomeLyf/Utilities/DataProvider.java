package HomeLyf.Utilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataProvider {
	static String path = System.getProperty("user.dir") + "\\TestData\\HomeLyfDataSheet.xlsx";
	static UserUtility lu = new UserUtility(path);

	@org.testng.annotations.DataProvider(name = "Data")
	public static String[][] getAllData() throws IOException {

		int rownum = lu.getRowCount("Sheet1");
		int colcount = lu.getCellCount("sheet1", 1);

		String apiData[][] = new String[rownum][colcount];

		for (int i = 1; i < rownum; i++) {
			for (int j = 0; j < colcount - 1; j++) {
				apiData[i][j] = lu.getCellData("Sheet1", i, j);
				System.out.println(apiData[i][j]);
			}
		}
		return apiData;
	}

	@org.testng.annotations.DataProvider(name = "userEmail")
	public static String[] getEmpId() throws IOException {

		int rownum = lu.getRowCount("Sheet1");
		String apiData[] = new String[rownum];
		for (int i = 1; i <= rownum; i++) {
			apiData[i - 1] = lu.getCellData("Sheet1", i, 1);
		}
		return apiData;
	}

	@org.testng.annotations.DataProvider(name = "useremailAndPassword")
	public static Object[][] getPassword() throws IOException {
		int rownum = lu.getRowCount("Sheet1");
		Object[][] emailAndPassword = new Object[rownum - 1][2];

		for (int i = 1; i < rownum; i++) {
			emailAndPassword[i - 1][0] = lu.getCellData("Sheet1", i, 1);
			emailAndPassword[i - 1][1] = lu.getCellData("Sheet1", i, 3);
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

//	------------------------------------------QDTASK User----------------------------------------------------//

	@org.testng.annotations.DataProvider(name = "qdtasUserAlldata")
	public static String[][] qdtaskUserAllData() throws IOException {

		int rownum = lu.getRowCount("QDTAS1");
		int colcount = lu.getCellCount("QDTAS1", 1);

		String apiData[][] = new String[rownum][colcount];

		for (int i = 1; i < rownum; i++) {
			for (int j = 0; j < colcount - 1; j++) {
				try {
					apiData[i][j] = lu.getCellData("Sheet1", i, j);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(apiData[i][j]);
			}
		}
		return apiData;
	}

	@org.testng.annotations.DataProvider(name = "qdtasLoginDetails")
	public static String[][] qdtasLoginDetail() throws IOException {

		String adminData[][] = new String[1][2];
		adminData[0][0] = lu.getCellData("QDTAS1", 1, 1);
		adminData[0][1] = lu.getCellData("QDTAS1", 1, 2);
		return adminData;
	}

	@org.testng.annotations.DataProvider(name = "qdtasNewUserDetails")
		public static String[][] qdtasNewUserData() throws IOException {
	
			int rowcount = lu.getRowCount("QDTAS1");
			int colcount = lu.getCellCount("QDTAS1", 1);
			System.out.println("Row count= " + rowcount);
			System.out.println("colomn count= " + colcount);
			String NewUserData[][] = new String[rowcount-1][colcount-2];
	
			for (int i = 2; i < rowcount+1; i++) {
				for (int j = 0; j < colcount - 2; j++) {
					NewUserData[i - 2][j] = lu.getCellData("QDTAS1", i, j);
				}
			}
			return NewUserData;
		}

	@org.testng.annotations.DataProvider(name = "qdtasNewLoginDetails")
	public static String[][] qdtasNewLoginDetail() throws IOException {

		int rowcount = lu.getRowCount("QDTAS1");
		String adminData[][] = new String[1][2];

		for (int i = 2; i <=rowcount; i++) {
			adminData[i-2][0] = lu.getCellData("QDTAS1", 2, 1);
			adminData[i-2][1] = lu.getCellData("QDTAS1", 2, 2);
		}
		return adminData;
	}
	
	@org.testng.annotations.DataProvider(name = "qdtasUpdateUserDetails")
		public static String[][] qdtasUpdateUserData() throws IOException {
	
		int rowcount = lu.getRowCount("QDTAS1");
		int colcount = lu.getCellCount("QDTAS1", 1);
		System.out.println("Row count= " + rowcount);
		System.out.println("colomn count= " + colcount);
		String NewUserData[][] = new String[rowcount-1][colcount];

		for (int i = 2; i < rowcount+1; i++) {
			for (int j = 0; j < colcount; j++) {
				NewUserData[i - 2][j] = lu.getCellData("QDTAS1", i, j);
			}
		}
		return NewUserData;
		}
}
