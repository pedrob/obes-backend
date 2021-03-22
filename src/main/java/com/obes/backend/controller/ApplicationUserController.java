package com.obes.backend.controller;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.http.HttpStatus;

import com.obes.backend.repository.ApplicationUserRepository;
import com.obes.backend.exception.BadRequestException;
import com.obes.backend.exception.NotFoundException;
import com.obes.backend.exception.UsernameAlreadyInUseException;
import com.obes.backend.model.Address;
import com.obes.backend.model.ApplicationUser;
import com.obes.backend.model.CreditCard;

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

    @GetMapping("/users/{username}")
    public ApplicationUser getUser(@PathVariable String username) {
        return userRepository.findByUsername(username).map(user -> {
            return user;
        }).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @PatchMapping("users/{username}")
    public ApplicationUser partialUpdateUser(@PathVariable String username, @RequestBody Map<String, Object> jsonBody) {
        return userRepository.findByUsername(username).map(user -> {
            ApplicationUser userUpdated = partialUpdateValues(jsonBody, user);
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

    private ApplicationUser partialUpdateValues(Map<String, Object> jsonBody, ApplicationUser user) {
        ApplicationUser userUpdated = (ApplicationUser) setValues(jsonBody, user);
        return userUpdated;
    }

    private <T> Object setValues(Map<String, Object> jsonBody, Object object) {
        jsonBody.forEach((key, value) -> {
            if (key.equals("address")) {
                ApplicationUser user = (ApplicationUser) object;
                Address address = (Address) setValues((Map<String, Object>)value, user.getAddress());
                user.setAddress(address);
            }
            else if (key.equals("creditCard")) {
                ApplicationUser user = (ApplicationUser) object;
                CreditCard creditCard = (CreditCard) setValues((Map<String, Object>)value, user.getCreditCard());
                user.setCreditCard(creditCard);
            } 
            else {
                Field field = ReflectionUtils.findRequiredField(object.getClass(), key); 
                field.setAccessible(true); 
                ReflectionUtils.setField(field, object, value);
            }
        });
        return object;
    }

    
}