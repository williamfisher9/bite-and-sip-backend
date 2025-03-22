package com.apps.biteandsip.service.impl;

import com.apps.biteandsip.dao.FoodCategoryRepository;
import com.apps.biteandsip.dao.FoodItemRepository;
import com.apps.biteandsip.dao.PromoRepository;
import com.apps.biteandsip.dao.SettingsRepository;
import com.apps.biteandsip.dto.FoodCategoryDTO;
import com.apps.biteandsip.dto.FoodItemDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.exceptions.FoodCategoryNotFoundException;
import com.apps.biteandsip.model.FoodCategory;
import com.apps.biteandsip.model.FoodItem;
import com.apps.biteandsip.model.PromoCode;
import com.apps.biteandsip.service.AppService;
import jdk.jfr.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppServiceImpl implements AppService {
    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodItemRepository foodItemRepository;
    private final PromoRepository promoRepository;
    private final SettingsRepository settingsRepository;
    private final ModelMapper mapper;

    @Autowired
    public AppServiceImpl(FoodCategoryRepository foodCategoryRepository, FoodItemRepository foodItemRepository, PromoRepository promoRepository, SettingsRepository settingsRepository, ModelMapper mapper) {
        this.foodCategoryRepository = foodCategoryRepository;
        this.foodItemRepository = foodItemRepository;
        this.promoRepository = promoRepository;
        this.settingsRepository = settingsRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseMessage createCategory(FoodCategoryDTO foodCategoryDTO) {
        FoodCategory foodCategory = mapper.map(foodCategoryDTO, FoodCategory.class);
        foodCategory.setActive(false);
        return new ResponseMessage(foodCategoryRepository.save(foodCategory), 200);
    }

    @Override
    public ResponseMessage createFoodItem(FoodItemDTO foodItemDTO) {
        FoodItem foodItem = mapper.map(foodItemDTO, FoodItem.class);
        foodItem.setActive(false);
        foodItem.setRating(0);
        FoodCategory category = foodCategoryRepository.findById(foodItemDTO.getFoodCategory())
                .orElseThrow(() -> new FoodCategoryNotFoundException("Food category was not found"));

        foodItem.setCategory(category);

        return new ResponseMessage(foodItemRepository.save(foodItem), 200);
    }

    @Override
    public ResponseMessage createPromoCode(PromoCode promoCode) {
        return new ResponseMessage(promoRepository.save(promoCode), 200);
    }

    @Override
    public ResponseMessage getParamValueByName(String name) {
        return new ResponseMessage(settingsRepository.findByParamName(name), 200);
    }

    @Override
    public ResponseMessage getFoodCategories() {
        return new ResponseMessage(foodCategoryRepository.findAll(), 200);
    }

    @Override
    public ResponseMessage getFoodItems() {
        return new ResponseMessage(foodItemRepository.findAll(), 200);
    }
}
