package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.request.MovieRequest;
import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.entity.Movie;
import com.example.movieticketWeb.mapper.MovieMapper;
import com.example.movieticketWeb.repository.IMovieRepository;
import com.example.movieticketWeb.service.IMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
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
    public MovieResponse getMovieById(int movieID) {
        return null;
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
    public void updateMovie(int movieID, MovieRequest movieRequest) {

    }

    @Override
    public void deleteMovie(int movieID) {

    }

    @Override
    public List<MovieResponse> searchMovie(String movieName) {
        return List.of();
    }


    @Override
    public List<MovieResponse> getMovies(int offset, int limit) {
        // Kiểm tra trong Redis trước
//        List<MovieResponse> cachedMovies = (List<MovieResponse>) redisTemplate.opsForValue().get(MOVIE_CACHE_KEY);
        List<MovieResponse> cachedMovies = null;
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
