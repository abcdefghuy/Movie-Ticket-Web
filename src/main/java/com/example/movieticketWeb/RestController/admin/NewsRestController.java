package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.request.MovieRequest;
import com.example.movieticketWeb.dto.request.NewsOrDiscountRequest;
import com.example.movieticketWeb.entity.NewsOrDiscount;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.entity.Res;
import com.example.movieticketWeb.service.IImageService;
import com.example.movieticketWeb.service.IMovieService;
import com.example.movieticketWeb.service.INewsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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
    public ResponseEntity<?> addMovie(@RequestPart("news") @Valid NewsOrDiscountRequest newsRequest,
                                      @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException, GeneralSecurityException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = handleImageUpload(imageFile);
            newsRequest.setImages(imageUrl);
        }
        else {
            newsRequest.setImages(
                    "https://thumbs.dreamstime.com/b/news-woodn-dice-depicting-letters-bundle-small-newspapers-leaning-left-dice-34802664.jpg");
        }

        boolean isAdded = newsService.addNews(newsRequest);
        if (isAdded) {
            return ResponseEntity.ok(Map.of("message", "Add News successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Add News failed"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id,
                                         @RequestPart("news") @Valid NewsOrDiscountRequest newsRequest,
                                         @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException, GeneralSecurityException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = handleImageUpload(imageFile);
            newsRequest.setImages(imageUrl);
        }

        boolean isUpdated = newsService.updateNews(id, newsRequest);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "Update News successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Update News failed"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        boolean isDeleted = newsService.deleteNews(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "Delete News successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Delete News failed"));
    }
    @GetMapping()
    public ResponseEntity<?> getAllMovies(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "5") int recordPerPage) {
        return ResponseEntity.ok(newsService.getAllNews(page, recordPerPage));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }


    private String handleImageUpload(MultipartFile file) throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
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
