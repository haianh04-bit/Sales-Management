package com.codegym.validations;

import com.codegym.validations.custom.Image;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

public class ImageValidator implements ConstraintValidator<Image, MultipartFile> {
    private static final Long MAX_SIZE = 2 * 1024 * 1024L; // 2MB

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }

        String lowerCaseFileName = fileName.toLowerCase();
        boolean isImage = lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg") ||
                lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".gif");
        boolean isSizeValid = file.getSize() <= MAX_SIZE;
        return isImage && isSizeValid;
    }
}
