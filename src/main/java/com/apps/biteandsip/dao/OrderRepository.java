package com.apps.biteandsip.dao;

import com.apps.biteandsip.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerId(Long customerId);

    @Query(value = "update orders set status = :newStatus where uuid = :uuid", nativeQuery = true)
    int updateOrderStatus(@Param("status") String status, @Param("uuid") UUID uuid);

    @Query(value = "SELECT c.status, COUNT(c.status) FROM Order AS c GROUP BY c.status ORDER BY c.status DESC")
    List<Object[]> countOrdersByStatus();

    @Query(value = "SELECT sum(c.totalPrice) FROM Order AS c where c.status=DELIVERED")
    Object sumOfDeliveredOrders();
}
