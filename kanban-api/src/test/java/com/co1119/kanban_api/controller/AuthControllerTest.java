package com.co1119.kanban_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.co1119.kanban.controller.AuthController;
import com.co1119.kanban.dto.request.LoginRequest;
import com.co1119.kanban.dto.request.RegisterRequest;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.service.KanbanUserDetailsService;
import com.co1119.kanban.service.UserService;
import com.co1119.kanban.utility.JwtTokenUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtility jwtTokenUtility;

    @Mock
    private KanbanUserDetailsService kanbanUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthController controller = new AuthController(userService, authenticationManager, jwtTokenUtility,
                kanbanUserDetailsService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void registerUser_success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("password");

        User user = new User("test@example.com", "hashed");
        user.setId(1L);

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processResult").value("SUCCESS"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.userEmail").value("test@example.com"));
    }

    @Test
    void registerUser_failure() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("password");

        when(userService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Email already taken"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.processResult").value("BAD_REQUEST"));
    }

    @Test
    void login_success() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("password");

        // authenticationManager.authenticate should not throw
        when(authenticationManager.authenticate(any())).thenReturn(null);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .roles("USER")
                .build();

        when(kanbanUserDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtTokenUtility.generateToken(userDetails)).thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processResult").value("SUCCESS"))
                .andExpect(jsonPath("$.accessToken").value("dummy-jwt-token"))
                .andExpect(jsonPath("$.tokenType").value(org.hamcrest.Matchers.notNullValue()));
    }

    @Test
    void login_failure_bad_credentials() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("wrong-password");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad creds"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

}
