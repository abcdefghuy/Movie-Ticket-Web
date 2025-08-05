package com.example.movieticketWeb.restController.admin;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.movieticketWeb.dto.request.NewsOrDiscountRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.NewsOrDiscountResponse;
import com.example.movieticketWeb.dto.response.PagedResponse;
import com.example.movieticketWeb.entity.Res;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IImageService;
import com.example.movieticketWeb.service.INewsService;

@RestController
@RequestMapping("/admin/news")
public class NewsRestController {
    private final INewsService newsService;
    private final IImageService imageService;

    public NewsRestController(INewsService newsService, IImageService imageService) {
        this.newsService = newsService;
        this.imageService = imageService;
    }

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addMovie(
            @RequestPart("news") @Valid NewsOrDiscountRequest newsRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException, GeneralSecurityException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = handleImageUpload(imageFile);
            newsRequest.setImages(imageUrl);
        } else {
            newsRequest.setImages(
                    "https://thumbs.dreamstime.com/b/news-woodn-dice-depicting-letters-bundle-small-newspapers-leaning-left-dice-34802664.jpg");
        }

        boolean isAdded = newsService.addNews(newsRequest);
        if (isAdded) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.CREATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(
            @PathVariable Long id,
            @RequestPart("news") @Valid NewsOrDiscountRequest newsRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException, GeneralSecurityException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = handleImageUpload(imageFile);
            newsRequest.setImages(imageUrl);
        }

        boolean isUpdated = newsService.updateNews(id, newsRequest);
        if (isUpdated) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.UPDATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        boolean isDeleted = newsService.deleteNews(id);
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.DELETED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @GetMapping()
    public ResponseEntity<?> getAllMovies(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int recordPerPage) {
        Page<NewsOrDiscountResponse> news = newsService.getAllNews(page, recordPerPage);
        PagedResponse<NewsOrDiscountResponse> response = new PagedResponse<>(
                news.getContent(),
                news.getNumber(),
                news.getSize(),
                news.getTotalElements(),
                news.getTotalPages(),
                news.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, newsService.getNewsById(id)));
    }

    private String handleImageUpload(MultipartFile file)
            throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
        String originalFileName = file.getOriginalFilename();

        String existingImageUrl = getSavedImageUrl(originalFileName);
        if (existingImageUrl != null) {

            // Thay vì tải file, chỉ lấy InputStream từ Google Drive
            try (InputStream existingFileInputStream = imageService.getFileInputStream(existingImageUrl);
                    InputStream newFileInputStream = file.getInputStream()) {

                String existingFileHash = getChecksum(existingFileInputStream);
                String newFileHash = getChecksum(newFileInputStream);

                if (existingFileHash.equals(newFileHash)) {
                    // Ảnh trùng -> Dùng lại ảnh cũ
                    return existingImageUrl;
                }
            } catch (IOException e) {
                e.printStackTrace(); // Xử lý lỗi khi lấy InputStream từ Google Drive
            }

            // Ảnh khác nội dung -> Đổi tên
            originalFileName = System.currentTimeMillis() + "_" + originalFileName;
        }

        // Lưu ảnh tạm thời
        File tempFile = File.createTempFile("upload_", null);
        file.transferTo(tempFile);

        // Upload ảnh lên Google Drive
        Res res = imageService.uploadImageToDrive(tempFile, originalFileName);

        if (res.getStatus() == 200) {
            // Gán đường dẫn ảnh cho movie.image
            return res.getUrl();
        } else {
            throw new IOException("Failed to upload image to Google Drive");
        }
    }

    private String getChecksum(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = inputStream.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        byte[] bytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    private String getSavedImageUrl(String fileName) {
        // Truy vấn Google Drive hoặc database để lấy URL ảnh dựa trên tên file
        return imageService.findUrlByFileName(fileName);
    }
}
