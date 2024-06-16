package gpb.itfactory.shevelamiddleservice.integration.mocks;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

public class AccountMock {

    private final static Long USER_ID = 123456L;

    public static void setupCreateUserAccountResponseSuccess(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.NO_CONTENT.value())
                ));
    }

    public static void setupCreateUserAccountResponseFail(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(createErrorJSON("Error"))
                ));
    }

    public static void setupCreateUserAccountResponseIfNoConnection(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                ));
    }

    public static void setupGetUserAccountsV2ResponseIfAccountIsPresent(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBody(createAccountJSON())
                ));
    }

    public static void setupGetUserAccountsV2ResponseIfErrorNoAccount(WireMockServer mockService) {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.ACCEPTED.value())
                                .withBody(createErrorJSON("User does not have account"))
                ));
    }

    public static void setupGetUserAccountsV2ResponseIfNoConnection(WireMockServer mockService) {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                ));
    }

    private static String createAccountJSON() {
        return """
                [
                    {
                    \"accountId\": \"%s\",
                    \"accountName\": \"test\",
                    \"amount\": \"5000\"
                    }
                ]
                """.formatted(UUID.randomUUID().toString());
    }

    private static String createErrorJSON(String message) {

        return """ 
                {
                \"message\": \"%s\",
                \"type\": \"GeneralError\",
                \"code\": \"123\",
                \"trace_id\": \"%s\"
                }
                """.formatted(message, UUID.randomUUID());
    }
}
