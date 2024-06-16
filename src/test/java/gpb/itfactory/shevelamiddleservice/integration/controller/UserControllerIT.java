package gpb.itfactory.shevelamiddleservice.integration.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import gpb.itfactory.shevelamiddleservice.controller.UserController;
import gpb.itfactory.shevelamiddleservice.dto.TelegramUserDto;
import gpb.itfactory.shevelamiddleservice.integration.mocks.UserMock;
import gpb.itfactory.shevelamiddleservice.integration.WireMockConfig;
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
public class UserControllerIT {

    private final WireMockServer wireMockServer;
    private final UserController userController;

    @Autowired
    public UserControllerIT(WireMockServer wireMockServer, UserController userController) {
        this.wireMockServer = wireMockServer;
        this.userController = userController;
    }

    @Test
    void createUserIfUserNotPresentAndCreateSuccess(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);
        UserMock.setupCreateUserResponseSuccess(wireMockServer);

        String actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult).isEqualTo("User %s has been successfully registered in the MiniBank".formatted(telegramUserDto.getUsername()));
    }

    @Test
    void createUserIfUserIsNotPresentAndCreateFail(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);
        UserMock.setupCreateUserResponseFail(wireMockServer);

        String actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult).startsWith("Error");
    }

    @Test
    void createUserIfUserIsPresent(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        UserMock.setupCreateUserResponseFail(wireMockServer);

        String actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult).startsWith("User already exists in the MiniBank");
    }

    @Test
    void createUserIfUserNotPresentAndNoConnection(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);
        UserMock.setupCreateUserResponseIfNoConnection(wireMockServer);

        String actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult).isEqualTo("Sorry, server connection problem");
    }

    @Test
    void createUserIfGetUserByTelegramIdRequestNoConnection(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfNoConnection(wireMockServer);

        String actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult).startsWith("Sorry, server error or connection problem");
    }

    @Test
    void getUserByTelegramIdIfUserIsPresent(){
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);

        String actualResult = userController.getUserByTelegramIdV2(123456L);

        Assertions.assertThat(actualResult).isEqualTo("User is registered in the MiniBank");
    }

    @Test
    void getUserByTelegramIdIfUserIsNotPresent(){
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);

        String actualResult = userController.getUserByTelegramIdV2(123456L);

        Assertions.assertThat(actualResult).startsWith("Error");
    }

    @Test
    void getUserByTelegramIdIfNoConnection(){
        UserMock.setupGetUserByTelegramIdResponseIfNoConnection(wireMockServer);

        String actualResult = userController.getUserByTelegramIdV2(123456L);

        Assertions.assertThat(actualResult).isEqualTo("Sorry, server error or connection problem");
    }
}
