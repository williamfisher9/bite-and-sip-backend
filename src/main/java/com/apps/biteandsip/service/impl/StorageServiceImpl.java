package com.apps.biteandsip.service.impl;

import com.apps.biteandsip.exceptions.StorageException;
import com.apps.biteandsip.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class StorageServiceImpl implements StorageService {
    @Value("${file.upload.directory}")
    private String fileUploadDirectory;

    @Override
    public String store(MultipartFile file) {
        if(file.isEmpty()){
            throw new StorageException("File is empty");
        }

        if(!isAllowedFileType(file)){
            throw new StorageException("File type is not allowed");
        }

        try {
            String newFileName = UUID.randomUUID() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            file.transferTo( new File(fileUploadDirectory + newFileName));
            return newFileName;
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
    }

    private boolean isAllowedFileType(MultipartFile file) {
        boolean isAllowed = false;
        if(file.getContentType().contains("png") ||
                file.getContentType().contains("jpg") ||
                file.getContentType().contains("jpeg") ||
                file.getContentType().contains("gif")){
            isAllowed = true;
        }

        return isAllowed;
    }


}
