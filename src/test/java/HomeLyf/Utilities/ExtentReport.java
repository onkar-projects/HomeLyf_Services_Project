package HomeLyf.Utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;

public class ExtentReport implements ITestListener {
	ExtentSparkReporter esr;
	ExtentReports er;
	ExtentTest test;

	@BeforeTest

	public void configurereports() {// add environment details
		esr = new ExtentSparkReporter("Extent_Report.html");
		er = new ExtentReports();
		er.attachReporter(esr);
		er.setSystemInfo("System", "Dell");
		er.setSystemInfo("OS", "window11");
		er.setSystemInfo("Host", "HomeLyf");

		// confi. to change look & feel
		esr.config().setDocumentTitle("Extent report file");
		esr.config().setReportName("Test report");
		esr.config().setTheme(Theme.STANDARD);
		esr.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
	}

	public void onStart(ITestContext result) {
		configurereports();
		System.out.println("On Start Method invoked");
	}

	public void onFinish(ITestContext result) {
		System.out.println("On Finished Method invoked");
		er.flush();
	}

	public void onTestFailure(ITestResult result) {
		System.out.println("Name of test method failed-" + result.getName());
		test = er.createTest(result.getName());
		test.log(Status.FAIL,
				MarkupHelper.createLabel("Name of failed Test case is " + result.getName(), ExtentColor.RED));
	}

	public void onTestSkipped(ITestResult result) {
		System.out.println("Name of test method skipped-" + result.getName());
		test = er.createTest(result.getName());
		test.log(Status.SKIP,
				MarkupHelper.createLabel("Name of Skipped Test case is " + result.getName(), ExtentColor.YELLOW));
	}

	public void onTestStart(ITestResult result) {
		System.out.println("Name of test method started-" + result.getName());
	}

	public void onTestSuccess(ITestResult result) {
		System.out.println("Name of test method successfully executed-" + result.getName());
		test = er.createTest(result.getName());
		test.log(Status.PASS,
				MarkupHelper.createLabel("Name of Pass Test case is " + result.getName(), ExtentColor.GREEN));
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}

	@AfterMethod
	public void getTestresult(ITestResult result) {

		if (result.getStatus() == ITestResult.FAILURE) {
			test.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " Fail", ExtentColor.RED));
			test.fail(result.getThrowable());
		}
		if (result.getStatus() == ITestResult.SUCCESS) {
			test.log(Status.PASS, MarkupHelper.createLabel(result.getName() + " Pass", ExtentColor.GREEN));
		}

		if (result.getStatus() == ITestResult.SKIP) {
			test.log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " Skip", ExtentColor.YELLOW));
		}
	}
}
