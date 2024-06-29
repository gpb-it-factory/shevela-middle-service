package gpb.itfactory.shevelamiddleservice.integration.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import gpb.itfactory.shevelamiddleservice.controller.TransferController;
import gpb.itfactory.shevelamiddleservice.dto.TelegramTransferDto;
import gpb.itfactory.shevelamiddleservice.integration.mocks.AccountMock;
import gpb.itfactory.shevelamiddleservice.integration.mocks.TransferMock;
import gpb.itfactory.shevelamiddleservice.integration.mocks.UserMock;
import gpb.itfactory.shevelamiddleservice.service.TransferService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class TransferControllerIT {

    private WireMockServer wireMockServer;
    private final TransferService transferService;

    @Autowired
    public TransferControllerIT(TransferService transferService) {
        this.transferService = transferService;
    }

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
    }

    @Test
    void createTransferIfUser1AndUser2IsPresentAccountIsPresentAndCreateSuccess(){
        TelegramTransferDto telegramTransferDto = TelegramTransferDto.builder().from("test1").fromId(123456).to("test2").ammount(100).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        TransferMock.setupGetUserByTelegramUsernameResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);
        TransferMock.setupCreateTransferResponseSuccess(wireMockServer);
        TransferController transferController = new TransferController(transferService);

        ResponseEntity<String> actualResult = transferController.createTransferV2(telegramTransferDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void createTransferIfUser1AndUser2IsPresentAccountIsPresentAndCreateFailIfServerError(){
        TelegramTransferDto telegramTransferDto = TelegramTransferDto.builder().from("test1").fromId(123456).to("test2").ammount(100).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        TransferMock.setupGetUserByTelegramUsernameResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);
        TransferMock.setupCreateTransferResponseFail(wireMockServer);
        TransferController transferController = new TransferController(transferService);

        ResponseEntity<String> actualResult = transferController.createTransferV2(telegramTransferDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void createTransferIfUser1AndUser2IsPresentAccountIsPresentAndCreateFailIfNotEnoughMoney(){
        TelegramTransferDto telegramTransferDto = TelegramTransferDto.builder().from("test1").fromId(123456).to("test2").ammount(10000).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        TransferMock.setupGetUserByTelegramUsernameResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);
        TransferController transferController = new TransferController(transferService);

        ResponseEntity<String> actualResult = transferController.createTransferV2(telegramTransferDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createTransferIfUser1AndUser2IsPresentAccountIsPresentAndCreateFailIfNoConnection(){
        TelegramTransferDto telegramTransferDto = TelegramTransferDto.builder().from("test1").fromId(123456).to("test2").ammount(100).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        TransferMock.setupGetUserByTelegramUsernameResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);
        TransferMock.setupCreateTransferResponseIfNoConnection(wireMockServer);
        TransferController transferController = new TransferController(transferService);

        ResponseEntity<String> actualResult = transferController.createTransferV2(telegramTransferDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void createTransferIfUser1IsPresentAccountIsPresentAndUser2IsNotPresent(){
        TelegramTransferDto telegramTransferDto = TelegramTransferDto.builder().from("test1").fromId(123456).to("test2").ammount(100).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        TransferMock.setupGetUserByTelegramUsernameResponseIfUserIsNotPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);
        TransferMock.setupCreateTransferResponseIfNoConnection(wireMockServer);
        TransferController transferController = new TransferController(transferService);

        ResponseEntity<String> actualResult = transferController.createTransferV2(telegramTransferDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createTransferIfUser1AndUser2IsPresentAndAccountIsNotPresent(){
        TelegramTransferDto telegramTransferDto = TelegramTransferDto.builder().from("test1").fromId(123456).to("test2").ammount(100).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        TransferMock.setupGetUserByTelegramUsernameResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsNotPresent(wireMockServer);
        TransferMock.setupCreateTransferResponseIfNoConnection(wireMockServer);
        TransferController transferController = new TransferController(transferService);

        ResponseEntity<String> actualResult = transferController.createTransferV2(telegramTransferDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @AfterEach
    void cleanUp(){
        wireMockServer.stop();
    }

}
