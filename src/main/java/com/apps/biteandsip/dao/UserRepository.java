package com.apps.biteandsip.dao;

import com.apps.biteandsip.model.Coupon;
import com.apps.biteandsip.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String val);

    @Query(value = "SELECT * FROM users m WHERE m.username LIKE %:username% and m.user_type = :userType", nativeQuery = true)
    Page<User> searchByUsernameAndUserType(String username, String userType, Pageable pageable);

    @Query(value = "SELECT * FROM users m WHERE m.username LIKE %:username% and m.user_type != :userType", nativeQuery = true)
    List<User> searchByUsernameAndUserTypeNot(String username, String userType);

    Page<User> findByUsernameContainingIgnoreCaseAndAndUserTypeNotIn(String username, List<String> types, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update users m set m.username = :username, m.first_name = :firstName, m.last_name = :lastName WHERE m.id = :id", nativeQuery = true)
    void updateUser(Long id, String firstName, String lastName, String username);

    @Modifying
    @Transactional
    @Query(value = "update users m set m.username = :username, m.first_name = :firstName, m.last_name = :lastName WHERE m.id = :id", nativeQuery = true)
    void updateUserAuthorities(Long id, String firstName, String lastName, String username);


    Page<User> findByUserType(String type, Pageable pageable);

    Page<User> findByUserTypeNotIn(List<String> types, Pageable pageable);
}
