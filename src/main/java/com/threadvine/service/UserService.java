package com.threadvine.service;

import com.threadvine.io.ChangePasswordRequest;
import com.threadvine.model.User;
import com.threadvine.records.RegisterRequest;

public interface UserService {

    User registerUser(RegisterRequest registerRequest);

    User getUserByEmail(String email);

    void changePassword(String email, ChangePasswordRequest changePasswordRequest);
}
