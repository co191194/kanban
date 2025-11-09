package com.co1119.kanban_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.co1119.kanban.dto.request.RegisterRequest;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.repository.UserRepository;
import com.co1119.kanban.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // MockitoExtension will initialize mocks
    }

    @Test
    void registerUser_success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("a@b.com");
        req.setPassword("plain");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        User created = userService.registerUser(req);

        assertEquals(10L, created.getId());
        assertEquals("a@b.com", created.getEmail());
        assertEquals("hashed", created.getPassword());
    }

    @Test
    void registerUser_emailTaken_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("a@b.com");
        req.setPassword("plain");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(new User("a@b.com", "x")));

        assertThrows(RuntimeException.class, () -> userService.registerUser(req));
    }
}
