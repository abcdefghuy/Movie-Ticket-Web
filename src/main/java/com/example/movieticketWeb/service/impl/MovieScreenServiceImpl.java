package com.example.movieticketWeb.service.impl;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.movieticketWeb.dto.request.MovieScreenRequest;
import com.example.movieticketWeb.dto.response.*;
import com.example.movieticketWeb.entity.Movie;
import com.example.movieticketWeb.entity.MovieScreenings;
import com.example.movieticketWeb.entity.Room;
import com.example.movieticketWeb.exception.ResourceNotFoundException;
import com.example.movieticketWeb.mapper.MovieScreenMapper;
import com.example.movieticketWeb.repository.IMovieRepository;
import com.example.movieticketWeb.repository.IMovieScreenRepository;
import com.example.movieticketWeb.repository.IRoomRepository;
import com.example.movieticketWeb.service.IMovieScreenService;

@Service
public class MovieScreenServiceImpl implements IMovieScreenService {
    private final IMovieScreenRepository movieScreenRepository;
    private final IRoomRepository roomRepository;
    private final IMovieRepository movieRepository;
    private final MovieScreenMapper movieScreenMapper;
    private final SeatStatusServiceImpl seatStatusService;
    private final RedisTemplate<String, Object> redisTemplate;

    public MovieScreenServiceImpl(
            IMovieScreenRepository movieScreenRepository,
            IRoomRepository roomRepository,
            IMovieRepository movieRepository,
            MovieScreenMapper movieScreenMapper,
            SeatStatusServiceImpl seatStatusService,
            RedisTemplate<String, Object> redisTemplate) {
        this.movieScreenRepository = movieScreenRepository;
        this.roomRepository = roomRepository;
        this.movieRepository = movieRepository;
        this.movieScreenMapper = movieScreenMapper;
        this.seatStatusService = seatStatusService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Page<MovieScreenResponse> getMovieScreens(Long roomId, int page, int soluong) {
        String cacheKey = "movieScreenByRoom:" + roomId;

        List<MovieScreenResponse> cachedScreens =
                (List<MovieScreenResponse>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedScreens != null && !cachedScreens.isEmpty()) {
            return paginateMovieScreen(cachedScreens, page, soluong);
        }

        Page<MovieScreenings> movieScreenPage =
                movieScreenRepository.getMovieScreenByRoomId(roomId, PageRequest.of(page - 1, soluong));

        List<MovieScreenResponse> movieScreenResponses =
                movieScreenPage.stream().map(movieScreenMapper::toDTO).collect(Collectors.toList());
        redisTemplate.opsForValue().set(cacheKey, movieScreenResponses, Duration.ofMinutes(30));

        return paginateMovieScreen(movieScreenResponses, page, soluong);
    }

    private Page<MovieScreenResponse> paginateMovieScreen(
            List<MovieScreenResponse> cachedMovieScreens, int page, int recordsPerPage) {
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, cachedMovieScreens.size());
        return new PageImpl<>(
                cachedMovieScreens.subList(start, end),
                PageRequest.of(page - 1, recordsPerPage),
                cachedMovieScreens.size());
    }

