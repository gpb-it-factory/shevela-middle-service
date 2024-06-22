package gpb.itfactory.shevelamiddleservice.integration.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import gpb.itfactory.shevelamiddleservice.controller.AccountController;
import gpb.itfactory.shevelamiddleservice.dto.CreateAccountDto;
import gpb.itfactory.shevelamiddleservice.integration.WireMockConfig;
import gpb.itfactory.shevelamiddleservice.integration.mocks.AccountMock;
import gpb.itfactory.shevelamiddleservice.integration.mocks.UserMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ContextConfiguration(classes = { WireMockConfig.class })
public class AccountControllerIT {

    private final WireMockServer wireMockServer;
    private final AccountController accountController;

    @Autowired
    public AccountControllerIT(WireMockServer wireMockServer, AccountController userController) {
        this.wireMockServer = wireMockServer;
        this.accountController = userController;
    }

    @Test
    void createUserAccountIfUserIsPresentAccountNotPresentAndCreateSuccess(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoAccount(wireMockServer);
        AccountMock.setupCreateUserAccountResponseSuccess(wireMockServer);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void createUserAccountIfUserIsPresentAndAccountNotPresentAndCreateFail(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoAccount(wireMockServer);
        AccountMock.setupCreateUserAccountResponseFail(wireMockServer);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void createUserAccountIfUserIsPresentAndAccountNotPresentAndNoConnectionToCreate(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoAccount(wireMockServer);
        AccountMock.setupCreateUserAccountResponseIfNoConnection(wireMockServer);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void createUserAccountIfUserIsPresentAndAccountIsPresent(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void createUserAccountIfUserIsNotPresent(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createUserAccountIfUserIsPresentAndGetUserAccountsRequestNoConnection(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoConnection(wireMockServer);

        ResponseEntity<String> actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void getUserAccountsV2IfUserIsPresentAndHaveAccount() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUserAccountsV2IfUserIsPresentAndGetRequestFail() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoAccount(wireMockServer);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getUserAccountsV2IfUserIsPresentAndGetRequestServerError() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfServerError(wireMockServer);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getUserAccountsV2IfUserIsNotPresent() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getUserAccountsV2IfGetUserRequestNoConnection() {
        UserMock.setupGetUserByTelegramIdResponseIfNoConnection(wireMockServer);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void getUserAccountsV2IfGetAccountRequestNoConnection() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoConnection(wireMockServer);

        ResponseEntity<String> actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }
}
