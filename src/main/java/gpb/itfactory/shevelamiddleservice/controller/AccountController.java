package gpb.itfactory.shevelamiddleservice.controller;

import gpb.itfactory.shevelamiddleservice.dto.CreateAccountDto;
import gpb.itfactory.shevelamiddleservice.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v2/middle")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {                           // *** !!!
        this.accountService = accountService;
    }

    @PostMapping("/users/{tgUserId}/accounts")
    public ResponseEntity<String> createUserAccountV2(@RequestBody CreateAccountDto createAccountDto,
                                                      @PathVariable Long tgUserId) {
        log.info("Receive request from TelegramBot: < create account >");
        try {
            return accountService.createUserAccountV2(tgUserId, createAccountDto);
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            log.info("Receive response from BackendService: < create account >  %s"
                    .formatted(exception.toString()));
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAs(String.class));
        }
        catch (RestClientException exception) {
            log.error(exception.toString());
            return ResponseEntity.status(502)
                    .body(createErrorJSON("Backend server unknown or connection error when create account",
                            "createUserAccountError", "210"));
        }
    }

    @GetMapping("/users/{tgUserId}/accounts")
    public ResponseEntity<String> getUserAccountsV2(@PathVariable Long tgUserId) {
        log.info("Receive request from TelegramBot: < get accounts >");
        try {
            return accountService.getUserAccountsV2(tgUserId);
        } catch (HttpServerErrorException exception) {
            log.info("Receive response from BackendService: < get user account >  %s"
                    .formatted(exception.toString()));
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAs(String.class));
        } catch (RestClientException exception) {
            log.error(exception.toString());
            return ResponseEntity.status(502)
                    .body(createErrorJSON("Backend server unknown or connection error " +
                            "when user accounts verification", "getUserAccountsError", "213"));
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
