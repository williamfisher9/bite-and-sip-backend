package com.apps.biteandsip.controller;

import com.apps.biteandsip.dto.*;
import com.apps.biteandsip.service.AppService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/app")
public class AppController {
    private final AppService appService;

    @Value("${file.upload.directory}")
    private String fileUploadDirectory;

    @Value("${stripe.api.key}")
    private String stripeApiKey;


    @Autowired
    public AppController(AppService appService) {
        this.appService = appService;
    }

    @RequestMapping(value = "/public/food-categories", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getFoodCategories(){
        ResponseMessage responseMessage = appService.getFoodCategories();
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/public/image-download/{imageName}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getFoodCategories(@PathVariable("imageName") String imageName){
        String filePath = fileUploadDirectory + imageName;
        Path path = new File(filePath).toPath();
        FileSystemResource resource = new FileSystemResource(path);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/public/food-items", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getFoodItems(){
        ResponseMessage responseMessage = appService.getFoodItems();
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/public/food-items/category/{id}", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getFoodItemsByCategoryId(@PathVariable("id") Long id){
        ResponseMessage responseMessage = appService.getFoodItemsByCategoryId(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/settings", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getAdminSettings(){
        ResponseMessage responseMessage = appService.getAdminSettings();
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-categories/new", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> createFoodCategory(@RequestParam("name") String name,@RequestParam("active") boolean active, @RequestPart("file") MultipartFile file){
        ResponseMessage responseMessage = appService.createCategory(file, name, active);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }


    @RequestMapping(value = "/admin/food-items/new", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> createFoodItem(@RequestParam("name") String name,
                                                          @RequestParam("price") String price,
                                                          @RequestParam("description") String description,
                                                          @RequestParam("active") boolean active,
                                                          @RequestParam("categoryId") Long categoryId,
                                                          @RequestPart("file") MultipartFile file){

        ResponseMessage responseMessage = appService.createFoodItem(file, name, active, price, description, categoryId);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }



    @RequestMapping(value = "/admin/food-categories/update/{id}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> updateFoodCategory(@PathVariable("id") Long id, @RequestParam("name") String name,@RequestParam("active") boolean active, @RequestPart(value = "file", required = false) MultipartFile file){
        ResponseMessage responseMessage = appService.updateCategory(id, name, active, file);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-items/update/{id}", method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> updateFoodItem(@PathVariable("id") Long id,
                                                          @RequestParam("name") String name,
                                                          @RequestParam("price") String price,
                                                          @RequestParam("description") String description,
                                                          @RequestParam("active") boolean active,
                                                          @RequestParam("categoryId") Long categoryId,
                                                          @RequestPart(value = "file", required = false) MultipartFile file){
        ResponseMessage responseMessage = appService.updateFoodItem(id, name, active, file, price, description, categoryId);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-categories", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> adminFoodCategories(@RequestParam("page_number") int pageNumber,
                                                               @RequestParam("page_size") int pageSize,
                                                               @RequestParam("search_val") String searchVal){
        ResponseMessage responseMessage = appService.getAdminFoodCategories(pageNumber, pageSize, searchVal);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-categories-ddl", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getAdminFoodCategoriesDDL(){
        ResponseMessage responseMessage = appService.getFoodCategories();
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-items", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> adminFoodItems(@RequestParam("page_number") int pageNumber,
                                                          @RequestParam("page_size") int pageSize,
                                                          @RequestParam("search_val") String searchVal){
        ResponseMessage responseMessage = appService.getAdminFoodItems(pageNumber, pageSize, searchVal);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }


    @RequestMapping(value = "/admin/food-categories/{itemId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> adminGetFoodCategoryById(@PathVariable("itemId") Long itemId){
        ResponseMessage responseMessage = appService.getFoodCategoryById(itemId);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-items/{itemId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> adminGetFoodItemById(@PathVariable("itemId") Long itemId){
        ResponseMessage responseMessage = appService.getFoodItemById(itemId);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-items/update-order", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> updateFoodItemOrder(@RequestBody Map<String, String> values){
        ResponseMessage responseMessage = appService.updateFoodItemOrder(values);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-categories/update-order", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> updateFoodCategoryOrder(@RequestBody Map<String, String> values){
        ResponseMessage responseMessage = appService.updateFoodCategoryOrder(values);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    /*
    * Coupons endpoints
    * */
    @RequestMapping(value = "/admin/coupons/new", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> createCoupon(@RequestBody CouponDTO couponDTO){
        ResponseMessage responseMessage = appService.createCoupon(couponDTO);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/coupons/{itemId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> adminGetCouponById(@PathVariable("itemId") Long itemId){
        ResponseMessage responseMessage = appService.getCouponById(itemId);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    /*@RequestMapping(value = "/admin/coupons", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getCoupons(){
        ResponseMessage responseMessage = appService.getCoupons();
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }*/

    @RequestMapping(value = "/admin/coupons", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getAdminCoupons(@RequestParam("page_number") int pageNumber,
                                                          @RequestParam("page_size") int pageSize,
                                                          @RequestParam("search_val") String searchVal){
        ResponseMessage responseMessage = appService.getCoupons(pageNumber, pageSize, searchVal);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }



    @RequestMapping(value = "/admin/coupons/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ResponseMessage> updateFoodCategory(@PathVariable("id") Long id, @RequestBody CouponDTO couponDTO){
        ResponseMessage responseMessage = appService.updateCoupon(id, couponDTO);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    /*@RequestMapping(value = "/admin/coupons/search", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> adminSearchCoupons(@RequestBody Map<String, String> values){
        ResponseMessage responseMessage = null;
        if(!values.get("val").equalsIgnoreCase("-")){
            responseMessage = appService.searchCoupons(values.get("val"));
        } else {
            responseMessage = appService.getCoupons();
        }

        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }*/

    @RequestMapping(value = "/public/coupons/code/{code}", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getCouponByCode(@PathVariable("code") String code){
        ResponseMessage responseMessage = appService.getCouponByCode(code);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }



    @RequestMapping(value = "/admin/users/{userType}", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> adminGetUsersByType(@PathVariable("userType") String userType,
                                                               @RequestParam("page_number") int pageNumber,
                                                                @RequestParam("page_size") int pageSize,
                                                                @RequestParam("search_val") String searchVal){
        ResponseMessage responseMessage = appService.getUsersByType(userType, pageNumber, pageSize, searchVal);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    /*@RequestMapping(value = "/admin/users/{userType}/search", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> adminSearchCustomers(@PathVariable("userType") String userType, @RequestBody Map<String, String> values){
        ResponseMessage responseMessage = null;
        if(!values.get("val").equalsIgnoreCase("")){
            responseMessage = appService.searchUsers(values.get("val"), userType);
        } else {
            responseMessage = appService.getUsersByType(userType);
        }

        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }*/

    @RequestMapping(value = "/admin/roles", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> adminGetRoles(){
        ResponseMessage responseMessage = appService.getEmployeeRoles();
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/orders/update", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> updateOrderStatus(@RequestBody Map<String, String> values)  {
        return new ResponseEntity<>(appService.updateOrderStatus(values), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/settings/update/{id}", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> updateSettingsParam(@RequestBody Map<String, String> values,
                                                               @PathVariable("id") Long id)  {
        return new ResponseEntity<>(appService.updateSettingsParam(values, id), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkout/authenticate-user/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> createConfirmIntent(@PathVariable("customerId") Long customerId)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return new ResponseEntity<>(appService.initialAuthentication(customerId, currentPrincipalName), HttpStatus.OK);
    }

    @RequestMapping(value = "/customer/{customerId}/orders", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> customerGetOrders(@PathVariable("customerId") Long customerId){
        ResponseMessage responseMessage = appService.getCustomerOrders(customerId);
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/orders", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> adminGetOrders(@RequestParam String customerId){
        ResponseMessage responseMessage = appService.getAdminOrders(Long.parseLong(customerId));
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/dashboard", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> getAdminDashboard(){
        ResponseMessage responseMessage = appService.getAdminDashboard();
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/checkout/create-confirm-intent", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> createConfirmIntent(@RequestBody Map<String, Object> items)  {
        return new ResponseEntity<>(appService.confirmOrder(items), HttpStatus.OK);
    }

    /*
    @RequestMapping(value = "/checkout/create-payment-intent", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> createPaymentIntent(@RequestBody StripePaymentIntentDTO intentDTO) throws StripeException {
        return new ResponseEntity<>(appService.createPaymentIntent(intentDTO), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkout/confirm-order", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> confirmOrder(@RequestBody Map<String, Object> items)  {
        return new ResponseEntity<>(appService.confirmOrder(items), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkout/{id}/confirm", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> confirmPaymentIntent(@PathVariable("id") String id) {
        return new ResponseEntity<>(appService.confirmPaymentIntent(id), HttpStatus.OK);
    }
    */
}
