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
import com.apps.biteandsip.service.StorageService;
import jdk.jfr.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AppServiceImpl implements AppService {
    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodItemRepository foodItemRepository;
    private final PromoRepository promoRepository;
    private final SettingsRepository settingsRepository;
    private final ModelMapper mapper;
    private final StorageService storageService;

    @Autowired
    public AppServiceImpl(FoodCategoryRepository foodCategoryRepository, FoodItemRepository foodItemRepository, PromoRepository promoRepository, SettingsRepository settingsRepository, ModelMapper mapper, StorageService storageService) {
        this.foodCategoryRepository = foodCategoryRepository;
        this.foodItemRepository = foodItemRepository;
        this.promoRepository = promoRepository;
        this.settingsRepository = settingsRepository;
        this.mapper = mapper;
        this.storageService = storageService;
    }

    @Override
    public ResponseMessage createCategory(MultipartFile file, String name, boolean active) {
        String fileName = storageService.store(file);

        FoodCategory foodCategory = new FoodCategory(name, fileName);
        foodCategory.setActive(active);

        return new ResponseMessage(foodCategoryRepository.save(foodCategory), 201);
    }

    @Override
    public ResponseMessage updateCategory(Long id, String name, boolean active, MultipartFile file) {
        FoodCategory foodCategory = foodCategoryRepository.findById(id).orElseThrow(() -> new FoodCategoryNotFoundException("Food category was not found"));
        if(file != null){
            String fileName = storageService.store(file);
            foodCategory.setImageSource(fileName);
        }

        foodCategory.setActive(active);
        foodCategory.setName(name);

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
        List<FoodCategory> categories = foodCategoryRepository.findAll();
        for(FoodCategory foodCategory: categories){
            foodCategory.setImageSource("http://localhost:8080/api/v1/app/public/food-categories/image/" + foodCategory.getImageSource());
        }
        return new ResponseMessage(categories, 200);
    }

    @Override
    public ResponseMessage getFoodCategoryById(Long id) {
        FoodCategory foodCategory = foodCategoryRepository.findById(id).orElseThrow(() -> new FoodCategoryNotFoundException("Category was not found"));
        foodCategory.setImageSource("http://localhost:8080/api/v1/app/public/food-categories/image/" + foodCategory.getImageSource());
        return new ResponseMessage(foodCategory, 200);
    }

    @Override
    public ResponseMessage searchFoodCategories(String val) {
        return new ResponseMessage(foodCategoryRepository.findByNameContainingIgnoreCase(val), 200);
    }

    @Override
    public ResponseMessage getFoodItems() {
        return new ResponseMessage(foodItemRepository.findAll(), 200);
    }
}
