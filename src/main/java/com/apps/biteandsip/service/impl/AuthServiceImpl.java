package com.apps.biteandsip.service.impl;

import com.apps.biteandsip.dao.*;
import com.apps.biteandsip.dto.*;
import com.apps.biteandsip.exceptions.DuplicateUsernameException;
import com.apps.biteandsip.exceptions.RoleNotFoundException;
import com.apps.biteandsip.model.*;
import com.apps.biteandsip.security.JwtUtils;
import com.apps.biteandsip.service.AuthService;
import com.apps.biteandsip.service.EmailService;
import com.apps.biteandsip.service.StorageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final JdbcTemplate jdbcTemplate;
    private final StorageService storageService;
    private final OrderRepository orderRepository;
    private final PasswordTokenRepository passwordTokenRepository;

    @Value("${image.download.url}")
    private String imageDownloadUrl;

    @Value("${backend.url}")
    private String backendUrl;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, ModelMapper modelMapper, AuthorityRepository authorityRepository, EmailService emailService, JdbcTemplate jdbcTemplate, StorageService storageService, OrderRepository orderRepository, PasswordTokenRepository passwordTokenRepository){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.modelMapper = modelMapper;
        this.authorityRepository = authorityRepository;
        this.emailService = emailService;
        this.jdbcTemplate = jdbcTemplate;
        this.storageService = storageService;
        this.orderRepository = orderRepository;
        this.passwordTokenRepository = passwordTokenRepository;
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

            if(String.valueOf(((User) authentication.getPrincipal()).getId()).equalsIgnoreCase("1"))
                response.put("homePageUrl", "/biteandsip/admin/dashboard");
            else
                response.put("homePageUrl", "/biteandsip/home");

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
        user.setPhoneNumber(registerRequestDTO.getPhoneNumber());
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
        employeeDTO.setActive(user.isEnabled());
        employeeDTO.setFirstName(user.getFirstName());
        employeeDTO.setLastName(user.getLastName());
        employeeDTO.setPhoneNumber(user.getPhoneNumber());
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
        user.setPhoneNumber(employeeDTO.getPhoneNumber());
        user.setAuthorities(Set.of(authorityRepository.findById(employeeDTO.getRoleId()).get()));
        user.setEnabled(employeeDTO.isActive());

        String sql1 = """
           UPDATE users SET username = ?, first_name = ?, last_name = ?, user_type = ?, phone_number = ?, is_enabled = ? WHERE id = ?;
           """;

        jdbcTemplate.update(sql1, user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                authorityRepository.findById(employeeDTO.getRoleId()).get().getAuthority().substring(5),
                user.getPhoneNumber(),
                user.isEnabled(),
                user.getId());

        String sql2 = "UPDATE user_authorities SET authority_id = ? WHERE user_id = ?;";

        jdbcTemplate.update(sql2,
                authorityRepository.findById(employeeDTO.getRoleId()).get().getId(),
                user.getId());

        return new ResponseMessage("User updated successfully", 200);
    }

    @Override
    public ResponseMessage forgotPassword(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username was not found"));
        PasswordToken passwordToken = new PasswordToken();
        passwordToken.setToken(UUID.randomUUID().toString());
        passwordToken.setUserId(user.getId());
        passwordToken.setTokenExpirationDate(LocalDateTime.now().plusDays(1));
        passwordTokenRepository.save(passwordToken);

        emailService.sendSimpleMessage(username, "Bite and Sip - Reset Password",
                "<a href=" + backendUrl + "/api/v1/app/public/reset-password/"  + passwordToken.getToken()  + ">Reset Password</a>");
        return new ResponseMessage("CHECK YOUR INBOX FOR PASSWORD RESET EMAIL", 200);
    }

    @Override
    public ResponseMessage resetPassword(String passwordToken) {
        PasswordToken retrievedToken = passwordTokenRepository.findByToken(passwordToken)
                .orElseThrow(() -> new UsernameNotFoundException(""));

        if(isTokenNonExpired(retrievedToken)){
            return new ResponseMessage("proceed", 202);
        }

        return new ResponseMessage("not found", 404);
    }

    private boolean isTokenNonExpired(PasswordToken retrievedToken) {
        if(retrievedToken.getTokenExpirationDate().isAfter(LocalDateTime.now()))
            return true;

        return false;
    }


    @Override
    public ResponseMessage getUserProfile(Long id) {
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("username was not found"));
        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("phoneNumber", user.getPhoneNumber());

        if(user.getImageSource() != null){
            response.put("imageSource", imageDownloadUrl + user.getImageSource());
        } else {
            response.put("imageSource", "");
        }


        return new ResponseMessage(response, 200);
    }

    @Override
    public ResponseMessage updateUserProfile(Long id,
                                             String username,
                                             String firstName,
                                             String lastName,
                                             String password,
                                             String phoneNumber,
                                             String imageSource,
                                             MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("username was not found"));

        if(file != null){
            String fileName = storageService.store(file);
            user.setImageSource(fileName);
        }

        if(imageSource.isEmpty() && file == null){
            user.setImageSource(null);
        }

        if(!password.isEmpty()){
            user.setPassword(passwordEncoder.encode(password));
        }

        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);

        userRepository.save(user);

        return new ResponseMessage("user updated successfully", 200);
    }

    @Override
    public ResponseMessage getCustomerById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("user was not found"));

        if(!user.getUserType().equalsIgnoreCase("CUSTOMER")){
            throw new UsernameNotFoundException("user was not found");
        }

        List<Order> orders = orderRepository.findByCustomerId(id);

        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setUsername(user.getUsername());
        responseDTO.setFirstName(user.getFirstName());
        responseDTO.setLastName(user.getLastName());

        for(Order order : orders){
            for(OrderItem orderItem : order.getItems()){
                orderItem.getItem().setImageSource(
                        orderItem.getItem().getImageSource().startsWith("http") ?
                                orderItem.getItem().getImageSource() :
                                imageDownloadUrl + orderItem.getItem().getImageSource()
                );
            }
        }

        responseDTO.setOrders(orders);

        if(user.getImageSource() != null){
            responseDTO.setImageSource(imageDownloadUrl + user.getImageSource());
        } else {
            responseDTO.setImageSource("");
        }
        
        responseDTO.setPhoneNumber(user.getPhoneNumber());

        return new ResponseMessage(responseDTO, 200);
    }
}
