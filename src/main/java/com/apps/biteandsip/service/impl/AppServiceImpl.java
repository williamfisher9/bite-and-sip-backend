package com.apps.biteandsip.service.impl;

import com.apps.biteandsip.dao.*;
import com.apps.biteandsip.dto.*;
import com.apps.biteandsip.enums.OrderStatus;
import com.apps.biteandsip.exceptions.CouponNotFoundException;
import com.apps.biteandsip.exceptions.FoodCategoryNotFoundException;
import com.apps.biteandsip.exceptions.FoodItemNotFoundException;
import com.apps.biteandsip.model.*;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final OrderRepository orderRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${backend.url}")
    private String backendUrl;

    @Value("${image.download.url}")
    private String imageDownloadUrl;

    @Autowired
    public AppServiceImpl(FoodCategoryRepository foodCategoryRepository, FoodItemRepository foodItemRepository, CouponRepository couponRepository, SettingsRepository settingsRepository, ModelMapper mapper, StorageService storageService, UserRepository userRepository, AuthorityRepository authorityRepository, OrderRepository orderRepository) {
        this.foodCategoryRepository = foodCategoryRepository;
        this.foodItemRepository = foodItemRepository;
        this.couponRepository = couponRepository;
        this.settingsRepository = settingsRepository;
        this.mapper = mapper;
        this.storageService = storageService;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.orderRepository = orderRepository;
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
            foodCategory.setImageSource(imageDownloadUrl + foodCategory.getImageSource());
        }
        return new ResponseMessage(categories, 200);
    }

    @Override
    public ResponseMessage getFoodCategoryById(Long id) {
        FoodCategory foodCategory = foodCategoryRepository.findById(id).orElseThrow(() -> new FoodCategoryNotFoundException("Category was not found"));
        foodCategory.setImageSource(imageDownloadUrl + foodCategory.getImageSource());
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
            foodCategory.setImageSource(imageDownloadUrl + foodCategory.getImageSource());
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
            foodItem.setImageSource(imageDownloadUrl + foodItem.getImageSource());
        }

        return new ResponseMessage(foodItems, 200);
    }

    @Override
    public ResponseMessage getFoodItems() {
        List<FoodCategory> categories = foodCategoryRepository.findAll().stream()
                .filter(FoodCategory::isActive)
                .toList();

        List<FoodItem> foodItems = foodItemRepository.findAll()
                .stream().filter(item -> item.isActive() && item.getCategory().isActive())
                .toList();

        for(FoodCategory category: categories){
            category.setImageSource(imageDownloadUrl + category.getImageSource());
        }

        for(FoodItem foodItem: foodItems){
            foodItem.setImageSource(imageDownloadUrl + foodItem.getImageSource());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("foodItems", foodItems);
        return new ResponseMessage(response, 200);
    }

    @Override
    public ResponseMessage getAdminFoodItems() {
        List<FoodCategory> categories = foodCategoryRepository.findAll();

        List<FoodItem> foodItems = foodItemRepository.findAll();

        for(FoodCategory category: categories){
            category.setImageSource(imageDownloadUrl + category.getImageSource());
        }

        for(FoodItem foodItem: foodItems){
            foodItem.setImageSource(imageDownloadUrl + foodItem.getImageSource());
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

        foodItem.setImageSource(imageDownloadUrl + foodItem.getImageSource());

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("foodItem", foodItem);
        return new ResponseMessage(response, 200);
    }

    @Override
    public ResponseMessage getFoodItemsByCategoryId(Long id) {
        List<FoodItem> foodItems;
        if(id == 0){
            foodItems = foodItemRepository.findAll()
                    .stream().filter(item -> item.isActive() && item.getCategory().isActive())
                    .toList();

            for(FoodItem foodItem: foodItems){
                foodItem.setImageSource(imageDownloadUrl + foodItem.getImageSource());
            }
        } else {
            foodItems = foodItemRepository.findByCategoryId(id)
                    .stream().filter(item -> item.isActive() && item.getCategory().isActive())
                    .toList();

            for(FoodItem foodItem: foodItems){
                foodItem.setImageSource(imageDownloadUrl + foodItem.getImageSource());
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
        FoodItem foodItem = foodItemRepository.findById(id).orElseThrow(() -> new FoodItemNotFoundException("Food item was not found"));
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

        Map<String, String> paymentIntentDetails = new HashMap<>();
        paymentIntentDetails.put("clientSecret", paymentIntent.getClientSecret());
        paymentIntentDetails.put("paymentId", paymentIntent.getId());

        return new ResponseMessage(paymentIntentDetails, 200);
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
                        .setReturnUrl(backendUrl + "/cart/payment-status")
                        .build();
        try {
            PaymentIntent paymentIntent = resource.confirm(params);
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
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new CouponNotFoundException("Coupon was not found"));

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
    public ResponseMessage getUsersByType(String userType) {
        List<User> users = new ArrayList<>();
        if(userType.equalsIgnoreCase("customers")){
            users = userRepository.findByUserType("CUSTOMER");
        } else if(userType.equalsIgnoreCase("employees")) {
            users = userRepository.findByUserTypeNot("CUSTOMER").stream()
                    .filter(record -> !record.getUserType().equalsIgnoreCase("ADMIN"))
                    .toList();
        }
        
        return new ResponseMessage(users, 200);
    }

    @Override
    public ResponseMessage getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code).orElseThrow(() -> new CouponNotFoundException("Coupon was not found"));
        return new ResponseMessage(coupon, 200);
    }

    @Override
    public ResponseMessage searchUsers(String val, String userType) {
        List<User> users;
        if(val.isEmpty()){
            users = userRepository.findByUserType(userType);
        } else {
            if(userType.equalsIgnoreCase("customers")){
                users = userRepository.searchByUsernameAndUserType(val, "CUSTOMER");
            } else {
                users = userRepository.searchByUsernameAndUserTypeNot(val, "CUSTOMER");
            }
        }

        return new ResponseMessage(users, 200);
    }

    @Override
    public ResponseMessage getEmployeeRoles() {
        List<Authority> authorities = authorityRepository.findAll().stream().filter(item -> !item.getAuthority().equalsIgnoreCase("ROLE_CUSTOMER") && !item.getAuthority().equalsIgnoreCase("ROLE_ADMIN")).toList();;
        return new ResponseMessage(authorities, 200);
    }

    @Override
    @Transactional
    public ResponseMessage confirmOrder(Map<String, Object> items){
        String confirmationTokenId = (String) items.get("confirmationTokenId");
        String couponCode = (String) items.get("coupon");
        User user = userRepository.findById(Long.valueOf((String) items.get("customerId"))).orElseThrow(() -> new UsernameNotFoundException(""));

        Coupon coupon = couponRepository.findByCode(couponCode).orElse(null);

        Order order = new Order();
        order.setCustomer(user);
        order.setCreationDate(LocalDateTime.now());
        order.setLastUpdateDate(LocalDateTime.now());
        order.setStatus(OrderStatus.RECEIVED);



        Set<OrderItem> orderItems = new HashSet<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for(Object cartItem : (ArrayList<?>) items.get("cartItems")){
            CartItemDTO cartItemDTO = mapper.map(cartItem, CartItemDTO.class);
            FoodItem foodItem = foodItemRepository.findById(cartItemDTO.getFoodItemId()).orElseThrow(() -> new FoodItemNotFoundException("Food item was not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(foodItem);
            orderItem.setQuantity(cartItemDTO.getQuantity());
            orderItem.setPricePerItem(BigDecimal.valueOf(foodItem.getPrice()));
            orderItem.setTotalPricePerItem(BigDecimal.valueOf(foodItem.getPrice()).multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())));
            orderItems.add(orderItem);
            orderItem.setOrder(order);
            subtotal = subtotal.add(BigDecimal.valueOf(foodItem.getPrice()).multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())));
        }

        if(coupon != null){
            order.setCoupon(couponCode);
            subtotal = subtotal.subtract(BigDecimal.valueOf(coupon.getAmount()));
        }

        subtotal = subtotal.add(BigDecimal.valueOf(5L));

        BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(5)).divide(BigDecimal.valueOf(100));
        order.setDeliveryFee(BigDecimal.valueOf(5L));

        BigDecimal totalPrice = subtotal.add(taxAmount);

        order.setTotalPrice(totalPrice);
        order.setItems(orderItems);

        Stripe.apiKey = stripeApiKey;

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue())
                .setCurrency("usd")
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                .setConfirm(true)
                .setConfirmationToken(confirmationTokenId)
                .setReturnUrl(backendUrl + "/payment-status")
                .build();

        PaymentIntent resource = null;
        try {
            resource = PaymentIntent.create(params);
            if(resource.getStatus().equalsIgnoreCase("succeeded")){
                order.setPaymentId(resource.getId());
                orderRepository.save(order);
                return new ResponseMessage("success", 200);
            } else {
                return new ResponseMessage("STRIPE: " + resource.getId() + " " + resource.getStatus(), 400);
            }
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public ResponseMessage getCustomerOrders(Long customerId) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new UsernameNotFoundException("username was not found"));

        List<Order> orders = orderRepository.findByCustomerId(customerId);

        for(Order order : orders){
            for(OrderItem orderItem : order.getItems()){
                orderItem.getItem().setImageSource(
                        orderItem.getItem().getImageSource().startsWith("https") || orderItem.getItem().getImageSource().startsWith("http") ?
                                orderItem.getItem().getImageSource() :
                                imageDownloadUrl + orderItem.getItem().getImageSource()
                );
            }
        }

        return new ResponseMessage(orders, 200);
    }

    @Override
    public ResponseMessage getAdminOrders() {
        List<Order> orders = orderRepository.findAll();

        for(Order order : orders){
            for(OrderItem orderItem : order.getItems()){
                orderItem.getItem().setImageSource(
                        orderItem.getItem().getImageSource().startsWith("http") ?
                                orderItem.getItem().getImageSource() :
                                imageDownloadUrl + orderItem.getItem().getImageSource()
                );
            }
        }

        return new ResponseMessage(orders, 200);
    }

    @Override
    public ResponseMessage getAdminDashboard() {
        Map<String, Object> result = new HashMap<>();
        // pending items for the date

        // handled items for the date


        return new ResponseMessage("success", 200);
    }
}
