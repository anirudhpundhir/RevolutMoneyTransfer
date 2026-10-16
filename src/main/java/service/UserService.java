package service;

import entity.Account;
import entity.User;
import entity.dto.UserDTO;

import java.util.Collection;
import java.util.List;

public interface UserService {

    /** Creates a user together with an empty default account. */
    User addUser(UserDTO user);

    Collection<User> getUsers();

    /** @throws exception.NotFoundException if no such user */
    User getUser(String id);

    List<Account> getAccounts(String userId);

    Account openAccount(String userId, String name, entity.Money initialFunds);

    User editUser(String id, UserDTO changes);

    void deleteUser(String id);

    boolean userExist(String id);
}
