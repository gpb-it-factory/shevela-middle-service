package gpb.itfactory.shevelamiddleservice.integration.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UserMock {

    private final static Long USER_ID = 123456L;

    public static void setupCreateUserResponseSuccess(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NO_CONTENT.value())));
    }

    public static void setupCreateUserResponseFail(WireMockServer mockService) {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody(createErrorJSON("Create user internal server error",
                                "createUserError", "100"))));
    }

    public static void setupCreateUserResponseIfNoConnection(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));
    }

    public static void setupGetUserByTelegramIdResponseIfUserIsPresent(WireMockServer mockService){
        String userIdJSON = "{\"userId\": \"123456\"}";
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(userIdJSON)));
    }

    public static void setupGetUserByTelegramIdResponseIfUserIsNotPresent(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withBody(createErrorJSON("User is not registered in the MiniBank",
                                "getUserError", "101"))));
    }

    public static void setupGetUserByTelegramIdResponseFail(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody(createErrorJSON("Registration verification internal server error",
                                "getUserAccountsError", "103"))));
    }

    public static void setupGetUserByTelegramIdResponseIfNoConnection(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/"  + USER_ID))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));
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
