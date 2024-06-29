package gpb.itfactory.shevelamiddleservice.integration.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import gpb.itfactory.shevelamiddleservice.controller.AccountController;
import gpb.itfactory.shevelamiddleservice.dto.CreateAccountDto;
import gpb.itfactory.shevelamiddleservice.integration.WireMockConfig;
import gpb.itfactory.shevelamiddleservice.integration.mocks.AccountMock;
import gpb.itfactory.shevelamiddleservice.integration.mocks.UserMock;
import gpb.itfactory.shevelamiddleservice.service.AccountService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
public class AccountControllerIT {

    private WireMockServer wireMockServer;
    private final AccountService accountService;

    @Autowired
    public AccountControllerIT(AccountService accountService) {
        this.accountService = accountService;
    }

    @BeforeEach
    void setUp(){
        wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
    }

    @Test
    void createUserAccountIfUserIsPresentAccountNotPresentAndCreateSuccess(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoAccount(wireMockServer);
        AccountMock.setupCreateUserAccountResponseSuccess(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void createUserAccountIfUserIsPresentAndAccountNotPresentAndCreateFail(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoAccount(wireMockServer);
        AccountMock.setupCreateUserAccountResponseFail(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void createUserAccountIfUserIsPresentAndAccountNotPresentAndNoConnectionToCreate(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoAccount(wireMockServer);
        AccountMock.setupCreateUserAccountResponseIfNoConnection(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void createUserAccountIfUserIsPresentAndAccountIsPresent(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void createUserAccountIfUserIsNotPresent(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createUserAccountIfUserIsPresentAndGetUserAccountsRequestNoConnection(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoConnection(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void getUserAccountsV2IfUserIsPresentAndHaveAccount() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUserAccountsV2IfUserIsPresentAndGetRequestFail() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoAccount(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getUserAccountsV2IfUserIsPresentAndGetRequestServerError() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfServerError(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getUserAccountsV2IfUserIsNotPresent() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getUserAccountsV2IfGetUserRequestNoConnection() {
        UserMock.setupGetUserByTelegramIdResponseIfNoConnection(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void getUserAccountsV2IfGetAccountRequestNoConnection() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoConnection(wireMockServer);
        AccountController accountController = new AccountController(accountService);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @AfterEach
    void cleanUp(){
        wireMockServer.stop();
    }
}
