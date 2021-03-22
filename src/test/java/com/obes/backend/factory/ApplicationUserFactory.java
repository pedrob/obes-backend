package com.obes.backend.factory;

import com.obes.backend.model.ApplicationUser;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ApplicationUserFactory {

    private static int idGenerator = 0;

    public static ApplicationUser createUser(String username, String password) {
        idGenerator++;
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        return ApplicationUser.builder()
                .id(idGenerator)
                .username(username)
                .password(encodedPassword)
                .build();
    }
    
}
