package com.library.service;

import com.library.entity.User;
import java.util.List;

public interface UserService {
    User addUser(User user);
    User findUserById(Long id);
    List<User> findAllUsers();
    void deleteUser(Long id);
}