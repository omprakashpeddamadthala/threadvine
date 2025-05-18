package com.threadvine.service;

import com.threadvine.io.ChangePasswordRequest;
import com.threadvine.model.User;

public interface UserService {

    User registerUser(User user);

    User getUserByEmail(String email);

    void changePassword(String email, ChangePasswordRequest changePasswordRequest);
}
