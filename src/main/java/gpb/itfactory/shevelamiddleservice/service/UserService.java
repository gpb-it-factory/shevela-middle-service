package gpb.itfactory.shevelamiddleservice.service;

import gpb.itfactory.shevelamiddleservice.client.ClientManager;
import gpb.itfactory.shevelamiddleservice.dto.TelegramUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class UserService {

    private final ClientManager<RestClient> restClientManager;

    public UserService(ClientManager<RestClient> restClientManager) {
        this.restClientManager = restClientManager;
    }

    public ResponseEntity<String> createUserV2(TelegramUserDto telegramUserDto) {
        log.info("Create request to BackendService: < register new user >");
        try {
            ResponseEntity<String> getUserByTelegramIdV2Response = getUserByTelegramIdV2(telegramUserDto.getTgUserId());
            if (getUserByTelegramIdV2Response.getStatusCode() == HttpStatus.NOT_FOUND) {
                ResponseEntity<String> responseEntity = restClientManager.getClient()
                        .post().uri("/users").body(telegramUserDto).retrieve().toEntity(String.class);
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
            ResponseEntity<String> responseEntity = restClientManager.getClient()
                    .get().uri("/users/"  + tgUserId).retrieve().toEntity(String.class);
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

    public ResponseEntity<String> getUserByTelegramUsernameV2(String tgUsername) {
        log.info("Create request to BackendService: < find user by tgUserId >");
        try {
            ResponseEntity<String> responseEntity = restClientManager.getClient()
                    .get().uri("/users?tgUsername="  + tgUsername).retrieve().toEntity(String.class);
            log.info("Receive response from BackendService: < get user by tgUsername > - User is registered with userId = %s"
                    .formatted(responseEntity.getBody()));
            return ResponseEntity.status(200).body("{\"message\": \"User is registered in the MiniBank\"}");
        } catch (HttpClientErrorException exception) {
            log.info("Receive response from BackendService: < get user by tgUsername >  %s"
                    .formatted(exception.toString()));
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAs(String.class));
        } catch (RestClientException exception) {
            log.error("RestClientException exception: " + exception);
            throw exception;
        }
    }
}
