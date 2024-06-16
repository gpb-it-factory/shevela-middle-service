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

    public String createUserV2(TelegramUserDto telegramUserDto) {
        log.info("Create request to BackendService: < register new user >");
        try {
            String getUserByTelegramIdV2Response = getUserByTelegramIdV2(telegramUserDto.getTgUserId());
            if (getUserByTelegramIdV2Response.equals("User is registered in the MiniBank")) {
                return "User already exists in the MiniBank";
            } else if (getUserByTelegramIdV2Response.contains("User is not registered in the MiniBank")) {
                ResponseEntity<ErrorDto> responseEntity = restTemplate.postForEntity(URL, telegramUserDto, ErrorDto.class);
                return buildResponseToCreateUserV2(responseEntity, telegramUserDto);
            }
            return getUserByTelegramIdV2Response;
        } catch (RestClientException exception) {
            log.error(exception.toString());
        }
        log.error("BackendService connection problems");
        return "Sorry, server connection problem";
    }

    private String buildResponseToCreateUserV2(ResponseEntity<ErrorDto> responseEntity, TelegramUserDto telegramUserDto) {
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            log.info("Receive response from BackendService: < register new user > - HttpStatus.204");
            return "User %s has been successfully registered in the MiniBank".formatted(telegramUserDto.getUsername());
        }
        ErrorDto errorDto = responseEntity.getBody();
        log.info("Receive response from BackendService: < register new user > - Error: %s".formatted(errorDto.toString()));
        return "Error << Unknown user registration server error >>";
    }

    public String getUserByTelegramIdV2(long tgUserId) {
        log.info("Create request to BackendService: < find user by tgUserId >");
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(URL+ "/" + tgUserId, String.class);
            return buildResponseToGetUserByTelegramIdV2(responseEntity);
        } catch (JsonMappingException e) {
            log.error(e.toString());
        } catch (RestClientException | JsonProcessingException exception) {
            log.error(exception.toString());
        }
        log.error("BackendService connection problems");
        return "Sorry, server error or connection problem";
    }

    private String buildResponseToGetUserByTelegramIdV2(ResponseEntity<String> responseEntity) throws JsonProcessingException {
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("Receive response from BackendService: < find user by tgUserId > - User is registered with userId = %s"
                    .formatted(responseEntity.getBody()));
            return "User is registered in the MiniBank";
        }
        ObjectMapper mapper = new ObjectMapper();
        ErrorDto errorDto = mapper.readValue(responseEntity.getBody(), ErrorDto.class);
        log.info("Receive response from BackendService: < find user by tgUserId > - Error %s".formatted(errorDto.toString()));
        if (errorDto.getMessage().contains("User is not registered in the MiniBank")) {
            return "Error << User is not registered in the MiniBank >>";
        }
        return "Error << Unknown server error when verifying user registration >>";
    }
}
