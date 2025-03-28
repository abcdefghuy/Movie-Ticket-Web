package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.request.MovieRequest;
import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.entity.Movie;
import com.example.movieticketWeb.mapper.MovieMapper;
import com.example.movieticketWeb.repository.IMovieRepository;
import com.example.movieticketWeb.service.IMovieService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements IMovieService {
    private final IMovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String MOVIE_CACHE_KEY = "allMovies";
    @Override
    public List<MovieResponse> getAllMovies() {
        return List.of();
    }

    @Override
    public MovieResponse getMovieById(Long movieID) {
        String cacheKey = "movie:" + movieID;
        MovieResponse cachedMovie = (MovieResponse) redisTemplate.opsForValue().get(cacheKey);

        if (cachedMovie == null) {
            Movie movie = movieRepository.findById(movieID)
                    .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieID));

            // Chuyển đổi sang DTO trước khi lưu vào Redis
            cachedMovie = movieMapper.toDTO(movie);
            redisTemplate.opsForValue().set(cacheKey, cachedMovie, Duration.ofHours(1));
        }
        return cachedMovie;
    }
    @Override
    public boolean addMovie(MovieRequest movieRequest) {
        try {
            Movie movie = movieMapper.toEntity(movieRequest);
            movieRepository.save(movie);
            redisTemplate.delete(MOVIE_CACHE_KEY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateMovie(Long movieID, MovieRequest movieRequest) {
        try{
            Movie movie = movieRepository.findById(movieID)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));
            movie.setMovieName(movieRequest.getMovieName());
            movie.setCategory(movieRequest.getCategory());
            movie.setMovieDuration(movieRequest.getMovieDuration());
            movie.setDescription(movieRequest.getDescription());
            movie.setStatus(movieRequest.isStatus());
            movie.setReleaseDay(movieRequest.getReleaseDay());
            movie.setMovieTrailer(movieRequest.getMovieTrailer());
            movie.setImage(movieRequest.getImage());
            movieRepository.save(movie);
            List<MovieResponse> cachedMovies = (List<MovieResponse>) redisTemplate.opsForValue().get(MOVIE_CACHE_KEY);
            if (cachedMovies != null) {
                cachedMovies = cachedMovies.stream()
                        .map(m -> m.getMovieID() == movieID.intValue() ? movieMapper.toDTO(movie) : m)                            .collect(Collectors.toList());
                redisTemplate.opsForValue().set(MOVIE_CACHE_KEY, cachedMovies, Duration.ofHours(1));
            }
            String movieCacheKey = "movie:" + movieID;
            redisTemplate.opsForValue().set(movieCacheKey, movieMapper.toDTO(movie), Duration.ofHours(1));
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deleteMovie(Long movieID) {
        try{
            movieRepository.deleteById(movieID);
            redisTemplate.delete(MOVIE_CACHE_KEY);
            redisTemplate.delete("movie:" + movieID);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    public List<MovieResponse> searchMovie(String movieName) {
        return List.of();
    }


    @Override
    public List<MovieResponse> getMovies(int offset, int limit) {
        // Kiểm tra trong Redis trước
        List<MovieResponse> cachedMovies = (List<MovieResponse>) redisTemplate.opsForValue().get(MOVIE_CACHE_KEY);
        if (cachedMovies == null) {
            // Nếu chưa có cache, lấy dữ liệu từ database và lưu vào Redis
            List<MovieResponse> movies = movieRepository.findAll().stream()
                    .map(movieMapper::toDTO)
                    .collect(Collectors.toList());

            // Lưu vào Redis với TTL là 30 phút
            redisTemplate.opsForValue().set(MOVIE_CACHE_KEY, movies, Duration.ofMinutes(30));

            return paginateMovies(movies, offset, limit);
        }
        return paginateMovies(cachedMovies, offset, limit);
    }

    // Hàm phân trang từ danh sách đã cache
    private List<MovieResponse> paginateMovies(List<MovieResponse> movies, int offset, int limit) {
        int start = Math.min(offset, movies.size());
        int end = Math.min(start + limit, movies.size());
        return movies.subList(start, end);
    }

    @Override
    public int getNoOfRecords() {
        return movieRepository.countMovies();
    }

    @Override
    public Page<MovieResponse> searchMovies(String keyword, int page, int recordsPerPage) {
        List<MovieResponse> cachedMovies = getMoviesFromCache();

        if (!cachedMovies.isEmpty()) {
            // Nếu có dữ liệu trong Redis, lọc theo từ khóa và phân trang
            List<MovieResponse> filteredMovies = filterMoviesByKeyword(cachedMovies, keyword);
            return paginateMovie(filteredMovies, page, recordsPerPage);
        }

        // Nếu không có trong Redis, lấy từ Database
        Page<Movie> moviePage = movieRepository.searchMoviesByKeyword(
                keyword,
                PageRequest.of(page - 1, recordsPerPage) // Trừ page - 1 ở đây
        );

        // Chuyển đổi từ Entity -> DTO
        List<MovieResponse> movieResponses = moviePage.getContent()
                .stream()
                .map(movieMapper::toDTO)
                .toList();

        return new PageImpl<>(movieResponses, PageRequest.of(page - 1, recordsPerPage), moviePage.getTotalElements());
    }

    private List<MovieResponse> filterMoviesByKeyword(List<MovieResponse> cachedMovies, String keyword) {
        return cachedMovies.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    // Tìm kiếm phim theo từ khóa + danh mục
    @Override
    public Page<MovieResponse> searchMovies(String keyword, List<String> categories, int page, int recordsPerPage) {
        List<MovieResponse> cachedMovies = getMoviesFromCache();

        if (!cachedMovies.isEmpty()) {
            // Nếu có dữ liệu trong cache, lọc theo từ khóa + danh mục và phân trang
            List<MovieResponse> filteredMovies = filterMovies(cachedMovies, keyword, categories);
            return paginateMovie(filteredMovies, page, recordsPerPage);
        }

        // Nếu cache không có, lấy dữ liệu từ database
        Page<Movie> moviePage = movieRepository.searchMoviesByKeywordAndCategories(
                keyword, categories, PageRequest.of(page - 1, recordsPerPage));

        // Chuyển đổi từ Entity -> DTO và lưu cache
        List<MovieResponse> movieResponses = moviePage.getContent().stream().map(movieMapper::toDTO).toList();

        return new PageImpl<>(movieResponses, PageRequest.of(page - 1, recordsPerPage), moviePage.getTotalElements());
    }

    @Override
    public List<MovieResponse> getComingSoonMovies() {
        LocalDate today = LocalDate.now();
        LocalDate nextTwoMonths = today.plusMonths(2);
        // Lấy danh sách phim từ cache Redis
        List<MovieResponse> cachedMovies = getMoviesFromCache();
        if (!cachedMovies.isEmpty()) {
            return cachedMovies.stream()
                    .filter(movie -> movie.getReleaseDay() != null &&
                            movie.isStatus() && // Chỉ lấy phim có status = true
                            !movie.getReleaseDay().before(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant())) &&
                            !movie.getReleaseDay().after(Date.from(nextTwoMonths.atStartOfDay(ZoneId.systemDefault()).toInstant())))
                    .sorted(Comparator.comparing(MovieResponse::getReleaseDay)) // Sắp xếp theo ngày phát hành
                    .collect(Collectors.toList());
        }
        // Nếu không có dữ liệu trong cache, lấy từ DB
        List<Movie> movies = movieRepository.findComingSoonMovies();
        return movies.stream()
                .map(movieMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponse> getMoviesShowing() {
        LocalDate today = LocalDate.now();

        // Lấy danh sách phim từ cache Redis
        List<MovieResponse> cachedMovies = getMoviesFromCache();
        if (!cachedMovies.isEmpty()) {
            return cachedMovies.stream()
                    .filter(movie -> movie.getReleaseDay() != null &&
                            movie.isStatus() && // Chỉ lấy phim có status = true
                            !movie.getReleaseDay().after(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()))) // Chỉ lấy phim đã phát hành
                    .sorted(Comparator.comparing(MovieResponse::getReleaseDay).reversed()) // Sắp xếp giảm dần theo ngày phát hành
                    .collect(Collectors.toList());
        }

        // Nếu không có dữ liệu trong cache, lấy từ DB
        List<Movie> movies = movieRepository.findMoviesShowing();
        return movies.stream()
                .map(movieMapper::toDTO)
                .collect(Collectors.toList());
    }

    private List<MovieResponse> filterMovies(List<MovieResponse> cachedMovies, String keyword, List<String> categories) {
        return cachedMovies.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(keyword.toLowerCase()))
                .filter(movie -> categories == null || categories.isEmpty() ||
                        Arrays.stream(movie.getCategory().split(",\\s*")) // Tách thể loại bằng dấu phẩy
                                .anyMatch(categories::contains))
                .toList();
    }

    // Đếm số lượng kết quả theo từ khóa
    @Override
    public int getNoOfSearchResults(String keyword) {
        List<MovieResponse> cachedMovies = getMoviesFromCache();

        if (cachedMovies.isEmpty()) {
            return movieRepository.countMoviesByKeyword(keyword);
        }

        return (int) cachedMovies.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(keyword.toLowerCase()))
                .count();
    }

    // Đếm số lượng kết quả theo từ khóa + danh mục
    @Override
    public int getNoOfSearchResults(String keyword, List<String> categories) {
        List<MovieResponse> cachedMovies = getMoviesFromCache();

        if (cachedMovies.isEmpty()) {
            return movieRepository.countMoviesByKeywordAndCategories(keyword, categories);
        }

        return (int) cachedMovies.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(keyword.toLowerCase()))
                .filter(movie -> categories.contains(movie.getCategory()))
                .count();
    }

    // Lấy danh sách phim từ Redis, nếu không có thì trả về danh sách rỗng
    private List<MovieResponse> getMoviesFromCache() {
        List<MovieResponse> cachedMovies = (List<MovieResponse>) redisTemplate.opsForValue().get(MOVIE_CACHE_KEY);
        return cachedMovies != null ? cachedMovies : List.of();
    }
    private Page<MovieResponse> paginateMovie(List<MovieResponse> movies, int page, int recordsPerPage) {
        page = Math.max(page, 1);
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, movies.size());

        // Kiểm tra nếu start vượt quá số lượng phần tử -> Trả về danh sách rỗng
        List<MovieResponse> pageContent = (start < movies.size()) ? movies.subList(start, end) : List.of();

        // Trả về PageImpl chứa dữ liệu phân trang
        return new PageImpl<>(pageContent, PageRequest.of(page - 1, recordsPerPage), movies.size());
    }


}
