package gpb.itfactory.shevelamiddleservice.controller;

import gpb.itfactory.shevelamiddleservice.dto.TelegramUserDto;
import gpb.itfactory.shevelamiddleservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v2/middle")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {          // *** !!!
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUserV2(@RequestBody TelegramUserDto telegramUserDto) {
        log.info("Receive request from TelegramBot: < register new user >");
        try {
            return userService.createUserV2(telegramUserDto);
        } catch (HttpServerErrorException exception) {
            log.info("Receive response from BackendService: < get user by tgUserId >  %s"
                    .formatted(exception.toString()));
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAs(String.class));
        } catch (RestClientException exception) {
            log.error(exception.toString());
            return ResponseEntity.status(502).body(createErrorJSON(
                    "Backend server unknown or connection error when create user", "createUserError", "110"));
        }
    }

    @GetMapping("/users/{tgUserId}")
    public ResponseEntity<String> getUserByTelegramIdV2(@PathVariable Long tgUserId) {
        log.info("Receive request from TelegramBot: < get user by tgUserId >");
        try {
            return userService.getUserByTelegramIdV2(tgUserId);
        } catch (HttpServerErrorException exception) {
            log.info("Receive response from BackendService: < get user by tgUserId >  %s"
                    .formatted(exception.toString()));
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAs(String.class));
        } catch (RestClientException exception) {
            log.error(exception.toString());
            return ResponseEntity.status(502).body(createErrorJSON(
                    "Backend server unknown or connection error when registration verification", "getUserError", "113"));
        }
    }

    private String createErrorJSON(String message, String type, String code) {

        return """ 
                {
                \"message\": \"%s\",
                \"type\": \"%s\",
                \"code\": \"%s\",
                \"trace_id\": \"%s\"
                }
                """.formatted(message, type, code, UUID.randomUUID());
    }
}
