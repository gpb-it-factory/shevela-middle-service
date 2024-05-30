package gpb.itfactory.shevelamiddleservice.mapper;

import gpb.itfactory.shevelamiddleservice.entity.User;
import gpb.itfactory.shevelamiddleservice.dto.controller.TelegramUserDto;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
public class UserMapper {

    private long tgUserId;

    public User map(TelegramUserDto telegramUserDto) {
        return User.builder()
                .username(telegramUserDto.getUsername())
                .tgUserId(getTgUserId())
                .build();
    }

    public long getTgUserId() {
        setTgUserId(tgUserId+1);
        return tgUserId;
    }
}
