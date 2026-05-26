package vn.fastfood.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {
    public String storeProductImage(MultipartFile file, String currentImageName) {
        if (file == null || file.isEmpty()) {
            return currentImageName;
        }

        String fileName = Paths.get(file.getOriginalFilename()).getFileName().toString().replace(" ", "_");
        Path imageDir = Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "resources", "images");
        Path imagePath = imageDir.resolve(fileName);

        try {
            Files.createDirectories(imageDir);
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu hình ảnh", e);
        }
    }
}
