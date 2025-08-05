package com.example.movieticketWeb.restController.admin;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.movieticketWeb.dto.request.MovieRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.dto.response.PagedResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.entity.Res;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IImageService;
import com.example.movieticketWeb.service.IMovieService;

@RestController
@RequestMapping("/admin/movies")
public class MovieRestController {
    @Autowired
    private IMovieService movieService;

    @Autowired
    private IImageService imageService;

    @GetMapping
    public ResponseEntity<?> listMovies(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int soluong) {
        // Lấy danh sách phim
        Page<MovieResponse> movies = movieService.getMovies(page, soluong);
        PagedResponse<MovieResponse> response = new PagedResponse<>(
                movies.getContent(),
                movies.getNumber(),
                movies.getSize(),
                movies.getTotalElements(),
                movies.getTotalPages(),
                movies.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, movieService.getMovieById(id)));
    }

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addMovie(
            @AuthenticationPrincipal Person person,
            @RequestPart("movie") @Valid MovieRequest movieRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException, GeneralSecurityException {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
        }
        movieRequest.setMovieTrailer(processYouTubeUrl(movieRequest.getMovieTrailer()));
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = handleImageUpload(imageFile);
            movieRequest.setImage(imageUrl);
        } else {
            movieRequest.setImage(
                    "https://thumbs.dreamstime.com/b/news-woodn-dice-depicting-letters-bundle-small-newspapers-leaning-left-dice-34802664.jpg");
        }

        boolean isAdded = movieService.addMovie(movieRequest);
        if (isAdded) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.CREATED));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(
            @PathVariable Long id,
            @RequestPart("movie") @Valid MovieRequest movieDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException, GeneralSecurityException {

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = handleImageUpload(imageFile);
            movieDTO.setImage(imageUrl);
        }
        boolean isEdited = movieService.updateMovie(id, movieDTO);
        if (isEdited) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.UPDATED));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
        //

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        boolean isDeleted = movieService.deleteMovie(id);
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.DELETED));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMovies(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "category", required = false) List<String> categories,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "soluong", defaultValue = "5") int recordsPerPage) {

        boolean searchAll = categories == null || categories.contains("All");
        Page<MovieResponse> movies;

        if (searchAll) {
            movies = movieService.searchMovies(keyword, page, recordsPerPage);
        } else {
            movies = movieService.searchMovies(keyword, categories, page, recordsPerPage);
        }
        PagedResponse<MovieResponse> response = new PagedResponse<>(
                movies.getContent(),
                movies.getNumber(),
                movies.getSize(),
                movies.getTotalElements(),
                movies.getTotalPages(),
                movies.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }

    //    @GetMapping("/coming-soon")
    //    public ResponseEntity<?> getComingSoonMovies() {
    //        List<MovieResponse> movies = movieService.getComingSoonMovies();
    //        Map<String, Object> response = new HashMap<>();
    //        response.put("movies", movies);
    //        return ResponseEntity.ok(response);
    //    }

    private String processYouTubeUrl(String url) {
        String videoId = null;
        if (url != null) {
            if (url.contains("youtube.com/watch")) {
                String[] urlParts = url.split("[?&]");
                for (String part : urlParts) {
                    if (part.startsWith("v=")) {
                        videoId = part.split("=")[1];
                        break;
                    }
                }
            } else if (url.contains("youtu.be")) {
                String[] urlParts = url.split("/");
                if (urlParts.length > 0) {
                    videoId = urlParts[urlParts.length - 1];
                }
            }
            if (videoId != null) {
                return "https://www.youtube.com/watch?v=" + videoId;
            }
        }
        return url;
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
