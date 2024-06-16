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
        AccountMock.setupGetUserAccountsV2ResponseIfErrorNoAccount(wireMockServer);
        AccountMock.setupCreateUserAccountResponseSuccess(wireMockServer);

        String actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult).contains("Account %s has been successfully created".formatted(createAccountDto.getAccountName()));
    }

    @Test
    void createUserAccountIfUserIsPresentAndAccountNotPresentAndCreateFail(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfErrorNoAccount(wireMockServer);
        AccountMock.setupCreateUserAccountResponseFail(wireMockServer);

        String actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult).startsWith("Error");
    }

    @Test
    void createUserAccountIfUserIsPresentAndAccountNotPresentAndNoConnectionToCreate(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfErrorNoAccount(wireMockServer);
        AccountMock.setupCreateUserAccountResponseIfNoConnection(wireMockServer);

        String actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult).startsWith("Sorry, server connection problem");
    }

    @Test
    void createUserAccountIfUserIsPresentAndAccountIsPresent(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);

        String actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult).startsWith("Account is already open");
    }

    @Test
    void createUserAccountIfUserIsPresentAndGetUserAccountsRequestErrorOrNoConnection(){
        CreateAccountDto createAccountDto = CreateAccountDto.builder().accountName("testAccount").build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfNoConnection(wireMockServer);

        String actualResult = accountController.createUserAccountV2(createAccountDto,123456L);

        Assertions.assertThat(actualResult).startsWith("Sorry, server error or connection problem");
    }

    @Test
    void getUserAccountsV2IfUserIsPresentAndHaveAccount() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfAccountIsPresent(wireMockServer);

        String actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult).contains("User has open account");
    }

    @Test
    void getUserAccountsV2IfUserIsPresentAndGetRequestError() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        AccountMock.setupGetUserAccountsV2ResponseIfErrorNoAccount(wireMockServer);

        String actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult).startsWith("Error");
    }

    @Test
    void getUserAccountsV2IfUserIsNotPresent() {
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);

        String actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult).startsWith("Error");
    }

    @Test
    void getUserAccountsV2IfGetUserRequestNoConnection() {
        AccountMock.setupGetUserAccountsV2ResponseIfNoConnection(wireMockServer);

        String actualResult = accountController.getUserAccountsV2(123456L);

        Assertions.assertThat(actualResult).startsWith("Sorry, server error or connection problem");
    }

}
