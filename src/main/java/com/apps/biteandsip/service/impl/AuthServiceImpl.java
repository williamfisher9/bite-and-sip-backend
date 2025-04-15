package com.apps.biteandsip.service.impl;

import com.apps.biteandsip.dao.*;
import com.apps.biteandsip.dto.*;
import com.apps.biteandsip.enums.UserTokenStatus;
import com.apps.biteandsip.enums.UserTokenType;
import com.apps.biteandsip.exceptions.*;
import com.apps.biteandsip.model.*;
import com.apps.biteandsip.security.JwtUtils;
import com.apps.biteandsip.service.AuthService;
import com.apps.biteandsip.service.EmailService;
import com.apps.biteandsip.service.StorageService;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.transaction.annotation.Transactional;
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
    private final UserTokenRepository userTokenRepository;
    private final SettingsRepository settingsRepository;

    @Value("${image.download.url}")
    private String imageDownloadUrl;

    @Value("${backend.url}")
    private String backendUrl;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, ModelMapper modelMapper, AuthorityRepository authorityRepository, EmailService emailService, JdbcTemplate jdbcTemplate, StorageService storageService, OrderRepository orderRepository, UserTokenRepository userTokenRepository, SettingsRepository settingsRepository){
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
        this.userTokenRepository = userTokenRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public ResponseMessage createUser(RegisterRequestDTO registerRequestDTO) throws MessagingException {
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
        user.setEnabled(false);
        user.setUserType(user.getUserType() == null || user.getUserType().equalsIgnoreCase("ADMIN") ?
                "CUSTOMER" : user.getUserType());

        Authority authority = authorityRepository.findByAuthority("ROLE_"+user.getUserType())
                .orElseThrow(() -> new RoleNotFoundException("Role was not found!"));

        user.setAuthorities(Set.of(authority));
        User savedUser = userRepository.save(user);

        UserToken userToken = new UserToken();
        userToken.setToken(UUID.randomUUID().toString());
        userToken.setUserId(savedUser.getId());
        userToken.setTokenExpirationDate(LocalDateTime.now().plusDays(1));
        userToken.setStatus(UserTokenStatus.NOT_USED);
        userToken.setType(UserTokenType.VERIFY_ACCOUNT_TOKEN);
        userTokenRepository.save(userToken);

        emailService.sendSimpleMessage(user.getUsername(),
                "Bite and Sip - Verify Your Email Address",
                String.format("<a href='%s%s%s'>Verify Account</a>", backendUrl, "/verify-account/", userToken.getToken()));

        return new ResponseMessage("user created successfully", 201);
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
            response.put("dashboardRefreshInterval", settingsRepository.findByParamName("DASHBOARD_AUTO_REFRESH_INTERVAL_IN_SECONDS").get().getParamValue());

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
    @Transactional
    public ResponseMessage createEmployee(RegisterRequestDTO registerRequestDTO) throws MessagingException {
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
        user.setEnabled(false);

        Authority authority = authorityRepository.findById(registerRequestDTO.getUserType())
                        .orElseThrow(() -> new RoleNotFoundException("Role was not found!"));

        user.setUserType(authority.getAuthority().substring(5));

        user.setAuthorities(Set.of(authority));
        User savedUser = userRepository.save(user);

        UserToken userToken = new UserToken();
        userToken.setToken(UUID.randomUUID().toString());
        userToken.setUserId(savedUser.getId());
        userToken.setTokenExpirationDate(LocalDateTime.now().plusDays(1));
        userToken.setStatus(UserTokenStatus.NOT_USED);
        userToken.setType(UserTokenType.VERIFY_ACCOUNT_AND_SET_PASSWORD_TOKEN);
        userTokenRepository.save(userToken);

        emailService.sendSimpleMessage(user.getUsername(),
                "Bite and Sip - Verify Your Email Address and Set Your Password",
                String.format("<a href='%s%s%s'>Verify Account and Set Password</a>", backendUrl, "/verify-account-and-set-password/", userToken.getToken()));


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
    public ResponseMessage forgotPassword(String username) throws MessagingException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username was not found"));
        UserToken userToken = new UserToken();
        userToken.setToken(UUID.randomUUID().toString());
        userToken.setUserId(user.getId());
        userToken.setTokenExpirationDate(LocalDateTime.now().plusDays(1));
        userToken.setStatus(UserTokenStatus.NOT_USED);
        userToken.setType(UserTokenType.PASSWORD_RESET_TOKEN);
        userTokenRepository.save(userToken);

        emailService.sendSimpleMessage(username, "Bite and Sip - Reset Your Password",
                String.format("<a href='%s%s%s'>Reset Password</a>", backendUrl, "/reset-password/", userToken.getToken()));
        return new ResponseMessage("CHECK YOUR INBOX FOR PASSWORD RESET EMAIL", 200);
    }

    @Override
    public ResponseMessage resetPassword(String password, String token) {
        UserToken retrievedToken = userTokenRepository.findByToken(token)
                .orElseThrow(() -> new UsernameNotFoundException(""));

        if(retrievedToken.getStatus() == UserTokenStatus.USED){
            throw new UserTokenStatusException("Token was used before");
        }

        if(retrievedToken.getType() != UserTokenType.PASSWORD_RESET_TOKEN){
            throw new UserTokenTypeException("Token type mismatch");
        }

        if(!isTokenNonExpired(retrievedToken)) {
            throw new UserTokenExpired("Token expired");
        }

        User user = userRepository.findById(retrievedToken.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("username was not found"));

        user.setPassword(passwordEncoder.encode(password));
        user.setLastUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        retrievedToken.setStatus(UserTokenStatus.USED);
        userTokenRepository.save(retrievedToken);

        return new ResponseMessage("LOGIN WITH THE NEW PASSWORD", 200);
    }

    private boolean isTokenNonExpired(UserToken retrievedToken) {
        if(retrievedToken.getTokenExpirationDate().isAfter(LocalDateTime.now()))
            return true;

        return false;
    }





    @Override
    public ResponseMessage verifyUserAccount(String token) throws MessagingException {
        UserToken retrievedToken = userTokenRepository.findByToken(token)
                .orElseThrow(() -> new UsernameNotFoundException(""));

        if(retrievedToken.getStatus() == UserTokenStatus.USED){
            throw new UserTokenStatusException("Token was used for verification");
        }

        if(!retrievedToken.getType().equals(UserTokenType.VERIFY_ACCOUNT_TOKEN)
        && !retrievedToken.getType().equals(UserTokenType.VERIFY_ACCOUNT_AND_SET_PASSWORD_TOKEN)){
            throw new UserTokenTypeException("Token type mismatch");
        }

        User user = userRepository.findById(retrievedToken.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("username was not found"));

        user.setEnabled(true);
        user.setLastUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        retrievedToken.setStatus(UserTokenStatus.USED);
        userTokenRepository.save(retrievedToken);

        if(retrievedToken.getType() == UserTokenType.VERIFY_ACCOUNT_AND_SET_PASSWORD_TOKEN ){
            UserToken userToken = new UserToken();
            userToken.setToken(UUID.randomUUID().toString());
            userToken.setUserId(user.getId());
            userToken.setTokenExpirationDate(LocalDateTime.now().plusDays(1));
            userToken.setStatus(UserTokenStatus.NOT_USED);
            userToken.setType(UserTokenType.PASSWORD_RESET_TOKEN);
            userTokenRepository.save(userToken);

            emailService.sendSimpleMessage(user.getUsername(), "Bite and Sip - Reset Your Password",
                    String.format("<a href='%s%s%s'>Reset Password</a>", backendUrl, "/reset-password/", userToken.getToken()));
            return new ResponseMessage("CHECK YOUR INBOX FOR PASSWORD RESET EMAIL", 200);
        }

        return new ResponseMessage("ACCOUNT VERIFIED", 200);
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
