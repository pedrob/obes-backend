package com.obes.backend.controller;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.obes.backend.repository.ApplicationUserRepository;
import com.obes.backend.service.UserDetailsServiceImpl;
import com.obes.backend.exception.BadRequestException;
import com.obes.backend.exception.NotFoundException;
import com.obes.backend.exception.UsernameAlreadyInUseException;
import com.obes.backend.model.ApplicationUser;

@RestController
public class ApplicationUserController {

    private ApplicationUserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserDetailsServiceImpl userService;

    @Autowired
    public ApplicationUserController(ApplicationUserRepository userRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsServiceImpl userService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@Valid @RequestBody ApplicationUser user) {
        validateUser(user);
        user.setCreatedAt(new Date());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @GetMapping("/users/{username}")
    public ApplicationUser getUser(@PathVariable String username) {
        return userRepository.findByUsername(username).map(user -> {
            return user;
        }).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @PatchMapping("users/{username}")
    public ApplicationUser partialUpdateUser(@PathVariable String username, 
    @Valid @RequestBody Map<String, Object> jsonBody) {
        return userRepository.findByUsername(username).map(user -> {
            ApplicationUser userUpdated = userService.partialUpdate(jsonBody, user);
            userRepository.save(userUpdated);
            return userUpdated;
        }).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void validateUser(ApplicationUser user) {
        if (user.getUsername().equals("") || user.getPassword().equals("")) {
            throw new BadRequestException("Username or password invalid");
        }
        Optional<ApplicationUser> foundedUser = userRepository.findByUsername(user.getUsername());
        if (foundedUser.isPresent()) {
            throw new UsernameAlreadyInUseException("Username already in use");
        }
    }


    
}