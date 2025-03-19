package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.request.MovieRequest;
import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.entity.Movie;
import com.example.movieticketWeb.mapper.MovieMapper;
import com.example.movieticketWeb.repository.IMovieRepository;
import com.example.movieticketWeb.service.IMovieService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        Movie movie = (Movie) redisTemplate.opsForValue().get(cacheKey);

        if (movie == null) {
            movie = movieRepository.findById(movieID)
                    .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieID));

            // 🟢 Lưu lại vào cache để lần sau lấy nhanh hơn
            redisTemplate.opsForValue().set(cacheKey, movie, Duration.ofHours(1));
        }

        // 🎯 Chuyển đổi sang MovieResponse để trả về
        return movieMapper.toDTO(movie);
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
            boolean updated = false;

            for (Field field : Movie.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object oldValue = field.get(movie);
                Object newValue = field.get(movieRequest);

                if (!Objects.equals(oldValue, newValue)) {
                    field.set(movie, newValue);
                    updated = true;
                }
            }
            if(updated){
                movieRepository.save(movie);
                List<MovieResponse> cachedMovies = (List<MovieResponse>) redisTemplate.opsForValue().get(MOVIE_CACHE_KEY);
                if (cachedMovies != null) {
                    cachedMovies = cachedMovies.stream()
                            .map(m -> m.getMovieID() == movieID.intValue() ? movieMapper.toDTO(movie) : m)                            .collect(Collectors.toList());
                    redisTemplate.opsForValue().set(MOVIE_CACHE_KEY, cachedMovies, Duration.ofHours(1));
                }
                String movieCacheKey = "movie:" + movieID;
                redisTemplate.opsForValue().set(movieCacheKey, movie, Duration.ofHours(1));
            }

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
}
