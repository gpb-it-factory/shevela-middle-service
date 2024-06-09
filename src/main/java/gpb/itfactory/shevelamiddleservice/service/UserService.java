package gpb.itfactory.shevelamiddleservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpb.itfactory.shevelamiddleservice.dto.ErrorDto;
import gpb.itfactory.shevelamiddleservice.dto.TelegramUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class UserService {

    @Value("${backend.service.url}")
    String URL;
    private final RestTemplate restTemplate;
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createUser(TelegramUserDto telegramUserDto) {
        log.info("Create request to BackendService: < register new user >");
        try {
            ResponseEntity<ErrorDto> responseEntity = restTemplate.postForEntity(URL, telegramUserDto, ErrorDto.class);
            return buildResponseToCreateUser(responseEntity, telegramUserDto);
        } catch (RestClientException exception) {
            log.error(exception.toString());
        }
        log.error("BackendService connection problems");
        return "Sorry, server connection problem";
    }

    private String buildResponseToCreateUser(ResponseEntity<ErrorDto> responseEntity, TelegramUserDto telegramUserDto) {
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            log.info("Receive response from BackendService: < register new user > - HttpStatus.204");
            return "User %s has been successfully registered in the MiniBank".formatted(telegramUserDto.getUsername());
        }
        ErrorDto errorDto = responseEntity.getBody();
        log.info("Receive response from BackendService: < register new user > - Error: %s".formatted(errorDto.toString()));
        return "Error Message: %s. Type: %s".formatted(errorDto.getMessage(), errorDto.getType());
    }

    public String findUserByTgId(long tgUserId) {
        log.info("Create request to BackendService: < find user by tgUserId >");
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(URL+ "/" + tgUserId, String.class);
            return buildResponseToFindUserByTgId(responseEntity);
        } catch (JsonMappingException e) {
            log.error(e.toString());
        } catch (RestClientException | JsonProcessingException exception) {
            log.error(exception.toString());
        }
        log.error("BackendService connection problems");
        return "Sorry, server error or connection problem";
    }

    private String buildResponseToFindUserByTgId(ResponseEntity<String> responseEntity) throws JsonProcessingException {
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("Receive response from BackendService: < find user by tgUserId > - User is registered with userId = %s"
                    .formatted(responseEntity.getBody()));
            return "User is registered in the MiniBank";
        }
        ObjectMapper mapper = new ObjectMapper();
        ErrorDto errorDto = mapper.readValue(responseEntity.getBody(), ErrorDto.class);
        log.info("Receive response from BackendService: < find user by tgUserId > - Error %s".formatted(errorDto.toString()));
        return "Error Message: %s. Type: %s".formatted(errorDto.getMessage(), errorDto.getType());
    }
}
