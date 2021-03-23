package com.obes.backend.service;

import org.springframework.data.util.ReflectionUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.obes.backend.repository.ApplicationUserRepository;
import com.obes.backend.model.Address;
import com.obes.backend.model.ApplicationUser;
import com.obes.backend.model.CreditCard;

import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private ApplicationUserRepository applicationUserRepository;

    public UserDetailsServiceImpl(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ApplicationUser> applicationUser = applicationUserRepository.findByUsername(username);
        if (!applicationUser.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return new User(applicationUser.get().getUsername(), applicationUser.get().getPassword(), emptyList());
    }

    public ApplicationUser partialUpdate(Map<String, Object> jsonBody, ApplicationUser user) {
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