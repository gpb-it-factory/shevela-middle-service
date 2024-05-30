package gpb.itfactory.shevelamiddleservice.controller;


import gpb.itfactory.shevelamiddleservice.entity.Error;
import gpb.itfactory.shevelamiddleservice.dto.client.UserDto;
import gpb.itfactory.shevelamiddleservice.mapper.CreateUserMapper;
import gpb.itfactory.shevelamiddleservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
public class MockBackendController {

    private final boolean CREATE_USER_FLAG = true;
    private final boolean FIND_USER_FLAG = true;
    private final String USER_ID_JSON = """
            {
              \"userId\": \"%s\"
            }
            """.formatted(UUID.randomUUID());
    private final String ERROR_JSON = """ 
            {
            \"message\": \"Произошло что-то ужасное, но станет лучше, честно\",
            \"type\": \"GeneralError\",
            \"code\": \"123\",
            \"trace_id\": \"%s\"
            }
            """.formatted(UUID.randomUUID());

    @PostMapping()
    public ResponseEntity<Error> create(@RequestBody UserDto userDto) {
        log.info("* Backend Service * Receive request from Middle Service: < register new user >");
        if (CREATE_USER_FLAG) {
            log.info("* Backend Service * Send response to Middle Service: < register new user > - HttpStatus.204");
            return ResponseEntity.noContent().build();
        }
        log.info("* Backend Service * Send response to Middle Service: < register new user > - Error");
        return ResponseEntity.of(Optional.of(createError()));
    }

    @GetMapping("/{tgUserId}")
    public ResponseEntity<String> findByTgId(@PathVariable long tgUserId) {
        log.info("* Backend Service * Receive request from Middle Service: < find user by tgUserId >");
        if (FIND_USER_FLAG) {
            log.info("* Backend Service * Send response to Middle Service: < find user by tgUserId > - HttpStatus.200");
            return ResponseEntity.status(HttpStatus.OK).body(USER_ID_JSON);
        }
        log.info("* Backend Service * Send response to Middle Service: < find user by tgUserId > - Error");
        return ResponseEntity.status(606).body(ERROR_JSON); // !!!
    }

    private Error createError() {
        return Error.builder()
                .message("Произошло что-то ужасное, но станет лучше, честно")
                .type("GeneralError")
                .code("123")
                .traceid(UUID.randomUUID().toString())
                .build();
    }
}
