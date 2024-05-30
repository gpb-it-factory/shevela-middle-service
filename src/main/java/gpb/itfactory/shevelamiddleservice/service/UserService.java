package gpb.itfactory.shevelamiddleservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpb.itfactory.shevelamiddleservice.entity.Error;
import gpb.itfactory.shevelamiddleservice.entity.User;
import gpb.itfactory.shevelamiddleservice.entity.UsersList;
import gpb.itfactory.shevelamiddleservice.dto.client.UserDto;
import gpb.itfactory.shevelamiddleservice.dto.controller.TelegramUserDto;
import gpb.itfactory.shevelamiddleservice.mapper.CreateUserMapper;
import gpb.itfactory.shevelamiddleservice.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class UserService {

    private final RestTemplate restTemplate;
    private final CreateUserMapper createUserMapper;
    private final UserMapper userMapper;
    private final UsersList usersList;
    String URL = "http://localhost:8081/users";

    public UserService(RestTemplate restTemplate, CreateUserMapper createUserMapper, UserMapper userMapper, UsersList usersList) {
        this.restTemplate = restTemplate;
        this.createUserMapper = createUserMapper;
        this.userMapper = userMapper;
        this.usersList = usersList;
    }

    public String createUser(TelegramUserDto telegramUserDto) {
        User user = userMapper.map(telegramUserDto);
        UserDto userDto = createUserMapper.map(user);
        log.info("Create request to BackendService: < register new user >");
        try {
            ResponseEntity<Error> responseEntity = restTemplate.postForEntity(URL, userDto, Error.class);
            return buildResponseToCreateUser(responseEntity, user);
        } catch (RestClientException exception) {
            log.error(exception.toString());
        }
        log.error("BackendService connection problems");
        return "Sorry, server connection problem";
    }

    private String buildResponseToCreateUser(ResponseEntity<Error> responseEntity, User user) {
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            usersList.addUser(user);
            log.info("Receive response from BackendService: < register new user > - HttpStatus.204");
            return "User %s has been successfully registered".formatted(user.getUsername());
        }
        log.info("Receive response from BackendService: < register new user > - Error: %s".formatted(responseEntity.getBody().toString()));
        Error error = responseEntity.getBody();
        return "Server error: %s. Type: %s".formatted(error.getMessage(), error.getType());
    }

    public String findUserByTgId(long tgUserId) {
        log.info("Create request to BackendService: < find user by tgUserId >");
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(URL+ "/" + tgUserId, String.class);
            return buildResponseToFindUserByTgId(tgUserId, responseEntity);
        } catch (JsonMappingException e) {
            log.error(e.toString());
        } catch (RestClientException | JsonProcessingException exception) {
            log.error(exception.toString());
        }
        log.error("BackendService connection problems");
        return "Sorry, server error or connection problem";
    }

    private String buildResponseToFindUserByTgId(long tgUserId, ResponseEntity<String> responseEntity) throws JsonProcessingException {
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("Receive response from BackendService: < find user by tgUserId > - User is registered: %s"
                    .formatted(responseEntity.getBody()));
            return "User %s is registered".formatted(usersList.getUserByTgUserId(tgUserId).get().getUsername());
        }
        ObjectMapper mapper = new ObjectMapper();
        Error error = mapper.readValue(responseEntity.getBody(), Error.class);
        log.info("Receive response from BackendService: < find user by tgUserId > - Error %s".formatted(error.toString()));
        return "Server error: %s. Type: %s".formatted(error.getMessage(), error.getType());
    }
}
