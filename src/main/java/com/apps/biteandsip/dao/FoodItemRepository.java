package com.apps.biteandsip.dao;

import com.apps.biteandsip.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByNameContainingIgnoreCase(String title);
    List<FoodItem> findByCategoryId(Long id);
}
