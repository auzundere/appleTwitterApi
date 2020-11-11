package apple.apitests;

import apple.base.TestBase;
import apple.base.annotations.TestCase;
import com.relevantcodes.extentreports.LogStatus;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class APITests extends TestBase {

    public APITests(){
        disableDummyLogs();
    }

    @Parameters("ids")
    @TestCase(testCaseId = 1234, testName = "Send a request without authorization")
    @Test(groups = {"smoke"}, priority = 1)
    public void notAuthorizedRequest(String ids, Method method) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        response = given().param("ids", ids).headers(headerMap).get(URL + "/tweets");
        log(response.getStatusCode() == RESPONSE_STATUS_CODE_401 ? LogStatus.PASS : LogStatus.FAIL, "Status Code: " + response.getStatusCode());
        JSONObject jsonBody = new JSONObject(response.asString());
        Assert.assertEquals(response.getStatusCode(), RESPONSE_STATUS_CODE_401, "Status code was not " + RESPONSE_STATUS_CODE_401);
        log(LogStatus.INFO, response.asString());
        Assert.assertEquals(jsonBody.toString(), new JSONObject("{\"title\":\"Unauthorized\",\"type\":\"about:blank\",\"status\":401,\"detail\":\"Unauthorized\"}").toString());
        logHeaders();
    }

    @Parameters("invalidIdList")
    @TestCase(testCaseId = 1235, testName = "Get tweets with invalid tweet Ids")
    @Test(groups = {"smoke"}, priority = 2)
    public void getTweetsWithInvalidIdList(String invalidIdList, Method method) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Authorization", "Bearer " + accessToken);
        response = given().param("ids", invalidIdList).headers(headerMap).get(URL + "/tweets");
        log(response.getStatusCode() == RESPONSE_STATUS_CODE_400 ? LogStatus.PASS : LogStatus.FAIL, "Status Code: " + response.getStatusCode());
        JSONObject jsonBody = new JSONObject(response.asString());
        Assert.assertEquals(response.getStatusCode(), RESPONSE_STATUS_CODE_400, "Status code was not " + RESPONSE_STATUS_CODE_400);
        log(LogStatus.INFO, response.asString());
        Assert.assertEquals(jsonBody.get("detail").toString(), "One or more parameters to your request was invalid.");
        logHeaders();
    }

    @Parameters("ids")
    @TestCase(testCaseId = 1236, testName = "Get tweets by tweet ids")
    @Test(groups = {"smoke"}, priority = 3)
    public void getTweetsById(String ids, Method method) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Authorization", "Bearer " + accessToken);
        response = given().param("ids", ids).headers(headerMap).get(URL + "/tweets");
        log(response.getStatusCode() == RESPONSE_STATUS_CODE_200 ? LogStatus.PASS : LogStatus.FAIL, "Status Code: " + response.getStatusCode());
        JSONObject jsonBody = new JSONObject(response.asString());
        Assert.assertEquals(response.getStatusCode(), RESPONSE_STATUS_CODE_200, "Status code was not " + RESPONSE_STATUS_CODE_200);
        log(LogStatus.INFO, response.asString());
        StringBuilder actualIds = new StringBuilder(String.valueOf(((JSONObject) jsonBody.getJSONArray("data").get(0)).get("id")));
        for (int i = 1; i < jsonBody.getJSONArray("data").length(); i++) {
            actualIds.append(",").append(((JSONObject) jsonBody.getJSONArray("data").get(i)).get("id"));
        }
        log(LogStatus.INFO, "Expected Tweet Ids: " + ids);
        log(LogStatus.INFO, "Actual Tweet Ids: " + actualIds);
        Assert.assertEquals(ids, actualIds.toString());
        Assert.assertEquals(ids.split(",").length, jsonBody.getJSONArray("data").length(), "Actual response does not have the same number of tweets with the requested number of tweets: ");
        logHeaders();
    }

    @Parameters({"correctIds", "wrongIds"})
    @TestCase(testCaseId = 1237, testName = "Get tweets by ids includes some invalid tweet Ids")
    @Test(groups = {"smoke"}, priority = 4)
    public void getTweetsByIdsWithWrongId(String correctIds, String wrongIds, Method method) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Authorization", "Bearer " + accessToken);
        response = given().param("ids", correctIds + "," + wrongIds).headers(headerMap).get(URL + "/tweets");
        log(response.getStatusCode() == RESPONSE_STATUS_CODE_200 ? LogStatus.PASS : LogStatus.FAIL, "Status Code: " + response.getStatusCode());
        JSONObject jsonBody = new JSONObject(response.asString());
        Assert.assertEquals(response.getStatusCode(), RESPONSE_STATUS_CODE_200, "Status code was not " + RESPONSE_STATUS_CODE_200);
        log(LogStatus.INFO, response.asString());
        StringBuilder actualIds = new StringBuilder(String.valueOf(((JSONObject) jsonBody.getJSONArray("data").get(0)).get("id")));
        for (int i = 1; i < jsonBody.getJSONArray("data").length(); i++) {
            actualIds.append(",").append(((JSONObject) jsonBody.getJSONArray("data").get(i)).get("id"));
        }
        log(LogStatus.INFO, "Expected Correct Tweet Ids: " + correctIds);
        log(LogStatus.INFO, "Actual Correct Tweet Ids: " + actualIds);
        Assert.assertEquals(correctIds, actualIds.toString());
        Assert.assertEquals(correctIds.split(",").length, jsonBody.getJSONArray("data").length(),
                "Actual response does not have the same number of tweets with the requested number of tweets: ");
        StringBuilder actualWrongIds = new StringBuilder(((JSONObject) jsonBody.getJSONArray("errors").get(0)).get("value").toString());
        for (int i = 1; i < jsonBody.getJSONArray("errors").length(); i++) {
            actualWrongIds.append(",").append(((JSONObject) jsonBody.getJSONArray("errors").get(i)).get("value").toString());
        }
        Assert.assertEquals(actualWrongIds.toString(), wrongIds, "The invalid TweetId is not listed, ");
        logHeaders();
    }
}