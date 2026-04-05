package com.inventory.service;

import com.inventory.dao.UserDAO;
import com.inventory.model.User;
import com.inventory.util.PasswordUtil;
import com.inventory.util.ValidationUtil;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this(new UserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String username, String password) {
        ValidationUtil.requireNonBlank(username, "Username");
        ValidationUtil.requireNonBlank(password, "Password");

        User user = userDAO.getUserByUsername(username);
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
