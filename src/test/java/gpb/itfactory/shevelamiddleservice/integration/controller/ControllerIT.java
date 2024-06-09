package gpb.itfactory.shevelamiddleservice.integration.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import gpb.itfactory.shevelamiddleservice.controller.Controller;
import gpb.itfactory.shevelamiddleservice.dto.TelegramUserDto;
import gpb.itfactory.shevelamiddleservice.integration.CreateUserMock;
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
public class ControllerIT {

    private final WireMockServer wireMockServer;
    private final Controller controller;

    @Autowired
    public ControllerIT(WireMockServer wireMockServer, Controller controller) {
        this.wireMockServer = wireMockServer;
        this.controller = controller;
    }

    @Test
    void createUserIfRegisterSuccess(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        CreateUserMock.setupCreateUserResponseSuccess(wireMockServer);

        String actualResult = controller.createUser(telegramUserDto);

        Assertions.assertThat(actualResult).isEqualTo("User %s has been successfully registered in the MiniBank".formatted(telegramUserDto.getUsername()));
    }

    @Test
    void createUserIfRegisterFail(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        CreateUserMock.setupCreateUserResponseFail(wireMockServer);

        String actualResult = controller.createUser(telegramUserDto);

        Assertions.assertThat(actualResult).startsWith("Error");
    }

    @Test
    void createUserIfNoConnection(){
        TelegramUserDto telegramUserDto = TelegramUserDto.builder().username("test").tgUserId(123456L).build();
        CreateUserMock.setupCreateUserResponseIfNoConnection(wireMockServer);

        String actualResult = controller.createUser(telegramUserDto);

        Assertions.assertThat(actualResult).isEqualTo("Sorry, server connection problem");
    }

    @Test
    void findUserByTgIdIfUserIsPresent(){
        CreateUserMock.setupFindUserByTgUserIdResponseSuccess(wireMockServer);

        String actualResult = controller.findUserByTgUserId(123456L);

        Assertions.assertThat(actualResult).isEqualTo("User is registered in the MiniBank");
    }

    @Test
    void findUserByTgIdIfUserIsNotPresent(){
        CreateUserMock.setupFindUserByTgUserIdResponseFail(wireMockServer);

        String actualResult = controller.findUserByTgUserId(123456L);

        Assertions.assertThat(actualResult).startsWith("Error");
    }

    @Test
    void findUserByTgIdIfNoConnection(){
        CreateUserMock.setupFindUserByTgUserIdResponseIfNoConnection(wireMockServer);

        String actualResult = controller.findUserByTgUserId(123456L);

        Assertions.assertThat(actualResult).isEqualTo("Sorry, server error or connection problem");
    }
}
