package HomeLyf.Utilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReport implements ITestListener {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static Map<Integer, ExtentTest> testMap = new HashMap<>();
    ExtentSparkReporter esr;
    String reportName;

    @BeforeTest
    public void setup() {
        File reportsDir = new File(".\\Reports\\");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        reportName = "HomeLyfServicesAPIProject.html";
        esr = new ExtentSparkReporter(reportsDir.getPath() + "\\" + reportName);
        extent = new ExtentReports();
        extent.attachReporter(esr);
        extent.setSystemInfo("System", "Dell");
        extent.setSystemInfo("OS", "window11");
        extent.setSystemInfo("Host", "HomeLyf");
        esr.config().setDocumentTitle("Extent report file");
        esr.config().setReportName("Test report");
        esr.config().setTheme(Theme.STANDARD);
        esr.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
    }

    public void onStart(ITestContext context) {
        setup();
        System.out.println("Test Suite started!");
    }

    public void onFinish(ITestContext context) {
        System.out.println(("Test Suite is ending!"));
        extent.flush();
    }

    public void onTestStart(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " started!"));
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(),
                result.getMethod().getDescription());
        test.set(extentTest);
        testMap.put((int) Thread.currentThread().threadId(), extentTest);
    }

    public void onTestSuccess(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " passed!"));
        test.get().log(Status.PASS, MarkupHelper.createLabel(result.getName() + " Test Case PASSED", ExtentColor.GREEN));
    }

    public void onTestFailure(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " failed!"));
        test.get().log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " Test Case FAILED due to below issues:",
                ExtentColor.RED));
        test.get().fail(result.getThrowable());
    }

    public void onTestSkipped(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " skipped!"));
        test.get().log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " Test Case SKIPPED", ExtentColor.YELLOW));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.get().log(Status.FAIL, "TEST CASE FAILED IS " + result.getName());
            test.get().log(Status.FAIL, "TEST CASE FAILED IS " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.get().log(Status.SKIP, "Test Case SKIPPED IS " + result.getName());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.get().log(Status.PASS, "Test Case PASSED IS " + result.getName());
        }
        extent.flush();
    }
}