    @Override
    public boolean addMovieScreen(MovieScreenRequest movieScreenRequest) {
        try {
            Movie movie =
                    movieRepository.findById(movieScreenRequest.getMovieId()).orElseThrow();
            Room room = roomRepository.findById(movieScreenRequest.getRoomId()).orElseThrow();
            MovieScreenings movieScreening =
                    movieScreenRepository.save(movieScreenMapper.toEntity(movieScreenRequest, movie, room));
            seatStatusService.addSeatStatusForScreening(movieScreening); // Thêm tất cả SeatStatus
            redisTemplate.delete("movieScreenByRoom:" + movieScreenRequest.getRoomId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateMovieScreen(Long movieScreenId, MovieScreenRequest movieScreenRequest) {
        try {
            Optional<MovieScreenings> movieScreeningsOptional = movieScreenRepository.findById(movieScreenId);
            if (movieScreeningsOptional.isPresent()) {
                MovieScreenings movieScreenings = movieScreeningsOptional.get();
                // 🔄 Cập nhật thông tin Room từ request
                movieScreenings.setScreeningDate(movieScreenRequest.getScreenDate());
                movieScreenings.setMovie(movieRepository
                        .findById(movieScreenRequest.getMovieId())
                        .orElseThrow());
                movieScreenings.setRoom(
                        roomRepository.findById(movieScreenRequest.getRoomId()).orElseThrow());
                movieScreenings.setStartHour(movieScreenRequest.getStartTime());
                movieScreenings.setEndHour(movieScreenRequest.getEndTime());
                movieScreenings.setStatus(movieScreenRequest.isStatus());
                // 💾 Lưu vào database
                movieScreenRepository.save(movieScreenings);

                // 🔥 Xóa dữ liệu cũ trong Redis
                redisTemplate.delete("movieScreenByRoom:" + movieScreenRequest.getRoomId());
                redisTemplate.delete("movie screen:" + movieScreenId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteMovieScreen(Long id) {
        try {
            MovieScreenings movieScreenings = movieScreenRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("MovieScreenings", "id", id.toString()));
            movieScreenRepository.delete(movieScreenings);
            redisTemplate.delete("movie screen:" + id);
            redisTemplate.delete(
                    "movieScreenByRoom:" + movieScreenings.getRoom().getRoomId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public MovieScreenResponse getMovieScreenById(Long id) {
        String cacheKey = "movie screen:" + id;
        // 🔥 Kiểm tra xem phòng có trong cache không
        MovieScreenResponse cachedMovieScreen =
                (MovieScreenResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedMovieScreen != null) {
            return cachedMovieScreen;
        }
        MovieScreenings movieScreenings = movieScreenRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MovieScreenings", "id", id.toString()));
        MovieScreenResponse movieScreenResponse = movieScreenMapper.toDTO(movieScreenings);
        // 🏦 Lưu vào Redis (lưu DTO, không lưu Entity)
        redisTemplate.opsForValue().set(cacheKey, movieScreenResponse, Duration.ofHours(1));
        return movieScreenResponse;
    }

    @Override
    public Page<MovieScreenResponse> searchMovieScreen(Long roomId, int page, int soluong, Date keyword) {
        String cacheKey = "movieScreenByRoom:" + roomId;
        List<MovieScreenResponse> cachedMovieScreens =
                (List<MovieScreenResponse>) redisTemplate.opsForValue().get(cacheKey);
        Page<MovieScreenResponse> movies = null;
        if (cachedMovieScreens != null) {
            List<MovieScreenResponse> filteredMovieScreens = filterMovieScreenByKeyword(cachedMovieScreens, keyword);
            return paginateMovieScreen(filteredMovieScreens, page, soluong);
        }
        List<MovieScreenings> movieScreenPage =
                movieScreenRepository.searchMovieScreeningsByScreenDate(roomId, keyword);
        return paginateMovieScreen(
                movieScreenPage.stream().map(movieScreenMapper::toDTO).collect(Collectors.toList()), page, soluong);
    }

    private List<MovieScreenResponse> filterMovieScreenByKeyword(
            List<MovieScreenResponse> cachedMovieScreens, Date keyword) {
        return cachedMovieScreens.stream()
                .filter(movieScreen -> movieScreen.getScreeningDate().equals(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public MovieScreenResponse getScreeningInfo(List<SeatStatusResponse> seatStatusResponses, long msID) {
        MovieScreenResponse movieScreenResponse = null; // this service's method
        try {
            movieScreenResponse = getMovieScreenById(msID);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("MovieScreenings", "id", String.valueOf(msID));
        }
        movieScreenResponse.setAvailableSeats(
                seatStatusResponses.stream().filter(seat -> !seat.isStatus()).count());
        return movieScreenResponse;
    }
}
