package gpb.itfactory.shevelamiddleservice.service;

import gpb.itfactory.shevelamiddleservice.dto.CreateAccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class AccountService {

    private final String BASE_URL;
    private final RestTemplate restTemplate;
    private final UserService userService;

    public AccountService(RestTemplate restTemplate,
                          UserService userService,
                          @Value("${backend.service.url}") String BASE_URL) {
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.BASE_URL = BASE_URL;
    }

    public ResponseEntity<String> createUserAccountV2(Long tgUserId, CreateAccountDto createAccountDto) {
        log.info("Create request to BackendService: < create account >");
        try {
            ResponseEntity<String> getUserByTelegramIdV2Response = userService.getUserByTelegramIdV2(tgUserId);
            if (getUserByTelegramIdV2Response.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<String> getUserAccountsV2Response = getUserAccountsV2(tgUserId);
                if (getUserAccountsV2Response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    String url = BASE_URL + "/users/" + tgUserId + "/accounts";
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, createAccountDto, String.class);
                    log.info("Receive response from BackendService: < create account > - HttpStatus.204");
                    return ResponseEntity.status(responseEntity.getStatusCode())
                            .body("{\"message\": \"Account has been successfully created\"}");
                }
                return getUserAccountsV2Response;
            }
            return getUserByTelegramIdV2Response;
        } catch (RestClientException exception) {
            log.error("RestClientException exception: " + exception);
            throw exception;
        }
    }

    public ResponseEntity<String> getUserAccountsV2(long tgUserId) {
        log.info("Create request to BackendService: < get accounts >");
        try {
            ResponseEntity<String> getUserByTelegramIdV2Response = userService.getUserByTelegramIdV2(tgUserId);
            if (getUserByTelegramIdV2Response.getStatusCode() == HttpStatus.OK) {
                String url = BASE_URL + "/users/" + tgUserId + "/accounts";
                return restTemplate.getForEntity(url, String.class);
            }
            return getUserByTelegramIdV2Response;
        } catch (HttpClientErrorException exception) {
            log.info("Receive response from BackendService: < get user account >  %s"
                    .formatted(exception.toString()));
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAs(String.class));
        } catch (RestClientException exception) {
            log.error("RestClientException exception: " + exception);
            throw exception;
        }
    }
}
