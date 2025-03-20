package com.apps.biteandsip.service;

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
    User getUserById(Long id);
    User getUserByUsername(String username);
    List<User> getUsers();
    boolean deleteUserById(Long id);
    User updateUser(Map<String, Object> userArgs);
}
