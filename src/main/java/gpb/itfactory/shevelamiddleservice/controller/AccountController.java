package gpb.itfactory.shevelamiddleservice.controller;

import gpb.itfactory.shevelamiddleservice.dto.CreateAccountDto;
import gpb.itfactory.shevelamiddleservice.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v2/middle")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/users/{tgUserId}/accounts")
    public String createUserAccountV2(@RequestBody CreateAccountDto createAccountDto,
                                      @PathVariable Long tgUserId) {
        log.info("Receive request from TelegramBot: < create account >");
        return accountService.createUserAccountV2(tgUserId, createAccountDto);
    }

    @GetMapping("/users/{tgUserId}/accounts")
    public String getUserAccountsV2(@PathVariable Long tgUserId) {
        log.info("Receive request from TelegramBot: < get accounts >");
        return accountService.getUserAccountsV2(tgUserId);
    }
}
