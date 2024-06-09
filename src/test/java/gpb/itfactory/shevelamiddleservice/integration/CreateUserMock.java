package gpb.itfactory.shevelamiddleservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

public class CreateUserMock {

    private final static Long USER_ID = 123456L;

    public static void setupCreateUserResponseSuccess(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/users"))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.NO_CONTENT.value())
                ));
    }
    public static void setupCreateUserResponseFail(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/users"))
                .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(createErrorJSON())
                ));
    }

    public static void setupCreateUserResponseIfNoConnection(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/users"))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                ));
    }

    public static void setupFindUserByTgUserIdResponseSuccess(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/users/" + USER_ID))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                ));
    }

    public static void setupFindUserByTgUserIdResponseFail(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/users/" + USER_ID))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.ACCEPTED.value())
                                .withBody(createErrorJSON())
                ));
    }

    public static void setupFindUserByTgUserIdResponseIfNoConnection(WireMockServer mockService){
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/users/" + USER_ID))
                .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                ));
    }

    private static String createErrorJSON() {

        return """ 
                {
                \"message\": \"Error\",
                \"type\": \"GeneralError\",
                \"code\": \"123\",
                \"trace_id\": \"%s\"
                }
                """.formatted( UUID.randomUUID());
    }
}
