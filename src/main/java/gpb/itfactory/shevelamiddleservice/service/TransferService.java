package gpb.itfactory.shevelamiddleservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpb.itfactory.shevelamiddleservice.client.ClientManager;
import gpb.itfactory.shevelamiddleservice.dto.TelegramTransferDto;
import gpb.itfactory.shevelamiddleservice.dto.TransferDto;
import gpb.itfactory.shevelamiddleservice.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TransferService {

    private final ClientManager<RestClient> restClientManager;
    private final UserService userService;
    private final AccountService accountService;

    public TransferService(ClientManager<RestClient> restClientManager, UserService userService, AccountService accountService) {
        this.restClientManager = restClientManager;
        this.userService = userService;
        this.accountService = accountService;
    }

    public ResponseEntity<String> createTransferV2(TelegramTransferDto telegramTransferDto) {
        log.info("Create request to BackendService: < create transfer >");
        try {
            long tgUserIdFrom = telegramTransferDto.getFromId();
            String tgUsernameTo = telegramTransferDto.getTo();
            float amount = telegramTransferDto.getAmmount();
            log.info("Create request to BackendService: < get recipient user by Telegram username >");
            ResponseEntity<String> getUserByTelegramUsernameV2Response = userService.getUserByTelegramUsernameV2(tgUsernameTo);
            if (getUserByTelegramUsernameV2Response.getStatusCode() == HttpStatus.OK) {
                log.info("Create request to BackendService: < get sender user account by Telegram id >");
                ResponseEntity<String> getUserAccountsV2Response = accountService.getUserAccountsV2(tgUserIdFrom);
                if (getUserAccountsV2Response.getStatusCode() == HttpStatus.OK) {
                    return buildResponse(telegramTransferDto, getUserAccountsV2Response, amount);
                }
                return getUserAccountsV2Response;
            }
            return getUserByTelegramUsernameV2Response;
        } catch (RestClientException exception) {
            log.error("RestClientException exception: " + exception);
            throw exception;
        } catch (JsonProcessingException exception) {
            log.error(exception.toString());
            return ResponseEntity.status(502).body(createErrorJSON(
                    "Backend server unknown or connection error when create transfer", "createTransferV2Error", "310"));
        }
    }

    private ResponseEntity<String> buildResponse(TelegramTransferDto telegramTransferDto, ResponseEntity<String> getUserAccountsV2Response, float amount) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Account> accounts = objectMapper.readValue(getUserAccountsV2Response.getBody(), new TypeReference<>() {});
        log.info("Receive response from BackendService: < get accounts > - Accounts: %s".formatted(accounts.toString()));
        float balance = accounts.get(0).getAmount();
        if (balance < amount) {
            log.info("* Middle Service * Send response to telegram bot: < create account > Error << Not enough money in the account >>");
            return ResponseEntity.status(400).body(createErrorJSON(
                    "Error << Not enough money in the account >>", "createTransferV2Error", "311"));
        }
        log.info("Create request to BackendService: < create transfer >");
        ResponseEntity<String> responseEntity = restClientManager.getClient()
                .post().uri("/transfers").body(mapToTransferDto(telegramTransferDto)).retrieve().toEntity(String.class);
        log.info("Receive response from BackendService: < create transfer > - HttpStatus.200 Transfer has been successfully completed: %s"
                .formatted(responseEntity.getBody()));
        return ResponseEntity.status(responseEntity.getStatusCode())
                .body("{\"message\": \"Transfer has been successfully completed\"}");
    }

    private TransferDto mapToTransferDto(TelegramTransferDto telegramTransferDto) {
        return TransferDto.builder()
                .to(telegramTransferDto.getTo())
                .from(telegramTransferDto.getFrom())
                .amount(telegramTransferDto.getAmmount())
                .build();
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
