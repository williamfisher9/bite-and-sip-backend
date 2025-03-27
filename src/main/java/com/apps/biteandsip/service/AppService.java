package com.apps.biteandsip.service;

import com.apps.biteandsip.dto.CouponDTO;
import com.apps.biteandsip.dto.FoodItemDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.dto.StripePaymentIntentDTO;
import com.stripe.exception.StripeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AppService {
    ResponseMessage createCategory(MultipartFile file, String name, boolean active);

    ResponseMessage createFoodItem(MultipartFile file, String name, boolean active, String price, String description, Long categoryId);

    ResponseMessage updateCategory(Long id ,String name, boolean active, MultipartFile file);
    ResponseMessage createFoodItem(FoodItemDTO foodItem);
    ResponseMessage getParamValueByName(String name);
    ResponseMessage getFoodCategories();
    ResponseMessage getFoodCategoryById(Long id);
    ResponseMessage searchFoodCategories(String val);
    ResponseMessage searchFoodItems(String val);
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
}
