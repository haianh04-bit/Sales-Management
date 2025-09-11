package com.codegym.services;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Component
public class FileUploadService {

    public String uploadFile(String uploadDirFile, MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf(".")); // ví dụ ".jpg"
            }
            // Sinh tên file random
            String fileName = UUID.randomUUID().toString() + ext;
            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(uploadDirFile);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            // Lưu file vào ổ đĩa
            File dest = new File(uploadDir + "/" + fileName);
            file.transferTo(dest);
            return  fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteFile(String filePath) {
        File file = new java.io.File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                System.out.println("Failed to delete file: " + filePath);
            }
        } else {
            System.out.println("File not found: " + filePath);
        }
    }
}
