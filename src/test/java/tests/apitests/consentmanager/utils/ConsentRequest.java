package tests.apitests.consentmanager.utils;

import static tests.apitests.consentmanager.TestBuilders.grantPINPayload;
import static tests.apitests.consentmanager.TestBuilders.revokePINPayload;
import static tests.apitests.healthinformationuser.TestBuilders.createConsentRequestPayload;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import tests.apitests.helpers.PropertiesCache;
import tests.apitests.helpers.utils.Login;

public class ConsentRequest {

  public Response createConsent(String id) {

    // create consent-request at HIU
    RestAssured.baseURI = PropertiesCache.getInstance().getProperty("HIUBaseURL");
    RestAssured.useRelaxedHTTPSValidation();
    RequestSpecification request = RestAssured.given();
    request.header("Content-Type", "application/json");
    request.header("Authorization", new Login().getHIUAuthToken());

    request.body(createConsentRequestPayload(id));
    Response response = request.post("/v1/hiu/consent-requests");
    return response;
  }

  public String verifyConsentPIN(String consent) {

    // verify consent-pin and generate pin-authorization token
    String authToken = new Login().getCMAuthToken();
    RequestSpecification request = RestAssured.given();
    request.header("Content-Type", "application/json");
    request.header("Authorization", authToken);

    if (consent.equalsIgnoreCase("grant")) {
      request.body(grantPINPayload());
    } else if (consent.equalsIgnoreCase("revoke")) {
      request.body(revokePINPayload());
    }
    Response response = request.post("/patients/verify-pin");
    JsonPath jsonPathEvaluator = response.jsonPath();
    return jsonPathEvaluator.getString("temporaryToken");
  }

  public String fetchConsentRequestId(Response response, String patient) {

    // identifies the consent-request-id of patient's consent-request in the GET consent-requests at
    // HIU
    String consentRequestId = "";
    JsonPath jsonPathEvaluator = response.jsonPath();
    List<String> patientList = jsonPathEvaluator.getList("patient.id");
    if (patientList != null) {
      for (int i = 0; i < (patientList.size() - 1); i++) {
        if (patientList.get(i).equalsIgnoreCase(patient)) {
          if ((jsonPathEvaluator.getString("status[" + i + "]")).equalsIgnoreCase("REQUESTED")) {
            consentRequestId = jsonPathEvaluator.getString("consentRequestId[" + i + "]");
            break;
          }
        }
      }
    } else {
      System.out.println("patient list is null");
    }
    return consentRequestId;
  }

  public String fetchConsentStatus(Response response, String consentRequestId) {

    // identifies the status of consent in the GET consent-requests at HIU
    String status = "";
    JsonPath jsonPathEvaluator = response.jsonPath();
    List<String> consentRequestIds = jsonPathEvaluator.getList("consentRequestId");
    if (consentRequestIds != null) {
      for (int i = 0; i < (consentRequestIds.size() - 1); i++) {
        if (consentRequestIds.get(i).equalsIgnoreCase(consentRequestId)) {
          status = jsonPathEvaluator.getString("status[" + i + "]");
          break;
        }
      }
    } else {
      System.out.println("Consent-request id is null");
    }
    return status;
  }
}
