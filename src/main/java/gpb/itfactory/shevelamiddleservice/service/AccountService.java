package gpb.itfactory.shevelamiddleservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpb.itfactory.shevelamiddleservice.dto.CreateAccountDto;
import gpb.itfactory.shevelamiddleservice.dto.ErrorDto;
import gpb.itfactory.shevelamiddleservice.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class AccountService {
    @Value("${backend.service.url}")
    private String BASE_URL;
    private final RestTemplate restTemplate;
    private final UserService userService;

    public AccountService(RestTemplate restTemplate, UserService userService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    public String createUserAccountV2(Long tgUserId, CreateAccountDto createAccountDto) {
        log.info("Create request to BackendService: < create account >");
        try {
            String getUserAccountsV2Response = getUserAccountsV2(tgUserId);
            if (getUserAccountsV2Response.contains("User does not have account")) {
                String url = BASE_URL + "/" + tgUserId + "/accounts";
                ResponseEntity<ErrorDto> responseEntity = restTemplate.postForEntity(url, createAccountDto, ErrorDto.class);
                return buildResponseToCreateUserAccountV2(responseEntity, createAccountDto);
            } else if (getUserAccountsV2Response.contains("User has open account")) {
                return "Account is already open. %s".formatted(getUserAccountsV2Response);
            }
            return getUserAccountsV2Response;
        } catch (RestClientException exception) {
            log.error(exception.toString());
        }
        log.error("BackendService connection problems");
        return "Sorry, server connection problem";
    }

    private String buildResponseToCreateUserAccountV2(ResponseEntity<ErrorDto> responseEntity, CreateAccountDto createAccountDto) {
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            log.info("Receive response from BackendService: < create account > - HttpStatus.204");
            return "Account %s has been successfully created".formatted(createAccountDto.getAccountName());
        }
        ErrorDto errorDto = responseEntity.getBody();
        log.info("Receive response from BackendService: < create account > - Error: %s".formatted(errorDto.toString()));
        return "Error << Unknown server error when opening an account >>";
    }

    public String getUserAccountsV2(long tgUserId)  {
        log.info("Create request to BackendService: < get accounts >");
        try {
            String getUserByTelegramIdV2Response = userService.getUserByTelegramIdV2(tgUserId);
            if (getUserByTelegramIdV2Response.equals("User is registered in the MiniBank")){
                String url = BASE_URL + "/" + tgUserId + "/accounts";
                ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
                return buildResponseToGetUserAccountsV2(responseEntity);
            }
            return getUserByTelegramIdV2Response;
        } catch (JsonMappingException e) {
            log.error(e.toString());
        } catch (RestClientException | JsonProcessingException exception) {
            log.error(exception.toString());
        }
        log.error("BackendService connection problems");
        return "Sorry, server error or connection problem";
    }

    private String buildResponseToGetUserAccountsV2(ResponseEntity<String> responseEntity) throws JsonProcessingException {
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Account> accounts = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>(){});
            log.info("Receive response from BackendService: < get accounts > - Accounts: %s"
                    .formatted(accounts.toString()));
            Account account = accounts.get(0);
            return "User has open account: %s, amount: %s".formatted(account.getAccountName(), account.getAmount());
        }
        ObjectMapper mapper = new ObjectMapper();
        ErrorDto errorDto = mapper.readValue(responseEntity.getBody(), ErrorDto.class);
        log.info("Receive response from BackendService: < get accounts > - Error %s".formatted(errorDto.toString()));
        if (errorDto.getMessage().contains("User does not have account")) {
            return "Error << User does not have account >>";
        }
        return "Error << Unknown server error when requesting account balance >>";
    }
}
