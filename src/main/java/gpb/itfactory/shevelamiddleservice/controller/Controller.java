package gpb.itfactory.shevelamiddleservice.controller;

import gpb.itfactory.shevelamiddleservice.entity.User;
import gpb.itfactory.shevelamiddleservice.entity.UsersList;
import gpb.itfactory.shevelamiddleservice.dto.controller.TelegramUserDto;
import gpb.itfactory.shevelamiddleservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/middle")
public class Controller {

    private final UserService userService;
    private final UsersList usersList;

    public Controller(UserService userService, UsersList usersList) {
        this.userService = userService;
        this.usersList = usersList;
    }

    @PostMapping("/users")
    public String createUser(@RequestBody TelegramUserDto telegramUserDto) {
        log.info("Receive request from TelegramBot: < register new user >");
        if (usersList.getUserByUsername(telegramUserDto.getUsername()).isPresent()) {
//            User user = usersList.getUserByUsername(telegramUserDto.getUsername()).get();
            log.info("Status: user %s is already exists".formatted(telegramUserDto.getUsername()));
//            log.info(user.toString());
            return "User %s already exists".formatted(telegramUserDto.getUsername());
        }
        log.info("Request from controller to service on MiddleService: < register new user > username: "
                + telegramUserDto.getUsername());
        return userService.createUser(telegramUserDto);
    }

    @GetMapping("/users/{username}")
    public String findUserByTgUsername(@PathVariable String username) {
        log.info("Receive request from TelegramBot: < find user by tgUserId >");
        Optional<User> user = usersList.getUserByUsername(username);
        if (user.isPresent()) {
            log.info("Status: user %s is registered".formatted(username));
            log.info("Request from controller to service on MiddleService: < find user by tgUserId > tgUserId = %s"
                    .formatted(user.get().getTgUserId()));
            return userService.findUserByTgId(user.get().getTgUserId());
        }
        log.info("Build response: User %s is not registered".formatted(username));
        return "User %s is not registered".formatted(username);
    }

}
