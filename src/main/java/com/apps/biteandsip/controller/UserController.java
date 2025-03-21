package com.apps.biteandsip.controller;

import com.apps.biteandsip.dto.LoginRequestDTO;
import com.apps.biteandsip.dto.RegisterRequestDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.security.JwtUtils;
import com.apps.biteandsip.service.AuthService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/app")
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
    public ResponseEntity<ResponseMessage> createUser(@RequestBody @Valid RegisterRequestDTO registerRequestDTO){
        ResponseMessage responseMessage = authService.createUser(registerRequestDTO);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/public/auth/login", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> authenticateUser(@RequestBody @Valid LoginRequestDTO loginRequestDTO){
        ResponseMessage responseMessage = authService.authenticateUser(loginRequestDTO);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }
}
