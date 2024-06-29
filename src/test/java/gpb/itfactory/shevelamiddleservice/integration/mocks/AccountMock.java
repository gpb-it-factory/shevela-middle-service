package gpb.itfactory.shevelamiddleservice.integration.mocks;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

public class AccountMock {

    private final static Long USER_ID = 123456L;

    public static void setupCreateUserAccountResponseSuccess(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NO_CONTENT.value())
                        .withBody(createAccountNameJSON())));
    }

    public static void setupCreateUserAccountResponseFail(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody(createErrorJSON("Create user account error",
                                "createUserAccountError", "200"))));
    }

    public static void setupCreateUserAccountResponseIfNoConnection(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users/"  + USER_ID  + "/accounts"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));
    }

    public static void setupGetUserAccountsV2ResponseIfAccountIsPresent(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(createAccountJSON())));
    }

    public static void setupGetUserAccountsV2ResponseIfNoAccount(WireMockServer mockService) {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withBody(createErrorJSON("User does not have account",
                                "getUserAccountsError", "201"))));
    }

    public static void setupGetUserAccountsV2ResponseIfServerError(WireMockServer mockService) {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody(createErrorJSON("Error receiving accounts",
                                "getUserAccountsError", "203"))));
    }

    public static void setupGetUserAccountsV2ResponseIfNoConnection(WireMockServer mockService) {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/"  + USER_ID  + "/accounts"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));
    }

    public static void setupGetUserAccountsV2ResponseIfAccountIsNotPresent(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID + "/accounts"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withBody(createErrorJSON("User does not have account", "getUserAccountsError", "201"))));
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

    private static String createAccountNameJSON() {
        return """
                {
                \"accountName\": \"test\",
                }
                """;
    }

    private static String createErrorJSON(String message, String type, String code) {

        return """ 
                {
                \"message\": \"%s\",
                \"type\": \"%s\",
                \"code\": \"%s\",
                \"trace_id\": \"%s\"
                }
                """.formatted(message, type, code, UUID.randomUUID());
    }
}
