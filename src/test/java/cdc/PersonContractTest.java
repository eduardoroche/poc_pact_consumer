package cdc;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import cdc.rule.RandomPortRule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;


public class PersonContractTest {
    @ClassRule
    public static RandomPortRule randomPort = new RandomPortRule();

    @Rule
    public PactProviderRuleMk2 mockedProviderServer = new PactProviderRuleMk2("person-provider", "127.0.0.1",
            randomPort.getPort(), this);

    @Pact(consumer = "person-consumer", provider = "person-provider")
    public RequestResponsePact savePerson(PactDslWithProvider builder) {

        return builder.given(
                "A person is saved")
                .uponReceiving("POST REQUEST")
                .path("/person")
                .method("POST")
                .body("{\"name\":\"Roche\"}")
                .willRespondWith()
                .status(200)
                .toPact();
    }

    @PactVerification(fragment = "savePerson")
    @Test
    public void generateSavePersonContract() throws IOException {
        //Arrange
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = mockedProviderServer.getUrl().concat("/person");
        Request request = new Request.Builder().url(url).post(RequestBody.create(JSON, "{\"name\":\"Roche\"}")).build();
        int expectedStatusCode = HttpStatus.SC_CREATED;

        //Act
        int actualStatus = new OkHttpClient().newCall(request).execute().code();

        //Assert
        Assert.assertEquals(expectedStatusCode, actualStatus);
    }

    @Pact(consumer = "person-consumer", provider = "person-provider")
    public RequestResponsePact updatePerson(PactDslWithProvider builder) {

        return builder.given(
                "A person is updated with an existing id")
                .uponReceiving("PUT REQUEST")
                .path("/person/1")
                .method("PUT")
                .body("{\"name\":\"updated Roche\",\"id\":1}")
                .willRespondWith()
                .status(204)
                .toPact();
    }

    @PactVerification(fragment = "updatePerson")
    @Test
    public void generateUpdatePersonContract() throws IOException {
        //Arrange
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = mockedProviderServer.getUrl().concat("/person/1");
        Request request = new Request.Builder().url(url).put(RequestBody.create(JSON, "{\"name\":\"updated Roche\",\"id\":1}")).build();
        int expectedStatusCode = HttpStatus.SC_NO_CONTENT;

        //Act
        int actualStatus = new OkHttpClient().newCall(request).execute().code();

        //Assert
        Assert.assertEquals(expectedStatusCode, actualStatus);
    }

    @Pact(consumer = "person-consumer", provider = "person-provider")
    public RequestResponsePact getPersonById(PactDslWithProvider builder) {

        return builder.given(
                "A person is requested with an existing id")
                .uponReceiving("GET REQUEST")
                .path("/person/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(LambdaDsl.newJsonBody(body -> {
                    body.id("id", 1l).stringValue("name", "Roche");
                }).build())
                .toPact();
    }

    @PactVerification(fragment = "getPersonById")
    @Test
    public void generateGetPersonContract() throws IOException {
        //Arrange
        Long expectedId = 1l;
        String expectedName = "Roche";
        String url = mockedProviderServer.getUrl().concat("/person/").concat(expectedId.toString());
        Request request = new Request.Builder().url(url).build();
        ObjectMapper mapper = new ObjectMapper();

        //Act
        String actualJsonData = new OkHttpClient().newCall(request).execute().body().string();

        //Assert
        JsonNode jsonNode = mapper.readTree(actualJsonData);
        Assert.assertEquals(expectedId.toString(), jsonNode.path("id").toString());
        Assert.assertEquals(expectedName, jsonNode.get("name").toString().replaceAll("\"", ""));
    }

    @Pact(consumer = "person-consumer", provider = "person-provider")
    public RequestResponsePact deletePerson(PactDslWithProvider builder) {

        return builder.given(
                "A person is deleted with an existing id")
                .uponReceiving("DELETE REQUEST")
                .path("/person/1")
                .method("DELETE")
                .willRespondWith()
                .status(200)
                .toPact();
    }

    @PactVerification(fragment = "deletePerson")
    @Test
    public void generateDeletePersonContract() throws IOException {
        //Arrange
        Long id = 1l;
        String url = mockedProviderServer.getUrl().concat("/person/").concat(id.toString());
        Request request = new Request.Builder().url(url).delete().build();
        int expectedStatusCode = HttpStatus.SC_OK;

        //Act
        int actualStatus = new OkHttpClient().newCall(request).execute().code();

        //Assert
        Assert.assertEquals(expectedStatusCode, actualStatus);
    }
}
