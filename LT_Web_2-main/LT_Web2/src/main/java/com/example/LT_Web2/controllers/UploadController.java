package com.example.LT_Web2.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    // Thư mục lưu trữ file upload (có thể config trong application.properties)
    @Value("${upload.directory:uploads}")
    private String uploadDirectory;

    @Value("${server.port:8088}")
    private String serverPort;

    /**
     * Upload file cho products, tables, hoặc loại khác
     * POST /api/upload/{type}
     * 
     * @param type: "products", "tables", etc.
     * @param file: MultipartFile từ form-data
     * @return { "url": "http://localhost:8088/uploads/products/uuid-filename.jpg" }
     */
    @PostMapping("/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Map<String, String>> uploadFile(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file) {

        Map<String, String> response = new HashMap<>();

        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("error", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file type (chỉ chấp nhận ảnh)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "Only image files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDirectory, type);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file unique với UUID
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Lưu file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Tạo URL trả về (frontend có thể truy cập qua static resource)
            String fileUrl = String.format("http://localhost:%s/%s/%s/%s",
                    serverPort, uploadDirectory, type, uniqueFilename);

            response.put("url", fileUrl);
            response.put("filename", uniqueFilename);
            response.put("type", type);
            response.put("size", String.valueOf(file.getSize()));

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("error", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Xóa file đã upload (optional)
     * DELETE /api/upload/{type}/{filename}
     */
    @DeleteMapping("/{type}/{filename}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<Map<String, String>> deleteFile(
            @PathVariable String type,
            @PathVariable String filename) {

        Map<String, String> response = new HashMap<>();

        try {
            Path filePath = Paths.get(uploadDirectory, type, filename);

            if (!Files.exists(filePath)) {
                response.put("error", "File not found");
                return ResponseEntity.status(404).body(response);
            }

            Files.delete(filePath);
            response.put("message", "File deleted successfully");
            response.put("filename", filename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("error", "Failed to delete file: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
