package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.request.MovieRequest;
import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.service.IMovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/admin/movies")
public class MovieRestController {
    @Autowired
    private IMovieService movieService;
    private static final String UPLOAD_DIR = "D:/project_uploads";
    @GetMapping
    public ResponseEntity<Map<String, Object>> listMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int soluong) {
// Tính toán offset cho phân trang
        int offset = (page - 1) * soluong;

        // Lấy danh sách phim
        List<MovieResponse> movies = movieService.getMovies(offset, soluong);
        System.out.println(movies);
        // Lấy tổng số records
        int noOfRecords = movieService.getNoOfRecords();
        int noOfPages = (int) Math.ceil((double) noOfRecords / soluong);

        // Tạo response JSON
        Map<String, Object> response = new HashMap<>();
        response.put("movies", movies);
        response.put("noOfPages", noOfPages);
        response.put("currentPage", page);
        response.put("recordsPerPage", soluong);
        response.put("noOfRecords", noOfRecords);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable int id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }
    @PostMapping( consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addMovie(@AuthenticationPrincipal Person person,
            @RequestPart("movie") @Valid MovieRequest movieRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException, NoSuchAlgorithmException {
        if (person == null || !person.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
        }
        movieRequest.setMovieTrailer(processYouTubeUrl(movieRequest.getMovieTrailer()));
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = handleImageUpload(imageFile);
            movieRequest.setImage(imageUrl);
        }
        else {
            movieRequest.setImage(
                    "https://thumbs.dreamstime.com/b/news-woodn-dice-depicting-letters-bundle-small-newspapers-leaning-left-dice-34802664.jpg");
        }

        boolean isAdded = movieService.addMovie(movieRequest);
        if (isAdded) {
            return ResponseEntity.ok(movieRequest);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thêm phim thất bại");
    }

    @PutMapping("/{id}")
    public ResponseEntity updateMovie(@PathVariable int id, @RequestBody MovieRequest movieDTO) {
        movieService.updateMovie(id, movieDTO);
        return ResponseEntity.ok("Movie updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable int id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok("Movie deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchMovies(@RequestParam String keyword) {
        return ResponseEntity.ok(movieService.searchMovie(keyword));
    }
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

    private String handleImageUpload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // Tạo thư mục nếu chưa có
        }

        String originalFileName = file.getOriginalFilename();
        Path existingFilePath = Paths.get(UPLOAD_DIR, originalFileName);

        if (Files.exists(existingFilePath)) {
            // Ảnh đã tồn tại, kiểm tra nội dung
            try (InputStream existingFileInputStream = new FileInputStream(existingFilePath.toFile());
                 InputStream newFileInputStream = file.getInputStream()) {

                String existingFileHash = getChecksum(existingFileInputStream);
                String newFileHash = getChecksum(newFileInputStream);

                if (existingFileHash.equals(newFileHash)) {
                    // Ảnh trùng -> Dùng lại ảnh cũ
                    return uploadDir + originalFileName;
                }
            }
            // Ảnh khác nội dung -> Đổi tên
            originalFileName = System.currentTimeMillis() + "_" + originalFileName;
        }

        // Lưu ảnh mới
        Path filePath = Paths.get(UPLOAD_DIR, originalFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/movies/" + originalFileName;
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
}
