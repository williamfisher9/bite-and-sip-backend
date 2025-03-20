package com.apps.biteandsip.service;

import com.apps.biteandsip.dto.LoginRequest;
import com.apps.biteandsip.dto.RegisterRequest;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface AuthService {
    ResponseMessage createUser(RegisterRequest registerRequest);
    ResponseMessage authenticateUser(LoginRequest loginRequest);
    User getUserById(Long id);
    User getUserByUsername(String username);
    List<User> getUsers();
    boolean deleteUserById(Long id);
    User updateUser(Map<String, Object> userArgs);
}
