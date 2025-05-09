package com.apps.biteandsip.service;

import com.apps.biteandsip.dto.EmployeeDTO;
import com.apps.biteandsip.dto.LoginRequestDTO;
import com.apps.biteandsip.dto.RegisterRequestDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.model.User;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface AuthService {
    ResponseMessage createUser(RegisterRequestDTO registerRequestDTO) throws MessagingException;
    ResponseMessage authenticateUser(LoginRequestDTO loginRequestDTO);
    ResponseMessage getUserById(Long id);
    ResponseMessage getUserByUsername(String username);
    ResponseMessage getUsers();
    ResponseMessage deleteUserById(Long id);
    ResponseMessage updateUser(Map<String, Object> userArgs);

    ResponseMessage createEmployee(RegisterRequestDTO registerRequestDTO) throws MessagingException;
    ResponseMessage getEmployeeById(Long id);
    ResponseMessage updateEmployee(Long id, EmployeeDTO employeeDTO);

    ResponseMessage forgotPassword(String username) throws MessagingException;

    ResponseMessage resetPassword(String password, String token);

    ResponseMessage verifyUserAccount(String token) throws MessagingException;

    ResponseMessage getUserProfile(Long id);

    ResponseMessage updateUserProfile(Long id,
                                      String username,
                                      String firstName,
                                      String lastName,
                                      String password,
                                      String phoneNumber,
                                      String imageSource,
                                      MultipartFile file);

    ResponseMessage getCustomerById(Long id);
}
