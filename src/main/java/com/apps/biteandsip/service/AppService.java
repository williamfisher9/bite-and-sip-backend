package com.apps.biteandsip.service;

import com.apps.biteandsip.dto.FoodCategoryDTO;
import com.apps.biteandsip.dto.FoodItemDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.model.FoodCategory;
import com.apps.biteandsip.model.FoodItem;
import com.apps.biteandsip.model.PromoCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public interface AppService {
    ResponseMessage createCategory(MultipartFile file, String name, boolean active);
    ResponseMessage updateCategory(Long id ,String name, boolean active, MultipartFile file);
    ResponseMessage createFoodItem(FoodItemDTO foodItem);
    ResponseMessage createPromoCode(PromoCode promoCode);
    ResponseMessage getParamValueByName(String name);
    ResponseMessage getFoodCategories();
    ResponseMessage getFoodCategoryById(Long id);
    ResponseMessage searchFoodCategories(String val);
    ResponseMessage getFoodItems();
}
