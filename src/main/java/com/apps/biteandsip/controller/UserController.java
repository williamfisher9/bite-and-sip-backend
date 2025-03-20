package com.apps.biteandsip.controller;

import com.apps.biteandsip.dto.LoginRequest;
import com.apps.biteandsip.dto.RegisterRequest;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.model.User;
import com.apps.biteandsip.security.JwtUtils;
import com.apps.biteandsip.service.AuthService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class UserController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserController(AuthService authService, PasswordEncoder passwordEncoder, ModelMapper modelMapper, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @RequestMapping(value = "/public/auth/register", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> createUser(@RequestBody @Valid RegisterRequest registerRequest){
        ResponseMessage responseMessage = authService.createUser(registerRequest);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @RequestMapping(value = "/public/auth/login", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
        ResponseMessage responseMessage = authService.authenticateUser(loginRequest);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
