package gpb.itfactory.shevelamiddleservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDto {
    private String message;
    private String type;
    private String code;
    @JsonProperty("trace_id")
    private String traceId;
}
