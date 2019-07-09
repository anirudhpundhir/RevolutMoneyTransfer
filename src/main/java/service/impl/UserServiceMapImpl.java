package service.impl;

import entity.User;
import exception.UserException;
import service.UserService;

import java.util.Collection;
import java.util.HashMap;

public class UserServiceMapImpl implements UserService {
    private HashMap<String, User> userMap;
    private static UserServiceMapImpl instance;

    private static int id = 0;

    public static UserServiceMapImpl getInstance() {
        if (instance == null) {
            instance = new UserServiceMapImpl();
        }
        return instance;
    }

    private UserServiceMapImpl() {
        userMap = new HashMap<>();
    }

    @Override
    public User addUser(User user) {
        user.setId(String.valueOf(++id));
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return userMap.values();
    }

    @Override
    public User getUser(String id) {
        return userMap.get(id);
    }

    @Override
    public User editUser(User forEdit) throws UserException {
        try {
            if (forEdit.getId() == null)
                throw new UserException("ID cannot be blank");

            User toEdit = userMap.get(forEdit.getId());

            if (toEdit == null)
                throw new UserException("User not found");

            if (forEdit.getEmail() != null) {
                toEdit.setEmail(forEdit.getEmail());
            }
            if (forEdit.getFirstName() != null) {
                toEdit.setFirstName(forEdit.getFirstName());
            }
            if (forEdit.getLastName() != null) {
                toEdit.setLastName(forEdit.getLastName());
            }
            if (forEdit.getId() != null) {
                toEdit.setId(forEdit.getId());
            }

            return toEdit;
        } catch (Exception ex) {
            throw new UserException(ex.getMessage());
        }
    }

    @Override
    public void deleteUser(String id) {
        userMap.remove(id);
    }

    @Override
    public boolean userExist(String id) {
        return userMap.containsKey(id);
    }

}