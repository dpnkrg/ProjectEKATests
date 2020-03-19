package Tests.APITests.HIP;

import Tests.APITests.APIUtils.CentralRegistry;
import Tests.APITests.APIUtils.HIPPatientDiscovery;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HIPAPITest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://ncg-dev.projecteka.in/hip";
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void discoverPatient() {

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Authorization","Bearer "+ new CentralRegistry().getAuthToken());

        String patientDiscoverRequestBody = new HIPPatientDiscovery().getPatientDiscoverRequestBody();

        request.body(patientDiscoverRequestBody);
        Response response = request.post("/patients/discover");

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(jsonPathEvaluator.getString("patient.referenceNumber"), "123");
        Assert.assertEquals(jsonPathEvaluator.getString("patient.display"), "TestFirstName TestLastName");
        Assert.assertEquals(jsonPathEvaluator.getString("patient.careContexts[0].referenceNumber"), "121");
        jsonPathEvaluator.getString("patient.matchedBy").equals("[Mobile, Gender]");
    }
}