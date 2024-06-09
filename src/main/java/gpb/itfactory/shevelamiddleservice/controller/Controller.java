package gpb.itfactory.shevelamiddleservice.controller;

import gpb.itfactory.shevelamiddleservice.dto.TelegramUserDto;
import gpb.itfactory.shevelamiddleservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/middle")
public class Controller {

    private final UserService userService;

    public Controller(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public String createUser(@RequestBody TelegramUserDto telegramUserDto) {
        log.info("Receive request from TelegramBot: < register new user >");
        return userService.createUser(telegramUserDto);
    }

    @GetMapping("/users/{tgUserId}")
    public String findUserByTgUserId(@PathVariable Long tgUserId) {
        log.info("Receive request from TelegramBot: < find user by tgUserId >");
        return userService.findUserByTgId(tgUserId);
    }

}
