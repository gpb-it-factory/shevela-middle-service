package gpb.itfactory.shevelamiddleservice.integration.mocks;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class TransferMock {

    public static void setupGetUserByTelegramUsernameResponseIfUserIsPresent(WireMockServer mockService){
        String userIdJSON = "{\"userId\": \"123456\"}";
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users?tgUsername=test2"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(userIdJSON)));
    }

    public static void setupCreateTransferResponseSuccess(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/transfers"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(createTransferJSON())));
    }

    public static void setupCreateTransferResponseFail(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/transfers"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody(createErrorJSON("Create transfer internal server error",
                                "createTransferV2Error", "300"))));
    }

    public static void setupCreateTransferResponseIfNoConnection(WireMockServer mockService){
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/v2/transfers"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));
    }

    public static void setupGetUserByTelegramUsernameResponseIfUserIsNotPresent(WireMockServer mockService){
        String userIdJSON = "{\"userId\": \"123456\"}";
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/v2/users?tgUsername=test2"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withBody(createErrorJSON("User is not registered in the MiniBank", "getUserError", "102"))));
    }

    private static String createTransferJSON() {
        return """
                    {
                    \"transferId\": \"%s\",
                    }
                """.formatted(UUID.randomUUID());
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
