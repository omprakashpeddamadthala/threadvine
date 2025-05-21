package com.threadvine.records;

import com.threadvine.model.User;

public record RegisterRequest(String email,
                              String password,
                              User.Role role) {
}
