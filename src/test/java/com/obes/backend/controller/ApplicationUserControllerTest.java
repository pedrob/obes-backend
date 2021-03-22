package com.obes.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.obes.backend.model.ApplicationUser;
import com.obes.backend.repository.ApplicationUserRepository;
import com.obes.backend.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationUserControllerTest {

    @Autowired
	private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @MockBean
    private ApplicationUserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @BeforeEach
    public void setUp() { 
        ApplicationUser user = new ApplicationUser();
        user.setUsername("user");
        user.setPassword(bCryptPasswordEncoder.encode("password"));
        Optional<ApplicationUser> userOptional = Optional.of(user);

        when(userRepository.findByUsername(eq("user")))
        .thenReturn(userOptional);
    }

    //TODO: Mock the controller validations

    @Test
    public void shouldLoginSuccessfully() throws Exception {

        MvcResult result = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("username", "user");
                put("password", "password");
            }})))
            .andExpect(status().isOk())
            .andReturn();

        String receivedToken = result.getResponse().getContentAsString();
        assertEquals(tokenService.getUsernameFromToken(receivedToken), "user");
    }

    //TODO: Should add errors response

    @Test
    public void shouldFailToLoginWithBlankUsername() throws Exception {
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("username", "");
                put("password", "password");
            }})))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailToLoginWithNullUsername() throws Exception {
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("username", null);
                put("password", "password");
            }})))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailToLoginWithoutUsername() throws Exception {
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("password", "password");
            }})))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailToLoginWithWrongUsername() throws Exception {
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("username", "wrongUser");
                put("password", "password");
            }})))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailToLoginWithBlankPassword() throws Exception {
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("username", "user");
                put("password", "");
            }})))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailToLoginWithNullPassword() throws Exception {
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("username", "user");
                put("password", null);
            }})))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailToLoginWithoutPassword() throws Exception {
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("username", "user");
            }})))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailToLoginWithWrongPassword() throws Exception {
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("username", "user");
                put("password", "wrongPassword");
            }})))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldCreateAUserSuccessfully() throws Exception {
        mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                put("username", "user2");
                put("password", "password2");
            }})))
            .andExpect(status().isCreated());
    }

}