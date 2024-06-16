package gpb.itfactory.shevelamiddleservice.integration.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

public class UserMock {

    private final static Long USER_ID = 123456L;

    public static void setupCreateUserResponseSuccess(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users"))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.NO_CONTENT.value())
                ));
    }

    public static void setupCreateUserResponseFail(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users"))
                .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(createErrorJSON("Error"))
                ));
    }

    public static void setupCreateUserResponseIfNoConnection(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/users"))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                ));
    }

    public static void setupGetUserByTelegramIdResponseIfUserIsPresent(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                ));
    }

    public static void setupGetUserByTelegramIdResponseIfUserIsNotPresent(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.ACCEPTED.value())
                                .withBody(createErrorJSON("User is not registered in the MiniBank"))
                ));
    }

    public static void setupGetUserByTelegramIdResponseIfNoConnection(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users/" + USER_ID))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                ));
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
