package gpb.itfactory.shevelamiddleservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Error {
    private String message;
    private String type;
    private String code;
    @JsonProperty("trace_id")
    private String traceid;
}
