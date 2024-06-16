package gpb.itfactory.shevelamiddleservice.controller;

import gpb.itfactory.shevelamiddleservice.dto.TelegramUserDto;
import gpb.itfactory.shevelamiddleservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v2/middle")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public String createUserV2(@RequestBody TelegramUserDto telegramUserDto) {
        log.info("Receive request from TelegramBot: < register new user >");
        return userService.createUserV2(telegramUserDto);
    }

    @GetMapping("/users/{tgUserId}")
    public String getUserByTelegramIdV2(@PathVariable Long tgUserId) {
        log.info("Receive request from TelegramBot: < find user by tgUserId >");
        return userService.getUserByTelegramIdV2(tgUserId);
    }

}
