package com.apps.biteandsip.service;

import com.apps.biteandsip.dto.FoodCategoryDTO;
import com.apps.biteandsip.dto.FoodItemDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.model.FoodCategory;
import com.apps.biteandsip.model.FoodItem;
import com.apps.biteandsip.model.PromoCode;
import org.springframework.stereotype.Service;

@Service
public interface AppService {
    ResponseMessage createCategory(FoodCategoryDTO foodCategory);
    ResponseMessage createFoodItem(FoodItemDTO foodItem);
    ResponseMessage createPromoCode(PromoCode promoCode);
    ResponseMessage getParamValueByName(String name);
    ResponseMessage getFoodCategories();
    ResponseMessage getFoodItems();
}
