package com.apps.biteandsip.dao;

import com.apps.biteandsip.model.Coupon;
import com.apps.biteandsip.model.FoodCategory;
import com.apps.biteandsip.model.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Page<Coupon> findByCodeContainingIgnoreCase(String val, Pageable pageable);

    Optional<Coupon> findByCode(String code);
}