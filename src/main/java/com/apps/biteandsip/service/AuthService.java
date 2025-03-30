package com.apps.biteandsip.service;

import com.apps.biteandsip.dto.EmployeeDTO;
import com.apps.biteandsip.dto.LoginRequestDTO;
import com.apps.biteandsip.dto.RegisterRequestDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface AuthService {
    ResponseMessage createUser(RegisterRequestDTO registerRequestDTO);
    ResponseMessage authenticateUser(LoginRequestDTO loginRequestDTO);
    ResponseMessage getUserById(Long id);
    ResponseMessage getUserByUsername(String username);
    ResponseMessage getUsers();
    ResponseMessage deleteUserById(Long id);
    ResponseMessage updateUser(Map<String, Object> userArgs);

    ResponseMessage createEmployee(RegisterRequestDTO registerRequestDTO);
    ResponseMessage getEmployeeById(Long id);
    ResponseMessage updateEmployee(Long id, EmployeeDTO employeeDTO);
}
