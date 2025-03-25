package com.apps.biteandsip.controller;

import com.apps.biteandsip.dto.FoodItemDTO;
import com.apps.biteandsip.dto.ResponseMessage;
import com.apps.biteandsip.model.PromoCode;
import com.apps.biteandsip.service.AppService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(value = "/admin/food-items/update/{id}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    public ResponseEntity<ResponseMessage> adminFoodCategories(){
        ResponseMessage responseMessage = appService.getFoodCategories();
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-items", method = RequestMethod.GET)
    public ResponseEntity<ResponseMessage> adminFoodItems(){
        ResponseMessage responseMessage = appService.getFoodItems();
        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }


    @RequestMapping(value = "/admin/food-categories/search", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> adminFoodCategoriesSearch(@RequestBody Map<String, String> values){
        ResponseMessage responseMessage = null;
        if(!values.get("val").equalsIgnoreCase("-")){
            responseMessage = appService.searchFoodCategories(values.get("val"));
        } else {
            responseMessage = appService.getFoodCategories();
        }

        return new ResponseEntity<>(responseMessage, HttpStatusCode.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/admin/food-items/search", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> adminFoodItemsSearch(@RequestBody Map<String, String> values){
        ResponseMessage responseMessage = null;
        if(!values.get("val").equalsIgnoreCase("-")){
            responseMessage = appService.searchFoodItems(values.get("val"));
        } else {
            responseMessage = appService.getFoodItems();
        }

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
