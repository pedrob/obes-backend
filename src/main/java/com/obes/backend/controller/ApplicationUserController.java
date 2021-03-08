package com.obes.backend.controller;

import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.obes.backend.repository.ApplicationUserRepository;
import com.obes.backend.exception.UsernameAlreadyInUseException;
import com.obes.backend.model.ApplicationUser;

@RestController
public class ApplicationUserController {

    private ApplicationUserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApplicationUserController(ApplicationUserRepository userRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid ApplicationUser user) {
        validateUser(user);
        user.setCreatedAt(new Date());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    private void validateUser(ApplicationUser user) {
        Optional<ApplicationUser> foundedUser = userRepository.findByUsername(user.getUsername());
        if (foundedUser.isPresent()) {
            throw new UsernameAlreadyInUseException("Username already in use");
        }
    }

}