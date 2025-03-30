package com.apps.biteandsip.service.impl;

import com.apps.biteandsip.dao.AuthorityRepository;
import com.apps.biteandsip.dao.MenuRepository;
import com.apps.biteandsip.dao.UserRepository;
import com.apps.biteandsip.dto.*;
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
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, ModelMapper modelMapper, AuthorityRepository authorityRepository, EmailService emailService, MenuRepository menuRepository, JdbcTemplate jdbcTemplate){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.modelMapper = modelMapper;
        this.authorityRepository = authorityRepository;
        this.emailService = emailService;
        this.menuRepository = menuRepository;
        this.jdbcTemplate = jdbcTemplate;
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
        user.setUserType(user.getUserType() == null || user.getUserType().equalsIgnoreCase("ADMIN") ?
                "CUSTOMER" : user.getUserType());

        Authority authority = authorityRepository.findByAuthority("ROLE_"+user.getUserType())
                .orElseThrow(() -> new RoleNotFoundException("Role was not found!"));

        user.setAuthorities(Set.of(authority));
        User savedUser = userRepository.save(user);

        emailService.sendSimpleMessage(user.getUsername(), "verification email", "link");

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


    @Override
    public ResponseMessage createEmployee(RegisterRequestDTO registerRequestDTO) {
        if(userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()){
            throw new DuplicateUsernameException("Username already exists in the system");
        }

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setFirstName(registerRequestDTO.getFirstName());
        user.setLastName(registerRequestDTO.getLastName());
        user.setUserCreationDate(LocalDateTime.now());
        user.setLastUpdateDate(LocalDateTime.now());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        Authority authority = authorityRepository.findById(registerRequestDTO.getUserType())
                        .orElseThrow(() -> new RoleNotFoundException("Role was not found!"));

        user.setUserType(authority.getAuthority().substring(5));

        user.setAuthorities(Set.of(authority));
        User savedUser = userRepository.save(user);

        emailService.sendSimpleMessage(user.getUsername(), "verification email", "link");

        return new ResponseMessage(savedUser, 201);
    }

    @Override
    public ResponseMessage getEmployeeById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("user was not found"));

        if(user.getUserType().equalsIgnoreCase("CUSTOMER")){
            throw new UsernameNotFoundException("user was not found");
        }

        List<Authority> authorities = authorityRepository.findAll().stream()
                .filter(item -> !item.getAuthority().equalsIgnoreCase("ROLE_CUSTOMER") && !item.getAuthority().equalsIgnoreCase("ROLE_ADMIN")).toList();

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(user.getId());
        employeeDTO.setUsername(user.getUsername());
        employeeDTO.setFirstName(user.getFirstName());
        employeeDTO.setLastName(user.getLastName());
        employeeDTO.setRoleId(((Authority) user.getAuthorities().stream().toList().get(0)).getId());
        employeeDTO.setRoles(authorities);
        employeeDTO.setUserType(user.getUserType());

        return new ResponseMessage(employeeDTO, 200);
    }

    @Override
    public ResponseMessage updateEmployee(Long id, EmployeeDTO employeeDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("user was not found"));

        user.setUsername(employeeDTO.getUsername());
        user.setFirstName(employeeDTO.getFirstName());
        user.setLastName(employeeDTO.getLastName());
        user.setAuthorities(Set.of(authorityRepository.findById(employeeDTO.getRoleId()).get()));

        System.out.println(user);

        String sql1 = """
           UPDATE users SET username = ?, first_name = ?, last_name = ?, user_type = ? WHERE id = ?;
           """;

        jdbcTemplate.update(sql1, user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                authorityRepository.findById(employeeDTO.getRoleId()).get().getAuthority().substring(5),
                user.getId());

        String sql2 = "UPDATE user_authorities SET authority_id = ? WHERE user_id = ?;";

        jdbcTemplate.update(sql2,
                authorityRepository.findById(employeeDTO.getRoleId()).get().getId(),
                user.getId());

        return new ResponseMessage("User updated successfully", 200);
    }
}
