package gpb.itfactory.shevelamiddleservice.controller;


import gpb.itfactory.shevelamiddleservice.dto.TelegramTransferDto;
import gpb.itfactory.shevelamiddleservice.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v2/middle")
public class TransferController {

    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfers")
    public ResponseEntity<String> createTransferV2(@RequestBody TelegramTransferDto telegramTransferDto) {
        log.info("Receive request from TelegramBot: < create transfer >");
        try {
            return transferService.createTransferV2(telegramTransferDto);
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            log.info("Receive response from BackendService: < create transfer >  %s".formatted(exception.toString()));
            return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAs(String.class));
        } catch (RestClientException exception) {
            log.error(exception.toString());
            return ResponseEntity.status(502).body(createErrorJSON(
                    "Backend server unknown or connection error when create transfer", "createTransferV2Error", "310"));
        }
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
