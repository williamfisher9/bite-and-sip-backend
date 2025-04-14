package com.apps.biteandsip.service;

import com.apps.biteandsip.dto.*;
import com.apps.biteandsip.model.Order;
import com.stripe.exception.StripeException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Service
public interface AppService {
    ResponseMessage createCategory(MultipartFile file, String name, boolean active);

    ResponseMessage createFoodItem(MultipartFile file, String name, boolean active, String price, String description, Long categoryId);

    ResponseMessage updateCategory(Long id ,String name, boolean active, MultipartFile file);
    //ResponseMessage createFoodItem(FoodItemDTO foodItem);
    ResponseMessage getParamValueByName(String name);
    ResponseMessage getFoodCategories();
    ResponseMessage getFoodCategoryById(Long id);
    //ResponseMessage searchFoodCategories(String val);
    //ResponseMessage searchFoodItems(String val);
    ResponseMessage getFoodItems();
    ResponseMessage getFoodItemById(Long id);
    ResponseMessage getFoodItemsByCategoryId(Long id);
    ResponseMessage updateFoodItem(Long id ,String name, boolean active, MultipartFile file, String price, String description, Long categoryId);

    ResponseMessage createPaymentIntent(StripePaymentIntentDTO intentDTO) throws StripeException;
    ResponseMessage confirmPaymentIntent(String id);

    ResponseMessage getCoupons();
    ResponseMessage createCoupon(CouponDTO couponDTO);
    ResponseMessage getCouponById(Long id);
    ResponseMessage getCouponByCode(String code);
    ResponseMessage updateCoupon(Long id, CouponDTO couponDTO);
    ResponseMessage searchCoupons(String val);

    ResponseMessage getUsersByType(String userType);
    ResponseMessage searchUsers(String val, String userType);

    ResponseMessage getEmployeeRoles();

    ResponseMessage confirmOrder(Map<String, Object> items);
    ResponseMessage getCustomerOrders(Long customerId);
    ResponseMessage getAdminOrders(Long customerId);

    ResponseMessage getAdminFoodItems(int pageNumber, int pageSize, String searchVal);

    ResponseMessage getAdminFoodCategories(int pageNumber, int pageSize, String searchVal);

    ResponseMessage getAdminDashboard();
    ResponseMessage initialAuthentication(Long customerId, String currentPrincipalName);

    ResponseMessage updateFoodItemOrder(Map<String, String> values);
    ResponseMessage updateFoodCategoryOrder(Map<String, String> values);

    ResponseMessage updateOrderStatus(Map<String, String> values);
}
