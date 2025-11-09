package com.co1119.kanban.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.co1119.kanban.dto.request.LoginRequest;
import com.co1119.kanban.dto.request.RegisterRequest;
import com.co1119.kanban.dto.response.AbstractKanbanResponse;
import com.co1119.kanban.dto.response.AuthResponse;
import com.co1119.kanban.dto.response.ErrorResponse;
import com.co1119.kanban.dto.response.RegisterResponse;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.service.KanbanUserDetailsService;
import com.co1119.kanban.service.UserService;
import com.co1119.kanban.utility.JwtTokenUtility;
import com.co1119.kanban.utility.ResponseUtility;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtility jwtTokenUtility;
    private final KanbanUserDetailsService kanbanUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<? extends AbstractKanbanResponse> registerUser(@RequestBody RegisterRequest request) {
        try {
            User registerUser = userService.registerUser(request);

            return ResponseUtility.createSuccessResponse(new RegisterResponse(registerUser));
        } catch (RuntimeException e) {
            return ResponseUtility.createBadRequestResponse(new ErrorResponse());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws Exception {

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("認証に失敗しました。");
        }

        final UserDetails userDetails = kanbanUserDetailsService.loadUserByUsername(request.getEmail());

        final String jwtToken = jwtTokenUtility.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

}
