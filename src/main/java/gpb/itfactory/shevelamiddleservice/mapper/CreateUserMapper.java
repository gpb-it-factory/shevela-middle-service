package gpb.itfactory.shevelamiddleservice.mapper;

import gpb.itfactory.shevelamiddleservice.entity.User;
import gpb.itfactory.shevelamiddleservice.dto.client.UserDto;
import org.springframework.stereotype.Component;

@Component
public class CreateUserMapper {

    public UserDto map(User user) {
        return UserDto.builder()
                .tgUserId(user.getTgUserId())
                .build();
    }
}
