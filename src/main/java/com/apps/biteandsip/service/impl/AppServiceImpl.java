package com.apps.biteandsip.service.impl;

import com.apps.biteandsip.dao.*;
import com.apps.biteandsip.dto.*;
import com.apps.biteandsip.exceptions.CouponNotFoundException;
import com.apps.biteandsip.exceptions.FoodCategoryNotFoundException;
import com.apps.biteandsip.exceptions.FoodItemNotFoundException;
import com.apps.biteandsip.exceptions.OrderNotFoundException;
import com.apps.biteandsip.model.*;
import com.apps.biteandsip.service.AppService;
import com.apps.biteandsip.service.StorageService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private final OrderStatusRepository orderStatusRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${backend.url}")
    private String backendUrl;

    @Value("${image.download.url}")
    private String imageDownloadUrl;

    @Autowired
    public AppServiceImpl(FoodCategoryRepository foodCategoryRepository, FoodItemRepository foodItemRepository, CouponRepository couponRepository, SettingsRepository settingsRepository, ModelMapper mapper, StorageService storageService, UserRepository userRepository, AuthorityRepository authorityRepository, OrderRepository orderRepository, OrderStatusRepository orderStatusRepository) {
        this.foodCategoryRepository = foodCategoryRepository;
        this.foodItemRepository = foodItemRepository;
        this.couponRepository = couponRepository;
        this.settingsRepository = settingsRepository;
        this.mapper = mapper;
        this.storageService = storageService;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    @Override
    public ResponseMessage createCategory(MultipartFile file, String name, boolean active) {
        String fileName = storageService.store(file);

        FoodCategory foodCategory = new FoodCategory(name, fileName);
        foodCategory.setActive(active);

        if(foodCategoryRepository.count() == 0){
            foodCategory.setSortingOrder(0);
        } else {
            int max = foodCategoryRepository.findTopSortingOrder().get();
            foodCategory.setSortingOrder(max+1);
        }

        return new ResponseMessage(foodCategoryRepository.save(foodCategory), 201);
    }

    @Override
    public ResponseMessage createFoodItem(MultipartFile file, String name, boolean active, String price, String description, Long categoryId) {

        FoodCategory foodCategory = foodCategoryRepository.findById(categoryId).orElseThrow(() -> new FoodCategoryNotFoundException("Food category was not found"));

        String fileName = storageService.store(file);


        FoodItem foodItem = new FoodItem(name, fileName, description, Float.parseFloat(price), 0, active);
        foodItem.setCategory(foodCategory);

        if(foodItemRepository.count() == 0){
            foodItem.setSortingOrder(0);
        } else {
            int max = foodItemRepository.findTopSortingOrder().get();
            foodItem.setSortingOrder(max+1);
        }

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
    public ResponseMessage getParamValueByName(String name) {
        return new ResponseMessage(settingsRepository.findByParamName(name), 200);
    }

    @Override
    public ResponseMessage getFoodCategories() {
        List<FoodCategory> categories = foodCategoryRepository.findAll();

        setFullImageDownloadLink(categories, null);

        for(FoodCategory category : categories){
            category.setItems(null);
        }

        return new ResponseMessage(categories, 200);
    }

    @Override
    public ResponseMessage getFoodCategoryById(Long id) {
        FoodCategory foodCategory = foodCategoryRepository.findById(id).orElseThrow(() -> new FoodCategoryNotFoundException("Category was not found"));
        foodCategory.setImageSource(imageDownloadUrl + foodCategory.getImageSource());
        return new ResponseMessage(foodCategory, 200);
    }

    /*@Override
    public ResponseMessage searchFoodCategories(String val) {
        List<FoodCategory> categories;
        if(val.isEmpty()){
            categories = foodCategoryRepository.findAll();
        } else {
            categories = foodCategoryRepository.findByNameContainingIgnoreCase(val);
        }

        setFullImageDownloadLink(categories, null);

        return new ResponseMessage(categories, 200);
    }*/

    /*@Override
    public ResponseMessage searchFoodItems(String val) {
        List<FoodItem> foodItems;
        if(val.isEmpty()){
            foodItems = foodItemRepository.findAll();
        } else {
            foodItems = foodItemRepository.findByNameContainingIgnoreCase(val);
        }

        setFullImageDownloadLink(null, foodItems);

        foodItems.sort(Comparator.comparingInt(FoodItem::getSortingOrder));

        return new ResponseMessage(foodItems, 200);
    }*/

    @Override
    public ResponseMessage getFoodItems() {
        List<FoodCategory> categories = foodCategoryRepository.findAll().stream()
                .filter(FoodCategory::isActive)
                .sorted(Comparator.comparingInt(FoodCategory::getSortingOrder))
                .collect(Collectors.toList());

        categories.forEach(f -> f.setItems(null));



        List<FoodItem> foodItems = foodItemRepository.findAll()
                .stream().filter(item -> item.isActive() && item.getCategory().isActive())
                .toList();

        setFullImageDownloadLink(categories, foodItems);

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("foodItems", foodItems.stream().sorted(Comparator.comparingInt(FoodItem::getSortingOrder)));
        return new ResponseMessage(response, 200);
    }

    @Override
    public ResponseMessage getAdminFoodItems(int pageNumber, int pageSize, String searchVal) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sortingOrder"));
        Page<FoodItem> foodItems;

        if(searchVal.isEmpty())
            foodItems = foodItemRepository.findAll(pageable);
        else
            foodItems = foodItemRepository.findByNameContainingIgnoreCase(searchVal, pageable);

        setFullImageDownloadLink(null, foodItems);

        return new ResponseMessage(foodItems, 200);
    }

    @Override
    public ResponseMessage getAdminFoodCategories(int pageNumber, int pageSize, String searchVal) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sortingOrder"));
        Page<FoodCategory> categories;

        if(searchVal.isEmpty())
            categories = foodCategoryRepository.findAll(pageable);
        else
            categories = foodCategoryRepository.findByNameContainingIgnoreCase(searchVal, pageable);

        setFullImageDownloadLink(categories, null);

        for (FoodCategory foodCategory : categories) {
            foodCategory.setItems(null);
        }

        return new ResponseMessage(categories, 200);
    }

    private void setFullImageDownloadLink(Object categories, Object foodItems) {
        if(categories != null){
            if(categories instanceof List<?>) {
                for (FoodCategory category : (List<FoodCategory>) categories) {
                    category.setImageSource(imageDownloadUrl + category.getImageSource());
                }
            }

            if(categories instanceof Page<?>){
                for (FoodCategory category : (Page<FoodCategory>) categories) {
                    category.setImageSource(imageDownloadUrl + category.getImageSource());
                }
            }
        }

        if(foodItems != null){
            if(foodItems instanceof List<?>){
                for(FoodItem foodItem: (List<FoodItem>) foodItems){
                    foodItem.setImageSource(imageDownloadUrl + foodItem.getImageSource());
                }
            }

            if(foodItems instanceof Page<?>){
                for(FoodItem foodItem: (Page<FoodItem>) foodItems){
                    foodItem.setImageSource(imageDownloadUrl + foodItem.getImageSource());
                }
            }
        }
    }


    @Override
    public ResponseMessage getFoodItemById(Long id) {
        List<FoodCategory> categories = foodCategoryRepository.findAll();
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new FoodItemNotFoundException("Food item was not found"));

        foodItem.setImageSource(imageDownloadUrl + foodItem.getImageSource());

        for(FoodCategory category : categories){
            category.setItems(null);
        }

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
        } else {
            foodItems = foodItemRepository.findByCategoryId(id)
                    .stream().filter(item -> item.isActive() && item.getCategory().isActive())
                    .toList();
        }

        setFullImageDownloadLink(null, foodItems);

        return new ResponseMessage(foodItems.stream().sorted(Comparator.comparingInt(FoodItem::getSortingOrder)), 200);
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

    /*@Override
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


     */

    /*@Override
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
    }*/

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

        for(User user : users){
            user.setImageSource(imageDownloadUrl + user.getImageSource());
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

        for(User user : users){
            user.setImageSource(imageDownloadUrl + user.getImageSource());
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
        order.setStatus(orderStatusRepository.findByState("RECEIVED").get(0));



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
            order.setCouponAmount(BigDecimal.valueOf(coupon.getAmount()));
            subtotal = subtotal.subtract(BigDecimal.valueOf(coupon.getAmount()));
        }

        subtotal = subtotal.add(BigDecimal.valueOf(5L));

        BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(5)).divide(BigDecimal.valueOf(100));
        order.setDeliveryFee(BigDecimal.valueOf(5L));

        BigDecimal totalPrice = subtotal.add(taxAmount);

        order.setTax(taxAmount);
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
    public ResponseMessage getAdminOrders(Long customerId) {
        List<Order> orders;
        if(customerId == 0)
             orders = orderRepository.findAll();
        else {
            User user = userRepository.findById(customerId)
                    .orElseThrow(() -> new UsernameNotFoundException("username not found"));
            orders = orderRepository.findByCustomerId(customerId);
        }

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
        result.put("orders_count_by_status", orderRepository.countOrdersByStatus());

        // sum of delivered order
        result.put("sum_of_delivered_orders", orderRepository.sumOfDeliveredOrders());


        return new ResponseMessage(result, 200);
    }

    @Override
    public ResponseMessage initialAuthentication(Long customerId, String currentPrincipalName) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new UsernameNotFoundException("username was not found"));

        if(!user.getUsername().equalsIgnoreCase(currentPrincipalName)){
            return new ResponseMessage("user details not matching records", 403);
        }

        return new ResponseMessage("success", 200);
    }

    @Override
    public ResponseMessage updateFoodItemOrder(Map<String, String> values) {
        int draggedItemIndex = Integer.parseInt(values.get("draggedItemIndex"));
        int draggedOverItemIndex = Integer.parseInt(values.get("draggedOverItemIndex"));
        int pageNumber = Integer.parseInt(values.get("pageNumber"));
        int pageSize = Integer.parseInt(values.get("pageSize"));

        // find all items and sort by sorting order
        List<FoodItem> foodItems = foodItemRepository.findAll();
        foodItems.sort(Comparator.comparingInt(FoodItem::getSortingOrder));

        List<FoodItem> toBeRemoved = foodItems.stream()
                .filter((item) -> item.getSortingOrder() == draggedItemIndex).toList();
        foodItems.remove(toBeRemoved.get(0));
        foodItems.add(draggedOverItemIndex, toBeRemoved.get(0));

        int index = 0;
        for(FoodItem foodItem : foodItems){
            foodItem.setSortingOrder(index++);
        }


        foodItemRepository.saveAll(foodItems);

        //List<FoodCategory> categories = foodCategoryRepository.findAll();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sortingOrder"));
        Page<FoodItem> updatedFoodItems = foodItemRepository.findAll(pageable);
        updatedFoodItems = foodItemRepository.findAll(pageable);

        setFullImageDownloadLink(null, foodItems);

        return new ResponseMessage(updatedFoodItems, 200);
    }

    @Override
    public ResponseMessage updateFoodCategoryOrder(Map<String, String> values) {
        int draggedItemIndex = Integer.parseInt(values.get("draggedItemIndex"));
        int draggedOverItemIndex = Integer.parseInt(values.get("draggedOverItemIndex"));
        int pageNumber = Integer.parseInt(values.get("pageNumber"));
        int pageSize = Integer.parseInt(values.get("pageSize"));

        // find all items and sort by sorting order
        List<FoodCategory> foodCategories = foodCategoryRepository.findAll();
        foodCategories.sort(Comparator.comparingInt(FoodCategory::getSortingOrder));

        for(FoodCategory category : foodCategories){
            category.setItems(null);
        }

        List<FoodCategory> toBeRemoved = foodCategories.stream()
                .filter((item) -> item.getSortingOrder() == draggedItemIndex).toList();

        foodCategories.remove(toBeRemoved.get(0));
        foodCategories.add(draggedOverItemIndex, toBeRemoved.get(0));

        int index = 0;
        for(FoodCategory category : foodCategories){
            category.setSortingOrder(index++);
        }

        foodCategoryRepository.saveAll(foodCategories);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sortingOrder"));
        Page<FoodCategory> updatedFoodCategories = foodCategoryRepository.findAll(pageable);

        setFullImageDownloadLink(updatedFoodCategories, null);

        return new ResponseMessage(updatedFoodCategories, 200);
    }

    @Override
    public ResponseMessage updateOrderStatus(Map<String, String> values) {
        String authority = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().toList().get(0).getAuthority();

        if(!authority.equalsIgnoreCase("ROLE_KITCHEN") &&
                !authority.equalsIgnoreCase("ROLE_WAITER") &&
                !authority.equalsIgnoreCase("ROLE_ADMIN"))
            return new ResponseMessage("improper authority type", HttpStatus.BAD_REQUEST.value());


        String action = values.get("action");
        int statusId = Integer.parseInt(values.get("status"));
        String uuid = values.get("uuid");

        Order order = orderRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new OrderNotFoundException("order was not found"));

        if(order.getStatus().getId() != statusId){
            List<Order> orders = orderRepository.findAll();

            for(Order order1 : orders){
                for(OrderItem orderItem : order1.getItems()){
                    orderItem.getItem().setImageSource(
                            orderItem.getItem().getImageSource().startsWith("http") ?
                                    orderItem.getItem().getImageSource() :
                                    imageDownloadUrl + orderItem.getItem().getImageSource()
                    );
                }
            }

            return new ResponseMessage(orders, 200);
        }

        if(action.equalsIgnoreCase("cancel")){
            order.setStatus(orderStatusRepository.findByState("CANCELLED").get(0));
            orderRepository.save(order);
        }

        if(action.equalsIgnoreCase("proceed")){
            OrderStatus orderStatus = order.getStatus();

            //OrderStatus orderStatus = OrderStatus.getNextStatus(status);
            if(!orderStatus.isTerminalState()){
                order.setStatus(orderStatusRepository.findByState(orderStatus.getNextState()).get(0));
                order.setUpdatedBy((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                orderRepository.save(order);
            }
        }

        List<Order> orders = orderRepository.findAll();

        for(Order order1 : orders){
            for(OrderItem orderItem : order1.getItems()){
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
    public ResponseMessage getAdminSettings() {
        return new ResponseMessage(settingsRepository.findAll(), 200);
    }
}
