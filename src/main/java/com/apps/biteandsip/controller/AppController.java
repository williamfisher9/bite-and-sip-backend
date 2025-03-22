package com.apps.biteandsip.controller;

import com.apps.biteandsip.dto.FoodCategoryDTO;
import com.apps.biteandsip.dto.FoodItemDTO;
import com.apps.biteandsip.dto.RegisterRequestDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.model.FoodCategory;
import com.apps.biteandsip.model.FoodItem;
import com.apps.biteandsip.model.PromoCode;
import com.apps.biteandsip.service.AppService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/app")
public class AppController {
    private final AppService appService;

    @Autowired
    public AppController(AppService appService) {
        this.appService = appService;
    }

    @RequestMapping(value = "/public/food-categories", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getFoodCategories(){
        ResponseMessage responseMessage = appService.getFoodCategories();
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/public/food-items", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getFoodItems(){
        ResponseMessage responseMessage = appService.getFoodItems();
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-categories", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> createFoodCategory(@RequestBody @Valid FoodCategoryDTO foodCategory){
        ResponseMessage responseMessage = appService.createCategory(foodCategory);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-items", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> createFoodItem(@RequestBody @Valid FoodItemDTO foodItemDTO){
        ResponseMessage responseMessage = appService.createFoodItem(foodItemDTO);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/promo-codes", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> createFoodItem(@RequestBody @Valid PromoCode promoCode){
        ResponseMessage responseMessage = appService.createPromoCode(promoCode);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }
}
