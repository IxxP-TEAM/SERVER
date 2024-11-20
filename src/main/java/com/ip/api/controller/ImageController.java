package com.ip.api.controller;

import com.ip.api.config.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final S3Service s3Service;

    public ImageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null) {
                throw new IllegalArgumentException("파일이 null입니다.");
            }
            System.out.println("파일 이름: " + file.getOriginalFilename());
            String imageUrl = s3Service.uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            return new ResponseEntity<>(imageUrl, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("이미지 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
