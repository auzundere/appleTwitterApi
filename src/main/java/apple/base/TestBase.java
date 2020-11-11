package apple.base;

import apple.base.annotations.TestCase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public class TestBase {
    public static final int RESPONSE_STATUS_CODE_200 = 200;
    public static final int RESPONSE_STATUS_CODE_201 = 201;
    public static final int RESPONSE_STATUS_CODE_400 = 400;
    public static final int RESPONSE_STATUS_CODE_401 = 401;
    public static final int RESPONSE_STATUS_CODE_500 = 500;
    TestBase testBase;
    String serviceURL;
    String apiURL;
    public static ExtentReports extent;
    protected String URL;
    public static ExtentTest reporting;
    protected Logger LOGGER = Logger.getLogger(TestBase.class.getSimpleName());
    public static Response response;
    protected String accessToken = getProperty("accessToken");

    public TestBase() {
        //Disables weird extra logging records comes from http request.
        disableDummyLogs();
    }

    @BeforeSuite
    public void startTest() {
        extent = new ExtentReports(getProperty("reportPath") + "/" + System.currentTimeMillis() + "/reports.html", true);
        extent.loadConfig(new File(getProperty("reportConfigPath")));
    }

    @BeforeMethod
    public void setup(Method method) {
        int testCase = 0;
        String testName = "";
        String startTestMessage = "---------------------------- Executing " + method.getName() + " ----------------------------";
        String testCaseAndTestName = "";
        if (method.getAnnotation(TestCase.class) != null) {
            testCase = method.getAnnotation(TestCase.class).testCaseId();
            testName = method.getAnnotation(TestCase.class).testName();
            testCaseAndTestName = ":: (Test Case Id: " + testCase + ") :: (Test Name: " + testName + ")";
            startTestMessage = "---------------------------- Executing " + testCaseAndTestName.substring(3) + " ----------------------------";
        }
        reporting = extent.startTest("(Class: " + this.getClass().getSimpleName() + ") :: (Method: " + method.getName() + ")" + testCaseAndTestName, testName);
        testBase = new TestBase();
        serviceURL = getProperty("URL");
        apiURL = getProperty("serviceURL");
        URL = serviceURL + apiURL;
        log(LogStatus.INFO, startTestMessage);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log(LogStatus.FAIL, "Test Case Failed is " + result.getName());
            log(LogStatus.FAIL, "Test Case Failed is " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
        }
        extent.endTest(reporting);
    }

    @AfterTest
    public void afterSuite() {
        extent.flush();
        extent.close();
    }


    public void logHeaders(ExtentTest reporting, Logger LOGGER, Response response) {
        Headers headersArray = response.getHeaders();
        log(reporting, LOGGER, null, "Response Headers:");
        log(reporting, LOGGER, null, "=================");
        for (Header header : headersArray) {
            log(reporting, LOGGER, LogStatus.PASS, header.getName() + ": " + header.getValue());
        }
    }

    protected void log(ExtentTest reporting, Logger LOGGER, LogStatus logStatus, String message) {
        if (logStatus != null) {
            reporting.log(logStatus, message);
            LOGGER.info(logStatus + " " + message);
        } else {
            reporting.log(LogStatus.INFO, message);
            LOGGER.info(message);
        }
    }

    protected String prettyPrinting(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(json);
    }

    protected void log(LogStatus logStatus, String message) {
        log(reporting, LOGGER, logStatus, message);
    }

    protected void logHeaders() {
        logHeaders(reporting, LOGGER, response);
    }

    public static String getProperty(String key) {
        Properties properties = null;
        try {
            properties = new Properties();
            String pwd = System.getProperty("user.dir");
            FileInputStream inputStream = new FileInputStream(pwd + "/src/main/resources/conf.properties");
            properties.load(inputStream);

        } catch (IOException e) {

            e.printStackTrace();
        }
        return properties.getProperty(key);
    }

    protected void disableDummyLogs() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("org.apache.http");
        root.setLevel(ch.qos.logback.classic.Level.INFO);
    }
}
