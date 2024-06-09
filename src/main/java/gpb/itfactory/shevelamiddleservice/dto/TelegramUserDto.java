package gpb.itfactory.shevelamiddleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TelegramUserDto {
    public String username;
    private Long tgUserId;
}
