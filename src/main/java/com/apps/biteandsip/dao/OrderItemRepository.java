package com.apps.biteandsip.dao;

import com.apps.biteandsip.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query(value = "select a.item_id, b.name, sum(a.quantity) as sum from order_items a, food_items b where a.item_id = b.id group by item_id order by sum desc", nativeQuery = true)
    List<Object[]> getTopSellingItems();
}
