package com.ecommerce.project.serviceImpl;

import com.ecommerce.project.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String updateImage(String path, MultipartFile file) throws IOException {

        // Get original image name
        String originalFileName = file.getOriginalFilename();

        // Generate unique ID
        String uniqueId = UUID.randomUUID().toString();

        // Extract extension (e.g., ".jpg")
        // Create unique filename: uniqueId + extension (e.g., "123e4567.jpg")
        String fileName = uniqueId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        String filePath = path + File.separator + fileName;

        // Check if FOLDER exists, create if not
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }
}
