package com.apps.biteandsip.dao;

import com.apps.biteandsip.model.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
    Page<FoodCategory> findByNameContainingIgnoreCase(String title, Pageable pageable);

    @Query(value = "SELECT MAX(sorting_order) FROM categories", nativeQuery = true)
    Optional<Integer> findTopSortingOrder();
}
