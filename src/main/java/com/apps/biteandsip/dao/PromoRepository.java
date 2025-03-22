package com.apps.biteandsip.dao;

import com.apps.biteandsip.model.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCode(String code);
}
