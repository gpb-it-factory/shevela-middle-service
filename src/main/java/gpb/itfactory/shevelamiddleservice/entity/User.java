package gpb.itfactory.shevelamiddleservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String username;
    private Long tgUserId;
    private List<Account> accounts;
}
