package gpb.itfactory.shevelamiddleservice.entity;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Getter
public class UsersList {
    private final List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findAny();
    }
    public Optional<User> getUserByTgUserId(long tgUserId) {
        return users.stream().filter(user -> user.getTgUserId().equals(tgUserId)).findAny();
    }

}
