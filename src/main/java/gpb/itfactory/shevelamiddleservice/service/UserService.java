package gpb.itfactory.shevelamiddleservice.service;

import gpb.itfactory.shevelamiddleservice.dto.TelegramUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class UserService {

    private final String BASE_URL;
    private final RestTemplate restTemplate;

    public UserService(RestTemplate restTemplate, @Value("${backend.service.url}") String BASE_URL) {
        this.restTemplate = restTemplate;
        this.BASE_URL = BASE_URL;
    }

    public ResponseEntity<String> createUserV2(TelegramUserDto telegramUserDto) {
        log.info("Create request to BackendService: < register new user >");
        try {
            ResponseEntity<String> getUserByTelegramIdV2Response = getUserByTelegramIdV2(telegramUserDto.getTgUserId());
            if (getUserByTelegramIdV2Response.getStatusCode() == HttpStatus.NOT_FOUND) {
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                        BASE_URL + "/users", telegramUserDto, String.class);
                log.info("Receive response from BackendService: < register new user > - HttpStatus.204");
                return ResponseEntity.status(responseEntity.getStatusCode())
                        .body(("{\"message\": \"User %s has been successfully registered in the MiniBank\"}")
                                .formatted(telegramUserDto.getUsername()));
            }
            return getUserByTelegramIdV2Response;
        } catch (RestClientException exception) {
            log.error("RestClientException exception: " + exception);
            throw exception;
        }
    }

    public ResponseEntity<String> getUserByTelegramIdV2(long tgUserId) {
        log.info("Create request to BackendService: < get user by tgUserId >");
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                    BASE_URL + "/users/" + tgUserId, String.class);
            log.info(("Receive response from BackendService: < get user by tgUserId > - " +
                    "User is registered with userId = %s").formatted(responseEntity.getBody()));
            return ResponseEntity.status(200).body("{\"message\": \"User is registered in the MiniBank\"}");
        } catch (HttpClientErrorException exception) {

            System.out.println(exception.toString());

            log.info("Receive response from BackendService: < get user by tgUserId >  %s"
                    .formatted(exception.toString()));
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAs(String.class));
        } catch (RestClientException exception) {
            log.error("RestClientException exception: " + exception);
            throw exception;
        }
    }
}
