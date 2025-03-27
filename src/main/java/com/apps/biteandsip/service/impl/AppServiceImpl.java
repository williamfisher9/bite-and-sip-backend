package com.apps.biteandsip.service.impl;

import com.apps.biteandsip.dao.FoodCategoryRepository;
import com.apps.biteandsip.dao.FoodItemRepository;
import com.apps.biteandsip.dao.CouponRepository;
import com.apps.biteandsip.dao.SettingsRepository;
import com.apps.biteandsip.dto.CouponDTO;
import com.apps.biteandsip.dto.FoodItemDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.dto.StripePaymentIntentDTO;
import com.apps.biteandsip.exceptions.FoodCategoryNotFoundException;
import com.apps.biteandsip.exceptions.FoodItemNotFoundException;
import com.apps.biteandsip.model.Coupon;
import com.apps.biteandsip.model.FoodCategory;
import com.apps.biteandsip.model.FoodItem;
import com.apps.biteandsip.service.AppService;
import com.apps.biteandsip.service.StorageService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class AppServiceImpl implements AppService {
    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodItemRepository foodItemRepository;
    private final CouponRepository couponRepository;
    private final SettingsRepository settingsRepository;
    private final ModelMapper mapper;
    private final StorageService storageService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    public AppServiceImpl(FoodCategoryRepository foodCategoryRepository, FoodItemRepository foodItemRepository, CouponRepository couponRepository, SettingsRepository settingsRepository, ModelMapper mapper, StorageService storageService) {
        this.foodCategoryRepository = foodCategoryRepository;
        this.foodItemRepository = foodItemRepository;
        this.couponRepository = couponRepository;
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
    public ResponseMessage createFoodItem(MultipartFile file, String name, boolean active, String price, String description, Long categoryId) {

        FoodCategory foodCategory = foodCategoryRepository.findById(categoryId).orElseThrow(() -> new FoodCategoryNotFoundException("Food category was not found"));

        String fileName = storageService.store(file);


        FoodItem foodItem = new FoodItem(name, fileName, description, Float.parseFloat(price), 0, active);
        foodItem.setCategory(foodCategory);

        return new ResponseMessage(foodItemRepository.save(foodItem), 201);
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
    public ResponseMessage getParamValueByName(String name) {
        return new ResponseMessage(settingsRepository.findByParamName(name), 200);
    }

    @Override
    public ResponseMessage getFoodCategories() {
        List<FoodCategory> categories = foodCategoryRepository.findAll();
        for(FoodCategory foodCategory: categories){
            foodCategory.setImageSource("http://localhost:8080/api/v1/app/public/image-download/" + foodCategory.getImageSource());
        }
        return new ResponseMessage(categories, 200);
    }

    @Override
    public ResponseMessage getFoodCategoryById(Long id) {
        FoodCategory foodCategory = foodCategoryRepository.findById(id).orElseThrow(() -> new FoodCategoryNotFoundException("Category was not found"));
        foodCategory.setImageSource("http://localhost:8080/api/v1/app/public/image-download/" + foodCategory.getImageSource());
        return new ResponseMessage(foodCategory, 200);
    }

    @Override
    public ResponseMessage searchFoodCategories(String val) {
        List<FoodCategory> foodCategories;
        if(val.isEmpty()){
            foodCategories = foodCategoryRepository.findAll();
        } else {
            foodCategories = foodCategoryRepository.findByNameContainingIgnoreCase(val);
        }

        for(FoodCategory foodCategory: foodCategories){
            foodCategory.setImageSource("http://localhost:8080/api/v1/app/public/image-download/" + foodCategory.getImageSource());
        }

        return new ResponseMessage(foodCategories, 200);
    }

    @Override
    public ResponseMessage searchFoodItems(String val) {
        List<FoodItem> foodItems;
        if(val.isEmpty()){
            foodItems = foodItemRepository.findAll();
        } else {
            foodItems = foodItemRepository.findByNameContainingIgnoreCase(val);
        }

        for(FoodItem foodItem: foodItems){
            foodItem.setImageSource("http://localhost:8080/api/v1/app/public/image-download/" + foodItem.getImageSource());
        }

        return new ResponseMessage(foodItems, 200);
    }

    @Override
    public ResponseMessage getFoodItems() {
        List<FoodCategory> categories = foodCategoryRepository.findAll();
        List<FoodItem> foodItems = foodItemRepository.findAll();

        for(FoodCategory category: categories){
            category.setImageSource("http://localhost:8080/api/v1/app/public/image-download/" + category.getImageSource());
        }

        for(FoodItem foodItem: foodItems){
            foodItem.setImageSource("http://localhost:8080/api/v1/app/public/image-download/" + foodItem.getImageSource());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("foodItems", foodItems);
        return new ResponseMessage(response, 200);
    }

    @Override
    public ResponseMessage getFoodItemById(Long id) {
        List<FoodCategory> categories = foodCategoryRepository.findAll();
        FoodItem foodItem = foodItemRepository.findById(id).orElseThrow(() -> new FoodItemNotFoundException("Food item was not found"));

        foodItem.setImageSource("http://localhost:8080/api/v1/app/public/image-download/" + foodItem.getImageSource());

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("foodItem", foodItem);
        return new ResponseMessage(response, 200);
    }

    @Override
    public ResponseMessage getFoodItemsByCategoryId(Long id) {
        List<FoodItem> foodItems;
        if(id == 0){
            foodItems = foodItemRepository.findAll();

            for(FoodItem foodItem: foodItems){
                foodItem.setImageSource("http://localhost:8080/api/v1/app/public/image-download/" + foodItem.getImageSource());
            }
        } else {
            foodItems = foodItemRepository.findByCategoryId(id);

            for(FoodItem foodItem: foodItems){
                foodItem.setImageSource("http://localhost:8080/api/v1/app/public/image-download/" + foodItem.getImageSource());
            }
        }

        return new ResponseMessage(foodItems, 200);
    }

    @Override
    public ResponseMessage updateFoodItem(Long id,
                                          String name,
                                          boolean active,
                                          MultipartFile file,
                                          String price,
                                          String description, Long categoryId) {
        FoodCategory foodCategory = foodCategoryRepository.findById(categoryId).orElseThrow(() -> new FoodCategoryNotFoundException("Food item was not found"));
        FoodItem foodItem = foodItemRepository.findById(id).orElseThrow(() -> new FoodItemNotFoundException("Food category was not found"));
        if(file != null){
            String fileName = storageService.store(file);
            foodItem.setImageSource(fileName);
        }

        foodItem.setActive(active);
        foodItem.setName(name);
        foodItem.setDescription(description);
        foodItem.setPrice(Float.parseFloat(price));
        foodItem.setCategory(foodCategory);

        return new ResponseMessage(foodItemRepository.save(foodItem), 200);
    }

    @Override
    public ResponseMessage createPaymentIntent(StripePaymentIntentDTO intentDTO) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(intentDTO.getAmount())
                        .setCurrency("usd")
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        System.out.println(paymentIntent.getClientSecret());
        System.out.println(paymentIntent.getStatus());
        System.out.println(paymentIntent.getId());
        return new ResponseMessage(paymentIntent.getClientSecret(), 200);
    }

    @Override
    public ResponseMessage confirmPaymentIntent(String id) {
        Stripe.apiKey = stripeApiKey;
        PaymentIntent resource = null;
        try {
            resource = PaymentIntent.retrieve(id);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        PaymentIntentConfirmParams params =
                PaymentIntentConfirmParams.builder()
                        .setPaymentMethod("pm_card_visa")
                        .setReturnUrl("http://localhost:5173/biteandsip/cart/payment-status")
                        .build();
        try {
            PaymentIntent paymentIntent = resource.confirm(params);
            System.out.println(paymentIntent.getStatus());
            return new ResponseMessage(paymentIntent.getStatus(), 200);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public ResponseMessage getCoupons() {
        return new ResponseMessage(couponRepository.findAll(), 200);
    }

    @Override
    public ResponseMessage createCoupon(CouponDTO couponDTO) {
        Coupon coupon = new Coupon();
        coupon.setCode(couponDTO.getCode());
        coupon.setAmount(couponDTO.getAmount());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fromDateLocalDate = LocalDate.parse(couponDTO.getFromDate(), formatter);
        LocalDate toDateLocalDate = LocalDate.parse(couponDTO.getToDate(), formatter);

        coupon.setFromDate(fromDateLocalDate);
        coupon.setToDate(toDateLocalDate);
        coupon.setActive(couponDTO.isActive());
        return new ResponseMessage(couponRepository.save(coupon), 201);
    }

    @Override
    public ResponseMessage getCouponById(Long id) {
        return new ResponseMessage(couponRepository.findById(id).orElseThrow(() -> new FoodItemNotFoundException("Coupon was not found")), 200);
    }



    @Override
    public ResponseMessage updateCoupon(Long id, CouponDTO couponDTO) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new FoodCategoryNotFoundException("Coupon was not found"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fromDateLocalDate = LocalDate.parse(couponDTO.getFromDate(), formatter);
        LocalDate toDateLocalDate = LocalDate.parse(couponDTO.getToDate(), formatter);

        coupon.setActive(couponDTO.isActive());
        coupon.setCode(couponDTO.getCode());
        coupon.setAmount(couponDTO.getAmount());
        coupon.setFromDate(fromDateLocalDate);
        coupon.setToDate(toDateLocalDate);

        return new ResponseMessage(couponRepository.save(coupon), 200);
    }

    @Override
    public ResponseMessage searchCoupons(String val) {
        List<Coupon> coupons;
        if(val.isEmpty()){
            coupons = couponRepository.findAll();
        } else {
            coupons = couponRepository.findByCodeContainingIgnoreCase(val);
        }

        return new ResponseMessage(coupons, 200);
    }

    @Override
    public ResponseMessage getCouponByCode(String code) {
        System.out.println(code);
        Coupon coupon = null;
        if(!couponRepository.findByCode(code).isEmpty()){
            coupon = couponRepository.findByCode(code).get(0);
        }
        return new ResponseMessage(coupon, coupon == null ? 404 : 200);
    }
}
