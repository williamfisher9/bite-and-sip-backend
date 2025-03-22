package com.apps.biteandsip.service.impl;

import com.apps.biteandsip.dao.AuthorityRepository;
import com.apps.biteandsip.dao.MenuRepository;
import com.apps.biteandsip.dao.UserRepository;
import com.apps.biteandsip.dto.LoginRequestDTO;
import com.apps.biteandsip.dto.RegisterRequestDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.exceptions.DuplicateUsernameException;
import com.apps.biteandsip.exceptions.RoleNotFoundException;
import com.apps.biteandsip.model.Authority;
import com.apps.biteandsip.model.Menu;
import com.apps.biteandsip.model.User;
import com.apps.biteandsip.security.JwtUtils;
import com.apps.biteandsip.service.AuthService;
import com.apps.biteandsip.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ModelMapper modelMapper;
    private final AuthorityRepository authorityRepository;
    private final EmailService emailService;
    private final MenuRepository menuRepository;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, ModelMapper modelMapper, AuthorityRepository authorityRepository, EmailService emailService, MenuRepository menuRepository){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.modelMapper = modelMapper;
        this.authorityRepository = authorityRepository;
        this.emailService = emailService;
        this.menuRepository = menuRepository;
    }

    @Override
    public ResponseMessage createUser(RegisterRequestDTO registerRequestDTO) {
        registerRequestDTO.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        User user = modelMapper.map(registerRequestDTO, User.class);

        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new DuplicateUsernameException("Username already exists in the system");
        }

        user.setUserCreationDate(LocalDateTime.now());
        user.setLastUpdateDate(LocalDateTime.now());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        Authority authority = authorityRepository.findByAuthority("ROLE_ADMIN")
                .orElseThrow(() -> new RoleNotFoundException("Role was not found!"));

        user.setAuthorities(Set.of(authority));
        User savedUser = userRepository.save(user);

        emailService.sendSimpleMessage("hamza.hamdan@hotmail.com", "hello", "test123");

        return new ResponseMessage(savedUser, 201);
    }

    @Override
    public ResponseMessage authenticateUser(LoginRequestDTO loginRequestDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtUtils.generateToken(authentication));
            response.put("username", ((UserDetails) authentication.getPrincipal()).getUsername());
            response.put("userId", String.valueOf(((User) authentication.getPrincipal()).getId()));
            response.put("authorityId", String.valueOf(((User) authentication.getPrincipal()).getAuthorities().stream().map((item) -> ((Authority) item).getId()).toList().get(0)));
            response.put("targetUrl", generateTargetUrlFromAuthority(authentication));
            response.put("menuItems", getUserMenuItems(authentication));
            return new ResponseMessage(response, 200);
        } catch(AuthenticationException exc){
            return new ResponseMessage(exc.getMessage(), 404);
        }
    }

    private List<Menu> getUserMenuItems(Authentication authentication){
        return ((Authority)((User) authentication.getPrincipal()).getAuthorities().stream().toList().get(0))
                .getMenuItems().stream().toList();
    }

    private String generateTargetUrlFromAuthority(Authentication authentication){
        List<String> list = ((User) authentication.getPrincipal()).getAuthorities().stream().map((item) -> item.getAuthority()).toList();
        if(list.get(0).equalsIgnoreCase("ROLE_ADMIN")){
            return "/api/v1/app/admin";
        } else {
            return "/api/v1/app/customer";
        }
    }

    @Override
    public ResponseMessage getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(""));
        return new ResponseMessage(user, 200);
    }

    @Override
    public ResponseMessage getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));
        return new ResponseMessage(user, 200);
    }

    @Override
    public ResponseMessage getUsers() {
        return new ResponseMessage(userRepository.findAll(), 200);
    }

    @Override
    public ResponseMessage deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(""));
        userRepository.delete(user);
        return new ResponseMessage(true, 200);
    }

    @Override
    public ResponseMessage updateUser(Map<String, Object> userArgs) {
        return null;
    }
}
