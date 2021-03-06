package tests.apitests.consentmanager.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tests.apitests.helpers.utils.Login;
import static tests.apitests.consentmanager.TestBuilders.consentPINPayload;

public class CreatePINAPITest {

  String authToken;

  @BeforeClass(alwaysRun = true)
  public void setup() {
    authToken = new Login().getCMAuthToken();
  }

  @Test(groups = {"nhsDev"})
  public void createPINAPI() {

    // checks if the patient has consent-pin created
    RequestSpecification request = RestAssured.given();
    Response patientDetailsResponse =
        request.header("Authorization", authToken).get("/patients/me");
    String hasPIN = patientDetailsResponse.jsonPath().getString("hasTransactionPin");

    if (hasPIN.equalsIgnoreCase("true")) {
      System.out.println("Consent PIN already created for this user");
    } else if (hasPIN.equalsIgnoreCase("false")) {

      // if consent-pin not created for patient
      request.header("Content-Type", "application/json");
      request.header("Authorization", authToken);
      request.body(consentPINPayload());
      Response generatePINResponse = request.post("/patients/pin");
      Assert.assertEquals(generatePINResponse.getStatusCode(), 204);
    }
  }
}
