package gpb.itfactory.shevelamiddleservice.integration.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import gpb.itfactory.shevelamiddleservice.controller.UserController;
import gpb.itfactory.shevelamiddleservice.dto.TelegramUserDto;
import gpb.itfactory.shevelamiddleservice.integration.mocks.UserMock;
import gpb.itfactory.shevelamiddleservice.integration.WireMockConfig;
import gpb.itfactory.shevelamiddleservice.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
public class UserControllerIT {

    private WireMockServer wireMockServer;
    private final UserService userService;

    @Autowired
    public UserControllerIT(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
    }

    @Test
    void createUserIfUserNotPresentAndCreateSuccess(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);
        UserMock.setupCreateUserResponseSuccess(wireMockServer);
        UserController userController = new UserController(userService);

        ResponseEntity<String> actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void createUserIfUserIsNotPresentAndCreateFail(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);
        UserMock.setupCreateUserResponseFail(wireMockServer);
        UserController userController = new UserController(userService);

        ResponseEntity<String> actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void createUserIfUserIsPresent(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        UserController userController = new UserController(userService);

        ResponseEntity<String> actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void createUserIfUserNotPresentAndNoConnection() {
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);
        UserMock.setupCreateUserResponseIfNoConnection(wireMockServer);
        UserController userController = new UserController(userService);

        ResponseEntity<String> actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void createUserIfGetUserByTelegramIdRequestNoConnection(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        UserMock.setupGetUserByTelegramIdResponseIfNoConnection(wireMockServer);
        UserController userController = new UserController(userService);

        ResponseEntity<String> actualResult = userController.createUserV2(telegramUserDto);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void getUserByTelegramIdIfUserIsPresent(){
        UserMock.setupGetUserByTelegramIdResponseIfUserIsPresent(wireMockServer);
        UserController userController = new UserController(userService);

        ResponseEntity<String> actualResult = userController.getUserByTelegramIdV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUserByTelegramIdIfUserIsNotPresent(){
        UserMock.setupGetUserByTelegramIdResponseIfUserIsNotPresent(wireMockServer);
        UserController userController = new UserController(userService);

        ResponseEntity<String> actualResult = userController.getUserByTelegramIdV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getUserByTelegramIdIfServerError(){
        UserMock.setupGetUserByTelegramIdResponseFail(wireMockServer);
        UserController userController = new UserController(userService);

        ResponseEntity<String> actualResult = userController.getUserByTelegramIdV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getUserByTelegramIdIfNoConnection(){
        UserMock.setupGetUserByTelegramIdResponseIfNoConnection(wireMockServer);
        UserController userController = new UserController(userService);

        ResponseEntity<String> actualResult = userController.getUserByTelegramIdV2(123456L);

        Assertions.assertThat(actualResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @AfterEach
    void cleanUp(){
        wireMockServer.stop();
    }
}