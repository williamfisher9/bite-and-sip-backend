package com.apps.biteandsip.dao;

import com.apps.biteandsip.model.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    Page<FoodItem> findByNameContainingIgnoreCase(String title, Pageable pageable);
    List<FoodItem> findByCategoryId(Long id);

    @Query(value = "SELECT MAX(sorting_order) FROM food_items", nativeQuery = true)
    Optional<Integer> findTopSortingOrder();
}
